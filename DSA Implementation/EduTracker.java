import java.util.*;

/*
EduTracker Console Application

DSA CO Mapping:
CO1 - Linear search and sorting for module search and ranking
CO2 - Array and manual linked list for module storage and recent activity tracking
CO3 - queue, stack, and priority queue for workflow, undo, and goal management
CO4 - hash table for fast lookup and updates
CO5 - Practical application of data structures in learning analytics
CO6 - Complete menu-driven Java application integrating DSA concepts
*/

public class EduTracker {

    // CO4: Module statistics stored for efficient lookup and updates using hash table
    static class ModuleStats {
        String name;
        int opens;
        int clicks;
        int timeSpent;
        int scrollDepth;
        boolean bookmarked;
        boolean completed;

        ModuleStats(String name) {
            this.name = name;
            this.opens = 0;
            this.clicks = 0;
            this.timeSpent = 0;
            this.scrollDepth = 0;
            this.bookmarked = false;
            this.completed = false;
        }

        // CO1: Score computation supports comparison and ranking of module activity
        int getScore() {
            return opens + clicks + (bookmarked ? 3 : 0) + (completed ? 4 : 0);
        }
    }

    // CO3: Event objects are processed using queue-based workflow handling
    static class Event {
        String type;
        String topic;
        String extra;

        Event(String type, String topic, String extra) {
            this.type = type;
            this.topic = topic;
            this.extra = extra;
        }
    }

    // CO3: Action objects are stored in a stack for undo management
    static class Action {
        String type;
        String topic;
        boolean previousBool;
        Goal goalPayload;
        Goal[] goalsSnapshot;
        int goalsSnapshotSize;

        Action(String type, String topic, boolean previousBool) {
            this.type = type;
            this.topic = topic;
            this.previousBool = previousBool;
        }

        Action(String type, Goal goalPayload) {
            this.type = type;
            this.goalPayload = goalPayload;
        }

        Action(String type, Goal[] goalsSnapshot, int goalsSnapshotSize) {
            this.type = type;
            this.goalsSnapshot = goalsSnapshot;
            this.goalsSnapshotSize = goalsSnapshotSize;
        }

        Action(String type) {
            this.type = type;
        }
    }

    // CO3: Goal objects are used inside a manual priority queue for prioritized study tasks
    static class Goal {
        String text;
        int priority; // 1 high, 2 medium, 3 low

        Goal(String text, int priority) {
            this.text = text;
            this.priority = priority;
        }
    }

    // CO2: Array used to store module names
    static final String[] MODULE_NAMES = {
        "HTML Basics",
        "CSS Styling",
        "JavaScript",
        "DOM Interactivity",
        "Algorithms",
        "Data Structures"
    };

    // =========================
    // CO4: Hash Table
    // =========================
    static class ModuleHashTable {
        static class Entry {
            String key;
            ModuleStats value;
            Entry next;

            Entry(String key, ModuleStats value) {
                this.key = key;
                this.value = value;
            }
        }

        Entry[] table;
        int size;

        ModuleHashTable(int capacity) {
            table = new Entry[capacity];
            size = 0;
        }

        int hash(String key) {
            int h = 0;
            for (int i = 0; i < key.length(); i++) {
                h = (31 * h + key.charAt(i)) % table.length;
            }
            return Math.abs(h) % table.length;
        }

        void put(String key, ModuleStats value) {
            int index = hash(key);
            Entry head = table[index];

            Entry temp = head;
            while (temp != null) {
                if (temp.key.equals(key)) {
                    temp.value = value;
                    return;
                }
                temp = temp.next;
            }

            Entry newEntry = new Entry(key, value);
            newEntry.next = head;
            table[index] = newEntry;
            size++;
        }

        ModuleStats get(String key) {
            int index = hash(key);
            Entry temp = table[index];
            while (temp != null) {
                if (temp.key.equals(key)) {
                    return temp.value;
                }
                temp = temp.next;
            }
            return null;
        }

        boolean containsKey(String key) {
            return get(key) != null;
        }

