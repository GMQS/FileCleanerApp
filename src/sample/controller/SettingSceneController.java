package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import sample.common.RegexCheck;
import sample.model.AlertWindowCreator;
import sample.model.MyDirectoryChooser;
import sample.properties.AppProperty;
import sample.properties.DirectoryProperty;
import sample.properties.DirectoryPropertyList;
import sample.properties.TemporaryDirectoryProperty;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SettingSceneController {

    @FXML
    private CheckBox disableSettingCheckBox;
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
    private DirectoryProperty directoryProperty;
    private TemporaryDirectoryProperty tmpProperty;
    private int beforeIndex;
    private int afterIndex;

    public void onShowing(AppProperty appProperty, Stage stage) {
        this.directoryPropertyList = appProperty.getDirectoryPropertyList();
        this.tmpProperty = new TemporaryDirectoryProperty();
        this.thisStage = stage;

        previewExtensionText.textProperty().addListener((observableValue, s, t1) -> {
            if (directoryProperty.getExtensionsText().equalsIgnoreCase(t1)) {
                tmpProperty.setTmpExtension(null);
                applyBtn.setDisable(!tmpProperty.isChange());
                return;
            }
            tmpProperty.setTmpExtension(t1);
            applyBtn.setDisable(false);
        });

        disableSettingCheckBox.selectedProperty().addListener((observableValue, beforeBool, afterBool) -> {
            directoryProperty.setDisableSetting(afterBool);
            previewExtensionText.setDisable(afterBool);
            previewFolderChoiceBtn.setDisable(afterBool);
            previewExplorerBtn.setDisable(afterBool);
            previewFolderPathText.setDisable(afterBool);
        });

        listView.setItems(directoryPropertyList.getTitles());
        listView.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldIndex, newIndex) -> {
            this.beforeIndex = oldIndex.intValue();
            this.afterIndex = newIndex.intValue();
            changeConfirmation(this.beforeIndex);
            tmpProperty.clearAll();
            disableSettingCheckBox.setDisable(false);
            previewExtensionText.setDisable(false);
            deleteBtn.setDisable(false);
            previewFolderChoiceBtn.setDisable(false);
            previewExplorerBtn.setDisable(false);
            try {
                directoryProperty = directoryPropertyList.getProperty(newIndex.intValue());
                previewFolderPathText.setText(directoryProperty.getDstDirectoryText());
                previewExtensionText.setText(directoryProperty.getExtensionsText());
                disableSettingCheckBox.setSelected(directoryProperty.isDisableSetting());
                if (disableSettingCheckBox.isSelected()) {
                    previewExtensionText.setDisable(true);
                    previewFolderChoiceBtn.setDisable(true);
                    previewExplorerBtn.setDisable(true);
                    previewFolderPathText.setDisable(true);
                } else {
                    previewExtensionText.setDisable(false);
                    previewFolderChoiceBtn.setDisable(false);
                    previewExplorerBtn.setDisable(false);
                    previewFolderPathText.setDisable(false);
                }

            } catch (IndexOutOfBoundsException e) {
                disableSettingCheckBox.setDisable(true);
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

    public void onHidden() {
    }

    public void onHiding() {
        changeConfirmation(afterIndex);
    }

    private void changeConfirmation(final int index) {
        if (Objects.isNull(directoryProperty)) {
            return;
        }
        if (Objects.isNull(tmpProperty)) {
            return;
        }

        if (!tmpProperty.isChange()) {
            return;
        }

        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(thisStage);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle("??????");
        alert.setHeaderText(null);
        alert.setContentText("?????????????????????????????????????????????????????????");

        final Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL) {
            //Cancel Button
            return;
        }
        if (result.get() == ButtonType.OK) {
            //OK Button
            applyChangeSettings(index);
        }

    }

    private boolean applyChangeSettings(final int index) {
        if (RegexCheck.checkExtensionsValidation(previewExtensionText.getText())) {
            final String message = "???????????????????????????????????????????????????????????????????????????\n???????????????????????????????????????(\",\")??????????????????????????????\n????????????????????????????????????????????????\n(???:jpg,png,gif)";
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("???????????????????????????").setMessage(message).show();
            return false;
        }

        final List<String> inputExtensionList = Arrays.asList(previewExtensionText.getText().split(","));
        StringBuilder duplicateInfo = new StringBuilder();
        for (DirectoryProperty p : directoryPropertyList.getProperties()) {
            final List<String> extensions = Arrays.asList(p.getExtensionsArray());
            ListUtils.intersection(inputExtensionList, extensions).forEach(extension -> {
                final String path = p.getDstDirectory().getPath();
                if (directoryPropertyList.getProperty(index).getDstDirectory().equals(p.getDstDirectory())) {
                    return;
                }
                if (duplicateInfo.toString().contains(path)) {
                    duplicateInfo.append("\n????????????????????? : ").append(extension);
                } else {
                    duplicateInfo.append("\n\n?????????????????? : ").append(path).append("\n????????????????????? : ").append(extension);
                }
            });
        }
        if (!duplicateInfo.toString().isEmpty()) {
            final String ERROR_MESSAGE = "?????????????????????????????????????????????????????????????????????????????????????????????????????????!\n[??????]" + duplicateInfo;
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("????????????????????????").setMessage(ERROR_MESSAGE).show();
            return false;
        }
        directoryPropertyList.update(tmpProperty, index);
        tmpProperty.clearAll();
        return true;
    }

    @FXML
    private void addBtnClick() throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/sample/scene/custom_dir_scene.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        CustomDirSceneController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-base:black");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("??????????????????????????????");
        stage.initOwner(thisStage);
        stage.setOnShowing(e -> controller.onShowing(stage, directoryPropertyList.getProperties()));
        stage.setOnHidden(e -> controller.onHidden(directoryPropertyList));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void previewFolderChoiceBtnClick() {
        final File dir = new MyDirectoryChooser("??????????????????????????????", thisStage).createDirectoryChooser();
        if (Objects.isNull(dir)) {
            return;
        }
        for (DirectoryProperty p : directoryPropertyList.getProperties()) {
            if (p.getDstDirectory().equals(dir) && !directoryPropertyList.getProperty(afterIndex).getDstDirectory().equals(dir)) {
                new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("???????????????").setMessage("??????????????????????????????????????????").show();
                return;
            }
        }
        tmpProperty.setTmpDir(dir);
        previewFolderPathText.setText(dir.getPath());
        applyBtn.setDisable(false);
    }

    @FXML
    private void cancelBtn() {
        thisStage.close();
    }

    @FXML
    private void deleteBtnClick() {
        Alert dialog = new Alert(Alert.AlertType.WARNING, null, new ButtonType("??????", ButtonBar.ButtonData.OK_DONE), new ButtonType("???????????????", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.setTitle("????????????");
        dialog.setHeaderText(null);
        dialog.setContentText("?????????1????????????????????????");
        dialog.initOwner(thisStage);
        dialog.showAndWait();
        if (dialog.resultProperty().get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
            final int index = listView.getSelectionModel().getSelectedIndex();
            directoryPropertyList.remove(index);
        }
    }

    @FXML
    private void previewExplorerBtnClick() {
        try {
            Desktop.getDesktop().open(directoryPropertyList.getProperty(listView.getSelectionModel().getSelectedIndex()).getDstDirectory());
        } catch (Exception e) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("?????????").setMessage(e.getMessage()).show();
        }
    }

    @FXML
    private void applyBtnClick() {
        if(applyChangeSettings(afterIndex)){
            new AlertWindowCreator(Alert.AlertType.INFORMATION).setParentWindow(thisStage).setTitle("????????????").setMessage("??????????????????????????????").show();
            applyBtn.setDisable(true);
        }
    }

    @FXML
    private void extensionsListLabelClick() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.tohoho-web.com/ex/draft/extension.htm"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
