/**
 * Thrown when someone tries to register a patient whose ID is already in use.
 * It is a "checked" exception, so Java forces us to handle it with try-catch.
 */
public class DuplicatePatientException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicatePatientException(int patientId) {
        super("A patient with ID " + patientId + " is already registered.");
    }
}
