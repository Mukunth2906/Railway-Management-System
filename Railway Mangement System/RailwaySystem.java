package com.mycompany.railwaysystem;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract class Booking {
    String name;
    int age;

    Booking(String name, int age) {
        this.name = name;
        this.age = age;
    }

    abstract void bookingDetails();
}

class RailwayBooking extends Booking {
    String source;
    String destination;
    String trainClass;
    String berthPreference;
    String dateOfJourney;
    final String departureTime;
    final String arrivalTime;
    final int journeyDuration;
    final String trainNumber;
    final String pnrNumber;
    private static final Map<String, AtomicInteger> seatAvailability = new HashMap<>();

    static final Map<String, Integer> trainClassPrices = new HashMap<>() {{
        put("First Class", 1000);
        put("Sleeper", 500);
        put("General", 200);
    }};

    static {
        seatAvailability.put("First Class", new AtomicInteger(5));
        seatAvailability.put("Sleeper", new AtomicInteger(10));
        seatAvailability.put("General", new AtomicInteger(20));
    }

    RailwayBooking(String name, int age, String source, String destination, String trainClass, String dateOfJourney, String berthPreference) {
        super(name, age);
        this.source = source;
        this.destination = destination;
        this.trainClass = trainClass;
        this.dateOfJourney = dateOfJourney;
        this.berthPreference = berthPreference;
        this.journeyDuration = generateRandomDuration();
        this.departureTime = generateRandomTime();
        this.arrivalTime = calculateArrivalTime();
        this.trainNumber = generateTrainNumber();
        this.pnrNumber = generatePNR();
    }

    private int generateRandomDuration() {
        Random random = new Random();
        return random.nextInt(12) + 1;
    }

    private String generateRandomTime() {
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        String period = (hour < 12) ? "AM" : "PM";
        hour = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;
        return String.format("%02d:%02d %s", hour, minute, period);
    }

    private String calculateArrivalTime() {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeFormat.parse(departureTime));
            calendar.add(Calendar.HOUR, journeyDuration);
            return timeFormat.format(calendar.getTime());
        } catch (ParseException e) {
            return "Error calculating arrival time";
        }
    }

    private String generateTrainNumber() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(1000000));
    }

    private String generatePNR() {
        Random rand = new Random();
        return String.format("%010d", rand.nextInt(1000000000));
    }

    @Override
    void bookingDetails() {
        System.out.println("\n========= Booking Details =========");
        System.out.printf("Name: %s%nAge: %d%nSource: %s%nDestination: %s%nTrain Class: %s%nBerth Preference: %s%nDate of Journey: %s%n",
                name, age, source, destination, trainClass, berthPreference, dateOfJourney);
        System.out.printf("Departure Time: %s%nArrival Time: %s%nJourney Duration: %d hours%n",
                departureTime, arrivalTime, journeyDuration);
        System.out.printf("Train Number: %s%nPNR Number: %s%n", trainNumber, pnrNumber);
        System.out.println("====================================");
    }

    public boolean bookSeat() {
        AtomicInteger seats = seatAvailability.get(trainClass);
        if (seats != null && seats.get() > 0) {
            System.out.println("Seat booked successfully! Remaining seats in " + trainClass + ": " + seats.decrementAndGet());
            return true;
        } else {
            System.out.println("Sorry, no seats available in " + trainClass + ".");
            return false;
        }
    }

    public void cancelBooking() {
        AtomicInteger seats = seatAvailability.get(trainClass);
        if (seats != null) {
            System.out.println("Booking cancelled successfully. Seats now available in " + trainClass + ": " + seats.incrementAndGet());
            int refundAmount = getRefundAmount();
            System.out.println("You will receive a refund of ₹" + refundAmount);
        }
    }

    private int getRefundAmount() {
        return trainClassPrices.getOrDefault(trainClass, 0);
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }
}

public class RailwaySystem {

