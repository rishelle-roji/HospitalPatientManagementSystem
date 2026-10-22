// Web demo of the Java project: same rules, same data structures.
// Queue = array used FIFO | PriorityQueue = binary min-heap + comparator | Set = unique IDs
const LEVEL = { Critical: 1, Serious: 2, Moderate: 3 };

// Same rule as EmergencyComparator in Java: severity first, then arrival order.
const compareEmergency = (a, b) => (a.level - b.level) || (a.arrival - b.arrival);

class PriorityQueue {
  constructor(compare) { this.items = []; this.compare = compare; }
  get size() { return this.items.length; }
  add(item) {                                   // add at end, bubble UP
    const a = this.items; a.push(item);
    let i = a.length - 1;
    while (i > 0) {
      const p = (i - 1) >> 1;
      if (this.compare(a[i], a[p]) >= 0) break;
      [a[i], a[p]] = [a[p], a[i]]; i = p;
    }
  }
  poll() {                                      // take root, move last to root, sink DOWN
    const a = this.items;
    if (!a.length) return null;
    const top = a[0], last = a.pop();
    if (a.length) {
      a[0] = last;
      let i = 0;
      for (;;) {
        const l = 2 * i + 1, r = l + 1; let m = i;
        if (l < a.length && this.compare(a[l], a[m]) < 0) m = l;
        if (r < a.length && this.compare(a[r], a[m]) < 0) m = r;
        if (m === i) break;
        [a[i], a[m]] = [a[m], a[i]]; i = m;
      }
    }
    return top;
  }
  sorted() { return [...this.items].sort(this.compare); }   // a heap is not fully sorted
}

const regularQueue = [];
const emergencyQueue = new PriorityQueue(compareEmergency);
const ids = new Set();
const doctors = { regular: { name: "Dr. Meera Nair", treated: 0 }, emergency: { name: "Dr. Arjun Menon", treated: 0 } };
const logEntries = [];
let nextArrival = 1, type = "regular", severity = "Critical", lastCalled = "None yet";

const $ = (id) => document.getElementById(id);
function el(tag, cls, text) {
  const e = document.createElement(tag);
  if (cls) e.className = cls;
  if (text !== undefined) e.textContent = text;
  return e;
}
function say(text, kind) { $("message").textContent = text; $("message").className = kind; }
function log(op, text) { logEntries.unshift([op, text]); if (logEntries.length > 8) logEntries.pop(); }

