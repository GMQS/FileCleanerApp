package sample.controller;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.MoveFiles;
import sample.model.MyDirectoryChooser;
import sample.properties.AppProperties;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private CheckBox cleanInnerDirCheckBox;
    @FXML
    private CheckBox createExtensionDirCheckBox;
    @FXML
    private TextField rootFolderEditText;
    @FXML
    private CheckBox manualDirCheckBox;
    @FXML
    private Button manualDirSettingBtn;
    @FXML
    private ChoiceBox<String> optionChoiceBox;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button startBtn;
    @FXML
    private Button targetDirChoiceBtn;
    @FXML
    private Button moveTargetDirChoiceBtn;
    @FXML
    private Label targetDirText;
    @FXML
    private Label moveTargetDirText;


    private Stage mainStage;
    private AppProperties appProperties;

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public void exit() {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(appProperties);
            FileOutputStream fos = new FileOutputStream("data.json");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(jsonStr);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //セーブファイルをチェックしてロード
        try {
            Gson gson = new Gson();
            FileInputStream fis = new FileInputStream("data.json");
            ObjectInputStream ois = new ObjectInputStream(fis);
            appProperties = gson.fromJson((String) ois.readObject(), AppProperties.class);
            moveTargetDirText.setText(appProperties.getDstDirectoryPath().toString());
            targetDirText.setText(appProperties.getSrcDirectoryPath().toString());
            optionChoiceBox.setValue(appProperties.getDuplicateOption());
            manualDirCheckBox.setSelected(appProperties.isManualSelection());
            createExtensionDirCheckBox.setSelected(appProperties.isCreateExtensionDir());
            cleanInnerDirCheckBox.setSelected(appProperties.isCleanInnerDir());
            startButtonLiveState();
            setManualDirCheckBoxLiveState();
        } catch (Exception e) {
            appProperties = new AppProperties();
        }
    }

    private void showOptionWindow() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sample/scene/setting_scene.fxml")));
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("個別フォルダ設定");
        stage.setResizable(false);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    private void startButtonLiveState() {
        startBtn.setDisable(appProperties.getSrcDirectoryPath() == null || appProperties.getDstDirectoryPath() == null);
    }

    private void setManualDirCheckBoxLiveState() {
        if (manualDirCheckBox.isSelected()) {
            manualDirSettingBtn.setDisable(false);
            rootFolderEditText.setDisable(true);
            appProperties.setManualSelection(true);
        } else {
            manualDirSettingBtn.setDisable(true);
            rootFolderEditText.setDisable(false);
            appProperties.setManualSelection(false);
        }
    }

    private void chooserAction(final String chooserTitle, final boolean isMoveTargetDirectory) {
        final File dir = new MyDirectoryChooser(chooserTitle,mainStage).createDirectoryChooser();
        if (dir == null) {
            return;
        }
        String dirPath = dir.getAbsolutePath();
        if (isMoveTargetDirectory) {
            dirPath = dirPath.replaceAll("[\\\\]$", "");
            appProperties.setDstDirectoryPath(dirPath + "\\File Organizer");
            moveTargetDirText.setText(appProperties.getDstDirectoryPath().toString());
        } else {
            appProperties.setSrcDirectoryPath(dirPath);
            targetDirText.setText(appProperties.getSrcDirectoryPath().toString());
        }
        startButtonLiveState();
    }

    @FXML
    private void targetDirChoiceBtnClick(MouseEvent mouseEvent) {
        chooserAction("整理対象のディレクトリを選択", false);
    }

    @FXML
    private void moveTargetDirChoiceBtnClick(MouseEvent mouseEvent) {
        chooserAction("移動先のディレクトリを選択", true);
    }

    @FXML
    private void startBtnClick(MouseEvent mouseEvent) {
        new Thread(new MoveFiles(appProperties)).start();
    }

    @FXML
    private void duplicateOptionChanged(ActionEvent actionEvent) {
        appProperties.setDuplicateOption(optionChoiceBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void manualDirSettingBtnClick(MouseEvent mouseEvent) {
        try {
            showOptionWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void manualDirCheckBoxSelect(ActionEvent actionEvent) {
        setManualDirCheckBoxLiveState();
    }

    @FXML
    private void createExtensionDirCheckBoxSelect(ActionEvent actionEvent) {
        appProperties.setCreateExtensionDir(createExtensionDirCheckBox.isSelected());
    }

    @FXML
    private void cleanInnerDirCheckBoxSelect(ActionEvent actionEvent) {
        appProperties.setCleanInnerDir(cleanInnerDirCheckBox.isSelected());
    }
}
