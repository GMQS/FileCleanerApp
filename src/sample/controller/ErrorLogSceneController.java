package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorLogSceneController {
    @FXML
    private Button closeBtn;
    @FXML
    private TextArea logTextArea;

    private Stage thisStage;

    @FXML
    private void closeBtnClick(ActionEvent actionEvent) {
        thisStage.close();
    }

    public void onShowing(final Stage stage,final String logText){
        this.thisStage = stage;
        logTextArea.setText(logText);
    }
}
