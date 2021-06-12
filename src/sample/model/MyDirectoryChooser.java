package sample.model;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class MyDirectoryChooser {

    private final String title;
    private final Window parentWindow;

    public MyDirectoryChooser(final String title,final Window parentWindow) {
        this.title = title;
        this.parentWindow = parentWindow;
    }

    public File createDirectoryChooser(){
        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        final File selectedDirectory = chooser.showDialog(parentWindow);
        if (selectedDirectory == null) {
            return null;
        } else {
            return selectedDirectory.getAbsoluteFile();
        }
    }

}
