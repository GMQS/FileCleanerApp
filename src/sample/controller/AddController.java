package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddController implements Initializable {
    @FXML
    private TextField folderNameFiled;

    @FXML
    private Button confirmBtn;
    private Stage thisStage;

    private String folderName;

    public void setStage(Stage stage) {
        this.thisStage = stage;
    }

    public String getFolderName(){
        return this.folderName;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void confirmBtnClick(MouseEvent event) throws Exception{
        folderName = folderNameFiled.getText();
        thisStage.hide();
    }
}
