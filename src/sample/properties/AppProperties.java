package sample.properties;


import java.io.Serializable;
import java.nio.file.Path;

public class AppProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String srcDirectoryPath;
    private String dstDirectoryPath;

    private String fileDuplicateOption;
    private String folderDuplicateOption;
    private String folderFoundOption;

    public Path getSrcDirectoryPath() {
        return Path.of(srcDirectoryPath);
    }
    public Path getDstDirectoryPath() {
        return Path.of(dstDirectoryPath);
    }

    public void setSrcDirectoryPath(String srcDirectoryPath) {
        this.srcDirectoryPath = srcDirectoryPath;
    }

    public void setDstDirectoryPath(String dstDirectoryPath) {
        this.dstDirectoryPath = dstDirectoryPath;
    }

    public String getFileDuplicateOption() {
        return fileDuplicateOption;
    }

    public void setFileDuplicateOption(String fileDuplicateOption) {
        this.fileDuplicateOption = fileDuplicateOption;
    }

    public String getFolderDuplicateOption() {
        return folderDuplicateOption;
    }

    public void setFolderDuplicateOption(String folderDuplicateOption) {
        this.folderDuplicateOption = folderDuplicateOption;
    }

    public String getFolderFoundOption() {
        return folderFoundOption;
    }

    public void setFolderFoundOption(String folderFoundOption) {
        this.folderFoundOption = folderFoundOption;
    }
}
