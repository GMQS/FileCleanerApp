package sample.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import sample.model.filevisitor.BasicFileVisitor;
import sample.model.filevisitor.OverWriteFileVisitor;
import sample.model.filevisitor.RenameFileVisitor;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

public class MoveFiles implements Runnable {
    private final Path cleanDirPath;
    private final Path moveDirPath;
    private final ProgressBar progressBar;
    private final String option;

    public MoveFiles(final Path cleanDirPath, final Path moveDirPath, final ProgressBar progressBar, final String option) {
        this.cleanDirPath = cleanDirPath;
        this.moveDirPath = moveDirPath;
        this.progressBar = progressBar;
        this.option = option;
    }

    @Override
    public void run() {
        try {
            switch (option) {
                case "ファイル名を変更して移動": {
                    Files.walkFileTree(cleanDirPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new RenameFileVisitor(cleanDirPath, moveDirPath));
                    break;
                }
                case "ファイルを移動しない": {
                    Files.walkFileTree(cleanDirPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new BasicFileVisitor(cleanDirPath, moveDirPath));
                    break;
                }
                case "ファイルを上書きして移動": {
                    Files.walkFileTree(cleanDirPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new OverWriteFileVisitor(cleanDirPath, moveDirPath));
                    break;
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage("エラーが発生しました。").show());
        }
        Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.INFORMATION).setTitle("完了").setMessage("ファイルの整理が完了しました!").show());

    }


}
