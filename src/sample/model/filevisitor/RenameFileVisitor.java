package sample.model.filevisitor;

import sample.model.RenameFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

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
        final String fileName = file.getFileName().toString();
        final RenameFile renameFile = new RenameFile(fileName);
        final String mediaDirectory = getMediaDirectory(renameFile.getFileExtensionChar());
        final Path dstMediaDirPath = Paths.get(dstDirPath + mediaDirectory);
        if (!Files.exists(dstMediaDirPath)) {
            Files.createDirectory(dstMediaDirPath);
        }
//      Path dstFilePath = dstDirPath.resolve(srcDirPath.relativize(file));
        Path dstFilePath = Paths.get(dstDirPath + mediaDirectory + "/" + fileName);
        for (int i = 1; Files.exists(dstFilePath); i++) {
            final String rnmFileName = renameFile.rename("(" + i + ")");
            dstFilePath = Paths.get(dstDirPath + mediaDirectory + "/"+ rnmFileName);
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

    private String getMediaDirectory(final String extension) {
        switch (extension.toLowerCase()) {
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
            case "bmp":
                return "/画像ファイル";
            case "mp4":
            case "mov":
            case "wmv":
            case "flv":
            case "mpg":
            case "mpeg":
            case "mkv":
            case "avi":
            case "ts":
                return "/動画ファイル";
            case "wave":
            case "aif":
            case "mp3":
            case "aac":
            case "wma":
            case "3gp":
            case "ogg":
            case "oga":
            case "ape":
                return "/音声ファイル";
            default:
                return "/その他" + extension.toUpperCase();
        }
    }
}
