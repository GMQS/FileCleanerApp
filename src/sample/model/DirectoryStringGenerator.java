package sample.model;

public class DirectoryStringGenerator {

    public String getMediaDirectory(final String extension) {
        switch (extension.toLowerCase()) {
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
            case "bmp":
                return "\\画像ファイル";
            case "mp4":
            case "mov":
            case "wmv":
            case "flv":
            case "mpg":
            case "mpeg":
            case "mkv":
            case "avi":
            case "ts":
                return "\\動画ファイル";
            case "wave":
            case "wav":
            case "aif":
            case "mp3":
            case "aac":
            case "wma":
            case "3gp":
            case "ogg":
            case "oga":
            case "ape":
                return "\\音声ファイル";
            default:
                return "\\その他";
        }
    }
}
