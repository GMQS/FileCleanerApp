package sample.properties;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private BooleanProperty isCreateFolder;

    public boolean isCreateFolder() {
        return isCreateFolder.get();
    }

    public BooleanProperty isCreateFolderProperty() {
        if(Objects.isNull(isCreateFolder)){
            isCreateFolder = new SimpleBooleanProperty();
        }
        return isCreateFolder;
    }

    public void setIsCreateFolder(boolean isCreateFolder) {
        this.isCreateFolder.set(isCreateFolder);
    }

    public DirectoryPropertyList getDirectoryPropertyList() {
        if (Objects.isNull(directoryPropertyList)) {
            this.directoryPropertyList = new DirectoryPropertyList();
        }
        return this.directoryPropertyList;
    }

    public void setDirectoryPropertyList(DirectoryPropertyList directoryPropertyList) {
        this.directoryPropertyList = directoryPropertyList;
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

    public void setSrcDirectoryText(String srcDirectoryText) {
        this.srcDirectoryText.set(srcDirectoryText);
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
        this.dstDirectoryText.set(dstDirectoryText);
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

    public String getSrcDirectoryText() {
        return srcDirectoryText.get();
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

    public void setFileDuplicateOption(String fileDuplicateOption) {
        this.fileDuplicateOption.set(fileDuplicateOption);
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

    public void setFolderDuplicateOption(String folderDuplicateOption) {
        this.folderDuplicateOption.set(folderDuplicateOption);
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

    public void setFolderFoundOption(String folderFoundOption) {
        this.folderFoundOption.set(folderFoundOption);
    }
}
