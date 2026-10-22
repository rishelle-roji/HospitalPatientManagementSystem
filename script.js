// Web demo of the Java project: same rules, same data structures.
// Queue  -> a plain array used FIFO (push at rear, shift from front)
// PriorityQueue -> a binary min-heap with a custom comparator
// Set    -> prevents duplicate patient IDs

const SEVERITY_LEVEL = { Critical: 1, Serious: 2, Moderate: 3 };

// Same rule as EmergencyComparator in Java: severity first, then arrival order.
function compareEmergency(a, b) {
  if (a.level !== b.level) return a.level - b.level;
  return a.arrival - b.arrival;
}

class PriorityQueue {
  constructor(compare) { this.items = []; this.compare = compare; }
  get size() { return this.items.length; }

  add(item) {                       // add at the end, then bubble UP
    const a = this.items;
    a.push(item);
    let i = a.length - 1;
    while (i > 0) {
      const parent = (i - 1) >> 1;
      if (this.compare(a[i], a[parent]) >= 0) break;
      [a[i], a[parent]] = [a[parent], a[i]];
      i = parent;
    }
  }

  poll() {                          // take the root, move last to root, sink DOWN
    const a = this.items;
    if (a.length === 0) return null;
    const top = a[0];
    const last = a.pop();
    if (a.length > 0) {
      a[0] = last;
      let i = 0;
      while (true) {
        const left = 2 * i + 1, right = left + 1;
        let smallest = i;
        if (left < a.length && this.compare(a[left], a[smallest]) < 0) smallest = left;
        if (right < a.length && this.compare(a[right], a[smallest]) < 0) smallest = right;
        if (smallest === i) break;
        [a[i], a[smallest]] = [a[smallest], a[i]];
        i = smallest;
      }
    }
    return top;
  }

  // A heap is not stored in sorted order, so sort a copy for display.
  toSortedArray() { return [...this.items].sort(this.compare); }
}

const regularQueue = [];
const emergencyQueue = new PriorityQueue(compareEmergency);
const registeredIds = new Set();
const doctors = {
  regular: { name: "Dr. Meera Nair (General Physician)", treated: 0 },
  emergency: { name: "Dr. Arjun Menon (Emergency Medicine)", treated: 0 }
};
let nextArrival = 1;

const $ = (id) => document.getElementById(id);

function showMessage(text, kind) {
  const el = $("message");
  el.textContent = text;
  el.className = kind;
}

function validate(id, name, age, illness) {
  if (!Number.isInteger(id) || id <= 0) return "Patient ID must be a positive whole number.";
  if (registeredIds.has(id)) return "A patient with ID " + id + " is already registered.";
  if (!/^[A-Za-z][A-Za-z .'-]{0,24}$/.test(name)) return "Name must start with a letter, use only letters, spaces, . ' - and be at most 25 characters.";
  if (!Number.isInteger(age) || age < 0 || age > 120) return "Age must be a whole number between 0 and 120.";
  if (illness.length === 0 || illness.length > 30) return "Illness must be 1 to 30 characters.";
  return null;
}

function register(event) {
  event.preventDefault();
  const id = Number($("id").value);
  const name = $("name").value.trim();
  const age = Number($("age").value);
  const illness = $("illness").value.trim();
  const isEmergency = $("type").value === "emergency";

  const error = validate(id, name, age, illness);
  if (error) return showMessage(error, "error");

  const patient = { id, name, age, illness, arrival: nextArrival };
  if (isEmergency) {
    patient.severity = $("severity").value;
    patient.level = SEVERITY_LEVEL[patient.severity];
    emergencyQueue.add(patient);
  } else {
    regularQueue.push(patient);
  }
  registeredIds.add(id);
  nextArrival++;

  $("patient-form").reset();
  $("severity").hidden = true;
  showMessage("Registered " + name + " (ID " + id + ").", "ok");
  render();
}

function callPatient(kind) {
  const queue = kind === "regular" ? regularQueue : emergencyQueue;
  const size = kind === "regular" ? regularQueue.length : emergencyQueue.size;
  if (size === 0) return showMessage("There are no " + kind + " patients waiting.", "error");

  const patient = kind === "regular" ? queue.shift() : queue.poll();
  doctors[kind].treated++;
  showMessage("Now treating: " + patient.name + " (ID " + patient.id + ", " + patient.illness + ")", "ok");
  render();
}

function fillList(listId, patients, showSeverity) {
  const list = $(listId);
  list.textContent = "";
  if (patients.length === 0) {
    const li = document.createElement("li");
    li.className = "empty";
    li.textContent = "The queue is empty.";
    list.appendChild(li);
    return;
  }
  for (const p of patients) {
    const li = document.createElement("li");
    li.textContent = p.name + " (ID " + p.id + ")";
    if (showSeverity) {
      const badge = document.createElement("span");
      badge.className = "badge " + p.severity;
      badge.textContent = p.severity;
      li.appendChild(badge);
    }
    const detail = document.createElement("small");
    detail.textContent = "Age " + p.age + ", " + p.illness;
    li.appendChild(detail);
    list.appendChild(li);
  }
}

function render() {
  fillList("regular-list", regularQueue, false);
  fillList("emergency-list", emergencyQueue.toSortedArray(), true);
  for (const kind of ["regular", "emergency"]) {
    $(kind + "-doctor").textContent = doctors[kind].name + " | treated: " + doctors[kind].treated;
  }
}

$("patient-form").addEventListener("submit", register);
$("type").addEventListener("change", () => { $("severity").hidden = $("type").value !== "emergency"; });
$("call-regular").addEventListener("click", () => callPatient("regular"));
$("call-emergency").addEventListener("click", () => callPatient("emergency"));
render();