        void clear() {
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
            size = 0;
        }

        int size() {
            return size;
        }

        ModuleStats[] valuesArray() {
            ModuleStats[] arr = new ModuleStats[size];
            int idx = 0;
            for (int i = 0; i < table.length; i++) {
                Entry temp = table[i];
                while (temp != null) {
                    arr[idx++] = temp.value;
                    temp = temp.next;
                }
            }
            return arr;
        }
    }

    // =========================
    // CO3: Queue
    // =========================
    static class EventQueue {
        static class Node {
            Event data;
            Node next;

            Node(Event data) {
                this.data = data;
            }
        }

        Node front;
        Node rear;
        int size;

        void offer(Event e) {
            Node node = new Node(e);
            if (rear == null) {
                front = rear = node;
            } else {
                rear.next = node;
                rear = node;
            }
            size++;
        }

        Event poll() {
            if (front == null) return null;
            Event val = front.data;
            front = front.next;
            if (front == null) rear = null;
            size--;
            return val;
        }

        boolean isEmpty() {
            return front == null;
        }

        int size() {
            return size;
        }

        void clear() {
            front = rear = null;
            size = 0;
        }
    }

    // =========================
    // CO3: Stack
    // =========================
    static class ActionStack {
        static class Node {
            Action data;
            Node next;

            Node(Action data) {
                this.data = data;
            }
        }

        Node top;
        int size;

        void push(Action a) {
            Node node = new Node(a);
            node.next = top;
            top = node;
            size++;
        }

        Action pop() {
            if (top == null) return null;
            Action val = top.data;
            top = top.next;
            size--;
            return val;
        }

        boolean isEmpty() {
            return top == null;
        }

        void clear() {
            top = null;
            size = 0;
        }
    }

    // =========================
    // CO2: Linked List
    // =========================
    static class ActivityLinkedList {
        static class Node {
            String data;
            Node next;

            Node(String data) {
                this.data = data;
            }
        }

        Node head;
        int size;

        void addFirst(String message) {
            Node node = new Node(message);
            node.next = head;
            head = node;
            size++;
        }

        void removeLast() {
            if (head == null) return;

            if (head.next == null) {
                head = null;
                size = 0;
                return;
            }

            Node temp = head;
            while (temp.next.next != null) {
                temp = temp.next;
            }
            temp.next = null;
            size--;
        }

        boolean isEmpty() {
            return head == null;
        }

        int size() {
            return size;
        }

        void clear() {
            head = null;
            size = 0;
        }

        void printAll() {
            Node temp = head;
            while (temp != null) {
                System.out.println("- " + temp.data);
                temp = temp.next;
            }
        }
    }

    // =========================
    // CO3: Priority Queue (Min-Heap)
    // =========================
    static class GoalPriorityQueue {
        Goal[] heap;
        int size;

        GoalPriorityQueue(int capacity) {
            heap = new Goal[capacity];
            size = 0;
        }

        void ensureCapacity() {
            if (size < heap.length) return;
            Goal[] newHeap = new Goal[heap.length * 2];
            for (int i = 0; i < heap.length; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }

        boolean higherPriority(Goal a, Goal b) {
            return a.priority < b.priority;
        }

        void swap(int i, int j) {
            Goal temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }

        void offer(Goal g) {
            ensureCapacity();
            heap[size] = g;
            int i = size;
            size++;

            while (i > 0) {
                int parent = (i - 1) / 2;
                if (higherPriority(heap[i], heap[parent])) {
                    swap(i, parent);
                    i = parent;
                } else {
                    break;
                }
            }
        }

        Goal poll() {
            if (size == 0) return null;

            Goal root = heap[0];
            heap[0] = heap[size - 1];
            heap[size - 1] = null;
            size--;

            int i = 0;
            while (true) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                int smallest = i;

                if (left < size && higherPriority(heap[left], heap[smallest])) {
                    smallest = left;
                }
                if (right < size && higherPriority(heap[right], heap[smallest])) {
                    smallest = right;
                }

                if (smallest != i) {
                    swap(i, smallest);
                    i = smallest;
                } else {
                    break;
                }
            }

            return root;
        }

