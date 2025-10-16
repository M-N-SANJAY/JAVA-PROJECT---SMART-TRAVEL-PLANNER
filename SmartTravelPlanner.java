import java.util.*;
import java.io.*;

// Base Destination class
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

    public void displayInfo() {
        System.out.println("📍 " + name + ", " + country);
        System.out.println("   " + description);
        System.out.println("   Base Cost: $" + baseCost);
    }
}

// CityPlan - inherits from Destination
class CityPlan extends Destination {
    private ArrayList<String> attractions;
    private double transportCost;

    public CityPlan(String name, String country, double baseCost, String description) {
        super(name, country, baseCost, description);
        this.attractions = new ArrayList<>();
        this.transportCost = 50.0;
    }

    public void addAttraction(String attraction) {
        attractions.add(attraction);
    }

    public ArrayList<String> getAttractions() { return attractions; }
    public double getTransportCost() { return transportCost; }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Transport: $" + transportCost);
        if (!attractions.isEmpty()) {
            System.out.println("   Attractions: " + String.join(", ", attractions));
        }
    }
}

// TourPlan - inherits from Destination
class TourPlan extends Destination {
    private int durationDays;
    private String tourType;
    private double dailyCost;

    public TourPlan(String name, String country, double baseCost, String description, 
                    int durationDays, String tourType) {
        super(name, country, baseCost, description);
        this.durationDays = durationDays;
        this.tourType = tourType;
        this.dailyCost = 100.0;
    }

    public int getDurationDays() { return durationDays; }
    public String getTourType() { return tourType; }
    public double getDailyCost() { return dailyCost; }

    public double getTotalTourCost() {
        return baseCost + (dailyCost * durationDays);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Tour Type: " + tourType);
        System.out.println("   Duration: " + durationDays + " days");
        System.out.println("   Daily Cost: $" + dailyCost);
        System.out.println("   Total Tour Cost: $" + getTotalTourCost());
    }
}

// Custom Exceptions
class RouteNotFoundException extends Exception {
    public RouteNotFoundException(String message) {
        super(message);
    }
}

class InvalidBudgetException extends Exception {
    public InvalidBudgetException(String message) {
        super(message);
    }
}

// Location List Module
class LocationList {
    private ArrayList<Destination> destinations;

    public LocationList() {
        destinations = new ArrayList<>();
        loadDefaultDestinations();
    }

    private void loadDefaultDestinations() {
        // Add some city plans
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

        // Add some tour plans
        destinations.add(new TourPlan("Bali", "Indonesia", 400.0, 
            "Tropical paradise with beaches and temples", 7, "Beach & Culture"));
        
        destinations.add(new TourPlan("Swiss Alps", "Switzerland", 800.0, 
            "Mountain adventure with stunning views", 5, "Adventure"));
        
        destinations.add(new TourPlan("Dubai", "UAE", 900.0, 
            "Luxury and modern architecture", 4, "Luxury"));
    }

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
            System.out.println("✓ Loaded destinations from " + filename);
        }
    }

    public void displayAllDestinations() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     AVAILABLE DESTINATIONS             ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println((i + 1) + ". ");
            destinations.get(i).displayInfo();
            System.out.println();
        }
    }

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

// Cost Manager Module
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

    public boolean isWithinBudget() {
        return totalCost <= budget;
    }

    public void displayCostBreakdown() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         COST BREAKDOWN                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        for (String item : costBreakdown) {
            System.out.println("  • " + item);
        }
        System.out.println("\n  Total Cost: $" + totalCost);
        System.out.println("  Your Budget: $" + budget);
        System.out.println("  Remaining: $" + getRemainingBudget());
        
        if (isWithinBudget()) {
            System.out.println("  ✓ Within budget! 🎉");
        } else {
            System.out.println("  ⚠ Over budget by $" + Math.abs(getRemainingBudget()));
        }
    }
}

// Travel Planner Module
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

    public void displayPlan() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    YOUR TRAVEL PLAN - " + travelerName);
        System.out.println("╚════════════════════════════════════════╝\n");

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

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TRAVEL PLAN FOR: " + travelerName);
            writer.println("=" .repeat(50));
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

            System.out.println("\n✓ Travel plan saved to " + filename);
        }
    }
}

// Main Application
public class SmartTravelPlanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   SMART TRAVEL PLANNER                 ║");
        System.out.println("║   SDG 11: Sustainable Cities           ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            // Get traveler name
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Create planner
            TravelPlanner planner = new TravelPlanner(name);

            // Set budget
            System.out.print("Enter your budget ($): ");
            double budget = scanner.nextDouble();
            planner.setBudget(budget);

            // Load destinations
            LocationList locationList = new LocationList();
            
            // Optional: Load from file (uncomment if you have a file)
            // locationList.loadFromFile("destinations.txt");

            boolean planning = true;
            while (planning) {
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
                        locationList.displayAllDestinations();
                        break;

                    case 2:
                        locationList.displayAllDestinations();
                        System.out.print("Enter destination number: ");
                        int destNum = scanner.nextInt();
                        try {
                            Destination selected = locationList.getDestination(destNum - 1);
                            planner.addDestination(selected);
                            System.out.println("✓ Added " + selected.getName() + " to your plan!");
                        } catch (RouteNotFoundException e) {
                            System.out.println("❌ Error: " + e.getMessage());
                        }
                        break;

                    case 3:
                        planner.displayPlan();
                        break;

                    case 4:
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter filename (e.g., myplan.txt): ");
                        String filename = scanner.nextLine();
                        try {
                            planner.saveToFile(filename);
                        } catch (IOException e) {
                            System.out.println("❌ Error saving file: " + e.getMessage());
                        }
                        break;

                    case 5:
                        planning = false;
                        System.out.println("\n✈️  Happy travels! Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (InvalidBudgetException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("❌ Error: Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}import java.util.*;
import java.io.*;

// Base Destination class
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

    public void displayInfo() {
        System.out.println("📍 " + name + ", " + country);
        System.out.println("   " + description);
        System.out.println("   Base Cost: $" + baseCost);
    }
}

// CityPlan - inherits from Destination
class CityPlan extends Destination {
    private ArrayList<String> attractions;
    private double transportCost;

    public CityPlan(String name, String country, double baseCost, String description) {
        super(name, country, baseCost, description);
        this.attractions = new ArrayList<>();
        this.transportCost = 50.0;
    }

    public void addAttraction(String attraction) {
        attractions.add(attraction);
    }

    public ArrayList<String> getAttractions() { return attractions; }
    public double getTransportCost() { return transportCost; }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Transport: $" + transportCost);
        if (!attractions.isEmpty()) {
            System.out.println("   Attractions: " + String.join(", ", attractions));
        }
    }
}

