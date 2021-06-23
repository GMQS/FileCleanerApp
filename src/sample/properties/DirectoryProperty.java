package sample.properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectoryProperty {

    private File dstDirectory;
    private StringProperty dstDirectoryText;
    private StringProperty extensionsText;

    public boolean isSetProperty() {
        if (dstDirectory == null) {
            return false;
        }
        if(dstDirectoryText == null){
            return false;
        }
        if(extensionsText == null){
            return false;
        }
        if(dstDirectoryText.get().equals("")){
            return false;
        }
        return !extensionsText.get().equals("");
    }

    public void update(final DirectoryProperty property){
        this.dstDirectory = property.getDstDirectory();
        this.dstDirectoryText = property.dstDirectoryTextProperty();
        this.extensionsText = property.extensionsTextProperty();
    }

    public String[] getExtensionsArray() {
        return extensionsText.get().split(",");
    }

    public File getDstDirectory() {
        return dstDirectory;
    }

    public void setDstDirectory(File dstDirectory) {
        this.dstDirectory = dstDirectory;
    }

    public String getDstDirectoryText() {
        return dstDirectoryText.get();
    }

    public StringProperty dstDirectoryTextProperty() {
        if (Objects.isNull(dstDirectoryText)) {
            dstDirectoryText = new SimpleStringProperty("");
        }
        return dstDirectoryText;
    }

    public void setDstDirectoryText(String dstDirectoryText) {
        if(Objects.isNull(this.dstDirectoryText)){
            this.dstDirectoryText = new SimpleStringProperty(dstDirectoryText);
        }
        this.dstDirectoryText.set(dstDirectoryText);
    }

    public String getExtensionsText() {
        return extensionsText.get();
    }

    public StringProperty extensionsTextProperty() {
        if (Objects.isNull(extensionsText)) {
            extensionsText = new SimpleStringProperty("");
        }
        return extensionsText;
    }

    public void setExtensionsText(String extensions) {
        if (Objects.isNull(extensionsText)) {
            extensionsText = new SimpleStringProperty(extensions);
        }
        this.extensionsText.set(extensions);
    }
}
