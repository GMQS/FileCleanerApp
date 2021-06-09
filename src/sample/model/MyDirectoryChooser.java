package sample.model;

import javafx.stage.DirectoryChooser;

import java.io.File;

public class MyDirectoryChooser {
    private final DirectoryChooser chooser;


    public MyDirectoryChooser(final String chooserTitle){
        chooser = new DirectoryChooser();
        chooser.setTitle(chooserTitle);
    }
    public File show(){
        final File selectedDirectory = chooser.showDialog(null);
        if (selectedDirectory == null) {
            return null;
        } else {
            return selectedDirectory.getAbsoluteFile();
        }
    }

}
