/**
 * Represents a doctor who treats the patients that are called from the queues.
 */
public class Doctor {

    private final String name;
    private final String specialization;
    private int patientsTreated;

    public Doctor(String name, String specialization) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name must not be empty.");
        }
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor specialization must not be empty.");
        }
        this.name = name.trim();
        this.specialization = specialization.trim();
        this.patientsTreated = 0;
    }

    /** The doctor starts treating the given patient. */
    public void treat(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("There is no patient to treat.");
        }
        patientsTreated++;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getPatientsTreated() {
        return patientsTreated;
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
