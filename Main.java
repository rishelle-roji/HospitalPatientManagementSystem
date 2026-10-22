import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu-driven console interface for the Hospital Patient Management System.
 * This class only talks to the user. All the real work is done by
 * HospitalQueueManager.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static HospitalQueueManager manager;

    public static void main(String[] args) {
        Doctor generalDoctor = new Doctor("Dr. Meera Nair", "General Physician");
        Doctor emergencyDoctor = new Doctor("Dr. Arjun Menon", "Emergency Medicine");
        manager = new HospitalQueueManager(generalDoctor, emergencyDoctor);

        System.out.println("==========================================");
        System.out.println("   HOSPITAL PATIENT MANAGEMENT SYSTEM");
        System.out.println("   Queue and Priority Queue demonstration");
        System.out.println("==========================================");

        try {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readMenuChoice();

                switch (choice) {
                    case 1:
                        registerRegularPatient();
                        break;
                    case 2:
                        registerEmergencyPatient();
                        break;
                    case 3:
                        displayRegularQueue();
                        break;
                    case 4:
                        displayEmergencyQueue();
                        break;
                    case 5:
                        callNextRegularPatient();
                        break;
                    case 6:
                        callNextEmergencyPatient();
                        break;
                    case 7:
                        running = false;
                        System.out.println("\nThank you for using the system. Goodbye!");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a number from 1 to 7.");
                }
            }
        } catch (NoSuchElementException e) {
            // Happens if the input ends suddenly (for example Ctrl+D)
            System.out.println("\nInput closed. Exiting the program.");
        }
    }

    // ---------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------

    private static void printMenu() {
        System.out.println("\n------------------ MENU ------------------");
        System.out.println(" Waiting -> Regular: " + manager.getRegularQueueSize()
                + " | Emergency: " + manager.getEmergencyQueueSize());
        System.out.println("------------------------------------------");
        System.out.println(" 1. Register regular patient");
        System.out.println(" 2. Register emergency patient");
        System.out.println(" 3. Display regular queue");
        System.out.println(" 4. Display emergency queue");
        System.out.println(" 5. Call next regular patient");
        System.out.println(" 6. Call highest-priority emergency patient");
        System.out.println(" 7. Exit");
        System.out.println("------------------------------------------");
    }

    /** Returns the menu number, or -1 if the user typed something that is not a number. */
    private static int readMenuChoice() {
        System.out.print("Enter your choice (1-7): ");
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------------------------------------------------------------
    // Registration
    // ---------------------------------------------------------------

    private static void registerRegularPatient() {
        System.out.println("\n--- Register Regular Patient ---");
        int id = readUniqueId();
        String name = readName();
        int age = readInt("Enter age (" + Patient.MIN_AGE + "-" + Patient.MAX_AGE + "): ",
                          Patient.MIN_AGE, Patient.MAX_AGE);
        String illness = readIllness();

        try {
            manager.registerRegularPatient(id, name, age, illness);
            System.out.println("\nRegistered successfully. Position in regular queue: "
                    + manager.getRegularQueueSize());
        } catch (DuplicatePatientException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void registerEmergencyPatient() {
        System.out.println("\n--- Register Emergency Patient ---");
        int id = readUniqueId();
        String name = readName();
        int age = readInt("Enter age (" + Patient.MIN_AGE + "-" + Patient.MAX_AGE + "): ",
                          Patient.MIN_AGE, Patient.MAX_AGE);
        String illness = readIllness();
        Severity severity = readSeverity();

        try {
            manager.registerEmergencyPatient(id, name, age, illness, severity);
            int position = findEmergencyPosition(id);
            System.out.println("\nRegistered successfully. Current position in emergency queue: "
                    + position + " of " + manager.getEmergencyQueueSize());
        } catch (DuplicatePatientException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    /** Finds where a patient currently stands in the emergency queue (1 = next to be called). */
    private static int findEmergencyPosition(int id) {
        List<Patient> list = manager.getEmergencyPatientsInOrder();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                return i + 1;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------
    // Displaying the queues
    // ---------------------------------------------------------------

    private static void displayRegularQueue() {
        List<Patient> patients = manager.getRegularPatients();
        System.out.println("\n--- Regular Queue (First-Come-First-Served) ---");

        if (patients.isEmpty()) {
            System.out.println("The regular queue is empty.");
            return;
        }

        System.out.printf("%-5s %-6s %-25s %-4s %-30s%n", "Pos", "ID", "Name", "Age", "Illness");
        System.out.println("--------------------------------------------------------------------");
        int position = 1;
        for (Patient p : patients) {
            System.out.printf("%-5d %-6d %-25s %-4d %-30s%n",
                    position, p.getId(), p.getName(), p.getAge(), p.getIllness());
            position++;
        }
    }

    private static void displayEmergencyQueue() {
        List<Patient> patients = manager.getEmergencyPatientsInOrder();
        System.out.println("\n--- Emergency Queue (Most Severe First) ---");

        if (patients.isEmpty()) {
            System.out.println("The emergency queue is empty.");
            return;
        }

        System.out.printf("%-5s %-6s %-25s %-4s %-30s %-9s%n",
                "Pos", "ID", "Name", "Age", "Illness", "Severity");
        System.out.println("--------------------------------------------------------------------------------");
        int position = 1;
        for (Patient p : patients) {
            System.out.printf("%-5d %-6d %-25s %-4d %-30s %-9s%n",
                    position, p.getId(), p.getName(), p.getAge(), p.getIllness(), p.getSeverity());
            position++;
        }
    }

    // ---------------------------------------------------------------
    // Calling the next patient
    // ---------------------------------------------------------------

    private static void callNextRegularPatient() {
        try {
            Patient patient = manager.callNextRegularPatient();
            Doctor doctor = manager.getRegularDoctor();
            System.out.println("\nNow treating (regular): " + patient);
            System.out.println("Doctor: " + doctor + " | Patients treated so far: "
                    + doctor.getPatientsTreated());
        } catch (EmptyQueueException e) {
            System.out.println("\n" + e.getMessage());
        }
    }

    private static void callNextEmergencyPatient() {
        try {
            Patient patient = manager.callNextEmergencyPatient();
            Doctor doctor = manager.getEmergencyDoctor();
            System.out.println("\nNow treating (EMERGENCY): " + patient);
            System.out.println("Doctor: " + doctor + " | Patients treated so far: "
                    + doctor.getPatientsTreated());
        } catch (EmptyQueueException e) {
            System.out.println("\n" + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Input helpers: each one keeps asking until the input is valid
    // ---------------------------------------------------------------

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid whole number. Try again.");
            }
        }
    }

    private static int readUniqueId() {
        while (true) {
            int id = readInt("Enter patient ID (positive number): ", 1, Integer.MAX_VALUE);
            if (manager.isIdTaken(id)) {
                System.out.println("ID " + id + " is already registered. Please use a different ID.");
            } else {
                return id;
            }
        }
    }

    private static String readName() {
        while (true) {
            System.out.print("Enter patient name: ");
            String name = scanner.nextLine().trim();
            if (Patient.isValidName(name)) {
                return name;
            }
            System.out.println("Name must start with a letter, use only letters, spaces, . ' - "
                    + "and be at most " + Patient.MAX_NAME_LENGTH + " characters.");
        }
    }

    private static String readIllness() {
        while (true) {
            System.out.print("Enter illness: ");
            String illness = scanner.nextLine().trim();
            if (Patient.isValidIllness(illness)) {
                return illness;
            }
            System.out.println("Illness cannot be empty and must be at most "
                    + Patient.MAX_ILLNESS_LENGTH + " characters.");
        }
    }

    private static Severity readSeverity() {
        System.out.println("Severity levels:");
        System.out.println("  1. Critical  (treated first)");
        System.out.println("  2. Serious");
        System.out.println("  3. Moderate");
        int choice = readInt("Choose severity (1-3): ", 1, 3);

        if (choice == 1) {
            return Severity.CRITICAL;
        } else if (choice == 2) {
            return Severity.SERIOUS;
        } else {
            return Severity.MODERATE;
        }
    }
}
