package sample.model.filevisitor;

import sample.model.RenameFile;
import sample.properties.AppProperties;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class RenameFileVisitor implements FileVisitor<Path> {
    private final AppProperties appProperties;
    private final Path srcDirPath;
    private final Path dstDirPath;
    private Path tmpDir;

    public RenameFileVisitor(final AppProperties appProperties) {
        this.appProperties = appProperties;
        this.srcDirPath = appProperties.getSrcDirectoryPath();
        this.dstDirPath = appProperties.getDstDirectoryPath();
    }

    //ディレクトリをvisitする前にコールされる
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (dir.equals(srcDirPath)) {
            return FileVisitResult.CONTINUE;
        }
        switch (appProperties.getFolderFoundOption()) {
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
                    switch (appProperties.getFolderDuplicateOption()) {
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

    }

    //ファイルをvisitしたときにコールされる
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (!file.getParent().equals(srcDirPath)) {
            switch (appProperties.getFolderFoundOption()) {
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
        final Path extensionDirPath = Path.of(dstDirPath + "\\" + extension.toUpperCase());

        if (!Files.exists(extensionDirPath)) {
            Files.createDirectory(extensionDirPath);
        }

        Path dstFilePath = Path.of(extensionDirPath + "\\" + fileName);
        for (int i = 1; Files.exists(dstFilePath) && !file.equals(dstFilePath); i++) {
            final String rnmFileName = new RenameFile(fileName).rename("(" + i + ")");
            dstFilePath = Path.of(extensionDirPath + "\\" + rnmFileName);
        }
        Files.move(file, dstFilePath);
        return FileVisitResult.CONTINUE;
    }

    //ファイルのvisit失敗時にコールされる
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    //ディレクトリのvisit終了時にコールされる
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        if (dir.equals(srcDirPath)) {
            return FileVisitResult.CONTINUE;
        }
        try {
            Files.deleteIfExists(dir);
        } catch (IOException e) {
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.CONTINUE;
    }
}
