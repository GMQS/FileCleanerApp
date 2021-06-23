package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.MainController;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("scene/main_scene.fxml")));
        Parent root = loader.load();
        MainController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-base:black");
        stage.setTitle("お片付けツール");
        stage.setScene(scene);
        stage.setOnShowing(e -> controller.onShowing(stage));
        stage.setOnHidden(e -> controller.onHidden());
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
