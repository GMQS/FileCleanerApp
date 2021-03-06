package sample.model;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class AlertWindowCreator {

    private final Alert alert;

    public AlertWindowCreator(final Alert.AlertType alertType) {
        this.alert = new Alert(alertType);
    }

    public AlertWindowCreator setTitle(final String title){
        this.alert.setTitle(title);
        return this;
    }

    public AlertWindowCreator setMessage(final String message){
        this.alert.setContentText(message);
        return this;
    }

    public AlertWindowCreator setParentWindow(final Window window){
        this.alert.initOwner(window);
        return this;
    }

    public void show(){
        this.alert.setHeaderText(null);
        this.alert.showAndWait();
    }

}
