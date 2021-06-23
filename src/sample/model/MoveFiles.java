package sample.model;

import sample.model.filevisitor.FileVisitor;
import sample.properties.AppProperty;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class MoveFiles implements Runnable {

    private final AppProperty appProperty;
    private Callback callback;
    private ArrayList<Path> foundDirList;

    public MoveFiles(AppProperty appProperty) {
        foundDirList = new ArrayList<>();
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
        try {
            Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisitor(appProperty, foundDirList));

            if (!appProperty.isCreateFolder()) {
                callback.finished();
                return;
            }
            for (Path dir : foundDirList) {
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
            }
            callback.finished();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
