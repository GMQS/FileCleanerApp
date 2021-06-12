package sample.properties;


import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String srcDirectoryPath;
    private String dstDirectoryPath;

    private String duplicateOption;

    private String movieDirectoryPath;
    private String imageDirectoryPath;
    private String soundDirectoryPath;
    private String otherDirectoryPath;

    private boolean manualSelection;
    private boolean cleanInnerDir;
    private boolean createExtensionDir;
    private boolean deleteDirOnExit;

    public boolean isDeleteDirOnExit() {
        return deleteDirOnExit;
    }

    public void setDeleteDirOnExit(boolean deleteDirOnExit) {
        this.deleteDirOnExit = deleteDirOnExit;
    }

    public Path getImageDirectoryPath() {
        return Paths.get(imageDirectoryPath);
    }

    public void setImageDirectoryPath(String imageDirectoryPath) {
        this.imageDirectoryPath = imageDirectoryPath;
    }

    public Path getSoundDirectoryPath() {
        return Paths.get(soundDirectoryPath);
    }

    public void setSoundDirectoryPath(String soundDirectoryPath) {
        this.soundDirectoryPath = soundDirectoryPath;
    }

    public Path getMovieDirectoryPath() {
        return Paths.get(movieDirectoryPath);
    }

    public void setMovieDirectoryPath(String movieDirectoryPath) {
        this.movieDirectoryPath = movieDirectoryPath;
    }

    public Path getOtherDirectoryPath() {
        return Paths.get(otherDirectoryPath);
    }

    public void setOtherDirectoryPath(String otherDirectoryPath) {
        this.otherDirectoryPath = otherDirectoryPath;
    }

    public Path getSrcDirectoryPath() {
        return Paths.get(srcDirectoryPath);
    }

    public void setSrcDirectoryPath(String srcDirectoryPath) {
        this.srcDirectoryPath = srcDirectoryPath;
    }

    public Path getDstDirectoryPath() {
        return Paths.get(dstDirectoryPath);
    }

    public void setDstDirectoryPath(String dstDirectoryPath) {
        this.dstDirectoryPath = dstDirectoryPath;
    }

    public String getDuplicateOption() {
        return duplicateOption;
    }

    public void setDuplicateOption(String duplicateOption) {
        this.duplicateOption = duplicateOption;
    }

    public boolean isManualSelection() {
        return manualSelection;
    }

    public void setManualSelection(boolean manualSelection) {
        this.manualSelection = manualSelection;
    }

    public boolean isCleanInnerDir() {
        return cleanInnerDir;
    }

    public void setCleanInnerDir(boolean cleanInnerDir) {
        this.cleanInnerDir = cleanInnerDir;
    }

    public boolean isCreateExtensionDir() {
        return createExtensionDir;
    }

    public void setCreateExtensionDir(boolean createExtensionDir) {
        this.createExtensionDir = createExtensionDir;
    }
}
