package sample.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameFile {

    private final String fileName;

    public RenameFile(final String fileName) {
        this.fileName = fileName;
    }

    public String rename(final String addChar) {
        return buildText(this.fileName, addChar);
    }

    public String getFileExtensionChar() {
        final Pattern pattern = Pattern.compile("(?i:.*\\.(.*))");
        Matcher matcher = pattern.matcher(this.fileName);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private String buildText(final String text, final String addChar) {
        final Pattern pattern = Pattern.compile("(?i:.*\\.(.*))");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            final String extension = matcher.group(1);
            final String planeName = getPlaneName(text);
            return planeName + addChar + "." + extension;
        } else {
            return text + addChar;
        }
    }

    private String getPlaneName(final String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }
}
