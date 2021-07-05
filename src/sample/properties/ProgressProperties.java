package sample.properties;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class ProgressProperties {

    private final DoubleProperty progressProperty;
    private final StringProperty totalFileCountProperty;
    private final StringProperty doneCountProperty;
    private final StringProperty stepCountProperty;

    private long totalFileSize = 0L;
    private long doneFileSize = 0L;

    private long totalFileCount = 0L;
    private long doneFileCount = 0L;


    public ProgressProperties() {
        progressProperty = new SimpleDoubleProperty(0);
        totalFileCountProperty = new SimpleStringProperty("0");
        doneCountProperty = new SimpleStringProperty("0");
        stepCountProperty = new SimpleStringProperty("0");
    }


    public void setTotalFileCount(long totalFileCount) {
        this.totalFileCount = totalFileCount;
        Platform.runLater(() -> this.totalFileCountProperty.set(String.valueOf(totalFileCount)));
    }


    public void setDoneFileCount(long doneFileCount) {
        this.doneFileCount = doneFileCount;
        if (progressProperty.get() == INDETERMINATE_PROGRESS) {
            return;
        }
        Platform.runLater(() -> this.doneCountProperty.set(String.valueOf(doneFileCount)));
    }

    public void addDoneFileCount(long doneFileCount) {
        this.doneFileCount += doneFileCount;
        if (progressProperty.get() == INDETERMINATE_PROGRESS) {
            return;
        }
        Platform.runLater(() -> this.doneCountProperty.set(String.valueOf(this.doneFileCount)));
    }

    public void addDoneFileSize(long doneFileSize) {
        this.doneFileSize += doneFileSize;
        if (progressProperty.get() == INDETERMINATE_PROGRESS) {
            return;
        }
        if (this.totalFileSize == 0L) {
            return;
        }
        final double value = (double) this.doneFileSize / this.totalFileSize;
        this.progressProperty.set(value);
    }

    public void setTotalFileSize(final long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public long getTotalFileCount() {
        return totalFileCount;
    }

    public long getDoneFileCount() {
        return doneFileCount;
    }

    public long getDoneFileSize() {
        return doneFileSize;
    }

    public void setIndeterminate() {
        Platform.runLater(() -> {
            this.doneCountProperty.set("Unknown");
            this.totalFileCountProperty.set("Unknown");
            this.progressProperty.set(INDETERMINATE_PROGRESS);
        });
        this.totalFileSize = 0L;
        this.totalFileCount = 0L;
        this.doneFileCount = 0L;
        this.doneFileSize = 0L;
    }

    public void releaseIndeterminate() {
        Platform.runLater(() -> this.progressProperty.set(0));
    }

    public void resetAll() {
        Platform.runLater(() -> {
            this.doneCountProperty.set("0");
            this.totalFileCountProperty.set("0");
            this.progressProperty.set(0);
        });

        this.totalFileSize = 0L;
        this.totalFileCount = 0L;
        this.doneFileCount = 0L;
        this.doneFileSize = 0L;
    }

    public void resetDoneValue() {
        this.doneFileCount = 0L;
        this.doneFileSize = 0L;
    }

    public void setStepCount(int stepCount) {
        Platform.runLater(() -> this.stepCountProperty.set(String.valueOf(stepCount)));
    }

    public DoubleProperty progressProperty() {
        return progressProperty;
    }

    public StringProperty totalCountProperty() {
        return totalFileCountProperty;
    }

    public StringProperty doneCountProperty() {
        return doneCountProperty;
    }

    public StringProperty stepCountProperty() {
        return stepCountProperty;
    }

}
