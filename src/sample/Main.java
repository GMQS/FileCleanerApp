package sample;

import javafx.application.Application;
import javafx.application.Platform;
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
        controller.setStage(stage);
        stage.setTitle("お片付けツール");
        stage.setScene(new Scene(root, 500, 600));
        stage.setOnHidden(e -> controller.exit());
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
