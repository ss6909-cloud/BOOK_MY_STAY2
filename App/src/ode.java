import java.util.*;

// ===============================
// Custom Exception for Invalid Booking / Cancellation
// ===============================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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
// Inventory Class
// ===============================
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
        return inventory.get(roomType);
    }

    public void updateAvailability(String roomType, int count) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Cannot update invalid room type: " + roomType);
        }
        if (count < 0) {
            throw new InvalidBookingException("Cannot set negative availability for " + roomType);
        }
        inventory.put(roomType, count);
    }

    public void displayInventory() {
        System.out.println("---- Current Room Inventory ----\n");
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
// Reservation & Booking Queue
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
        if (roomID != null) System.out.println("Room ID         : " + roomID);
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
}

// ===============================
// Booking Service / Room Allocation
// ===============================
class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;
    private int roomCounter;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
        roomCounter = 100;
    }

    public void confirmReservation(Reservation reservation) {
        try {
            String roomType = reservation.getRoomType();
            int available = inventory.getAvailability(roomType);

            if (available <= 0) {
                System.out.println("Sorry, " + roomType + " is fully booked for " + reservation.getGuestName());
                return;
            }

            roomCounter++;
            String roomID = roomType.replaceAll(" ", "") + "-" + roomCounter;

            allocatedRooms.putIfAbsent(roomType, new HashSet<>());
            Set<String> allocatedSet = allocatedRooms.get(roomType);

            while (allocatedSet.contains(roomID)) {
                roomCounter++;
                roomID = roomType.replaceAll(" ", "") + "-" + roomCounter;
            }
            allocatedSet.add(roomID);

            inventory.updateAvailability(roomType, available - 1);

            reservation.setRoomID(roomID);
            System.out.println("Reservation Confirmed! Guest: " + reservation.getGuestName() + ", Room ID: " + roomID);

        } catch (InvalidBookingException e) {
            System.out.println("Error confirming reservation: " + e.getMessage());
        }
    }

    public boolean cancelReservation(Reservation reservation) {
        try {
            String roomType = reservation.getRoomType();
            String roomID = reservation.getRoomID();
            if (roomID == null) {
                System.out.println("Cannot cancel unconfirmed reservation for " + reservation.getGuestName());
                return false;
            }

            // Remove from allocatedRooms
            Set<String> allocatedSet = allocatedRooms.get(roomType);
            if (allocatedSet != null) allocatedSet.remove(roomID);

            // Increment inventory
            int currentAvailability = inventory.getAvailability(roomType);
            inventory.updateAvailability(roomType, currentAvailability + 1);

            // Clear room ID to mark cancellation
            reservation.setRoomID(null);

            System.out.println("Cancellation Successful! Guest: " + reservation.getGuestName() + ", Room Type: " + roomType);
            return true;

        } catch (InvalidBookingException e) {
            System.out.println("Error during cancellation: " + e.getMessage());
            return false;
        }
    }
}

// ===============================
// Booking History
// ===============================
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() { history = new ArrayList<>(); }

    public void recordReservation(Reservation reservation) { history.add(reservation); }

    public void removeReservation(Reservation reservation) { history.remove(reservation); }

    public void displayHistory() {
        System.out.println("\n---- Booking History ----\n");
        if (history.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        for (Reservation r : history) {
            r.displayReservation();
            System.out.println("---------------------------------------");
        }
    }
}

// ===============================
// Main Class
// ===============================
public class ode {
    public static void main(String[] args) {

        // Inventory Setup
        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);
        BookingHistory bookingHistory = new BookingHistory();

        // Sample Reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");
        Reservation r3 = new Reservation("Charlie", "Double Room");

        List<Reservation> reservations = Arrays.asList(r1, r2, r3);

        // Confirm Bookings
        System.out.println("\n---- Confirming Reservations ----\n");
        for (Reservation r : reservations) {
            bookingService.confirmReservation(r);
            if (r.getRoomID() != null) bookingHistory.recordReservation(r);
        }

        System.out.println("\nInventory after bookings:");
        inventory.displayInventory();

        System.out.println("\nBooking History:");
        bookingHistory.displayHistory();

        // ===============================
        // Perform Cancellations
        // ===============================
        System.out.println("\n---- Performing Cancellations ----\n");

        // Cancel Charlie's reservation
        bookingService.cancelReservation(r3);
        bookingHistory.removeReservation(r3);

        // Attempt to cancel a non-existent booking
        bookingService.cancelReservation(new Reservation("David", "Suite Room"));

        System.out.println("\nInventory after cancellations:");
        inventory.displayInventory();

        System.out.println("\nUpdated Booking History:");
        bookingHistory.displayHistory();

        System.out.println("\nAll operations completed successfully!");
    }
}