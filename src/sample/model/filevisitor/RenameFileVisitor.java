package sample.model.filevisitor;

import sample.model.RenameFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RenameFileVisitor implements FileVisitor<Path> {
    private final Path srcDirPath;
    private final Path dstDirPath;

    public RenameFileVisitor(final Path srcDirPath, final Path dstDirPath) {
        this.srcDirPath = srcDirPath;
        this.dstDirPath = dstDirPath;
    }

    //ディレクトリをvisitする前にコールされる
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path path = dstDirPath.resolve(srcDirPath.relativize(dir));
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return FileVisitResult.CONTINUE;
    }

    //ファイルをvisitしたときにコールされる
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path dstFilePath = dstDirPath.resolve(srcDirPath.relativize(file));
        for (int i = 1; Files.exists(dstFilePath); i++) {
            final String rnmFileName = new RenameFile(file.getFileName().toString()).rename("(" + i + ")");
            dstFilePath = Paths.get(dstDirPath + "/" + rnmFileName);
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
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (!srcDirPath.equals(dir) && exc == null) {
            Files.delete(dir);
        }
        return FileVisitResult.CONTINUE;
    }
}
