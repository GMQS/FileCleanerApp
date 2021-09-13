package sample.properties;

import java.io.File;

public class TemporaryDirectoryProperty {
    private File tmpDir;
    private String tmpExtension;
    private String tmpTitle;
    private String tmpPathText;

    public void clearAll() {
        this.tmpDir = null;
        this.tmpExtension = null;
        this.tmpTitle = null;
        this.tmpPathText = null;
    }

    public boolean isChange() {
        if (this.tmpDir != null) {
            return true;
        }
        if (this.tmpExtension != null) {
            return true;
        }
        return false;
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
        this.tmpPathText = tmpDir.getPath();
        if (tmpDir.getName().equals("")) {
            this.tmpTitle = tmpDir.getPath();
            return;
        }
        this.tmpTitle = tmpDir.getName();
    }

    public String getTmpExtension() {
        return tmpExtension;
    }

    public void setTmpExtension(String tmpExtension) {
        this.tmpExtension = tmpExtension;
    }

    public String getTmpTitle() {
        return tmpTitle;
    }

    public String getTmpPathText() {
        return tmpPathText;
    }
}
