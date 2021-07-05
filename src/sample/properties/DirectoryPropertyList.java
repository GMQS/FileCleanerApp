package sample.properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.exception.ListOverSizeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryPropertyList {

    private final List<DirectoryProperty> propertyList;
    private final ObservableList<String> titles;

    public DirectoryPropertyList() {
        this.propertyList = new ArrayList<>();
        this.titles = FXCollections.observableArrayList();
    }

    public ObservableList<String> getTitles() {
        return this.titles;
    }

    public List<DirectoryProperty> getProperties() {
        return this.propertyList;
    }

    public DirectoryProperty getProperty(final int index) throws IllegalArgumentException {
        return propertyList.get(index);
    }

    public void update(final DirectoryProperty property, final int index) {
        propertyList.get(index).update(property);
        String title = property.getDstDirectory().getName();
        if (title.equals("")) {
            title = property.getDstDirectory().getPath();
        }
        titles.set(index, title);
    }

    public void add(final DirectoryProperty... properties) throws ListOverSizeException {
        for (DirectoryProperty property : properties) {
            final String directoryName = property.getDstDirectory().getName();
            if (titles.size() >= 2000 || propertyList.size() >= 2000) {
                throw new ListOverSizeException("リストのサイズ上限です( >= 2000)");
            }
            if (directoryName.equals("")) {
                titles.add(property.getDstDirectory().getPath());
            } else {
                titles.add(directoryName);
            }
        }
        propertyList.addAll(Arrays.asList(properties));
    }

    public void remove(final int index) {
        propertyList.remove(index);
        titles.remove(index);
    }
}
