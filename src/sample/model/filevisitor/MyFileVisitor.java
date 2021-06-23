package sample.model.filevisitor;

import sample.model.RenameFile;
import sample.properties.AppProperty;
import sample.properties.DirectoryProperty;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Objects;

public class MyFileVisitor implements FileVisitor<Path> {
    private final AppProperty appProperty;
    private final Path srcDirPath;
    private final Path dstDirPath;
    private ArrayList<Path> foundDirList;
    private StringBuilder stackExceptionText;

    public MyFileVisitor(final AppProperty appProperty) {
        this.appProperty = appProperty;
        this.srcDirPath = appProperty.getSrcDirectoryPath();
        this.dstDirPath = appProperty.getDstDirectoryPath();
    }

    public ArrayList<Path> getFoundDirList() {
        if (Objects.isNull(foundDirList)) {
            foundDirList = new ArrayList<>();
        }
        return this.foundDirList;
    }

    public StringBuilder getStackExceptionText() {
        if (Objects.isNull(stackExceptionText)) {
            stackExceptionText = new StringBuilder();
        }
        return this.stackExceptionText;
    }

    //ディレクトリをvisitする前にコールされる
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (dir.equals(srcDirPath)) {
            return FileVisitResult.CONTINUE;
        }

        try {
            switch (appProperty.getFolderFoundOption()) {
                case "フォルダの中身をすべて整理して移動":
                case "フォルダを移動しない": {
                    return FileVisitResult.CONTINUE;
                }
                case "フォルダの中身は整理せずにそのまま移動": {
                    Path path = dstDirPath.resolve(srcDirPath.relativize(dir));
                    if (!Files.exists(path)) {
                        Files.move(dir, path);
                        return FileVisitResult.CONTINUE;
                    }
                    if (Files.exists(path)) {
                        switch (appProperty.getFolderDuplicateOption()) {
                            case "フォルダを移動しない":
                            case "フォルダを統合する": {
                                return FileVisitResult.CONTINUE;
                            }
                            case "フォルダ名を変更して移動": {
                                Path tmpPath = path;
                                for (int i = 1; Files.exists(tmpPath); i++) {
                                    tmpPath = Path.of(tmpPath + "(" + i + ")");
                                }
                                Files.move(dir, tmpPath);
                                return FileVisitResult.CONTINUE;
                            }
                        }
                        break;
                    }
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
            return FileVisitResult.CONTINUE;
        } catch (IOException e) {
            if (Objects.isNull(stackExceptionText)) {
                stackExceptionText = new StringBuilder();
            }
            stackExceptionText.append(e).append("\n\n");
            e.printStackTrace();
            return FileVisitResult.CONTINUE;
        }
    }

    //ファイルをvisitしたときにコールされる
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

        if (!file.getParent().equals(srcDirPath)) {
            switch (appProperty.getFolderFoundOption()) {
                case "フォルダの中身は整理せずにそのまま移動":
                case "フォルダを移動しない": {
                    return FileVisitResult.CONTINUE;
                }
                case "フォルダの中身をすべて整理して移動": {
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }

        final String fileName = file.getFileName().toString();
        final String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        Path extensionDirPath = dstDirPath;


        if (appProperty.isCreateFolder()) {
            extensionDirPath = Path.of(dstDirPath + "\\" + extension.toUpperCase() + "ファイル");
            if (Objects.isNull(foundDirList)) {
                foundDirList = new ArrayList<>();
            }
            foundDirList.add(extensionDirPath);
        }


        for (DirectoryProperty property : appProperty.getDirectoryPropertyList().getProperties()) {
            final String[] extensionsArray = property.getExtensionsArray();
            for (String ext : extensionsArray) {
                if (ext.equalsIgnoreCase(extension)) {
                    extensionDirPath = property.getDstDirectory().toPath();
                }
            }
        }

        try {
            if (!Files.exists(extensionDirPath)) {
                Files.createDirectory(extensionDirPath);
            }
            Path dstFilePath = Path.of(extensionDirPath + "\\" + fileName);

            switch (appProperty.getFileDuplicateOption()) {
                case "ファイル名を変更して移動": {
                    for (int i = 1; Files.exists(dstFilePath) && !file.equals(dstFilePath); i++) {
                        final String rnmFileName = new RenameFile(fileName).rename("(" + i + ")");
                        dstFilePath = Path.of(extensionDirPath + "\\" + rnmFileName);
                    }
                    Files.move(file, dstFilePath);
                    break;
                }
                case "ファイルを移動しない": {
                    if (Files.exists(dstFilePath)) {
                        break;
                    }
                    Files.move(file, dstFilePath);
                    break;
                }
                case "ファイルを上書きして移動": {
                    Files.move(file, dstFilePath, StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
            return FileVisitResult.CONTINUE;
        } catch (IOException e) {
            if (Objects.isNull(stackExceptionText)) {
                stackExceptionText = new StringBuilder();
            }
            stackExceptionText.append(e).append("\n\n");
            e.printStackTrace();
            return FileVisitResult.CONTINUE;
        }
    }

    //ファイルのvisit失敗時にコールされる
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if (exc != null) {
            if (Objects.isNull(stackExceptionText)) {
                stackExceptionText = new StringBuilder();
            }
            stackExceptionText.append(exc).append("\n\n");
        }
        return FileVisitResult.CONTINUE;
    }

    //ディレクトリのvisit終了時にコールされる
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        if (dir.equals(srcDirPath)) {
            return FileVisitResult.CONTINUE;
        }
        try {
            if (Files.list(dir).findAny().isEmpty()) {
                Files.delete(dir);
            }
        } catch (IOException e) {
            if (Objects.isNull(stackExceptionText)) {
                stackExceptionText = new StringBuilder();
            }
            stackExceptionText.append(e).append("\n\n");
            e.printStackTrace();
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.CONTINUE;
    }
}
