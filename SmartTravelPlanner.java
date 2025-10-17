import java.util.*;
import java.io.*;

/**
 * Main class for Smart Travel Planner Application.
 * Allows users to choose destinations, plan trips, view cost breakdowns, and save plans.
 */
public class SmartTravelPlanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n========================================");
        System.out.println("   SMART TRAVEL PLANNER                 ");
        System.out.println("========================================\n");

        try {
            // Get traveler name
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Create planner object
            TravelPlanner planner = new TravelPlanner(name);

            // Get and set budget
            System.out.print("Enter your budget ($): ");
            double budget = scanner.nextDouble();
            planner.setBudget(budget);

            // Initialize location list
            LocationList locationList = new LocationList();

            boolean planning = true;
            while (planning) {
                // Menu options
                System.out.println("\n--- MENU ---");
                System.out.println("1. View all destinations");
                System.out.println("2. Add destination to plan");
                System.out.println("3. View my travel plan");
                System.out.println("4. Save plan to file");
                System.out.println("5. Exit");
                System.out.print("Choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Display available destinations
                        locationList.displayAllDestinations();
                        break;

                    case 2:
                        // Add destination to user's plan
                        locationList.displayAllDestinations();
                        System.out.print("Enter destination number: ");
                        int destNum = scanner.nextInt();

                        try {
                            Destination selected = locationList.getDestination(destNum - 1);
                            planner.addDestination(selected);
                            System.out.println("Added " + selected.getName() + " to your plan!");
                        } catch (RouteNotFoundException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 3:
                        // Show travel plan
                        planner.displayPlan();
                        break;

                    case 4:
                        // Save plan to file
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter filename (e.g., myplan.txt): ");
                        String filename = scanner.nextLine();

                        try {
                            planner.saveToFile(filename);
                        } catch (IOException e) {
                            System.out.println("Error saving file: " + e.getMessage());
                        }
                        break;

                    case 5:
                        // Exit
                        planning = false;
                        System.out.println("\nHappy travels! Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (InvalidBudgetException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

/**
 * Represents a general travel destination.
 */
class Destination {
    protected String name;
    protected String country;
    protected double baseCost;
    protected String description;

    public Destination(String name, String country, double baseCost, String description) {
        this.name = name;
        this.country = country;
        this.baseCost = baseCost;
        this.description = description;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }
    public double getBaseCost() { return baseCost; }
    public String getDescription() { return description; }

    /** Displays destination details. */
    public void displayInfo() {
        System.out.println("Location: " + name + ", " + country);
        System.out.println("   " + description);
        System.out.println("   Base Cost: $" + baseCost);
    }
}

/**
 * Represents a city-type destination with attractions and transport costs.
 */
class CityPlan extends Destination {
    private ArrayList<String> attractions;
    private double transportCost;

    public CityPlan(String name, String country, double baseCost, String description) {
        super(name, country, baseCost, description);
        this.attractions = new ArrayList<>();
        this.transportCost = 50.0; // default cost
    }

    public void addAttraction(String attraction) { attractions.add(attraction); }
    public ArrayList<String> getAttractions() { return attractions; }
    public double getTransportCost() { return transportCost; }

    /** Displays detailed city info with attractions and transport. */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Transport: $" + transportCost);
        if (!attractions.isEmpty()) {
            System.out.println("   Attractions: " + String.join(", ", attractions));
        }
    }
}

/**
 * Represents a multi-day tour with additional costs and duration.
 */
class TourPlan extends Destination {
    private int durationDays;
    private String tourType;
    private double dailyCost;

    public TourPlan(String name, String country, double baseCost, String description,
                    int durationDays, String tourType) {
        super(name, country, baseCost, description);
        this.durationDays = durationDays;
        this.tourType = tourType;
        this.dailyCost = 100.0; // default per day cost
    }

    public int getDurationDays() { return durationDays; }
    public String getTourType() { return tourType; }
    public double getDailyCost() { return dailyCost; }

    /** Calculates total tour cost. */
    public double getTotalTourCost() {
        return baseCost + (dailyCost * durationDays);
    }

    /** Displays detailed tour information. */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Tour Type: " + tourType);
        System.out.println("   Duration: " + durationDays + " days");
        System.out.println("   Daily Cost: $" + dailyCost);
        System.out.println("   Total Tour Cost: $" + getTotalTourCost());
    }
}

/** Custom exception for missing route/destination. */
class RouteNotFoundException extends Exception {
    public RouteNotFoundException(String message) {
        super(message);
    }
}

/** Custom exception for invalid budgets. */
class InvalidBudgetException extends Exception {
    public InvalidBudgetException(String message) {
        super(message);
    }
}

/**
 * Holds all available destinations (city plans and tours).
 */
class LocationList {
    private ArrayList<Destination> destinations;

    public LocationList() {
        destinations = new ArrayList<>();
        loadDefaultDestinations();
    }

    /** Loads a few predefined destinations. */
    private void loadDefaultDestinations() {
        CityPlan paris = new CityPlan("Paris", "France", 500.0,
                "The City of Light with iconic landmarks");
        paris.addAttraction("Eiffel Tower");
        paris.addAttraction("Louvre Museum");
        paris.addAttraction("Notre-Dame");
        destinations.add(paris);

        CityPlan tokyo = new CityPlan("Tokyo", "Japan", 600.0,
                "Modern metropolis with rich culture");
        tokyo.addAttraction("Senso-ji Temple");
        tokyo.addAttraction("Tokyo Tower");
        tokyo.addAttraction("Shibuya Crossing");
        destinations.add(tokyo);

        CityPlan newYork = new CityPlan("New York", "USA", 700.0,
                "The city that never sleeps");
        newYork.addAttraction("Statue of Liberty");
        newYork.addAttraction("Central Park");
        newYork.addAttraction("Times Square");
        destinations.add(newYork);

        destinations.add(new TourPlan("Bali", "Indonesia", 400.0,
                "Tropical paradise with beaches and temples", 7, "Beach & Culture"));

        destinations.add(new TourPlan("Swiss Alps", "Switzerland", 800.0,
                "Mountain adventure with stunning views", 5, "Adventure"));

        destinations.add(new TourPlan("Dubai", "UAE", 900.0,
                "Luxury and modern architecture", 4, "Luxury"));
    }

    /** Optionally load custom destinations from file. */
    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String name = parts[0].trim();
                    String country = parts[1].trim();
                    double cost = Double.parseDouble(parts[2].trim());
                    String desc = parts[3].trim();
                    destinations.add(new Destination(name, country, cost, desc));
                }
            }
            System.out.println("Loaded destinations from " + filename);
        }
    }

    /** Displays all destinations. */
    public void displayAllDestinations() {
        System.out.println("\n========================================");
        System.out.println("     AVAILABLE DESTINATIONS             ");
        System.out.println("========================================\n");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println((i + 1) + ". ");
            destinations.get(i).displayInfo();
            System.out.println();
        }
    }

    /** Get a destination by index (1-based in menu). */
    public Destination getDestination(int index) throws RouteNotFoundException {
        if (index < 0 || index >= destinations.size()) {
            throw new RouteNotFoundException("Destination not found at index: " + index);
        }
        return destinations.get(index);
    }

    public ArrayList<Destination> getAllDestinations() {
        return destinations;
    }
}