        boolean isEmpty() {
            return size == 0;
        }

        int size() {
            return size;
        }

        void clear() {
            for (int i = 0; i < size; i++) {
                heap[i] = null;
            }
            size = 0;
        }

        Goal[] snapshot() {
            Goal[] copy = new Goal[size];
            for (int i = 0; i < size; i++) {
                copy[i] = new Goal(heap[i].text, heap[i].priority);
            }
            return copy;
        }

        GoalPriorityQueue copyQueue() {
            GoalPriorityQueue temp = new GoalPriorityQueue(Math.max(10, size + 5));
            for (int i = 0; i < size; i++) {
                temp.offer(new Goal(heap[i].text, heap[i].priority));
            }
            return temp;
        }
    }

    // CO4: Hash table used for module lookup and statistics updates
    static ModuleHashTable modules = new ModuleHashTable(23);

    // CO3: Queue used for buffering user interaction events
    static EventQueue eventQueue = new EventQueue();

    // CO3: Stack used to implement undo functionality
    static ActionStack undoStack = new ActionStack();

    // CO2: Linked list used to maintain recent activity timeline
    static ActivityLinkedList recentActivity = new ActivityLinkedList();

    // CO3: Priority queue used for ordered study goal management
    static GoalPriorityQueue goals = new GoalPriorityQueue(20);

    static int totalClicks = 0;
    static final int MAX_ACTIVITY = 10;

    // CO2: Linked list insertion and deletion maintain the recent activity list
    static void addActivity(String message) {
        recentActivity.addFirst(message);
        if (recentActivity.size() > MAX_ACTIVITY) {
            recentActivity.removeLast();
        }
    }

    // CO3: Queue insertion stores events before processing
    static void queueEvent(String type, String topic, String extra) {
        eventQueue.offer(new Event(type, topic, extra));
    }

    // CO5: Practical event-processing workflow used for analytics management
    static void processQueue() {
        while (!eventQueue.isEmpty()) {
            Event e = eventQueue.poll();

            if (modules.containsKey(e.topic)) {
                ModuleStats m = modules.get(e.topic);

                switch (e.type) {
                    case "open":
                        m.opens++;
                        addActivity("Opened " + e.topic);
                        break;
                    case "click":
                        totalClicks++;
                        m.clicks++;
                        break;
                    case "bookmark":
                        addActivity("Bookmarked " + e.topic);
                        break;
                    case "complete":
                        addActivity("Completed " + e.topic);
                        break;
                    case "time":
                        try {
                            int t = Integer.parseInt(e.extra);
                            m.timeSpent += t;
                        } catch (Exception ignored) {}
                        break;
                    case "scroll":
                        try {
                            int s = Integer.parseInt(e.extra);
                            if (s > m.scrollDepth) {
                                m.scrollDepth = s;
                            }
                        } catch (Exception ignored) {}
                        break;
                    case "note":
                        addActivity("Saved note in " + e.topic + ": " + e.extra);
                        break;
                }
            } else {
                switch (e.type) {
                    case "goal-add":
                        addActivity("Added goal: " + e.extra);
                        break;
                    case "goal-pop":
                        addActivity("Popped goal: " + e.extra);
                        break;
                    case "goal-clear":
                        addActivity("Cleared all goals");
                        break;
                }
            }
        }
    }

    // CO4: Hash table traversal used for aggregate bookmark computation
    static int getBookmarkCount() {
        int count = 0;
        ModuleStats[] values = modules.valuesArray();
        for (int i = 0; i < values.length; i++) {
            if (values[i].bookmarked) count++;
        }
        return count;
    }

    // CO4: Hash table traversal used for aggregate completion computation
    static int getCompletedCount() {
        int count = 0;
        ModuleStats[] values = modules.valuesArray();
        for (int i = 0; i < values.length; i++) {
            if (values[i].completed) count++;
        }
        return count;
    }

