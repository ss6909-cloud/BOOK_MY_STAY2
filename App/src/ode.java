/**
 * ode
 *
 * Book My Stay Application
 * Combines:
 * Use Case 1 - Welcome Message
 * Use Case 2 - Room Modeling
 * Use Case 3 - Centralized Inventory (HashMap)
 * Use Case 4 - Room Search & Availability Check
 *
 * Demonstrates abstraction, inheritance, polymorphism,
 * encapsulation, centralized state management, and read-only access.
 *
 * @author YourName
 * @version 4.0
 */

import java.util.HashMap;
import java.util.Map;

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

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

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

    // Constructor initializes availability
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability
    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // Display all inventory
    public void displayInventory() {
        System.out.println("---- Centralized Room Inventory ----\n");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Room Type       : " + entry.getKey());
            System.out.println("Available Rooms : " + entry.getValue());
            System.out.println("---------------------------------------");
        }
    }

    // Return the inventory map (read-only access for search)
    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory); // defensive copy
    }
}

// ===============================
// Use Case 4: Search Service
// ===============================
class SearchService {

    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Display only rooms with availability > 0
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
        System.out.println("Version          : v4.0");
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
        // Use Case 4: Room Search & Availability
        // ===============================
        SearchService searchService = new SearchService(inventory);
        System.out.println();
        searchService.searchAvailableRooms(allRooms);

        System.out.println("\nThank you for using Book My Stay Application!");
    }
}