# Hospital Patient Management System

A console-based Java application that manages hospital patients using two fundamental data structures: a **Queue** for regular patients and a **Priority Queue** for emergency patients.

> Data Structures mini-project — Amal Jyothi College of Engineering, S3 CSE-C
> Author: Rishelle Roji

---

## Project Description

Hospitals struggle to manage patient flow when regular check-ups and emergency cases arrive at the same time. This project solves that scheduling problem with two queues:

- **Regular patients** are served in the order they arrive (**First-Come-First-Served**) using a standard `Queue`.
- **Emergency patients** are assigned a severity level (**Critical**, **Serious** or **Moderate**) and are served using a `PriorityQueue`, so the most severe case is treated first **regardless of arrival time**.

The program is menu-driven, uses only the standard Java library, and is built with object-oriented principles.

## Objectives

- Demonstrate the practical use of **Queue (FIFO)** and **Priority Queue (heap-based)** data structures.
- Apply Java OOP concepts: classes, objects, constructors, encapsulation, enums and custom exceptions.
- Show a custom `Comparator` that defines priority order and breaks ties fairly.
- Handle invalid input, duplicate IDs and empty queues gracefully.

## Data Structures Used

| Purpose | Java type | Behaviour |
|---|---|---|
| Regular patients | `Queue<Patient>` (implemented by `LinkedList`) | FIFO: `add()` at the rear, `poll()` from the front |
| Emergency patients | `PriorityQueue<Patient>` with a custom `Comparator` | `poll()` always returns the most urgent patient |
| Duplicate-ID check | `Set<Integer>` (implemented by `HashSet`) | Fast lookup of every ID used so far |

### Emergency priority rule

| Severity | Priority number | Treated |
|---|---|---|
| Critical | 1 | first |
| Serious | 2 | second |
| Moderate | 3 | last |

The comparator compares the priority number first (smaller = more urgent). If two patients have the **same severity**, it compares their **arrival number**, so the patient who arrived earlier is treated first. This tie-break is needed because a Java `PriorityQueue` is not stable on its own.

## Features

- Register regular patients (unique ID, name, age, illness)
- Register emergency patients with a severity level
- Display the regular queue (in FCFS order) and the emergency queue (in priority order)
- Call the next regular patient for treatment
- Call the highest-priority emergency patient for treatment
- Duplicate patient IDs are rejected (across both queues, and even after a patient has been treated)
- Input validation for IDs, names, ages, illness and menu choices
- Friendly messages for empty queues and invalid input; the program never crashes on bad input

## Project Structure

```
HospitalPatientManagementSystem/
├── src/
│   ├── Main.java                      # Menu-driven console interface
│   ├── HospitalQueueManager.java      # Owns the Queue, PriorityQueue and ID set
│   ├── Patient.java                   # Patient data and validation
│   ├── Doctor.java                    # Doctor who treats called patients
│   ├── Severity.java                  # Enum: CRITICAL, SERIOUS, MODERATE
│   ├── DuplicatePatientException.java # Custom exception for duplicate IDs
│   └── EmptyQueueException.java       # Custom exception for empty queues
├── index.html                         # Web demo page (optional extra)
├── style.css                          # Web demo styling
├── script.js                          # Web demo logic (same queue rules in JavaScript)
├── sample_output.txt                  # Full sample console session
├── README.md
└── .gitignore
```

| Class | Responsibility |
|---|---|
| `Main` | Shows the menu, reads and validates user input, prints results |
| `HospitalQueueManager` | Registration, duplicate checking, queue operations, priority comparator |
| `Patient` | Stores one patient's details; validates them; immutable (no setters) |
| `Doctor` | Treats patients and counts how many were treated |
| `Severity` | Defines the three severity levels and their priority numbers |
| `DuplicatePatientException` | Thrown when an ID is already registered |
| `EmptyQueueException` | Thrown when calling a patient from an empty queue |

## Prerequisites

- **Java Development Kit (JDK) 11 or newer** (the code uses only basic features, and was tested with JDK 21)
- A terminal (Terminal on macOS, Command Prompt/PowerShell on Windows)
- Optional: Visual Studio Code with the *Extension Pack for Java*

Check your installation:

```bash
javac -version
java -version
```

## Compilation and Execution

Open a terminal in the project folder (the one containing `src`), then:

