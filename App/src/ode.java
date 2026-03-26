/**
 * ode
 *
 * Book My Stay Application
 * Combines:
 * Use Case 1 - Welcome Message
 * Use Case 2 - Room Modeling
 * Use Case 3 - Centralized Inventory (HashMap)
 * Use Case 4 - Room Search & Availability Check
 * Use Case 5 - Booking Request Queue (FIFO)
 * Use Case 6 - Reservation Confirmation & Room Allocation
 *
 * Demonstrates OOP, centralized inventory, search, booking queue, and safe allocation.
 *
 * @author YourName
 * @version 6.0
 */

import java.util.*;

// ===============================
// Abstract Room Class
// ===============================
abstract class Room {
    private int numberOfBeds;
    private double size;
    private double price;

    public Room(int numberOfBeds, double size, double price) {
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.price = price;
    }

    public int getNumberOfBeds() { return numberOfBeds; }
    public double getSize() { return size; }
    public double getPrice() { return price; }
    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println("Room Type       : " + getRoomType());
        System.out.println("Beds            : " + getNumberOfBeds());
        System.out.println("Size (sq.ft)    : " + getSize());
        System.out.println("Price per night : $" + getPrice());
    }
}

// ===============================
// Room Types
// ===============================
class SingleRoom extends Room {
    public SingleRoom() { super(1, 150.0, 50.0); }
    @Override public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 250.0, 90.0); }
    @Override public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 400.0, 150.0); }
    @Override public String getRoomType() { return "Suite Room"; }
}

// ===============================
// Use Case 3: Inventory Class
// ===============================
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void displayInventory() {
        System.out.println("---- Centralized Room Inventory ----\n");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Room Type       : " + entry.getKey());
            System.out.println("Available Rooms : " + entry.getValue());
            System.out.println("---------------------------------------");
        }
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }
}

// ===============================
// Use Case 4: Search Service
// ===============================
class SearchService {
    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) { this.inventory = inventory; }

    public void searchAvailableRooms(Room[] rooms) {
        System.out.println("---- Available Rooms ----\n");

        Map<String, Integer> currentInventory = inventory.getInventory();
        boolean anyAvailable = false;

        for (Room room : rooms) {
            int available = currentInventory.getOrDefault(room.getRoomType(), 0);
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available Rooms : " + available);
                System.out.println("---------------------------------------");
                anyAvailable = true;
            }
        }

        if (!anyAvailable) {
            System.out.println("No rooms available at the moment.");
        }
    }
}

// ===============================
// Use Case 5: Booking Requests
// ===============================
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void displayReservation() {
        System.out.println("Guest Name      : " + guestName);
        System.out.println("Requested Room  : " + roomType);
    }
}

class BookingQueue {
    private Queue<Reservation> queue;

    public BookingQueue() { queue = new LinkedList<>(); }

    public void addRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Booking request added for guest: " + reservation.getGuestName());
    }

    public Reservation processNextRequest() { return queue.poll(); }

    public boolean isEmpty() { return queue.isEmpty(); }

    public void displayQueue() {
        System.out.println("\n---- Current Booking Requests (FIFO) ----\n");
        for (Reservation r : queue) {
            r.displayReservation();
            System.out.println("---------------------------------------");
        }
        if (queue.isEmpty()) {
            System.out.println("No booking requests in the queue.");
        }
    }
}

// ===============================
// Use Case 6: Booking Service / Room Allocation
// ===============================
class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> set of roomIDs
    private int roomCounter; // for generating unique IDs

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
        this.roomCounter = 100; // starting ID
    }

    public void confirmReservation(Reservation reservation) {
        String roomType = reservation.getRoomType();
        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            System.out.println("Sorry, " + roomType + " is fully booked for guest " + reservation.getGuestName());
            return;
        }

        // Generate unique room ID
        roomCounter++;
        String roomID = roomType.replaceAll(" ", "") + "-" + roomCounter;

        // Ensure allocatedRooms map contains set
        allocatedRooms.putIfAbsent(roomType, new HashSet<>());
        Set<String> allocatedSet = allocatedRooms.get(roomType);

        // Assign room ID and prevent duplicates
        while (allocatedSet.contains(roomID)) {
            roomCounter++;
            roomID = roomType.replaceAll(" ", "") + "-" + roomCounter;
        }
        allocatedSet.add(roomID);

        // Update inventory
        inventory.updateAvailability(roomType, available - 1);

        // Confirmation message
        System.out.println("\nReservation Confirmed!");
        System.out.println("Guest Name : " + reservation.getGuestName());
        System.out.println("Room Type  : " + roomType);
        System.out.println("Room ID    : " + roomID);
        System.out.println("Remaining " + roomType + " : " + inventory.getAvailability(roomType));
    }
}

// ===============================
// Main Class
// ===============================
public class ode {

    public static void main(String[] args) {

        // ===============================
        // Use Case 1: Welcome Message
        // ===============================
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay Application ");
        System.out.println("=======================================");
        System.out.println("Application Name : Hotel Booking System");
        System.out.println("Version          : v6.0");
        System.out.println("=======================================\n");

        // ===============================
        // Use Case 2: Room Details
        // ===============================
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();
        Room[] allRooms = {single, doubleRoom, suite};

        System.out.println("---- Room Details ----\n");
        for (Room room : allRooms) {
            room.displayDetails();
            System.out.println("---------------------------------------");
        }

        // ===============================
        // Use Case 3: Inventory
        // ===============================
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        // ===============================
        // Use Case 4: Search Service
        // ===============================
        SearchService searchService = new SearchService(inventory);
        System.out.println();
        searchService.searchAvailableRooms(allRooms);

        // ===============================
        // Use Case 5: Booking Requests
        // ===============================
        BookingQueue bookingQueue = new BookingQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Double Room"));
        bookingQueue.addRequest(new Reservation("David", "Single Room"));
        bookingQueue.addRequest(new Reservation("Eve", "Suite Room"));

        bookingQueue.displayQueue();

        // ===============================
        // Use Case 6: Confirm Reservations / Allocate Rooms
        // ===============================
        BookingService bookingService = new BookingService(inventory);

        System.out.println("\n---- Processing Booking Requests ----\n");
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.processNextRequest();
            bookingService.confirmReservation(res);
        }

        System.out.println("\nFinal Inventory After Allocations:");
        inventory.displayInventory();

        System.out.println("\nThank you for using Book My Stay Application!");
    }
}