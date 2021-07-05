package sample.model.organizefile;

import org.apache.commons.io.FileUtils;
import sample.properties.AppProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class OptionalFileMover {

    private final AppProperty property;
    private final FileCounter counter;
    private final Path sourceFullPath;
    private Path targetFullPath;

    public OptionalFileMover(Path sourceFullPath, FileCounter counter, AppProperty property) {
        this.sourceFullPath = sourceFullPath;
        this.counter = counter;
        this.property = property;
    }

    public OptionalFileMover setTargetFullPath(Path targetFullPath) {
        this.targetFullPath = targetFullPath;
        return this;
    }

    public void move() throws IOException {

        final String fileName = sourceFullPath.getFileName().toString();
        final String duplicateOption = property.getFileDuplicateOption();

        if (property.isCheckContents()) {

            final boolean isSameFile = FileUtils.contentEquals(sourceFullPath.toFile(), targetFullPath.toFile());
            if (isSameFile) {
                Files.move(sourceFullPath, targetFullPath, StandardCopyOption.REPLACE_EXISTING);
                counter.addMoveFileCount();
                return;
            }

            if (property.getRadioButtonUserData().equals("RENAME")) {
                Path p = targetFullPath;
                for (int i = 1; Files.exists(p) && !sourceFullPath.equals(p); i++) {
                    final String rnm = new RenameFile(fileName).rename("(" + i + ")");
                    p = Path.of(targetFullPath.getParent() + "\\" + rnm);
                }
                Files.move(sourceFullPath, p);
                counter.addMoveFileCount();
            } else {
                counter.addSkipFileCount();
            }
            return;
        }

        if (duplicateOption.equals("ファイル名を変更して移動")) {
            Path p = targetFullPath;
            for (int i = 1; Files.exists(p) && !sourceFullPath.equals(p); i++) {
                final String rnm = new RenameFile(fileName).rename("(" + i + ")");
                p = Path.of(targetFullPath.getParent() + "\\" + rnm);
            }
            Files.move(sourceFullPath, p);
            counter.addMoveFileCount();
            return;
        }
        if (duplicateOption.equals("ファイルを上書きして移動")) {
            Files.move(sourceFullPath, targetFullPath, StandardCopyOption.REPLACE_EXISTING);
            counter.addMoveFileCount();
            return;
        }
        if (duplicateOption.equals("ファイルを移動しない")) {
            if (!Files.exists(targetFullPath)) {
                Files.move(sourceFullPath, targetFullPath);
                counter.addMoveFileCount();
                return;
            }
            counter.addSkipFileCount();
            return;
        }
        throw new IllegalArgumentException();
    }
}
