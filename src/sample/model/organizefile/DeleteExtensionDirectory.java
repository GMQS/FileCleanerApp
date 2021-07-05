package sample.model.organizefile;

import org.apache.commons.io.FileUtils;
import sample.properties.AppProperty;
import sample.properties.ProgressProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteExtensionDirectory {

    private final AppProperty appProperty;
    private final ProgressProperties progressProperties;
    private final List<Path> foundDirList;
    private final StringBuilder stackExceptionText;

    public DeleteExtensionDirectory(AppProperty appProperty, ProgressProperties progressProperties, List<Path> foundDirList, StringBuilder stackExceptionText) {
        this.appProperty = appProperty;
        this.progressProperties = progressProperties;
        this.foundDirList = foundDirList;
        this.stackExceptionText = stackExceptionText;
    }

    public void execute() {
        final List<Path> fileList = new ArrayList<>();
        for (Path dir : foundDirList) {
            try {
                Files.list(dir).forEach(fileList::add);
            } catch (IOException e) {
                stackExceptionText.append(e).append("\nファイルリストの取得に失敗しました").append("\n\n");
                progressProperties.setIndeterminate();
                e.printStackTrace();
            }
        }

        final long totalSize = fileList.stream().filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
        progressProperties.setTotalFileSize(totalSize);
        progressProperties.setTotalFileCount(fileList.size());

        for (Path dir : foundDirList) {
            if (Thread.interrupted()) {
                stackExceptionText.append("処理がユーザーによって中断されました").append("\n\n");
                return;
            }
            List<Path> list;
            try {
                list = Files.list(dir).collect(Collectors.toList());
            } catch (IOException e) {
                stackExceptionText.append(e).append("\nファイルリストの取得に失敗しました").append("\n\n");
                progressProperties.setIndeterminate();
                e.printStackTrace();
                continue;
            }
            final long fileSize = list.stream().filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
            final long fileCount = list.size();

            if (list.size() != 1) {
                progressProperties.addDoneFileSize(fileSize);
                progressProperties.addDoneFileCount(fileCount);
                continue;
            }

            final Path path = list.get(0);
            Path dstFilePath = Path.of(dir.getParent() + "\\" + path.getFileName());

            if (!Files.exists(dstFilePath)) {
                fileMove(path, dstFilePath, dir, stackExceptionText, fileCount, fileSize, false);
                continue;
            }

            if (appProperty.isCheckContents()) {
                try {
                    final boolean isSameFile = FileUtils.contentEquals(path.toFile(), dstFilePath.toFile());
                    if (isSameFile) {
                        fileMove(path, dstFilePath, dir, stackExceptionText, fileCount, fileSize, true);
                        continue;
                    }

                    if (appProperty.getRadioButtonUserData().equals("RENAME")) {
                        Path p = dstFilePath;
                        for (int i = 1; Files.exists(p) && !path.equals(p); i++) {
                            final String rnm = new RenameFile(path.getFileName().toString()).rename("(" + i + ")");
                            p = Path.of(dir.getParent() + "\\" + rnm);
                        }
                        fileMove(path, p, dir, stackExceptionText, fileCount, fileSize, false);
                    } else {
                        progressProperties.addDoneFileSize(fileSize);
                        progressProperties.addDoneFileCount(fileCount);
                    }
                    continue;
                } catch (IOException e) {
                    stackExceptionText.append(e).append("\nファイル内容の比較に失敗しました").append("\n\n");
                    e.printStackTrace();
                    progressProperties.addDoneFileSize(fileSize);
                    progressProperties.addDoneFileCount(fileCount);
                    continue;
                }
            }

            if (appProperty.getFileDuplicateOption().equals("ファイル名を変更して移動")) {
                for (int i = 1; Files.exists(Path.of(dir.getParent() + "\\" + path.getFileName())); i++) {
                    final String rnmFileName = new RenameFile(path.getFileName().toString()).rename("(" + i + ")");
                    dstFilePath = Path.of(dir.getParent() + "\\" + rnmFileName);
                }
                fileMove(path, dstFilePath, dir, stackExceptionText, fileCount, fileSize, false);
                continue;
            }

            if (appProperty.getFileDuplicateOption().equals("ファイルを上書きして移動")) {
                fileMove(path, dstFilePath, dir, stackExceptionText, fileCount, fileSize, true);
                continue;
            }
            if (appProperty.getFileDuplicateOption().equals("ファイルを移動しない")) {
                progressProperties.addDoneFileSize(fileSize);
                progressProperties.addDoneFileCount(fileCount);
            }
        }
    }

    private void fileMove(final Path source, final Path target, final Path dir, final StringBuilder stackExceptionText, long doneFileCount, long doneFileSize, final boolean overWrite) {
        try {
            if (overWrite) {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(source, target);
            }
        } catch (IOException e) {
            stackExceptionText.append(e).append("\nファイルの移動に失敗しました").append("\n\n");
            e.printStackTrace();
        }
        try {
            Files.deleteIfExists(dir);
        } catch (IOException e) {
            stackExceptionText.append(e).append("\nフォルダの削除に失敗しました").append("\n\n");
            e.printStackTrace();
        }
        progressProperties.addDoneFileSize(doneFileSize);
        progressProperties.addDoneFileCount(doneFileCount);
    }
}
