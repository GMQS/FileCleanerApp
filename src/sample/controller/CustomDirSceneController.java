package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import sample.common.JsonIO;
import sample.common.RegexCheck;
import sample.exception.ListOverSizeException;
import sample.model.AlertWindowCreator;
import sample.model.MyDirectoryChooser;
import sample.properties.DirectoryProperty;
import sample.properties.DirectoryPropertyList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomDirSceneController {
    @FXML
    private Label customDirectoryPathText;
    @FXML
    private Button choiceFolderBtn;
    @FXML
    private TextField extensionField;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button confirmBtn;

    private Stage thisStage;
    private DirectoryProperty property;
    private List<DirectoryProperty> properties;

    @FXML
    private void initialize() {
        property = new DirectoryProperty();
        customDirectoryPathText.textProperty().bind(property.dstDirectoryTextProperty());
        confirmBtn.disableProperty().bind(customDirectoryPathText.textProperty().isEmpty().or(extensionField.textProperty().isEmpty()));
    }

    public void onShowing(Stage stage, List<DirectoryProperty> properties) {
        this.properties = properties;
        this.thisStage = stage;
    }

    public void onHidden(DirectoryPropertyList list) {
        if (property.isSetProperty()) {
            try {
                list.add(property);
            } catch (ListOverSizeException e) {
                new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("サイズオーバーエラー").setMessage(e.getMessage()).show();
            }
        }
    }

    @FXML
    private void confirmBtnClick(MouseEvent event) {
        final String ERROR_MESSAGE_1 = "拡張子の入力テキストが不正です。確認してください。\n・複数指定する場合はカンマ(\",\")で区切ってください。\n・記号などを含めないでください。\n" + "(例:jpg,png,gif)";
        if (RegexCheck.checkExtensionsValidation(extensionField.getText())) {
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("入力テキストエラー").setMessage(ERROR_MESSAGE_1).show();
            return;
        }

        List<String> inputExtensionList = Arrays.asList(extensionField.getText().toLowerCase().split(","));
        StringBuilder duplicateInfo = new StringBuilder();
        for (DirectoryProperty p : properties) {
            List<String> extensions = Arrays.asList(p.getExtensionsArray());
            ListUtils.intersection(inputExtensionList, extensions).forEach(extension -> {
                final String path = p.getDstDirectory().getPath();
                if (duplicateInfo.toString().contains(path)) {
                    duplicateInfo.append("\n重複した拡張子 : ").append(extension);
                } else {
                    duplicateInfo.append("\n\nフォルダパス : ").append(path).append("\n重複した拡張子 : ").append(extension);
                }
            });
        }
        if (!duplicateInfo.toString().isEmpty()) {
            final String ERROR_MESSAGE_2 = "既に他のフォルダに登録されている拡張子が含まれているため登録できません!\n[情報]" + duplicateInfo;
            new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("拡張子重複エラー").setMessage(ERROR_MESSAGE_2).show();
            return;
        }


        property.setExtensionsText(extensionField.getText());
        thisStage.hide();
    }

    @FXML
    private void cancelBtnClick(MouseEvent event) {
        thisStage.hide();
    }

    @FXML
    private void choiceBtnClick(MouseEvent event) {
        final File dir = new MyDirectoryChooser("移動先フォルダを選択", thisStage).createDirectoryChooser();
        if (Objects.isNull(dir)) {
            return;
        }
        for (DirectoryProperty p : properties) {
            if (p.getDstDirectory().equals(dir)) {
                new AlertWindowCreator(Alert.AlertType.ERROR).setParentWindow(thisStage).setTitle("重複エラー").setMessage("既に登録済みのフォルダです。").show();
                return;
            }
        }
        property.setDstDirectory(dir);
        property.setDstDirectoryText(dir.getPath());
    }
}
