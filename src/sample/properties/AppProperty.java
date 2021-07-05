package sample.properties;


import javafx.beans.property.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class AppProperty {

    private DirectoryPropertyList directoryPropertyList;

    private File srcDirectory;
    private File dstDirectory;

    private StringProperty srcDirectoryText;
    private StringProperty dstDirectoryText;

    private StringProperty fileDuplicateOption;
    private StringProperty folderDuplicateOption;
    private StringProperty folderFoundOption;
    private BooleanProperty createFolder;
    private BooleanProperty checkContents;
    private BooleanProperty duplicateContentsDelete;
    private String radioButtonUserData;

    public boolean isDuplicateContentsDelete() {
        return duplicateContentsDelete.get();
    }

    public BooleanProperty duplicateContentsDeleteProperty() {
        if(Objects.isNull(duplicateContentsDelete)){
            duplicateContentsDelete = new SimpleBooleanProperty();
        }
        return duplicateContentsDelete;
    }

    public void setDuplicateContentsDelete(boolean isDuplicateContentsDelete) {
        this.duplicateContentsDelete.set(isDuplicateContentsDelete);
    }

    public String getRadioButtonUserData() {
        return radioButtonUserData;
    }

    public void setRadioButtonUserData(String test) {
        this.radioButtonUserData = test;
    }

    public boolean isCheckContents() {
        return checkContents.get();
    }

    public BooleanProperty checkContentsProperty() {
        if (Objects.isNull(checkContents)) {
            checkContents = new SimpleBooleanProperty();
        }
        return checkContents;
    }

    public void setCheckContents(boolean isCheckContents) {
        this.checkContents.set(isCheckContents);
    }

    public boolean isCreateFolder() {
        return createFolder.get();
    }

    public BooleanProperty createFolderProperty() {
        if (Objects.isNull(createFolder)) {
            createFolder = new SimpleBooleanProperty();
        }
        return createFolder;
    }

    public DirectoryPropertyList getDirectoryPropertyList() {
        if (Objects.isNull(directoryPropertyList)) {
            this.directoryPropertyList = new DirectoryPropertyList();
        }
        return this.directoryPropertyList;
    }

    public Path getSrcDirectoryPath() {
        return Path.of(this.srcDirectory.getPath());
    }

    public Path getDstDirectoryPath() {
        return Path.of(this.dstDirectory.getPath());
    }

    public StringProperty srcDirectoryTextProperty() {
        if (Objects.isNull(srcDirectoryText)) {
            srcDirectoryText = new SimpleStringProperty("");
        }
        return srcDirectoryText;
    }

    public StringProperty dstDirectoryTextProperty() {
        if (Objects.isNull(dstDirectoryText)) {
            dstDirectoryText = new SimpleStringProperty("");
        }
        return dstDirectoryText;
    }

    public File getSrcDirectory() {
        return srcDirectory;
    }

    public void setSrcDirectory(File srcDirectory) {
        this.srcDirectory = srcDirectory;
        srcDirectoryText.setValue(srcDirectory.getAbsolutePath());
    }

    public File getDstDirectory() {
        return dstDirectory;
    }

    public void setDstDirectory(File dstDirectory) {
        this.dstDirectory = dstDirectory;
        dstDirectoryText.setValue(dstDirectory.getAbsolutePath());
    }

    public String getFileDuplicateOption() {
        return fileDuplicateOption.get();
    }

    public StringProperty fileDuplicateOptionProperty() {
        if (Objects.isNull(fileDuplicateOption)) {
            fileDuplicateOption = new SimpleStringProperty("ファイル名を変更して移動");
        }
        return fileDuplicateOption;
    }

    public String getFolderDuplicateOption() {
        return folderDuplicateOption.get();
    }

    public StringProperty folderDuplicateOptionProperty() {
        if (Objects.isNull(folderDuplicateOption)) {
            folderDuplicateOption = new SimpleStringProperty("フォルダを統合する");
        }
        return folderDuplicateOption;
    }

    public String getFolderFoundOption() {
        return folderFoundOption.get();
    }

    public StringProperty folderFoundOptionProperty() {
        if (Objects.isNull(folderFoundOption)) {
            folderFoundOption = new SimpleStringProperty("フォルダを移動しない");
        }
        return folderFoundOption;
    }

}
