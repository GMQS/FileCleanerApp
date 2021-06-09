package sample.properties;

import java.io.File;

public class OperationDirectories {

    private File cleanTargetDir;
    private File movedTargetDir;

    public File getCleanTargetDir() {
        return cleanTargetDir;
    }

    public void setCleanTargetDir(File cleanTargetDir) {
        this.cleanTargetDir = cleanTargetDir;
    }

    public File getMovedTargetDir() {
        return movedTargetDir;
    }

    public void setMovedTargetDir(File movedTargetDir) {
        this.movedTargetDir = movedTargetDir;
    }
}
