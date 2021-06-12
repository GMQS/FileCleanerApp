package sample.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import sample.model.filevisitor.BasicFileVisitor;
import sample.model.filevisitor.OverWriteFileVisitor;
import sample.model.filevisitor.RenameFileVisitor;
import sample.properties.AppProperties;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

public class MoveFiles implements Runnable {

    private final AppProperties appProperties;


    public MoveFiles(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void run() {
        final Path srcDirectoryPath = appProperties.getSrcDirectoryPath();
        try {
            switch (appProperties.getDuplicateOption()) {
                case "ファイル名を変更して移動": {
                    Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS),Integer.MAX_VALUE, new RenameFileVisitor(appProperties));
                    break;
                }
                case "ファイルを移動しない": {
                    Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new BasicFileVisitor(appProperties));
                    break;
                }
                case "ファイルを上書きして移動": {
                    Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new OverWriteFileVisitor(appProperties));
                    break;
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage("エラーが発生しました。").show());
            e.printStackTrace();
        }
        Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.INFORMATION).setTitle("完了").setMessage("ファイルの整理が完了しました!").show());

    }


}