    // CO1: Aggregate computation used to evaluate module engagement levels
    static int getAverageScroll() {
        ModuleStats[] values = modules.valuesArray();
        if (values.length == 0) return 0;

        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i].scrollDepth;
        }
        return sum / values.length;
    }

    // CO5: Practical aggregation of total time across all modules
    static int getTotalTime() {
        int sum = 0;
        ModuleStats[] values = modules.valuesArray();
        for (int i = 0; i < values.length; i++) {
            sum += values[i].timeSpent;
        }
        return sum;
    }

    // CO1: Sorting used to rank top topics based on engagement score
    static ModuleStats[] getTopTopics() {
        ModuleStats[] list = modules.valuesArray();

        for (int i = 0; i < list.length - 1; i++) {
            for (int j = 0; j < list.length - i - 1; j++) {
                boolean shouldSwap = false;

                if (list[j].getScore() < list[j + 1].getScore()) {
                    shouldSwap = true;
                } else if (list[j].getScore() == list[j + 1].getScore()
                        && list[j].timeSpent < list[j + 1].timeSpent) {
                    shouldSwap = true;
                }

                if (shouldSwap) {
                    ModuleStats temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }

        return list;
    }

    // CO6: Traversal and display of module state within the application
    static void showModules() {
        System.out.println("\n--- MODULES ---");
        int i = 1;
        for (String name : MODULE_NAMES) {
            ModuleStats m = modules.get(name);
            System.out.println(i + ". " + name
                    + " | Opens: " + m.opens
                    + " | Bookmarked: " + (m.bookmarked ? "Yes" : "No")
                    + " | Completed: " + (m.completed ? "Yes" : "No"));
            i++;
        }
    }

    // CO5: Practical analytics reporting using multiple data structures
    static void showAnalytics() {
        processQueue();

        System.out.println("\n=== LIVE ANALYTICS ===");
        System.out.println("Total Clicks: " + totalClicks);
        System.out.println("Average Scroll Depth: " + getAverageScroll() + "%");
        System.out.println("Total Time Spent: " + getTotalTime() + "s");
        System.out.println("Queue Size: " + eventQueue.size());
        System.out.println("Bookmarks: " + getBookmarkCount());
        System.out.println("Completed Modules: " + getCompletedCount());
        System.out.println("Total Modules: " + modules.size());

        ModuleStats[] top = getTopTopics();
        System.out.println("\nTop Topics:");
        boolean any = false;
        for (int i = 0; i < Math.min(5, top.length); i++) {
            ModuleStats m = top[i];
            if (m.getScore() > 0 || m.timeSpent > 0) {
                any = true;
                System.out.println((i + 1) + ". " + m.name
                        + " | Score: " + m.getScore()
                        + " | Time: " + m.timeSpent + "s");
            }
        }
        if (!any) {
            System.out.println("No topic activity yet.");
        }

        System.out.println("\nRecent Activity:");
        if (recentActivity.isEmpty()) {
            System.out.println("No recent activity yet.");
        } else {
            recentActivity.printAll();
        }
    }

    // CO6: Module interaction workflow integrates multiple data structures in one application flow
    static void openModule(Scanner sc) {
        showModules();
        System.out.print("\nEnter module number to open: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice < 1 || choice > MODULE_NAMES.length) {
            System.out.println("Invalid module choice.");
            return;
        }

        String moduleName = MODULE_NAMES[choice - 1];
        queueEvent("open", moduleName, "");
        queueEvent("click", moduleName, "");
        processQueue();

        while (true) {
            ModuleStats m = modules.get(moduleName);
            System.out.println("\n=== " + moduleName + " ===");
            System.out.println("1. Toggle Bookmark");
            System.out.println("2. Toggle Complete");
            System.out.println("3. Add Time Spent");
            System.out.println("4. Update Scroll Depth");
            System.out.println("5. Save Note");
            System.out.println("6. Simulate Click");
            System.out.println("7. Show Module Stats");
            System.out.println("8. Back to Main Menu");
            System.out.print("Choose: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1: {
                    boolean prev = m.bookmarked;
                    m.bookmarked = !m.bookmarked;
                    undoStack.push(new Action("bookmark-toggle", moduleName, prev));
                    queueEvent("bookmark", moduleName, "");
                    processQueue();
                    System.out.println("Bookmark status changed.");
                    break;
                }
                case 2: {
                    boolean prev = m.completed;
                    m.completed = !m.completed;
                    undoStack.push(new Action("complete-toggle", moduleName, prev));
                    queueEvent("complete", moduleName, "");
                    processQueue();
                    System.out.println("Complete status changed.");
                    break;
                }
                case 3: {
                    System.out.print("Enter seconds spent: ");
                    int seconds = sc.nextInt();
                    sc.nextLine();
                    queueEvent("time", moduleName, String.valueOf(seconds));
                    processQueue();
                    System.out.println("Time updated.");
                    break;
                }
                case 4: {
                    System.out.print("Enter scroll depth (0-100): ");
                    int scroll = sc.nextInt();
                    sc.nextLine();
                    if (scroll < 0) scroll = 0;
                    if (scroll > 100) scroll = 100;
                    queueEvent("scroll", moduleName, String.valueOf(scroll));
                    processQueue();
                    System.out.println("Scroll depth updated.");
                    break;
                }
                case 5: {
                    System.out.print("Enter note: ");
                    String note = sc.nextLine();
                    queueEvent("note", moduleName, note);
                    processQueue();
                    System.out.println("Note saved.");
                    break;
                }
                case 6: {
                    queueEvent("click", moduleName, "");
                    processQueue();
                    System.out.println("Click recorded.");
                    break;
                }
                case 7: {
                    System.out.println("\nModule Stats:");
                    System.out.println("Name: " + m.name);
                    System.out.println("Opens: " + m.opens);
                    System.out.println("Clicks: " + m.clicks);
                    System.out.println("Time Spent: " + m.timeSpent + "s");
                    System.out.println("Scroll Depth: " + m.scrollDepth + "%");
                    System.out.println("Bookmarked: " + (m.bookmarked ? "Yes" : "No"));
                    System.out.println("Completed: " + (m.completed ? "Yes" : "No"));
                    break;
                }
                case 8:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // CO3: Goal insertion uses manual priority queue ordering for prioritized study management
    static void addGoal(Scanner sc) {
        System.out.print("Enter goal text: ");
        String text = sc.nextLine();

        System.out.print("Enter priority (1=High, 2=Medium, 3=Low): ");
        int priority = sc.nextInt();
        sc.nextLine();

        if (priority < 1 || priority > 3) {
            System.out.println("Invalid priority.");
            return;
        }

        Goal g = new Goal(text, priority);
        goals.offer(g);
        undoStack.push(new Action("goal-add"));
        queueEvent("goal-add", "Goals", text);
        processQueue();
        System.out.println("Goal added.");
    }

    // CO3: Manual priority queue removal returns the highest-priority study goal first
    static void popTopGoal() {
        if (goals.isEmpty()) {
            System.out.println("No goals to pop.");
            return;
        }

        Goal removed = goals.poll();
        undoStack.push(new Action("goal-pop", removed));
        queueEvent("goal-pop", "Goals", removed.text);
        processQueue();
        System.out.println("Popped top goal: " + removed.text);
    }

    // CO3: Manual priority queue state can be cleared and restored through stack-supported undo
    static void clearGoals() {
        if (goals.isEmpty()) {
            System.out.println("Goals already empty.");
            return;
        }

        Goal[] snapshot = goals.snapshot();
        int snapshotSize = goals.size();
        goals.clear();
        undoStack.push(new Action("goal-clear", snapshot, snapshotSize));
        queueEvent("goal-clear", "Goals", "");
        processQueue();
        System.out.println("All goals cleared.");
    }

    // CO3: Manual priority queue traversal is demonstrated by ordered display of goals
    static void showGoals() {
        System.out.println("\n--- STUDY GOALS ---");
        if (goals.isEmpty()) {
            System.out.println("No goals added yet.");
            return;
        }

        GoalPriorityQueue temp = goals.copyQueue();
        int i = 1;
        while (!temp.isEmpty()) {
            Goal g = temp.poll();
            String label = (g.priority == 1) ? "High" : (g.priority == 2) ? "Medium" : "Low";
            System.out.println(i + ". " + g.text + " (" + label + ")");
            i++;
        }
    }

    // CO1: Linear search is used to find matching modules from user input
    static void searchModules(Scanner sc) {
        System.out.print("Enter search text: ");
        String query = sc.nextLine().toLowerCase();

        System.out.println("\nSearch Results:");
        boolean found = false;
        for (String module : MODULE_NAMES) {
            if (module.toLowerCase().contains(query)) {
                System.out.println("- " + module);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching module found.");
        }
    }

    // CO3: Manual stack pop operation restores previous application state through undo
    static void undoLastAction() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        Action action = undoStack.pop();

        switch (action.type) {
            case "bookmark-toggle": {
                ModuleStats m = modules.get(action.topic);
                m.bookmarked = action.previousBool;
                addActivity("Undo bookmark change for " + action.topic);
                break;
            }
            case "complete-toggle": {
                ModuleStats m = modules.get(action.topic);
                m.completed = action.previousBool;
                addActivity("Undo completion change for " + action.topic);
                break;
            }
            case "goal-add": {
                if (!goals.isEmpty()) {
                    goals.poll();
                    addActivity("Undo goal add");
                }
                break;
            }
            case "goal-pop": {
                if (action.goalPayload != null) {
                    goals.offer(action.goalPayload);
                    addActivity("Undo goal pop");
                }
                break;
            }
            case "goal-clear": {
                if (action.goalsSnapshot != null) {
                    goals.clear();
                    for (int i = 0; i < action.goalsSnapshotSize; i++) {
                        goals.offer(action.goalsSnapshot[i]);
                    }
                    addActivity("Undo clear goals");
                }
                break;
            }
            default:
                System.out.println("Nothing to undo for this action.");
                return;
        }

        System.out.println("Last action undone.");
    }

    // CO6: Complete application reset demonstrates overall program control
    static void resetAll() {
        totalClicks = 0;
        eventQueue.clear();
        undoStack.clear();
        recentActivity.clear();
        goals.clear();

        modules.clear();
        for (String name : MODULE_NAMES) {
            modules.put(name, new ModuleStats(name));
        }

        System.out.println("All analytics and progress reset.");
    }

    // CO6: Main driver function integrates all data structures into a working application
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        for (String name : MODULE_NAMES) {
            modules.put(name, new ModuleStats(name));
        }

        while (true) {
            processQueue();

            System.out.println("\n==============================");
            System.out.println("        EDUTRACK CONSOLE      ");
            System.out.println("==============================");
            System.out.println("1. Show Modules");
            System.out.println("2. Open Module");
            System.out.println("3. Add Study Goal");
            System.out.println("4. Pop Top Goal");
            System.out.println("5. Clear Goals");
            System.out.println("6. Show Goals");
            System.out.println("7. Search Modules");
            System.out.println("8. Show Analytics");
            System.out.println("9. Undo Last Action");
            System.out.println("10. Reset All Data");
            System.out.println("11. Exit");
            System.out.print("Enter choice: ");

            int choice;
            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    showModules();
                    break;
                case 2:
                    openModule(sc);
                    break;
                case 3:
                    addGoal(sc);
                    break;
                case 4:
                    popTopGoal();
                    break;
                case 5:
                    clearGoals();
                    break;
                case 6:
                    showGoals();
                    break;
                case 7:
                    searchModules(sc);
                    break;
                case 8:
                    showAnalytics();
                    break;
                case 9:
                    undoLastAction();
                    break;
                case 10:
                    resetAll();
                    break;
                case 11:
                    System.out.println("Exiting EduTracker. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
