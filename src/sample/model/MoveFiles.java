package sample.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MoveFiles implements Runnable {
    private final Path cleanDirPath;
    private final Path moveDirPath;
    private final ProgressBar progressBar;
    private final String option;

    public MoveFiles(final Path cleanDirPath, final Path moveDirPath,final ProgressBar progressBar,final String option) {
        this.cleanDirPath = cleanDirPath;
        this.moveDirPath = moveDirPath;
        this.progressBar = progressBar;
        this.option = option;
    }

    @Override
    public void run() {
        Stream<Path> fileList = null;
        try {
            fileList = Files.list(cleanDirPath);
        } catch (IOException e) {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("読み込みエラー").setMessage("フォルダの読み込みに失敗しました。").show());
        } catch (SecurityException e) {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("セキュリティエラー").setMessage("フォルダの読み取り権限がありません。").show());
        }

        if (fileList == null) {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage("フォルダの中身が存在しないため終了しました。").show());
            return;
        }
        fileList.forEach(path -> {
            try {
                Path moveFilePath = Paths.get(moveDirPath + "/" + path.getFileName());
                for (int i = 1; Files.exists(moveFilePath); i++) {
                    if (Files.isDirectory(moveFilePath)) {
                        final String directoryName = path.getFileName() + "(" + i + ")";
                        moveFilePath = Paths.get(moveDirPath + "/" + directoryName);
                    } else {
                        final String fileName = new RenameFile(path.getFileName()).rename("(" + i + ")");
                        moveFilePath = Paths.get(moveDirPath + "/" + fileName);
                    }
                }
                Files.move(path, moveFilePath);
            } catch (IOException e) {
                Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage("エラーが発生しました。").show());
            }
        });
        Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.INFORMATION).setTitle("完了").setMessage("ファイルの整理が完了しました!").show());
    }



}
