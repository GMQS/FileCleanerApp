package sample.model.filevisitor;

import sample.properties.AppProperties;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class BasicFileVisitor implements FileVisitor<Path> {
    private final AppProperties appProperties;
    private final Path srcDirPath;
    private final Path dstDirPath;

    public BasicFileVisitor(final AppProperties appProperties) {
        this.appProperties = appProperties;
        this.srcDirPath = appProperties.getSrcDirectoryPath();
        this.dstDirPath = appProperties.getDstDirectoryPath();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path path = dstDirPath.resolve(srcDirPath.relativize(dir));
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path dstFilePath = dstDirPath.resolve(srcDirPath.relativize(file));
        if(!Files.exists(dstFilePath)){
            Files.move(file, dstFilePath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (!srcDirPath.equals(dir) && exc == null) {
            Files.delete(dir);
        }
        return FileVisitResult.CONTINUE;
    }
}
