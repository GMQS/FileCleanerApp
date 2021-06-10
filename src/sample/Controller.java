package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import sample.model.AlertWindowCreator;
import sample.model.MoveFiles;
import sample.model.MyDirectoryChooser;
import sample.model.RenameFile;
import sample.properties.OperationDirectories;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Controller implements Initializable {


    @FXML
    private ChoiceBox optionChoiceBox;
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

    private OperationDirectories operationDirectories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        operationDirectories = new OperationDirectories();
    }


    @FXML
    private void targetDirChoiceBtnClick(MouseEvent mouseEvent) {
        chooserAction("整理対象のディレクトリを選択",false);
    }

    @FXML
    private void moveTargetDirChoiceBtnClick(MouseEvent mouseEvent) {
        chooserAction("移動先のディレクトリを選択",true);
    }

    @FXML
    private void startBtnClick(MouseEvent mouseEvent) {
        final Path cleanTgtDirPath = Paths.get(operationDirectories.getCleanTargetDir().getAbsolutePath());
        final Path moveTgtDirPath = Paths.get(operationDirectories.getMovedTargetDir().getAbsolutePath()+ "/File Organizer");
        final String optionStr = optionChoiceBox.getSelectionModel().getSelectedItem().toString();
        new Thread(new MoveFiles(cleanTgtDirPath, moveTgtDirPath, progressBar,optionStr)).start();
    }


    private void startButtonLiveState() {
        startBtn.setDisable(operationDirectories.getCleanTargetDir() == null || operationDirectories.getMovedTargetDir() == null);
    }

    private void chooserAction(final String chooserTitle, final boolean isMoveTargetDirectory) {
        MyDirectoryChooser chooser = new MyDirectoryChooser(chooserTitle);
        final File dir = chooser.show();
        if (dir == null) {
            return;
        }
        if(isMoveTargetDirectory){
            operationDirectories.setMovedTargetDir(dir);
            moveTargetDirText.setText(operationDirectories.getMovedTargetDir().getAbsolutePath() + "/File Organizer");
        }else{
            operationDirectories.setCleanTargetDir(dir);
            targetDirText.setText(operationDirectories.getCleanTargetDir().getAbsolutePath());
        }
        startButtonLiveState();
    }
}
