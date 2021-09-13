package sample.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.common.JsonIO;
import sample.model.AlertWindowCreator;
import sample.model.MyDirectoryChooser;
import sample.model.organizefile.OrganizeFile;
import sample.properties.AppProperty;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

public class MainSceneController {
    @FXML
    private CheckBox useAdvancedSettingCheckBox;
    @FXML
    private CheckBox duplicateContentsDeleteCheckBox;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private RadioButton RenameWithCheckContentsRadioBtn;
    @FXML
    private RadioButton NotMoveWithCheckContentsRadioBtn;
    @FXML
    private CheckBox CheckContentsCheckBox;
    @FXML
    private Label stepInfoText;
    @FXML
    private Label stepCountText;
    @FXML
    private Label completeFileCountText;
    @FXML
    private Label totalFileCountText;
    @FXML
    private Label progressPercentText;
    @FXML
    private Button cancelBtn;
    @FXML
    private CheckBox createExtFolderCheckBox;
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
    private String JSON_FILE_PATH;


    private Stage thisStage;
    private AppProperty appProperty;
    private Thread task;

    public void onHidden() {
        try {
            JsonIO.saveToJsonFile(JSON_FILE_PATH, appProperty);
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setTitle("エラー").setMessage(e.getMessage()).setParentWindow(thisStage).show();
            e.printStackTrace();
            thisStage.close();
        }
    }

    public void onShowing(Stage stage) {
        this.thisStage = stage;
        try {
            appProperty = JsonIO.loadFromJsonFile(JSON_FILE_PATH, AppProperty.class);
        } catch (Exception e) {
            appProperty = new AppProperty();
        } finally {
            setupViews();
        }
    }

