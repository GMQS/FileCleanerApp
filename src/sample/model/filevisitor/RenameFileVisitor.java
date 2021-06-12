package sample.model.filevisitor;

import sample.model.DirectoryStringGenerator;
import sample.model.RenameFile;
import sample.properties.AppProperties;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RenameFileVisitor implements FileVisitor<Path> {
    private final AppProperties appProperties;
    private final Path srcDirPath;
    private final Path dstDirPath;

    public RenameFileVisitor(final AppProperties appProperties) {
        this.appProperties = appProperties;
        this.srcDirPath = appProperties.getSrcDirectoryPath();
        this.dstDirPath = appProperties.getDstDirectoryPath();
    }

    //ディレクトリをvisitする前にコールされる
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        final Path dstPath = Path.of(dstDirPath + "\\その他").resolve(srcDirPath.relativize(dir));
        if (!Files.exists(dstDirPath)) {
            Files.createDirectory(dstDirPath);
        }

        if (appProperties.isCleanInnerDir()) {

        } else {
            if (!Files.exists(dstPath) && !dir.equals(srcDirPath)) {
                final Path otherDir = Path.of(dstDirPath + "\\その他");
                if (!Files.exists(otherDir)) {
                    Files.createDirectory(otherDir);
                }
                Files.createDirectory(dstPath);
            }
        }

        return FileVisitResult.CONTINUE;
    }

    //ファイルをvisitしたときにコールされる
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (appProperties.isCleanInnerDir()) {

        } else {
            if (!file.getParent().equals(srcDirPath)) {
                Path path = Path.of(dstDirPath + "\\その他").resolve(srcDirPath.relativize(file));
                Files.move(file, path);
                return FileVisitResult.CONTINUE;
            }
        }

        final String fileName = file.getFileName().toString();
        final RenameFile renameFile = new RenameFile(fileName);
        final String extension = renameFile.getFileExtensionChar();
        final String mediaDirectory = new DirectoryStringGenerator().getMediaDirectory(extension);
        Path dstMediaDirPath = Path.of(dstDirPath + mediaDirectory);

        if (!Files.exists(dstMediaDirPath)) {
            Files.createDirectory(dstMediaDirPath);
        }
        if (appProperties.isCreateExtensionDir() && mediaDirectory.equals("\\その他") && !Files.exists(Path.of(dstMediaDirPath + "\\" + extension.toUpperCase()))) {
            Files.createDirectory(Path.of(dstMediaDirPath + "\\" + extension.toUpperCase()));
            dstMediaDirPath = Path.of(dstMediaDirPath + "\\" + extension.toUpperCase());
        }

        Path dstFilePath = Path.of(dstMediaDirPath + "\\" + fileName);
        for (int i = 1; Files.exists(dstFilePath) && !file.equals(dstFilePath); i++) {
            final String rnmFileName = renameFile.rename("(" + i + ")");
            dstFilePath = Path.of(dstMediaDirPath + "\\" + rnmFileName);
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
        try {
            if (!srcDirPath.equals(dir) && exc == null) {
                Files.delete(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.CONTINUE;
    }
}
