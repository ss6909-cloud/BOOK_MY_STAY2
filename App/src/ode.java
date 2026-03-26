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
 * Use Case 7 - Add-On Service Selection
 *
 * Demonstrates OOP, centralized inventory, search, booking queue, safe allocation, and add-on service management.
 *
 * @author YourName
 * @version 7.0
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
    private String roomID; // assigned after confirmation

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public void setRoomID(String roomID) { this.roomID = roomID; }
    public String getRoomID() { return roomID; }

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

        // Save room ID in reservation
        reservation.setRoomID(roomID);

        // Confirmation message
        System.out.println("\nReservation Confirmed!");
        System.out.println("Guest Name : " + reservation.getGuestName());
        System.out.println("Room Type  : " + roomType);
        System.out.println("Room ID    : " + roomID);
        System.out.println("Remaining " + roomType + " : " + inventory.getAvailability(roomType));
    }
}

// ===============================
// Use Case 7: Add-On Service
// ===============================
class Service {
    private String name;
    private double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    public void displayService() {
        System.out.println("Service Name : " + name);
        System.out.println("Price        : $" + price);
    }
}

class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices; // reservationID -> list of services

    public AddOnServiceManager() { reservationServices = new HashMap<>(); }

    public void addServiceToReservation(String roomID, Service service) {
        reservationServices.putIfAbsent(roomID, new ArrayList<>());
        reservationServices.get(roomID).add(service);
        System.out.println("Added service " + service.getName() + " to reservation " + roomID);
    }

    public void displayServices(String roomID) {
        List<Service> services = reservationServices.get(roomID);
        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services for reservation " + roomID);
            return;
        }
        System.out.println("---- Add-On Services for " + roomID + " ----");
        double total = 0;
        for (Service s : services) {
            s.displayService();
            total += s.getPrice();
            System.out.println("---------------------------------------");
        }
        System.out.println("Total Add-On Cost: $" + total);
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
        System.out.println("Version          : v7.0");
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
        List<Reservation> confirmedReservations = new ArrayList<>();
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.processNextRequest();
            bookingService.confirmReservation(res);
            confirmedReservations.add(res);
        }

        System.out.println("\nFinal Inventory After Allocations:");
        inventory.displayInventory();

        // ===============================
        // Use Case 7: Add-On Services
        // ===============================
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        Service breakfast = new Service("Breakfast", 15.0);
        Service spa = new Service("Spa Access", 40.0);
        Service airport = new Service("Airport Pickup", 25.0);

        // Example: attach add-ons to confirmed reservations
        for (Reservation res : confirmedReservations) {
            if (res.getRoomID() != null) {
                addOnManager.addServiceToReservation(res.getRoomID(), breakfast);
                addOnManager.addServiceToReservation(res.getRoomID(), spa);
            }
        }

        // Display add-on services for a specific reservation
        System.out.println();
        for (Reservation res : confirmedReservations) {
            if (res.getRoomID() != null) {
                addOnManager.displayServices(res.getRoomID());
            }
        }

        System.out.println("\nThank you for using Book My Stay Application!");
    }
}