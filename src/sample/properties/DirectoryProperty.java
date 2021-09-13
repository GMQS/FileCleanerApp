package sample.properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.Objects;

public class DirectoryProperty {

    private File dstDirectory;
    private StringProperty dstDirectoryText;
    private StringProperty extensionsText;
    private final BooleanProperty disableSetting;

    public DirectoryProperty() {
        this.disableSetting = new SimpleBooleanProperty(false);
    }

    public boolean isSetProperty() {
        if (dstDirectory == null) {
            return false;
        }
        if (dstDirectoryText == null) {
            return false;
        }
        if (extensionsText == null) {
            return false;
        }
        if (dstDirectoryText.get().equals("")) {
            return false;
        }
        return !extensionsText.get().equals("");
    }

    public void update(final TemporaryDirectoryProperty tmpProperty) {
        final File tmpDir = tmpProperty.getTmpDir();
        if (!getDstDirectory().equals(tmpDir) && tmpDir != null) {
            setDstDirectory(tmpDir);
        }

        final String tmpExtension = tmpProperty.getTmpExtension();
        if(!getExtensionsText().equals(tmpExtension) && tmpExtension != null){
            setExtensionsText(tmpExtension);
        }
    }

    public String[] getExtensionsArray() {
        return extensionsText.get().split(",");
    }

    public File getDstDirectory() {
        return dstDirectory;
    }

    public void setDstDirectory(final File dstDirectory) {
        this.dstDirectory = dstDirectory;
        if (Objects.isNull(this.dstDirectoryText)) {
            this.dstDirectoryText = new SimpleStringProperty(dstDirectory.getPath());
        }
        this.dstDirectoryText.set(dstDirectory.getPath());
    }

    public String getDstDirectoryText() {
        return dstDirectoryText.get();
    }

    public StringProperty dstDirectoryTextProperty() {
        if (Objects.isNull(dstDirectoryText)) {
            dstDirectoryText = new SimpleStringProperty("");
        }
        return this.dstDirectoryText;
    }

    public BooleanProperty disableSettingProperty() {
        return this.disableSetting;
    }

    public void setDisableSetting(final boolean isDisable) {
        this.disableSetting.set(isDisable);
    }

    public boolean isDisableSetting() {
        return this.disableSetting.get();
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

    public void setExtensionsText(final String extensions) {
        final String s = extensions.toLowerCase();
        if (Objects.isNull(extensionsText)) {
            extensionsText = new SimpleStringProperty(s);
        }
        this.extensionsText.set(s);
    }
}
