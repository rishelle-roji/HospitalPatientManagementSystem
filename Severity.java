/**
 * Severity levels for emergency patients.
 *
 * Each level has a priority number. A SMALLER number means MORE urgent,
 * because Java's PriorityQueue always removes the "smallest" element first.
 *
 *   CRITICAL -> 1 (treated first)
 *   SERIOUS  -> 2
 *   MODERATE -> 3 (treated last among emergencies)
 */
public enum Severity {
    CRITICAL(1),
    SERIOUS(2),
    MODERATE(3);

    private final int priorityLevel;

    Severity(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    /** Shows "CRITICAL" as "Critical", which looks nicer on screen. */
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
