/**
 * Thrown when we try to call the next patient but the queue is empty.
 * It is a "checked" exception, so Java forces us to handle it with try-catch.
 */
public class EmptyQueueException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmptyQueueException(String message) {
        super(message);
    }
}
