/**
 * ode
 *
 * Book My Stay Application
 * Combines:
 * Use Case 1 - Application Entry & Welcome Message
 * Use Case 2 - Room Types & Static Availability
 *
 * @author YourName
 * @version 2.1
 */

// Abstract Room class
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

// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 150.0, 50.0);
    }

    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 250.0, 90.0);
    }

    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 400.0, 150.0);
    }

    @Override
    public String getRoomType() {
        return "Suite Room";
    }
}

// Main class
public class ode {

    public static void main(String[] args) {

        // ===============================
        // ✅ Use Case 1: Welcome Message
        // ===============================
        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay Application ");
        System.out.println("=======================================");
        System.out.println("Application Name : Hotel Booking System");
        System.out.println("Version          : v2.1");
        System.out.println("=======================================\n");

        // ===============================
        // ✅ Use Case 2: Room Details
        // ===============================
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("---- Room Details & Availability ----\n");

        singleRoom.displayDetails();
        System.out.println("Available Rooms : " + singleAvailable);
        System.out.println("---------------------------------------");

        doubleRoom.displayDetails();
        System.out.println("Available Rooms : " + doubleAvailable);
        System.out.println("---------------------------------------");

        suiteRoom.displayDetails();
        System.out.println("Available Rooms : " + suiteAvailable);
        System.out.println("---------------------------------------");

        System.out.println("\nThank you for using Book My Stay Application!");
    }
}