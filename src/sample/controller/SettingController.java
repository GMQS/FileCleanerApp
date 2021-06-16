package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SettingController implements Initializable {

    @FXML
    private ListView<String> listView;


    private ObservableList<String> items;
    @FXML
    private Button addBtn;
    private Stage thisStage;

    public void setStage(Stage stage) {
        this.thisStage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        items = FXCollections.observableArrayList();
        listView.setItems(items);
    }

    @FXML
    private void addBtnClick(MouseEvent event) throws Exception{
        FXMLLoader loader =
                new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/add_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        AddController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("フォルダ追加");
        stage.initOwner(thisStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
        items.add(controller.getFolderName());
    }
}
