import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * The "brain" of the system. It owns the two queues and all the rules:
 *
 *   - regularQueue   : a normal Queue          -> First-Come-First-Served
 *   - emergencyQueue : a PriorityQueue         -> most severe first
 *   - registeredIds  : a Set of every ID used  -> prevents duplicates
 *
 * The queues are private. Other classes can only use them through the
 * public methods below (encapsulation).
 */
public class HospitalQueueManager {

    private final Queue<Patient> regularQueue = new LinkedList<>();
    private final PriorityQueue<Patient> emergencyQueue =
            new PriorityQueue<>(new EmergencyComparator());
    private final Set<Integer> registeredIds = new HashSet<>();

    private final Doctor regularDoctor;
    private final Doctor emergencyDoctor;

    // Goes up by 1 for every successful registration. It records arrival order.
    private long nextArrivalNumber = 1;

    public HospitalQueueManager(Doctor regularDoctor, Doctor emergencyDoctor) {
        if (regularDoctor == null || emergencyDoctor == null) {
            throw new IllegalArgumentException("Both doctors are required.");
        }
        this.regularDoctor = regularDoctor;
        this.emergencyDoctor = emergencyDoctor;
    }

    // ---------------------------------------------------------------
    // The rule that decides who is treated first among emergencies
    // ---------------------------------------------------------------
    private static class EmergencyComparator implements Comparator<Patient> {
        @Override
        public int compare(Patient first, Patient second) {
            // 1) Smaller priority number = more severe = comes out first
            int bySeverity = Integer.compare(first.getSeverity().getPriorityLevel(),
                                             second.getSeverity().getPriorityLevel());
            if (bySeverity != 0) {
                return bySeverity;
            }
            // 2) Same severity: the one who arrived earlier comes out first
            return Long.compare(first.getArrivalNumber(), second.getArrivalNumber());
        }
    }

    // ---------------------------------------------------------------
    // Registration
    // ---------------------------------------------------------------

    /** Adds a regular patient to the BACK of the regular queue. */
    public void registerRegularPatient(int id, String name, int age, String illness)
            throws DuplicatePatientException {
        // Creating the Patient also validates the details (may throw IllegalArgumentException)
        Patient patient = new Patient(id, name, age, illness, nextArrivalNumber);
        checkDuplicate(id);
        registeredIds.add(id);
        regularQueue.add(patient);          // Queue: add at the rear
        nextArrivalNumber++;
    }

    /** Adds an emergency patient to the priority queue. */
    public void registerEmergencyPatient(int id, String name, int age, String illness,
                                         Severity severity)
            throws DuplicatePatientException {
        Patient patient = new Patient(id, name, age, illness, severity, nextArrivalNumber);
        checkDuplicate(id);
        registeredIds.add(id);
        emergencyQueue.add(patient);        // PriorityQueue: placed by the comparator
        nextArrivalNumber++;
    }

    private void checkDuplicate(int id) throws DuplicatePatientException {
        if (registeredIds.contains(id)) {
            throw new DuplicatePatientException(id);
        }
    }

    /** True if this ID was ever registered (even if the patient was already treated). */
    public boolean isIdTaken(int id) {
        return registeredIds.contains(id);
    }

    // ---------------------------------------------------------------
    // Calling the next patient
    // ---------------------------------------------------------------

    /** Removes and returns the patient at the FRONT of the regular queue. */
    public Patient callNextRegularPatient() throws EmptyQueueException {
        if (regularQueue.isEmpty()) {
            throw new EmptyQueueException("There are no regular patients waiting.");
        }
        Patient patient = regularQueue.poll();   // Queue: remove from the front
        regularDoctor.treat(patient);
        return patient;
    }

    /** Removes and returns the MOST URGENT patient from the emergency queue. */
    public Patient callNextEmergencyPatient() throws EmptyQueueException {
        if (emergencyQueue.isEmpty()) {
            throw new EmptyQueueException("There are no emergency patients waiting.");
        }
        Patient patient = emergencyQueue.poll(); // PriorityQueue: remove the highest priority
        emergencyDoctor.treat(patient);
        return patient;
    }

    // ---------------------------------------------------------------
    // Viewing the queues (we return COPIES so nobody can damage the real queues)
    // ---------------------------------------------------------------

    /** Regular patients in the order they will be called (front to back). */
    public List<Patient> getRegularPatients() {
        return new ArrayList<>(regularQueue);
    }

    /**
     * Emergency patients in the order they will be called.
     * Iterating a PriorityQueue directly does NOT give sorted order
     * (it is stored as a heap), so we copy it and sort with the same comparator.
     */
    public List<Patient> getEmergencyPatientsInOrder() {
        List<Patient> list = new ArrayList<>(emergencyQueue);
        Collections.sort(list, new EmergencyComparator());
        return list;
    }

    public int getRegularQueueSize() {
        return regularQueue.size();
    }

    public int getEmergencyQueueSize() {
        return emergencyQueue.size();
    }

    public Doctor getRegularDoctor() {
        return regularDoctor;
    }

    public Doctor getEmergencyDoctor() {
        return emergencyDoctor;
    }
}