    private static final String USERNAME = "mukunth";
    private static final String PASSWORD = "12345";
    private static final List<RailwayBooking> bookings = new ArrayList<>();

    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    private static final String[] indianDistricts = {
            "Bengaluru", "Delhi", "Mumbai", "Chennai", "Kanyakumari", "Tuticorin", "Thirunelveli", "Trichy", "Kolkata", "Hyderabad",
            "Pune", "Ahmedabad", "Lucknow", "Jaipur", "Indore", "Bhopal", "Patna", "Vadodara", "Surat", "Chandigarh",
            "Coimbatore", "Tiruppur", "Salem", "Erode", "Mysore", "Nagpur", "Bhubaneswar", "Madurai", "Vijayawada"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RailwaySystem::initialize);
    }

    private static void initialize() {
        frame = new JFrame("Railway Booking System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);

        showLoginScreen();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showLoginScreen() {
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                cardLayout.show(mainPanel, "mainMenu");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);

        mainPanel.add(loginPanel, "loginScreen");
        showMainMenu();
    }

    private static void showMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton bookTicketButton = new JButton("Book a Ticket");
        JButton viewBookingsButton = new JButton("View Bookings");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton logoutButton = new JButton("Logout");
        JButton clearBookingsButton = new JButton("Clear All Bookings");

        bookTicketButton.addActionListener(e -> showBookingForm());
        viewBookingsButton.addActionListener(e -> displayBookings());
        cancelBookingButton.addActionListener(e -> showCancellationForm());
        logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "loginScreen"));
        clearBookingsButton.addActionListener(e -> clearAllBookings());

        menuPanel.add(bookTicketButton);
        menuPanel.add(viewBookingsButton);
        menuPanel.add(cancelBookingButton);
        menuPanel.add(clearBookingsButton);
        menuPanel.add(logoutButton);

        mainPanel.add(menuPanel, "mainMenu");
    }

    private static void showBookingForm() {
        JPanel bookingFormPanel = new JPanel(new GridLayout(10, 2, 10, 10)); // Increased rows for payment
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> sourceComboBox = new JComboBox<>(indianDistricts);
        JComboBox<String> destinationComboBox = new JComboBox<>(indianDistricts);
        JComboBox<String> trainClassComboBox = new JComboBox<>(RailwayBooking.trainClassPrices.keySet().toArray(new String[0]));
        JComboBox<String> berthPreferenceComboBox = new JComboBox<>(new String[]{"Lower", "Upper", "Middle"});
        JTextField dateField = new JTextField();

        JButton bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(e -> {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String source = (String) sourceComboBox.getSelectedItem();
            String destination = (String) destinationComboBox.getSelectedItem();
            String trainClass = (String) trainClassComboBox.getSelectedItem();
            String berthPreference = (String) berthPreferenceComboBox.getSelectedItem();
            String dateOfJourney = dateField.getText();

            RailwayBooking newBooking = new RailwayBooking(name, age, source, destination, trainClass, dateOfJourney, berthPreference);
            if (newBooking.bookSeat()) {
                bookings.add(newBooking);
                JOptionPane.showMessageDialog(frame, "Booking successful! Train: " + newBooking.getTrainNumber() + "\nPNR: " + newBooking.getPnrNumber());
                newBooking.bookingDetails();
            } else {
                JOptionPane.showMessageDialog(frame, "Sorry, no seats available in " + trainClass);
            }
        });

        bookingFormPanel.add(new JLabel("Name:"));
        bookingFormPanel.add(nameField);
        bookingFormPanel.add(new JLabel("Age:"));
        bookingFormPanel.add(ageField);
        bookingFormPanel.add(new JLabel("Source:"));
        bookingFormPanel.add(sourceComboBox);
        bookingFormPanel.add(new JLabel("Destination:"));
        bookingFormPanel.add(destinationComboBox);
        bookingFormPanel.add(new JLabel("Train Class:"));
        bookingFormPanel.add(trainClassComboBox);
        bookingFormPanel.add(new JLabel("Berth Preference:"));
        bookingFormPanel.add(berthPreferenceComboBox);
        bookingFormPanel.add(new JLabel("Date of Journey:"));
        bookingFormPanel.add(dateField);
        bookingFormPanel.add(new JLabel());
        bookingFormPanel.add(bookButton);

        mainPanel.add(bookingFormPanel, "bookingForm");
        cardLayout.show(mainPanel, "bookingForm");
    }

    private static void displayBookings() {
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No bookings to display.");
            return;
        }
        StringBuilder bookingDetails = new StringBuilder();
        for (RailwayBooking booking : bookings) {
            bookingDetails.append("Train: ").append(booking.getTrainNumber())
                    .append(" | PNR: ").append(booking.getPnrNumber()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, bookingDetails.toString());
    }

    private static void showCancellationForm() {
        JTextField pnrField = new JTextField();
        JPanel cancellationPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cancellationPanel.add(new JLabel("Enter PNR to cancel:"));
        cancellationPanel.add(pnrField);

        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.addActionListener(e -> {
            String enteredPnr = pnrField.getText();
            Optional<RailwayBooking> bookingToCancel = bookings.stream()
                    .filter(b -> b.getPnrNumber().equals(enteredPnr))
                    .findFirst();

            if (bookingToCancel.isPresent()) {
                bookingToCancel.get().cancelBooking();
                bookings.remove(bookingToCancel.get());
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid PNR Number!");
            }
        });

        cancellationPanel.add(cancelButton);
        mainPanel.add(cancellationPanel, "cancellationForm");
        cardLayout.show(mainPanel, "cancellationForm");
    }

    private static void clearAllBookings() {
        bookings.clear();
        JOptionPane.showMessageDialog(frame, "All bookings have been cleared!");
    }
}
