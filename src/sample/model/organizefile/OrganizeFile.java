package sample.model.organizefile;

import org.apache.commons.io.FileUtils;
import sample.model.organizefile.filevisitor.MyFileVisitor;
import sample.properties.AppProperty;
import sample.properties.ProgressProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class OrganizeFile implements Runnable {

    private final AppProperty appProperty;
    private final ProgressProperties progressProperties;
    private Callback callback;

    public OrganizeFile(AppProperty appProperty) {
        this.progressProperties = new ProgressProperties();
        this.appProperty = appProperty;
    }

    @FunctionalInterface
    public interface Callback {
        void finished(String result, String error);
    }

    public ProgressProperties getProgressProperties() {
        return this.progressProperties;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {

        //総ファイル数と総ファイルサイズを取得
        progressProperties.setIndeterminate();
        ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            es.execute(() -> {
                try {
                    final List<Path> fileList = Files.walk(appProperty.getSrcDirectoryPath()).filter(p -> p.toFile().isFile()).collect(Collectors.toList());
                    long size = fileList.stream().mapToLong(p -> p.toFile().length()).sum();
                    long count = fileList.size();
                    progressProperties.releaseIndeterminate();
                    progressProperties.setTotalFileCount(count);
                    progressProperties.setTotalFileSize(size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            es.shutdown();
        }

        final StringBuilder stackExceptionText = new StringBuilder();

        //重複ファイル削除
        progressProperties.setStepCount(1);
        if (appProperty.isDuplicateContentsDelete()) {
            List<Path> dirList = null;
            try {
                dirList = Files.walk(appProperty.getSrcDirectoryPath()).filter(p -> p.toFile().isDirectory()).collect(Collectors.toList());
            } catch (IOException e) {
                stackExceptionText.append(e).append("\nフォルダ一覧の取得に失敗しました").append("\n\n");
                e.printStackTrace();
            }
            if (Objects.nonNull(dirList)) {
                final List<Path> deleteFileList = new ArrayList<>();
                long deleteFileCount = 0;
                long deleteFileSize = 0;
                for (Path dir : dirList) {
                    try {
                        for (Path file1 : Files.list(dir).filter(a -> a.toFile().isFile()).collect(Collectors.toList())) {
                            if (Thread.interrupted()) {
                                stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
                                callback.finished(null, stackExceptionText.toString());
                                return;
                            }
                            if (deleteFileList.contains(file1)) {
                                continue;
                            }
                            try {
                                progressProperties.addDoneFileSize(Files.size(file1));
                            } catch (IOException e) {
                                progressProperties.setIndeterminate();
                                e.printStackTrace();
                            }

                            for (Path file2 : Files.list(dir).filter(a -> a.toFile().isFile()).collect(Collectors.toList())) {
                                try {
                                    if (FileUtils.contentEquals(file1.toFile(), file2.toFile()) && !file1.equals(file2)) {
                                        long size = 0L;
                                        try {
                                            size = Files.size(file2);
                                        } catch (IOException e) {
                                            progressProperties.setIndeterminate();
                                            e.printStackTrace();
                                        }
                                        try {
                                            Files.deleteIfExists(file2);
                                            deleteFileCount++;
                                            deleteFileSize += size;
                                            deleteFileList.add(file2);
                                            progressProperties.addDoneFileCount(1);
                                            progressProperties.addDoneFileSize(size);
                                        } catch (IOException e) {
                                            stackExceptionText.append(e).append("\n重複ファイルの削除に失敗しました").append("\n\n");
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (IOException e) {
                                    stackExceptionText.append(e).append("\nファイルの比較に失敗しました").append("\n\n");
                                    e.printStackTrace();
                                }
                            }
                            progressProperties.addDoneFileCount(1);
                        }
                    } catch (IOException e) {
                        stackExceptionText.append(e).append("\nファイル一覧の取得に失敗しました").append("\n\n");
                        e.printStackTrace();
                    }
                }
                final long totalFileCount = progressProperties.getTotalFileCount();
                final long totalFileSize = progressProperties.getTotalFileSize();

                //ファイル情報取得が完了していた場合にデータをセット
                if (totalFileCount != 0L && totalFileSize != 0L) {
                    progressProperties.setTotalFileCount(totalFileCount - deleteFileCount);
                    progressProperties.setTotalFileSize(totalFileSize - deleteFileSize);
                }
                progressProperties.resetDoneValue();
            }
        }

        //ファイル移動
        progressProperties.setStepCount(2);
        final MyFileVisitor visitor = new MoveFiles(appProperty, progressProperties, stackExceptionText).execute();


        progressProperties.setStepCount(3);
        if (!appProperty.isCreateFolder()) {
            callback.finished(visitor.getFormattedResultText(), stackExceptionText.toString());
            return;
        }

        progressProperties.resetAll();
        //拡張子フォルダ整理
        new DeleteExtensionDirectory(appProperty, progressProperties, visitor.getFoundDirList(), stackExceptionText).execute();
        callback.finished(visitor.getFormattedResultText(), stackExceptionText.toString());
    }
}