/**
 * Manages budget and expenses for travel.
 */
class CostManager {
    private double totalCost;
    private double budget;
    private ArrayList<String> costBreakdown;

    public CostManager(double budget) throws InvalidBudgetException {
        if (budget <= 0) {
            throw new InvalidBudgetException("Budget must be greater than zero!");
        }
        this.budget = budget;
        this.totalCost = 0.0;
        this.costBreakdown = new ArrayList<>();
    }

    public void addCost(String item, double cost) {
        totalCost += cost;
        costBreakdown.add(item + ": $" + cost);
    }

    public double getTotalCost() { return totalCost; }
    public double getBudget() { return budget; }
    public double getRemainingBudget() { return budget - totalCost; }
    public boolean isWithinBudget() { return totalCost <= budget; }

    /** Prints a clear cost summary for the trip. */
    public void displayCostBreakdown() {
        System.out.println("\n========================================");
        System.out.println("         COST BREAKDOWN                 ");
        System.out.println("========================================\n");
        for (String item : costBreakdown) {
            System.out.println("  - " + item);
        }
        System.out.println("\n  Total Cost: $" + totalCost);
        System.out.println("  Your Budget: $" + budget);
        System.out.println("  Remaining: $" + getRemainingBudget());

        if (isWithinBudget()) {
            System.out.println("  Within budget!");
        } else {
            System.out.println("  Over budget by $" + Math.abs(getRemainingBudget()));
        }
    }
}

/**
 * Handles user’s overall travel plan, including destinations and cost management.
 */
class TravelPlanner {
    private ArrayList<Destination> selectedDestinations;
    private CostManager costManager;
    private String travelerName;

    public TravelPlanner(String travelerName) {
        this.travelerName = travelerName;
        this.selectedDestinations = new ArrayList<>();
    }

    public void setBudget(double budget) throws InvalidBudgetException {
        this.costManager = new CostManager(budget);
    }

    /** Add a chosen destination and automatically update cost. */
    public void addDestination(Destination dest) {
        selectedDestinations.add(dest);
        costManager.addCost(dest.getName() + " (Base)", dest.getBaseCost());

        if (dest instanceof CityPlan) {
            CityPlan city = (CityPlan) dest;
            costManager.addCost(dest.getName() + " (Transport)", city.getTransportCost());
        } else if (dest instanceof TourPlan) {
            TourPlan tour = (TourPlan) dest;
            costManager.addCost(dest.getName() + " (Daily x" + tour.getDurationDays() + ")",
                    tour.getDailyCost() * tour.getDurationDays());
        }
    }

    /** Display full travel itinerary and budget summary. */
    public void displayPlan() {
        System.out.println("\n========================================");
        System.out.println("    YOUR TRAVEL PLAN - " + travelerName);
        System.out.println("========================================\n");

        if (selectedDestinations.isEmpty()) {
            System.out.println("  No destinations selected yet.");
            return;
        }

        System.out.println("Itinerary:\n");
        for (int i = 0; i < selectedDestinations.size(); i++) {
            System.out.println("Stop " + (i + 1) + ":");
            selectedDestinations.get(i).displayInfo();
            System.out.println();
        }

        costManager.displayCostBreakdown();
    }

    /** Save plan details to a text file. */
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TRAVEL PLAN FOR: " + travelerName);
            writer.println("==================================================");
            writer.println();

            for (int i = 0; i < selectedDestinations.size(); i++) {
                Destination dest = selectedDestinations.get(i);
                writer.println("Stop " + (i + 1) + ": " + dest.getName() + ", " + dest.getCountry());
                writer.println("Cost: $" + dest.getBaseCost());
                writer.println();
            }

            writer.println("Total Budget: $" + costManager.getBudget());
            writer.println("Total Cost: $" + costManager.getTotalCost());
            writer.println("Remaining: $" + costManager.getRemainingBudget());

            System.out.println("\nTravel plan saved to " + filename);
        }
    }
}
