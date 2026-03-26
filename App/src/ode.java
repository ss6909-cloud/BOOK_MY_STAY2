import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// ---------------------------
// Abstract Room Class
// ---------------------------
abstract class Room implements Serializable {
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
// Room Inventory with Persistence
// ---------------------------
class RoomInventory implements Serializable {
    private Map<String, Integer> inventory = new HashMap<>();

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
class Reservation implements Serializable {
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

    @Override
    public String toString() {
        return "Reservation{" +
                "guest='" + guestName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomID='" + roomID + '\'' +
                '}';
    }
}

// ---------------------------
// Booking Service with Persistence
// ---------------------------
class BookingService implements Serializable {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private List<Reservation> bookingHistory = new ArrayList<>();
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
        bookingHistory.add(reservation);

        System.out.println("Reservation Confirmed! Guest: " + reservation.getGuestName() + ", Room ID: " + roomID);
    }

    public synchronized void cancelReservation(Reservation reservation) {
        if (!bookingHistory.contains(reservation)) {
            System.out.println("Reservation not found: " + reservation.getGuestName());
            return;
        }
        bookingHistory.remove(reservation);
        String type = reservation.getRoomType();
        inventory.releaseRoom(type);
        allocatedRooms.get(type).remove(reservation.getRoomID());
        System.out.println("Reservation Cancelled: " + reservation.getGuestName() + ", Room ID: " + reservation.getRoomID());
    }

    public synchronized void displayBookingHistory() {
        System.out.println("\n---- Booking History ----");
        for (Reservation r : bookingHistory) {
            System.out.println(r);
        }
    }

    public List<Reservation> getBookingHistory() {
        return bookingHistory;
    }

    public Map<String, Set<String>> getAllocatedRooms() {
        return allocatedRooms;
    }
}

// ---------------------------
// Persistence Service
// ---------------------------
class PersistenceService {
    private static final String FILE_PATH = "hotel_system_data.ser";

    public static void saveState(RoomInventory inventory, BookingService bookingService) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(inventory);
            oos.writeObject(bookingService);
            System.out.println("\nSystem state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingService bookingService = (BookingService) ois.readObject();
            System.out.println("\nSystem state restored successfully.");
            return new Object[]{inventory, bookingService};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }
    }
}

// ---------------------------
// Main ODE Class: Data Persistence & Recovery
// ---------------------------
public class ode {

    public static void main(String[] args) {
        Object[] restored = PersistenceService.loadState();

        RoomInventory inventory;
        BookingService bookingService;

        if (restored != null) {
            inventory = (RoomInventory) restored[0];
            bookingService = (BookingService) restored[1];
        } else {
            inventory = new RoomInventory();
            bookingService = new BookingService(inventory);
        }

        // Simulate some bookings
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        bookingService.confirmReservation(r1);
        bookingService.confirmReservation(r2);
        bookingService.confirmReservation(r3);

        bookingService.displayBookingHistory();
        inventory.displayInventory();

        // Save state for future recovery
        PersistenceService.saveState(inventory, bookingService);
    }
}