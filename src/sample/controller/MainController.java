package sample.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.common.JsonIO;
import sample.model.AlertWindowCreator;
import sample.model.MoveFiles;
import sample.model.MyDirectoryChooser;
import sample.properties.AppProperty;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController {

    @FXML
    private CheckBox createExtFolderCheckBox;
    @FXML
    private Button dstExplorerBtn;
    @FXML
    private Button srcExplorerBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button dstChoiceBtn;
    @FXML
    private Button srcChoiceBtn;
    @FXML
    private Button advancedSettingBtn;
    @FXML
    private ChoiceBox<String> folderDuplicateOptionChoice;
    @FXML
    private ChoiceBox<String> folderFoundOptionChoice;
    @FXML
    private ChoiceBox<String> fileDuplicateOptionChoice;
    @FXML
    private Button startBtn;
    @FXML
    private Label targetDirText;
    @FXML
    private Label moveTargetDirText;

    private static final String JSON_FILE_PATH = "src/sample/properties/data.json";

    private Stage thisStage;
    private AppProperty appProperty;

    public void onHidden() {
        try {
            JsonIO.saveToJsonFile(JSON_FILE_PATH, appProperty);
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage(e.getMessage()).setParentWindow(thisStage).show();
            thisStage.close();
        }
    }

    public void onShowing(Stage stage){
        this.thisStage = stage;
        try {
            appProperty = JsonIO.loadFromJsonFile(JSON_FILE_PATH, AppProperty.class);
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage(e.getMessage()).setParentWindow(thisStage).show();
            appProperty = new AppProperty();
        } finally {
            viewBinding();
        }
    }

    @FXML
    private void initialize() {
    }

    private void viewBinding() {
        createExtFolderCheckBox.selectedProperty().bindBidirectional(appProperty.isCreateFolderProperty());
        targetDirText.textProperty().bind(appProperty.srcDirectoryTextProperty());
        moveTargetDirText.textProperty().bind(appProperty.dstDirectoryTextProperty());
        folderFoundOptionChoice.valueProperty().bindBidirectional(appProperty.folderFoundOptionProperty());
        folderDuplicateOptionChoice.valueProperty().bindBidirectional(appProperty.folderDuplicateOptionProperty());
        fileDuplicateOptionChoice.valueProperty().bindBidirectional(appProperty.fileDuplicateOptionProperty());
        startBtn.disableProperty().bind(targetDirText.textProperty().isEmpty().or(moveTargetDirText.textProperty().isEmpty()).or(progressBar.visibleProperty()));
    }

    private void showOptionWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/setting_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        SettingController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-base:black");
        stage.setScene(scene);
        stage.setTitle("個別フォルダ設定");
        stage.setResizable(false);
        stage.initOwner(thisStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnHidden(e -> controller.onHidden());
        stage.setOnShowing(e -> controller.onShowing(appProperty, stage));
        stage.showAndWait();
        try {
            JsonIO.saveToJsonFile(JSON_FILE_PATH,appProperty);
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage(e.getMessage()).setParentWindow(thisStage).show();
        }
    }

    private void chooserAction(final String chooserTitle, final boolean isSrcDirectory) {
        final File dir = new MyDirectoryChooser(chooserTitle, thisStage).createDirectoryChooser();
        if (Objects.isNull(dir)) {
            return;
        }
        if (isSrcDirectory) {
            appProperty.setSrcDirectory(dir);
        } else {
            appProperty.setDstDirectory(dir);
        }
    }

    @FXML
    private void startBtnClick(MouseEvent mouseEvent) {
        Thread.UncaughtExceptionHandler handler = (t, e) -> {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show());
            progressBar.setVisible(false);
        };
        MoveFiles.Callback callback = () -> {
            Platform.runLater(() -> new AlertWindowCreator(Alert.AlertType.INFORMATION).setParentWindow(thisStage).setTitle("完了").setMessage("ファイルの整理が完了しました!").show());
            progressBar.setVisible(false);
        };
        MoveFiles moveFiles = new MoveFiles(appProperty);
        moveFiles.setCallback(callback);
        Thread task = new Thread(moveFiles);
        task.setUncaughtExceptionHandler(handler);
        task.start();
        progressBar.setVisible(true);
    }

    @FXML
    public void advancedSettingBtnClick(MouseEvent event) {
        try {
            showOptionWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void srcChoiceBtnClick(MouseEvent event) {
        chooserAction("整理対象のフォルダを選択", true);
    }

    @FXML
    private void dstChoiceBtnClick(MouseEvent event) {
        chooserAction("移動先のフォルダを選択", false);
    }

    @FXML
    private void srcExplorerBtnClick(MouseEvent event) {
        try {
            Desktop.getDesktop().open(appProperty.getSrcDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show();
        }

    }
    @FXML
    private void dstExplorerBtnClick(MouseEvent event) {
        try {
            Desktop.getDesktop().open(appProperty.getDstDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show();
        }
    }
}