    @FXML
    private void initialize() {
        try {
            JSON_FILE_PATH =
                    Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString() + "/data.json";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setupViews() {

        RenameWithCheckContentsRadioBtn.setUserData("RENAME");
        NotMoveWithCheckContentsRadioBtn.setUserData("NOT_MOVE");

        if (Objects.nonNull(appProperty.getRadioButtonUserData())) {
            if (appProperty.getRadioButtonUserData().equals("RENAME")) {
                toggleGroup.selectToggle(RenameWithCheckContentsRadioBtn);
            } else {
                toggleGroup.selectToggle(NotMoveWithCheckContentsRadioBtn);
            }
        } else {
            RenameWithCheckContentsRadioBtn.setSelected(true);
            appProperty.setRadioButtonUserData("RENAME");
        }

        toggleGroup.selectedToggleProperty().addListener((observableValue, beforeToggle, afterToggle) -> appProperty.setRadioButtonUserData(afterToggle.getUserData().toString()));


        useAdvancedSettingCheckBox.disableProperty().bind(progressBar.disabledProperty().not());
        duplicateContentsDeleteCheckBox.disableProperty().bind(progressBar.disabledProperty().not());
        CheckContentsCheckBox.disableProperty().bind(progressBar.disabledProperty().not());
        RenameWithCheckContentsRadioBtn.disableProperty().bind(appProperty.checkContentsProperty().not().or(progressBar.disableProperty().not()));
        NotMoveWithCheckContentsRadioBtn.disableProperty().bind(appProperty.checkContentsProperty().not().or(progressBar.disableProperty().not()));
        srcChoiceBtn.disableProperty().bind(progressBar.disabledProperty().not());
        dstChoiceBtn.disableProperty().bind(progressBar.disabledProperty().not());
        fileDuplicateOptionChoice.disableProperty().bind(appProperty.checkContentsProperty().or(progressBar.disableProperty().not()));
        folderFoundOptionChoice.disableProperty().bind(progressBar.disabledProperty().not());
        folderDuplicateOptionChoice.disableProperty().bind(progressBar.disabledProperty().not());
        createExtFolderCheckBox.disableProperty().bind(progressBar.disabledProperty().not());
        advancedSettingBtn.disableProperty().bind(progressBar.disabledProperty().not().or(useAdvancedSettingCheckBox.selectedProperty().not()));
        startBtn.disableProperty().bind(targetDirText.textProperty().isEmpty().or(moveTargetDirText.textProperty().isEmpty()).or(progressBar.disableProperty().not()));
        cancelBtn.disableProperty().bind(startBtn.disableProperty().not().and(progressBar.disableProperty()));

        useAdvancedSettingCheckBox.selectedProperty().bindBidirectional(appProperty.useAdvancedSettingProperty());
        duplicateContentsDeleteCheckBox.selectedProperty().bindBidirectional(appProperty.duplicateContentsDeleteProperty());
        CheckContentsCheckBox.selectedProperty().bindBidirectional(appProperty.checkContentsProperty());
        createExtFolderCheckBox.selectedProperty().bindBidirectional(appProperty.createFolderProperty());
        targetDirText.textProperty().bind(appProperty.srcDirectoryTextProperty());
        moveTargetDirText.textProperty().bind(appProperty.dstDirectoryTextProperty());
        folderFoundOptionChoice.valueProperty().bindBidirectional(appProperty.folderFoundOptionProperty());
        folderDuplicateOptionChoice.valueProperty().bindBidirectional(appProperty.folderDuplicateOptionProperty());
        fileDuplicateOptionChoice.valueProperty().bindBidirectional(appProperty.fileDuplicateOptionProperty());

        duplicateContentsDeleteCheckBox.selectedProperty().addListener((observableValue, beforeBool, afterBool) -> {
            if (afterBool) {
                new AlertWindowCreator(Alert.AlertType.WARNING).setParentWindow(thisStage).setTitle("警告").setMessage("フォルダに含まれるファイル数が多い場合、処理に時間がかかる場合があります。できるだけ少ないファイル数で重複削除オプションを使用することをお勧めします。").show();
            }
        });

        stepCountText.textProperty().addListener((observableValue, beforeText, afterText) -> {
            if (afterText.equals("1")) {
                stepInfoText.setText("(重複ファイル削除中)");
                return;
            }
            if (afterText.equals("2")) {
                stepInfoText.setText("(ファイル移動中)");
                return;
            }
            if (afterText.equals("3")) {
                stepInfoText.setText("(フォルダ整理中)");
                return;
            }
            stepInfoText.setText("");
        });

        progressBar.progressProperty().addListener((observableValue, before, after) -> Platform.runLater(() -> {
            double value = after.doubleValue() * 100;
            final String str = String.format("%.0f", value);
            if (value <= 0) {
                progressPercentText.setText("---");
                return;
            }
            progressPercentText.setText(str);
        }));
        progressBar.disabledProperty().addListener((observableValue, beforeBool, afterBool) -> {
            if (progressBar.disabledProperty().get()) {
                progressBar.setProgress(0);
                totalFileCountText.setText("-");
                completeFileCountText.setText("-");
                stepCountText.setText("-");
            }
        });

    }

    private void showOptionWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/setting_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        SettingSceneController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-base:black");
        stage.setScene(scene);
        stage.setTitle("個別フォルダ設定");
        stage.setResizable(false);
        stage.initOwner(thisStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnHiding(e -> controller.onHiding());
        stage.setOnHidden(e -> controller.onHidden());
        stage.setOnShowing(e -> controller.onShowing(appProperty, stage));
        stage.showAndWait();
        try {
            JsonIO.saveToJsonFile(JSON_FILE_PATH, appProperty);
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
    private void startBtnClick() {
        if (!appProperty.getSrcDirectory().exists() || !appProperty.getDstDirectory().exists()) {
            StringBuilder message = new StringBuilder();
            if (!appProperty.getSrcDirectory().exists()) {
                message.append("・整理対象に指定したフォルダが存在しません。再選択してください。");
            }
            if (!appProperty.getDstDirectory().exists()) {
                message.append("\n\n・移動先に指定したフォルダが存在しません。再選択してください。");
            }
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("フォルダが存在しません").setMessage(message.toString()).show();
            return;
        }
        OrganizeFile organizeFile = new OrganizeFile(appProperty);
        organizeFile.setCallback((result, error) -> Platform.runLater(() -> {
            if (!error.isEmpty()) {
                final String message = "1件以上のエラーが発生しています。\n\n[エラー詳細情報]\n" + error;
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/error_log_scene.fxml")));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    ErrorLogSceneController controller = loader.getController();
                    Scene scene = new Scene(root);
                    scene.getRoot().setStyle("-fx-base:black");
                    stage.setScene(scene);
                    stage.setTitle("エラーログ");
                    stage.initOwner(thisStage);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setOnShowing(e -> controller.onShowing(stage, message));
                    stage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (result != null) {
                final String message = "ファイルの整理が完了しました!\n\n" + result;
                new AlertWindowCreator(Alert.AlertType.INFORMATION).setParentWindow(thisStage).setTitle("完了").setMessage(message).show();
            }
            progressBar.setDisable(true);
        }));
        task = new Thread(organizeFile);
        task.start();
        progressBar.progressProperty().bindBidirectional(organizeFile.getProgressProperties().progressProperty());
        totalFileCountText.textProperty().bindBidirectional(organizeFile.getProgressProperties().totalCountProperty());
        completeFileCountText.textProperty().bindBidirectional(organizeFile.getProgressProperties().doneCountProperty());
        stepCountText.textProperty().bindBidirectional(organizeFile.getProgressProperties().stepCountProperty());
        progressBar.setDisable(false);
    }

    @FXML
    public void advancedSettingBtnClick() {
        try {
            showOptionWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void srcChoiceBtnClick() {
        chooserAction("整理対象のフォルダを選択", true);
    }

    @FXML
    private void dstChoiceBtnClick() {
        chooserAction("移動先のフォルダを選択", false);
    }

    @FXML
    private void srcExplorerBtnClick() {
        try {
            Desktop.getDesktop().open(appProperty.getSrcDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show();
        }

    }

    @FXML
    private void dstExplorerBtnClick() {
        try {
            Desktop.getDesktop().open(appProperty.getDstDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show();
        }
    }

    @FXML
    private void cancelBtnClick() {
        if (Objects.isNull(task)) {
            return;
        }
        if (task.getState() == Thread.State.RUNNABLE) {
            task.interrupt();
        }
    }
}
