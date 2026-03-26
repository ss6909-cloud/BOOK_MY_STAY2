import java.util.*;
import java.util.concurrent.*;

// ---------------------------
// Abstract Room Class
// ---------------------------
abstract class Room {
    private int beds;
    private double size;
    private double price;

    public Room(int beds, double size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public int getBeds() { return beds; }
    public double getSize() { return size; }
    public double getPrice() { return price; }
    public abstract String getRoomType();
}

// ---------------------------
// Concrete Room Types
// ---------------------------
class SingleRoom extends Room {
    public SingleRoom() { super(1, 150.0, 50.0); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 250.0, 90.0); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 400.0, 150.0); }
    public String getRoomType() { return "Suite Room"; }
}

// ---------------------------
// Room Inventory with Thread-Safe Access
// ---------------------------
class RoomInventory {
    private final Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public synchronized int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public synchronized void releaseRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, available + 1);
    }

    public synchronized void displayInventory() {
        System.out.println("\n---- Current Room Inventory ----");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}

// ---------------------------
// Reservation Class
// ---------------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String roomID;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomID() { return roomID; }
    public void setRoomID(String roomID) { this.roomID = roomID; }
}

// ---------------------------
// Thread-Safe Booking Service
// ---------------------------
class BookingService {
    private final RoomInventory inventory;
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private int roomCounter = 100;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public synchronized void confirmReservation(Reservation reservation) {
        String type = reservation.getRoomType();
        if (!inventory.allocateRoom(type)) {
            System.out.println("Sorry, " + type + " is fully booked for " + reservation.getGuestName());
            return;
        }
        roomCounter++;
        String roomID = type.replaceAll(" ", "") + "-" + roomCounter;

        allocatedRooms.putIfAbsent(type, new HashSet<>());
        allocatedRooms.get(type).add(roomID);

        reservation.setRoomID(roomID);
        System.out.println("Reservation Confirmed! Guest: " + reservation.getGuestName() + ", Room ID: " + roomID);
    }
}

// ---------------------------
// Main ODE Class: Concurrent Booking Simulation
// ---------------------------
public class ode {

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Simulate multiple guest booking requests
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Suite Room"),
                new Reservation("Diana", "Single Room"),
                new Reservation("Ethan", "Double Room"),
                new Reservation("Fiona", "Suite Room"),
                new Reservation("George", "Single Room")
        );

        // Create a thread pool to simulate concurrent booking
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (Reservation r : reservations) {
            executor.execute(() -> bookingService.confirmReservation(r));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        inventory.displayInventory();
    }
}