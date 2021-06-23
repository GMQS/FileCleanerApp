package sample.model;

import sample.model.filevisitor.MyFileVisitor;
import sample.properties.AppProperty;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MoveFiles implements Runnable {

    private final AppProperty appProperty;
    private Callback callback;

    public MoveFiles(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    @FunctionalInterface
    public interface Callback {
        void finished();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        final Path srcDirectoryPath = appProperty.getSrcDirectoryPath();
        final MyFileVisitor visitor = new MyFileVisitor(appProperty);
        try {
            Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
        } catch (IOException ignored) {
            //Empty Block.
        }

        if (!appProperty.isCreateFolder()) {
            callback.finished();
            return;
        }
        StringBuilder stackExceptionText = visitor.getStackExceptionText();
        for (Path dir : visitor.getFoundDirList()) {
            try {
                List<Path> list = new ArrayList<>();
                if (!Files.exists(dir)) {
                    continue;
                }
                Files.list(dir).forEach(list::add);
                System.out.println("LIST SIZE : " + list.size());
                if (list.size() != 1) {
                    continue;
                }
                final Path path = list.get(0);
                Path dstFilePath = Path.of(dir.getParent() + "\\" + path.getFileName());
                if (!Files.exists(dstFilePath)) {
                    Files.move(path, dstFilePath);
                    Files.delete(dir);
                    continue;
                }

                if (appProperty.getFileDuplicateOption().equals("ファイル名を変更して移動")) {
                    for (int i = 1; Files.exists(Path.of(dir.getParent() + "\\" + path.getFileName())); i++) {
                        final String rnmFileName = new RenameFile(path.getFileName().toString()).rename("(" + i + ")");
                        dstFilePath = Path.of(dir.getParent() + "\\" + rnmFileName);
                    }
                    Files.move(path, dstFilePath);
                    Files.delete(dir);
                    continue;
                }

                if (appProperty.getFileDuplicateOption().equals("ファイルを上書きして移動")) {
                    Files.move(path, dstFilePath, StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(dir);
                }
            } catch (IOException e) {
                stackExceptionText.append(e).append("\n\n");
                e.printStackTrace();
            }
        }
        try {
            if (Files.list(srcDirectoryPath).findAny().isEmpty()) {
                Files.delete(srcDirectoryPath);
            }
        } catch (IOException e) {
            stackExceptionText.append(e).append("\n\n");
            e.printStackTrace();
        }
        if (!stackExceptionText.toString().isEmpty()) {
            throw new RuntimeException(stackExceptionText.toString());
        }
        callback.finished();
    }
}
