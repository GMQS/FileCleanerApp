package sample.model.organizefile;

import sample.model.organizefile.filevisitor.MyFileVisitor;
import sample.properties.AppProperty;
import sample.properties.ProgressProperties;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

public class MoveFiles {

    private final AppProperty appProperty;
    private final ProgressProperties progressProperties;
    private final StringBuilder stackExceptionText;

    public MoveFiles(AppProperty appProperty, ProgressProperties progressProperties, StringBuilder stackExceptionText) {
        this.appProperty = appProperty;
        this.progressProperties = progressProperties;
        this.stackExceptionText = stackExceptionText;
    }

    public MyFileVisitor execute() {


        final Path srcDirectoryPath = appProperty.getSrcDirectoryPath();
        final MyFileVisitor visitor = new MyFileVisitor(appProperty, progressProperties, stackExceptionText);

        try {
            Files.walkFileTree(srcDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return visitor;
    }

}
