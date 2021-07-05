package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import sample.common.RegexCheck;
import sample.model.AlertWindowCreator;
import sample.model.MyDirectoryChooser;
import sample.properties.AppProperty;
import sample.properties.DirectoryProperty;
import sample.properties.DirectoryPropertyList;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettingSceneController {

    @FXML
    private Label extensionsListLabel;
    @FXML
    private Button applyBtn;
    @FXML
    private Button previewExplorerBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private TextField previewExtensionText;
    @FXML
    private Button previewFolderChoiceBtn;
    @FXML
    private Label previewFolderPathText;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button addBtn;


    private Stage thisStage;
    private DirectoryPropertyList directoryPropertyList;
    private File tmpDir;

    @FXML
    private void initialize() {

    }

    public void onShowing(AppProperty appProperty, Stage stage) {
        this.directoryPropertyList = appProperty.getDirectoryPropertyList();
        this.thisStage = stage;
        listView.setItems(directoryPropertyList.getTitles());
        listView.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldIndex, newIndex) -> {
            previewExtensionText.setDisable(false);
            deleteBtn.setDisable(false);
            applyBtn.setDisable(false);
            previewFolderChoiceBtn.setDisable(false);
            previewExplorerBtn.setDisable(false);
            try {
                final DirectoryProperty property = directoryPropertyList.getProperty(newIndex.intValue());
                previewFolderPathText.setText(property.getDstDirectoryText());
                previewExtensionText.setText(property.getExtensionsText());
            } catch (IndexOutOfBoundsException e) {
                previewExtensionText.setDisable(true);
                deleteBtn.setDisable(true);
                applyBtn.setDisable(true);
                previewFolderChoiceBtn.setDisable(true);
                previewExplorerBtn.setDisable(true);
                previewFolderPathText.setText("");
                previewExtensionText.setText("");
            }

        });
    }

    public void setProperty(AppProperty appProperty) {
        directoryPropertyList = appProperty.getDirectoryPropertyList();
    }

    public void onHidden() {

    }

    @FXML
    private void addBtnClick(MouseEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/custom_dir_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        CustomDirSceneController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-base:black");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("個別フォルダ設定追加");
        stage.initOwner(thisStage);
        stage.setOnShowing(e -> controller.onShowing(stage, directoryPropertyList.getProperties()));
        stage.setOnHidden(e -> controller.onHidden(directoryPropertyList));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void previewFolderChoiceBtnClick(MouseEvent event) {
        final File dir = new MyDirectoryChooser("移動先フォルダを選択", thisStage).createDirectoryChooser();
        if (Objects.isNull(dir)) {
            return;
        }
        final int index = listView.getSelectionModel().getSelectedIndex();
        for (DirectoryProperty p : directoryPropertyList.getProperties()) {
            if (p.getDstDirectory().equals(dir) && !directoryPropertyList.getProperty(index).getDstDirectory().equals(dir)) {
                new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("重複エラー").setMessage("既に登録済みのフォルダです。").show();
                return;
            }
        }
        tmpDir = dir;
        previewFolderPathText.setText(dir.getPath());
    }

    @FXML
    private void cancelBtn(MouseEvent event) {
        thisStage.close();
    }

    @FXML
    private void deleteBtnClick(MouseEvent event) {
        Alert dialog = new Alert(Alert.AlertType.WARNING, null, new ButtonType("削除", ButtonBar.ButtonData.OK_DONE), new ButtonType("キャンセル", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.setTitle("削除確認");
        dialog.setHeaderText(null);
        dialog.setContentText("設定を1件削除しますか？");
        dialog.initOwner(thisStage);
        dialog.showAndWait();
        if (dialog.resultProperty().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
            final int index = listView.getSelectionModel().getSelectedIndex();
            directoryPropertyList.remove(index);
        }
    }

    @FXML
    private void previewExplorerBtnClick(MouseEvent event) {
        try {
            Desktop.getDesktop().open(directoryPropertyList.getProperty(listView.getSelectionModel().getSelectedIndex()).getDstDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("エラー").setMessage(e.getMessage()).show();
        }
    }

    @FXML
    private void applyBtnClick(MouseEvent event) {
        if (RegexCheck.checkExtensionsValidation(previewExtensionText.getText())) {
            final String message = "拡張子の入力テキストが不正です。確認してください。\n・複数指定する場合はカンマ(\",\")で区切ってください。\n・記号などを含めないでください。\n(例:jpg,png,gif)";
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("入力テキストエラー").setMessage(message).show();
            return;
        }

        List<String> inputExtensionList = Arrays.asList(previewExtensionText.getText().split(","));
        StringBuilder duplicateInfo = new StringBuilder();
        final int index = listView.getSelectionModel().getSelectedIndex();
        for (DirectoryProperty p : directoryPropertyList.getProperties()) {
            List<String> extensions = Arrays.asList(p.getExtensionsArray());
            ListUtils.intersection(inputExtensionList, extensions).forEach(extension -> {
                final String path = p.getDstDirectory().getPath();
                if (directoryPropertyList.getProperty(index).getDstDirectory().equals(p.getDstDirectory())) {
                    return;
                }
                if (duplicateInfo.toString().contains(path)) {
                    duplicateInfo.append("\n重複した拡張子 : ").append(extension);
                } else {
                    duplicateInfo.append("\n\nフォルダパス : ").append(path).append("\n重複した拡張子 : ").append(extension);
                }
            });
        }
        if (!duplicateInfo.toString().isEmpty()) {
            final String ERROR_MESSAGE = "既に他のフォルダに登録されている拡張子が含まれているため登録できません!\n[情報]" + duplicateInfo;
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("拡張子重複エラー").setMessage(ERROR_MESSAGE).show();
            return;
        }
        final DirectoryProperty property = new DirectoryProperty();
        File dir = tmpDir;
        if (Objects.isNull(dir)) {
            dir = directoryPropertyList.getProperty(index).getDstDirectory();
        }

        property.setDstDirectory(dir);
        property.setExtensionsText(previewExtensionText.getText());
        property.setDstDirectoryText(previewFolderPathText.getText());
        directoryPropertyList.update(property, index);
        new AlertWindowCreator(Alert.AlertType.INFORMATION).setParentWindow(thisStage).setTitle("登録変更").setMessage("変更を保存しました。").show();
    }

    @FXML
    private void extensionsListLabelClick(MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.tohoho-web.com/ex/draft/extension.htm"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
