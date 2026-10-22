/**
 * Represents one patient in the hospital.
 *
 * A patient is either REGULAR (severity is null) or EMERGENCY (severity is set).
 * All fields are private and final, so a Patient cannot be changed after it
 * is created (encapsulation + immutability).
 */
public class Patient {

    // Validation limits, shared with the menu in Main.java
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 120;
    public static final int MAX_NAME_LENGTH = 25;
    public static final int MAX_ILLNESS_LENGTH = 30;

    private final int id;
    private final String name;
    private final int age;
    private final String illness;
    private final Severity severity;      // null for regular patients
    private final long arrivalNumber;     // 1, 2, 3 ... used to break ties

    /** Constructor for a REGULAR patient. */
    public Patient(int id, String name, int age, String illness, long arrivalNumber) {
        validateDetails(id, name, age, illness);
        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.illness = illness.trim();
        this.severity = null;
        this.arrivalNumber = arrivalNumber;
    }

    /** Constructor for an EMERGENCY patient (has a severity level). */
    public Patient(int id, String name, int age, String illness,
                   Severity severity, long arrivalNumber) {
        validateDetails(id, name, age, illness);
        if (severity == null) {
            throw new IllegalArgumentException("An emergency patient must have a severity level.");
        }
        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.illness = illness.trim();
        this.severity = severity;
        this.arrivalNumber = arrivalNumber;
    }

    // ---------- Validation (one place for all the rules) ----------

    public static boolean isValidId(int id) {
        return id > 0;
    }

    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        return !trimmed.isEmpty()
                && trimmed.length() <= MAX_NAME_LENGTH
                && trimmed.matches("[A-Za-z][A-Za-z .'-]*");   // letters, space, . ' -
    }

    public static boolean isValidAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    public static boolean isValidIllness(String illness) {
        if (illness == null) {
            return false;
        }
        String trimmed = illness.trim();
        return !trimmed.isEmpty() && trimmed.length() <= MAX_ILLNESS_LENGTH;
    }

    private static void validateDetails(int id, String name, int age, String illness) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Patient ID must be a positive number.");
        }
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid patient name.");
        }
        if (!isValidAge(age)) {
            throw new IllegalArgumentException("Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        if (!isValidIllness(illness)) {
            throw new IllegalArgumentException("Illness must not be empty or too long.");
        }
    }

    // ---------- Getters (no setters: the object never changes) ----------

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getIllness() {
        return illness;
    }

    public Severity getSeverity() {
        return severity;
    }

    public long getArrivalNumber() {
        return arrivalNumber;
    }

    public boolean isEmergency() {
        return severity != null;
    }

    @Override
    public String toString() {
        String text = "ID " + id + ", " + name + ", age " + age + ", " + illness;
        if (isEmergency()) {
            text += " [" + severity + "]";
        }
        return text;
    }
}