function validate(id, name, age, illness) {
  if (!Number.isInteger(id) || id <= 0) return "Patient ID must be a positive whole number.";
  if (ids.has(id)) return "A patient with ID " + id + " is already registered.";
  if (!/^[A-Za-z][A-Za-z .'-]{0,24}$/.test(name)) return "Name must start with a letter, use letters, spaces, . ' - only, max 25 characters.";
  if (!Number.isInteger(age) || age < 0 || age > 120) return "Age must be a whole number from 0 to 120.";
  if (!illness || illness.length > 30) return "Illness must be 1 to 30 characters.";
  return null;
}

function register(e) {
  e.preventDefault();
  const id = Number($("id").value), name = $("name").value.trim();
  const age = Number($("age").value), illness = $("illness").value.trim();
  const error = validate(id, name, age, illness);
  if (error) return say(error, "error");
  const p = { id, name, age, illness, arrival: nextArrival++ };
  if (type === "emergency") {
    p.severity = severity; p.level = LEVEL[severity];
    emergencyQueue.add(p);
    log("add()", name + " joined the emergency queue as " + severity);
  } else {
    regularQueue.push(p);
    log("add()", name + " joined the regular queue");
  }
  ids.add(id);
  $("patient-form").reset();
  say("Registered " + name + " (ID " + id + "). Welcome!", "ok");
  render();
}

function call(kind) {
  const empty = kind === "regular" ? !regularQueue.length : !emergencyQueue.size;
  if (empty) return say("No " + kind + " patients are waiting right now.", "error");
  const p = kind === "regular" ? regularQueue.shift() : emergencyQueue.poll();
  doctors[kind].treated++;
  lastCalled = p.name;
  log("poll()", p.name + " called to " + doctors[kind].name + (p.severity ? " (" + p.severity + ")" : ""));
  say("Now treating " + p.name + " (" + p.illness + ").", "ok");
  render();
}

function chip(p, tag, tagClass, showSev) {
  const c = el("div", "chip" + (showSev ? " " + p.severity : ""));
  if (tag) c.appendChild(el("span", "tag " + tagClass, tag));
  c.appendChild(document.createTextNode(p.name));
  c.appendChild(el("small", "", "ID " + p.id + " | " + p.age + " yrs | " + p.illness));
  return c;
}

function render() {
  $("s-regular").textContent = regularQueue.length;
  $("s-emergency").textContent = emergencyQueue.size;
  $("s-treated").textContent = doctors.regular.treated + doctors.emergency.treated;
  $("s-last").textContent = lastCalled;
  for (const k of ["regular", "emergency"])
    $(k + "-doctor").textContent = doctors[k].name + " | treated so far: " + doctors[k].treated;

  const reg = $("regular-list"); reg.textContent = "";
  if (!regularQueue.length) reg.appendChild(el("span", "empty", "No one is waiting. Enjoy the calm."));
  regularQueue.forEach((p, i) => {
    const last = i === regularQueue.length - 1;
    reg.appendChild(chip(p, i === 0 ? "FRONT" : last ? "REAR" : "", i === 0 ? "" : "rear"));
    if (i === 0 && last) reg.lastChild.firstChild.textContent = "FRONT / REAR";
  });

  const order = emergencyQueue.sorted(), lanes = $("lanes"); lanes.textContent = "";
  for (const s of Object.keys(LEVEL)) {
    const lane = el("div", "lane " + s), chips = el("div", "chips");
    lane.appendChild(el("b", "", s));
    const group = order.filter((p) => p.severity === s);
    if (!group.length) chips.appendChild(el("span", "empty", "No one waiting"));
    group.forEach((p) => chips.appendChild(chip(p, p === order[0] ? "NEXT" : "", "next")));
    lane.appendChild(chips); lanes.appendChild(lane);
  }

  const heap = $("heap-strip"); heap.textContent = "";
  if (!emergencyQueue.size) heap.appendChild(el("span", "empty", "The heap is empty."));
  emergencyQueue.items.forEach((p, i) => {
    const c = el("div", "chip " + p.severity, p.name);
    c.appendChild(el("small", "", "index " + i + " | " + p.severity));
    heap.appendChild(c);
  });

  const ul = $("log"); ul.textContent = "";
  if (!logEntries.length) ul.appendChild(el("li", "empty", "Nothing yet. Register a patient to begin."));
  logEntries.forEach(([op, text]) => {
    const li = el("li"); li.appendChild(el("code", "", op)); li.appendChild(document.createTextNode(" " + text)); ul.appendChild(li);
  });
}

function showView() {
  const name = location.hash.slice(1) || "dashboard";
  const target = document.getElementById(name) ? name : "dashboard";
  document.querySelectorAll(".view").forEach((v) => v.classList.toggle("active", v.id === target));
  document.querySelectorAll("[data-nav]").forEach((a) => a.classList.toggle("active", a.dataset.nav === target));
}

document.querySelectorAll(".seg button").forEach((b) => b.addEventListener("click", () => {
  type = b.dataset.type;
  document.querySelectorAll(".seg button").forEach((x) => x.classList.toggle("on", x === b));
  $("severity-row").hidden = type !== "emergency";
}));
document.querySelectorAll(".pill").forEach((b) => b.addEventListener("click", () => {
  severity = b.dataset.sev;
  document.querySelectorAll(".pill").forEach((x) => x.classList.toggle("on", x === b));
}));
$("patient-form").addEventListener("submit", register);
$("call-regular").addEventListener("click", () => call("regular"));
$("call-emergency").addEventListener("click", () => call("emergency"));
window.addEventListener("hashchange", showView);
showView();
render();