```bash
# 1. Compile all source files into a folder called "out"
mkdir -p out
javac -d out src/*.java

# 2. Run the program
java -cp out Main
```

On Windows (Command Prompt) use:

```
mkdir out
javac -d out src\*.java
java -cp out Main
```

## Sample Interaction

Short excerpts from a real run (what the user types appears after each prompt). The complete, unedited session is in [`sample_output.txt`](sample_output.txt).

```
==========================================
   HOSPITAL PATIENT MANAGEMENT SYSTEM
   Queue and Priority Queue demonstration
==========================================

------------------ MENU ------------------
 Waiting -> Regular: 0 | Emergency: 0
------------------------------------------
 1. Register regular patient
 2. Register emergency patient
 3. Display regular queue
 4. Display emergency queue
 5. Call next regular patient
 6. Call highest-priority emergency patient
 7. Exit
------------------------------------------
Enter your choice (1-7): 1

[ ... Arun Kumar (101) and Bina Joseph (102) registered the same way ... ]

--- Register Regular Patient ---
Enter patient ID (positive number): 101
ID 101 is already registered. Please use a different ID.
Enter patient ID (positive number): 103
Enter patient name: Chandni R.
Enter age (0-120): abc
That is not a valid whole number. Try again.
Enter age (0-120): 22
Enter illness: Cold

Registered successfully. Position in regular queue: 3

[ ... emergency patients registered: 201 Moderate, 202 Critical, 203 Serious, 204 Critical ... ]

--- Emergency Queue (Most Severe First) ---
Pos   ID     Name                      Age  Illness                        Severity 
--------------------------------------------------------------------------------
1     202    Sam Varghese              52   Chest pain                     Critical 
2     204    Rahul Das                 61   Heart attack                   Critical 
3     203    Neha George               28   Fracture                       Serious  
4     201    Mona Thomas               40   Sprain                         Moderate 

[ ... regular and emergency patients called ... ]

Now treating (regular): ID 101, Arun Kumar, age 30, Fever
Doctor: Dr. Meera Nair (General Physician) | Patients treated so far: 1

[ ... menu shown again, user chooses 6 ... ]

Now treating (EMERGENCY): ID 202, Sam Varghese, age 52, Chest pain [Critical]
Doctor: Dr. Arjun Menon (Emergency Medicine) | Patients treated so far: 1

[ ... menu shown again, user chooses 6 ... ]

Now treating (EMERGENCY): ID 204, Rahul Das, age 61, Heart attack [Critical]
Doctor: Dr. Arjun Menon (Emergency Medicine) | Patients treated so far: 2
```

Notice that although Mona Thomas (Moderate) registered **first**, the two Critical patients are treated before her, and the two Critical patients are treated in arrival order (Sam, then Rahul).

## How It Works (short version)

1. `Main` reads input from the user and calls methods on `HospitalQueueManager`.
2. `registerRegularPatient()` creates a `Patient`, checks the ID against the `Set`, and calls `regularQueue.add(patient)`.
3. `registerEmergencyPatient()` does the same but adds to the `PriorityQueue`, which uses the comparator to place the patient correctly.
4. `callNextRegularPatient()` uses `regularQueue.poll()` (front of the line).
5. `callNextEmergencyPatient()` uses `emergencyQueue.poll()` (highest priority).
6. If a queue is empty, an `EmptyQueueException` is thrown and `Main` prints a friendly message.

## Web Demo (optional extra)

The **Java console application is the main project**. As a visual extra, the repository also contains a small web dashboard (`index.html`, `style.css`, `script.js`) with a sidebar (Dashboard, How it works, About). It shows the regular queue (FCFS), the emergency queue as three severity lanes (Critical > Serious > Moderate, then arrival order), an optional view of the internal heap array, and an operation log.

Because a browser cannot run Java, `script.js` re-implements the same logic: an array used as a queue, a hand-written binary min-heap with a comparator as the priority queue, and a `Set` for IDs.

To try it, open `index.html` in any browser. It can also be published with GitHub Pages (Settings > Pages > Deploy from a branch > `main` / root).

## Possible Future Improvements

These are intentionally **not** included, to keep the project focused:

- Saving patient data to a file
- More than one doctor per queue
- A fully featured graphical interface (only a small web demo is included)

## License

Created for educational purposes as a college mini-project.
