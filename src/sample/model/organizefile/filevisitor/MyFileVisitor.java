package sample.model.organizefile.filevisitor;

import org.apache.commons.io.FileUtils;
import sample.model.organizefile.FileCounter;
import sample.model.organizefile.OptionalFileMover;
import sample.properties.AppProperty;
import sample.properties.DirectoryProperty;
import sample.properties.ProgressProperties;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class MyFileVisitor implements FileVisitor<Path> {
    private final AppProperty appProperty;
    private final Path srcDirPath;
    private final Path dstDirPath;
    private final ArrayList<Path> foundDirList;
    private final StringBuilder stackExceptionText;
    private final FileCounter counter;
    private final ProgressProperties progressProperties;
    private final List<Path> visitedDirectories;
    private final List<Path> visitedFiles;
    private Path tmpDir;


    public MyFileVisitor(final AppProperty appProperty, final ProgressProperties progressProperties, final StringBuilder stackExceptionText) {
        this.appProperty = appProperty;
        this.srcDirPath = appProperty.getSrcDirectoryPath();
        this.dstDirPath = appProperty.getDstDirectoryPath();
        this.progressProperties = progressProperties;
        this.stackExceptionText = stackExceptionText;

        this.counter = new FileCounter();
        this.visitedFiles = new ArrayList<>();
        this.visitedDirectories = new ArrayList<>();
        this.foundDirList = new ArrayList<>();
    }

    public ArrayList<Path> getFoundDirList() {
        return this.foundDirList;
    }

    public String getFormattedResultText() {
        return this.counter.getFormattedResultText();
    }

    //ディレクトリをvisitする前にコールされる
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        if (Thread.interrupted()) {
            stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
            return FileVisitResult.TERMINATE;
        }

        //既に移動が完了しているディレクトリをvisitした場合はスキップする
        if (visitedDirectories.contains(dir)) {
            return FileVisitResult.SKIP_SUBTREE;
        }

        List<Path> list;
        long fileSize = 0L;
        long fileCount = 0L;
        long dirCount = 0L;
        try {
            list = Files.walk(dir).collect(Collectors.toList());
            fileSize = list.stream().filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
            fileCount = list.stream().filter(p -> p.toFile().isFile()).count();
            dirCount = list.stream().filter(p -> p.toFile().isDirectory()).count();

        } catch (IOException e) {
            stackExceptionText.append(e).append("\nファイル一覧の取得に失敗しました\nファイル情報の取得に失敗しました").append("\n\n");
            progressProperties.setIndeterminate();
        }

        //ルートディレクトリはスキップ
        if (dir.equals(srcDirPath)) {
            return FileVisitResult.CONTINUE;
        }

        final String folderFoundOption = appProperty.getFolderFoundOption();
        if (folderFoundOption.equals("フォルダの中身をすべて整理して移動")) {
            return FileVisitResult.CONTINUE;
        }

        if (folderFoundOption.equals("フォルダを移動しない")) {
            counter.addSkipFileCount(fileCount);
            counter.addSkipDirCount(dirCount);
            updateProgress(fileSize);
            return FileVisitResult.SKIP_SUBTREE;
        }

        if (folderFoundOption.equals("フォルダの中身は整理せずにそのまま移動")) {
            final Path path = dstDirPath.resolve(srcDirPath.relativize(dir));

            if (dir.equals(path)) {
                counter.addSkipFileCount(fileCount);
                counter.addSkipDirCount(dirCount);
                updateProgress(fileSize);
                return FileVisitResult.SKIP_SUBTREE;
            }

            if (!Files.exists(path)) {
                try {
                    Files.move(dir, path);
                    counter.addMoveFileCount(fileCount);
                    counter.addMoveDirCount(dirCount);
                } catch (IOException e) {
                    stackExceptionText.append(e).append("\nフォルダの移動に失敗しました").append("\n\n");
                    counter.addSkipFileCount(fileCount);
                    counter.addSkipDirCount(dirCount);
                } finally {
                    updateProgress(fileSize);
                }
                return FileVisitResult.SKIP_SUBTREE;
            }
            if (Files.exists(path)) {
                final String folderDuplicateOption = appProperty.getFolderDuplicateOption();
                if (folderDuplicateOption.equals("フォルダを統合する")) {
                    tmpDir = path;
                    return FileVisitResult.CONTINUE;
                }

                if (folderDuplicateOption.equals("フォルダを移動しない")) {
                    counter.addSkipFileCount(fileCount);
                    counter.addSkipDirCount(dirCount);
                    updateProgress(fileSize);
                    return FileVisitResult.SKIP_SUBTREE;
                }

                if (folderDuplicateOption.equals("フォルダ名を変更して移動")) {
                    Path tmpPath = path;
                    for (int i = 1; Files.exists(tmpPath); i++) {
                        tmpPath = Path.of(tmpPath + "(" + i + ")");
                    }
                    try {
                        Files.move(dir, tmpPath);
                        counter.addMoveFileCount(fileCount);
                        counter.addMoveDirCount(dirCount);
                    } catch (IOException e) {
                        stackExceptionText.append(e).append("\nフォルダの移動に失敗しました").append("\n\n");
                        counter.addSkipFileCount(fileCount);
                        counter.addSkipDirCount(dirCount);
                    } finally {
                        updateProgress(fileSize);
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }
        }
        stackExceptionText.append("予測不能なディレクトリvisitの処理ブロックに到達しました").append("\n\n");
        return FileVisitResult.TERMINATE;
    }

    //ファイルをvisitしたときにコールされる
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

        if (Thread.interrupted()) {
            stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
            return FileVisitResult.TERMINATE;
        }

        //既に移動したファイルをvisitした場合はスキップ
        if (visitedFiles.contains(file)) {
            return FileVisitResult.CONTINUE;
        }

        long fileSize = 0L;
        try {
            fileSize = Files.size(file);
        } catch (IOException e) {
            stackExceptionText.append(e).append("\nファイルサイズの取得に失敗しました").append("\n\n");
            progressProperties.setIndeterminate();
        }
        final String fileName = file.getFileName().toString();
        final String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        final String folderDuplicateOption = appProperty.getFolderDuplicateOption();
        final String folderFoundOption = appProperty.getFolderFoundOption();
        final OptionalFileMover fileMover = new OptionalFileMover(file, counter, appProperty);


        try {
            if (appProperty.useAdvancedSetting()) {
                if (file.getParent().equals(srcDirPath) || folderFoundOption.equals("フォルダの中身をすべて整理して移動")) {
                    //個別登録フォルダをチェック
                    for (DirectoryProperty property : appProperty.getDirectoryPropertyList().getProperties()) {
                        if (property.isDisableSetting()) {
                            continue;
                        }
                        if (Arrays.stream(property.getExtensionsArray()).anyMatch(p -> p.equalsIgnoreCase(extension))) {
                            final Path target = Path.of(property.getDstDirectory().toPath() + "\\" + file.getFileName());
                            fileMover.setTargetFullPath(target).move();
                            visitedFiles.add(target);
                            return FileVisitResult.CONTINUE;
                        }
                    }
                }
            }


            if (folderFoundOption.equals("フォルダの中身をすべて整理して移動")) {
                if (appProperty.isCreateFolder()) {
                    final Path extensionDir = Path.of(dstDirPath + "\\" + extension.toUpperCase() + "ファイル");
                    final Path target = Path.of(extensionDir + "\\" + file.getFileName());
                    if (foundDirList.stream().noneMatch(p -> p.equals(extensionDir))) {
                        foundDirList.add(extensionDir);
                    }
                    if (!Files.exists(extensionDir)) {
                        Files.createDirectory(extensionDir);
                        visitedDirectories.add(extensionDir);
                        fileMover.setTargetFullPath(target).move();
                        visitedFiles.add(target);
                        return FileVisitResult.CONTINUE;
                    }
                    if (Files.exists(extensionDir)) {
                        fileMover.setTargetFullPath(target).move();
                        visitedFiles.add(target);
                        return FileVisitResult.CONTINUE;
                    }
                }

                final Path target = Path.of(dstDirPath + "\\" + file.getFileName());
                fileMover.setTargetFullPath(target).move();
                visitedFiles.add(target);
                return FileVisitResult.CONTINUE;
            }

            if (folderDuplicateOption.equals("フォルダを統合する") && !file.getParent().equals(srcDirPath)) {
                final Path target = Path.of(tmpDir + "\\" + file.getFileName());
                fileMover.setTargetFullPath(target).move();
                visitedFiles.add(target);
                return FileVisitResult.CONTINUE;
            }


            if (appProperty.isCreateFolder()) {
                final Path extensionDir = Path.of(dstDirPath + "\\" + extension.toUpperCase() + "ファイル");
                final Path target = Path.of(extensionDir + "\\" + file.getFileName());
                if (foundDirList.stream().noneMatch(p -> p.equals(extensionDir))) {
                    foundDirList.add(extensionDir);
                }
                if (!Files.exists(extensionDir)) {
                    Files.createDirectory(extensionDir);
                    visitedDirectories.add(extensionDir);
                    fileMover.setTargetFullPath(target).move();
                    visitedFiles.add(target);
                    return FileVisitResult.CONTINUE;
                }
                if (Files.exists(extensionDir)) {
                    fileMover.setTargetFullPath(target).move();
                    visitedFiles.add(target);
                    return FileVisitResult.CONTINUE;
                }
            }

            final Path target = Path.of(dstDirPath + "\\" + file.getFileName());
            fileMover.setTargetFullPath(target).move();
            visitedFiles.add(target);
            return FileVisitResult.CONTINUE;

        } catch (IOException e) {
            stackExceptionText.append(e).append("\nファイルの移動に失敗しました").append("\n\n");
            counter.addSkipFileCount();
            return FileVisitResult.CONTINUE;
        } finally {
            updateProgress(fileSize);
        }
    }

    //ファイルのvisit失敗時にコールされる
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if (Thread.interrupted()) {
            stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
            return FileVisitResult.TERMINATE;
        }
        if (exc != null) {
            stackExceptionText.append(exc).append("\nファイルの読み込みに失敗しました").append("\n\n");
            counter.addSkipFileCount();
        }
        long fileSize = 0L;
        try {
            fileSize = Files.size(file);
        } catch (IOException e) {
            stackExceptionText.append(e).append("\nファイルサイズの取得に失敗しました").append("\n\n");
            progressProperties.setIndeterminate();
        }
        updateProgress(fileSize);
        return FileVisitResult.CONTINUE;
    }

    //ディレクトリのvisit終了時にコールされる
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        if (Thread.interrupted()) {
            stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
            return FileVisitResult.TERMINATE;
        }
        try {
            if (Files.list(dir).findAny().isEmpty()) {
                Files.deleteIfExists(dir);
            }
        } catch (IOException e) {
            stackExceptionText.append(e).append("\nフォルダの削除に失敗しました").append("\n\n");
            e.printStackTrace();
        }
        return FileVisitResult.CONTINUE;
    }

    private void updateProgress(long fileSize) {
        try {
            progressProperties.addDoneFileSize(fileSize);
            progressProperties.setDoneFileCount(counter.getTotalCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
