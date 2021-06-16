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
import sample.model.AlertWindowCreator;
import sample.model.MoveFiles;
import sample.model.MyDirectoryChooser;
import sample.properties.AppProperties;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

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
    private Button targetDirChoiceBtn;
    @FXML
    private Button moveDirChoiceBtn;
    @FXML
    private Label targetDirText;
    @FXML
    private Label moveTargetDirText;


    private Stage thisStage;
    private AppProperties appProperties;

    public void setStage(Stage stage) {
        thisStage = stage;
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
            folderDuplicateOptionChoice.setValue(appProperties.getFolderDuplicateOption());
            folderFoundOptionChoice.setValue(appProperties.getFolderFoundOption());
            fileDuplicateOptionChoice.setValue(appProperties.getFileDuplicateOption());

            startButtonLiveState();
        } catch (Exception e) {
            appProperties = new AppProperties();
        }
    }

    private void showOptionWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/setting_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        SettingController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("カスタムフォルダ設定");
        stage.setResizable(false);
        stage.initOwner(thisStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    private void startButtonLiveState() {
        startBtn.setDisable(appProperties.getSrcDirectoryPath() == null || appProperties.getDstDirectoryPath() == null);
    }

    private void chooserAction(final String chooserTitle, final boolean isMoveTargetDirectory) {
        final File dir = new MyDirectoryChooser(chooserTitle, thisStage).createDirectoryChooser();
        if (dir == null) {
            return;
        }
        String dirPath = dir.getAbsolutePath();
        if (isMoveTargetDirectory) {
            dirPath = dirPath.replaceAll("[\\\\]$", "");
            appProperties.setDstDirectoryPath(dirPath);
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
    private void folderDuplicateOptionChoiceAction(ActionEvent actionEvent) {
        appProperties.setFolderDuplicateOption(folderDuplicateOptionChoice.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void folderFoundOptionChoiceAction(ActionEvent actionEvent) {
        appProperties.setFolderFoundOption(folderFoundOptionChoice.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void fileDuplicateOptionChoiceAction(ActionEvent actionEvent) {
        appProperties.setFileDuplicateOption(fileDuplicateOptionChoice.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void advancedSettingBtnClick(MouseEvent event) {
        try {
            showOptionWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