// TourPlan - inherits from Destination
class TourPlan extends Destination {
    private int durationDays;
    private String tourType;
    private double dailyCost;

    public TourPlan(String name, String country, double baseCost, String description, 
                    int durationDays, String tourType) {
        super(name, country, baseCost, description);
        this.durationDays = durationDays;
        this.tourType = tourType;
        this.dailyCost = 100.0;
    }

    public int getDurationDays() { return durationDays; }
    public String getTourType() { return tourType; }
    public double getDailyCost() { return dailyCost; }

    public double getTotalTourCost() {
        return baseCost + (dailyCost * durationDays);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("   Tour Type: " + tourType);
        System.out.println("   Duration: " + durationDays + " days");
        System.out.println("   Daily Cost: $" + dailyCost);
        System.out.println("   Total Tour Cost: $" + getTotalTourCost());
    }
}

// Custom Exceptions
class RouteNotFoundException extends Exception {
    public RouteNotFoundException(String message) {
        super(message);
    }
}

class InvalidBudgetException extends Exception {
    public InvalidBudgetException(String message) {
        super(message);
    }
}

// Location List Module
class LocationList {
    private ArrayList<Destination> destinations;

    public LocationList() {
        destinations = new ArrayList<>();
        loadDefaultDestinations();
    }

    private void loadDefaultDestinations() {
        // Add some city plans
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

        // Add some tour plans
        destinations.add(new TourPlan("Bali", "Indonesia", 400.0, 
            "Tropical paradise with beaches and temples", 7, "Beach & Culture"));
        
        destinations.add(new TourPlan("Swiss Alps", "Switzerland", 800.0, 
            "Mountain adventure with stunning views", 5, "Adventure"));
        
        destinations.add(new TourPlan("Dubai", "UAE", 900.0, 
            "Luxury and modern architecture", 4, "Luxury"));
    }

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
            System.out.println("✓ Loaded destinations from " + filename);
        }
    }

    public void displayAllDestinations() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     AVAILABLE DESTINATIONS             ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println((i + 1) + ". ");
            destinations.get(i).displayInfo();
            System.out.println();
        }
    }

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

// Cost Manager Module
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

    public boolean isWithinBudget() {
        return totalCost <= budget;
    }

    public void displayCostBreakdown() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         COST BREAKDOWN                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        for (String item : costBreakdown) {
            System.out.println("  • " + item);
        }
        System.out.println("\n  Total Cost: $" + totalCost);
        System.out.println("  Your Budget: $" + budget);
        System.out.println("  Remaining: $" + getRemainingBudget());
        
        if (isWithinBudget()) {
            System.out.println("  ✓ Within budget! 🎉");
        } else {
            System.out.println("  ⚠ Over budget by $" + Math.abs(getRemainingBudget()));
        }
    }
}

// Travel Planner Module
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

    public void displayPlan() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    YOUR TRAVEL PLAN - " + travelerName);
        System.out.println("╚════════════════════════════════════════╝\n");

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

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TRAVEL PLAN FOR: " + travelerName);
            writer.println("=" .repeat(50));
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

            System.out.println("\n✓ Travel plan saved to " + filename);
        }
    }
}

// Main Application
public class SmartTravelPlanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   SMART TRAVEL PLANNER                 ║");
        System.out.println("║   SDG 11: Sustainable Cities           ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            // Get traveler name
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Create planner
            TravelPlanner planner = new TravelPlanner(name);

            // Set budget
            System.out.print("Enter your budget ($): ");
            double budget = scanner.nextDouble();
            planner.setBudget(budget);

            // Load destinations
            LocationList locationList = new LocationList();
            
            // Optional: Load from file (uncomment if you have a file)
            // locationList.loadFromFile("destinations.txt");

            boolean planning = true;
            while (planning) {
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
                        locationList.displayAllDestinations();
                        break;

                    case 2:
                        locationList.displayAllDestinations();
                        System.out.print("Enter destination number: ");
                        int destNum = scanner.nextInt();
                        try {
                            Destination selected = locationList.getDestination(destNum - 1);
                            planner.addDestination(selected);
                            System.out.println("✓ Added " + selected.getName() + " to your plan!");
                        } catch (RouteNotFoundException e) {
                            System.out.println("❌ Error: " + e.getMessage());
                        }
                        break;

                    case 3:
                        planner.displayPlan();
                        break;

                    case 4:
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter filename (e.g., myplan.txt): ");
                        String filename = scanner.nextLine();
                        try {
                            planner.saveToFile(filename);
                        } catch (IOException e) {
                            System.out.println("❌ Error saving file: " + e.getMessage());
                        }
                        break;

                    case 5:
                        planning = false;
                        System.out.println("\n✈️  Happy travels! Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (InvalidBudgetException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("❌ Error: Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
