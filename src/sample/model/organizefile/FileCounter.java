package sample.model.organizefile;

public class FileCounter {
    private long moveFileCount = 0;
    private long skipFileCount = 0;
    private long deleteFileCount = 0;

    private long moveDirCount = 0;
    private long skipDirCount = 0;

    public String getFormattedResultText() {
        return "[詳細情報]\n移動したファイル数 : " + moveFileCount + "\n移動したフォルダ数 : " + moveDirCount + "\nスキップしたファイル数 : " + skipFileCount + "\nスキップしたフォルダ数 : " + skipDirCount;
    }

    public long getTotalCount() {
        return moveFileCount + skipFileCount + deleteFileCount;
    }

    public void addSkipDirCount() {
        skipDirCount++;
    }

    public void addSkipDirCount(long fileCount) {
        skipDirCount += fileCount;
    }

    public void addSkipFileCount() {
        skipFileCount++;
    }

    public void addSkipFileCount(long fileCount) {
        skipFileCount += fileCount;
    }

    public void addMoveFileCount() {
        moveFileCount++;
    }

    public void addMoveFileCount(long fileCount) {
        moveFileCount += fileCount;
    }

    public void addDeleteFileCount() {
        deleteFileCount++;
    }

    public void addDeleteFileCount(long fileCount) {
        deleteFileCount += fileCount;
    }

    public void addMoveDirCount() {
        moveDirCount++;
    }

    public void addMoveDirCount(long fileCount) {
        moveDirCount += fileCount;
    }
}
