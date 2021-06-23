package sample.exception;

public class ListOverSizeException extends Exception {

    private static final long serialVersionUID = 1L;

    public ListOverSizeException(String msg) {
        super(msg);
    }
}
