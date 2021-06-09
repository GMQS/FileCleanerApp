package sample.model;

import javafx.scene.control.Alert;

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

    public void show(){
        this.alert.setHeaderText(null);
        this.alert.showAndWait();
    }

}
