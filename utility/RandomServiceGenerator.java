package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;
import entity.Cleaner.CleaningService;
import entity.CleaningServiceDAO;
import entity.UserDAO;
import entity.PlatformManager.ServiceCategory;

public class RandomServiceGenerator {
    
    // Arrays of sample data
    private static final String[] SERVICE_TITLES = {
        "Basic House Cleaning", "Deep Cleaning", "Spring Cleaning", "Office Cleaning",
        "Move-in/Move-out Cleaning", "Post-Construction Cleaning", "Carpet Cleaning",
        "Window Cleaning", "Kitchen Deep Clean", "Bathroom Sanitization",
        "Floor Polishing", "Dusting Service", "Upholstery Cleaning", "Oven Cleaning",
        "Refrigerator Cleaning", "Weekly Maintenance", "Bi-weekly Maintenance",
        "Monthly Maintenance", "One-time Cleaning", "Premium House Cleaning"
    };
    
    private static final String[] SERVICE_DESCRIPTIONS = {
        "Complete cleaning of all rooms including dusting, vacuuming, and mopping.",
        "Thorough cleaning of all areas including hard-to-reach spots and detailed attention.",
        "Comprehensive cleaning to refresh your home after winter.",
        "Professional cleaning for office spaces to maintain a productive environment.",
        "Detailed cleaning service for when you're moving in or out of a property.",
        "Specialized cleaning to remove construction debris and dust.",
        "Deep cleaning of carpets to remove stains and refresh fibers.",
        "Crystal clear window cleaning, inside and out.",
        "Detailed kitchen cleaning including appliances and cabinets.",
        "Complete sanitization of bathroom fixtures and surfaces.",
        "Professional floor polishing to restore shine and protect surfaces.",
        "Detailed dusting of all surfaces, fixtures, and hard-to-reach areas.",
        "Deep cleaning of upholstery to remove dirt and refresh fabrics.",
        "Specialized cleaning of ovens to remove grease and burnt residue.",
        "Thorough cleaning of refrigerators inside and out.",
        "Regular weekly cleaning maintenance to keep your space spotless.",
        "Bi-weekly cleaning service to maintain cleanliness with less frequency.",
        "Monthly deep cleaning to maintain your home or office.",
        "Single session comprehensive cleaning for special occasions.",
        "Luxury cleaning service with premium products and extra attention to detail."
    };
    
    private static final float[] PRICE_RANGES = {
        50.0f, 75.0f, 100.0f, 125.0f, 150.0f, 175.0f, 200.0f, 225.0f, 250.0f, 300.0f
    };
    
    private static final int[] DURATIONS = {60, 90, 120, 180, 240};
    
    private static final String[] AVAILABLE_DAYS = {
        "Mon,Tue,Wed,Thu,Fri", 
        "Mon,Wed,Fri", 
        "Tue,Thu", 
        "Mon,Tue,Wed,Thu,Fri,Sat", 
        "Sat,Sun"
    };
    
    private static final LocalTime[] START_TIMES = {
        LocalTime.of(8, 0),
        LocalTime.of(9, 0),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0)
    };
    
    private static final LocalTime[] END_TIMES = {
        LocalTime.of(15, 0),
        LocalTime.of(16, 0),
        LocalTime.of(17, 0),
        LocalTime.of(18, 0),
        LocalTime.of(20, 0)
    };
    
    private static Random random = new Random();
    
    public static CleaningService generateRandomService(String cleanerUsername) {
        String title = SERVICE_TITLES[random.nextInt(SERVICE_TITLES.length)];
        String description = SERVICE_DESCRIPTIONS[random.nextInt(SERVICE_DESCRIPTIONS.length)];
        float price = PRICE_RANGES[random.nextInt(PRICE_RANGES.length)];
        
        price += random.nextInt(50);
        
        CleaningService service = new CleaningService(title, description, price, cleanerUsername);
        
        int duration = DURATIONS[random.nextInt(DURATIONS.length)];
        String availableDays = AVAILABLE_DAYS[random.nextInt(AVAILABLE_DAYS.length)];
        LocalTime startTime = START_TIMES[random.nextInt(START_TIMES.length)];
 
        LocalTime endTime;
        do {
            endTime = END_TIMES[random.nextInt(END_TIMES.length)];
        } while (!startTime.isBefore(endTime));
        
        service.setServiceDuration(duration);
        service.setAvailableDays(availableDays);
        service.setAvailableStartTime(startTime);
        service.setAvailableEndTime(endTime);
        
        // Add categories safely
        try {
            List<ServiceCategory> allCategories = ServiceCategory.getAllCategories();
            if (allCategories != null && !allCategories.isEmpty()) {
                int numCategories = random.nextInt(3) + 1;
                List<Integer> addedCategoryIds = new ArrayList<>();
                
                for (int i = 0; i < numCategories && i < allCategories.size(); i++) {
                    // Pick a random category index
                    int categoryIndex = random.nextInt(allCategories.size());
                    ServiceCategory category = allCategories.get(categoryIndex);
                    
                    // Avoid duplicates
                    if (!addedCategoryIds.contains(category.getCategoryId())) {
                        addedCategoryIds.add(category.getCategoryId());
                        service.addCategory(category.getCategoryId());
                    }
                    
                    // Remove from the list 
                    allCategories.remove(categoryIndex);
                    
                    if (allCategories.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Note: Could not add categories to service: " + e.getMessage());
        }
        
        return service;
    }

    public static boolean generateRandomServices(String[] cleanerUsernames, int servicesPerCleaner) {
        CleaningServiceDAO serviceDAO = new CleaningServiceDAO();
        int createdCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 
            
            try {
                ensureServiceTableSchema(conn);
                
                for (String username : cleanerUsernames) {
                    for (int i = 0; i < servicesPerCleaner; i++) {
                        CleaningService service = generateRandomService(username);
                        boolean success = serviceDAO.createService(service);
                        if (success) {
                            createdCount++;
                            System.out.println("Created service: " + service.getTitle() + " for " + username);
                        }
                    }
                }
                
                conn.commit(); 
                System.out.println("Successfully created " + createdCount + " cleaning services.");
                return true;
                
            } catch (SQLException e) {
                conn.rollback(); 
                System.out.println("Error generating services: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); 
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Ensures that the cleaning_services table has all required columns
    private static void ensureServiceTableSchema(Connection conn) throws SQLException {
        // Check if service_duration column exists
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns " +
                         "WHERE table_schema = DATABASE() " +
                         "AND table_name = 'cleaning_services' " +
                         "AND column_name = 'service_duration'";
                         
        boolean hasServiceDuration = false;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next()) {
                hasServiceDuration = rs.getInt(1) > 0;
            }
        }
        
        if (!hasServiceDuration) {
            System.out.println("Adding missing columns to cleaning_services table...");
            
            try (Statement stmt = conn.createStatement()) {
                try {
                    stmt.execute("ALTER TABLE cleaning_services ADD COLUMN service_duration INT DEFAULT 60");
                    System.out.println("Added service_duration column");
                } catch (SQLException e) {
                    System.out.println("Note: " + e.getMessage());
                }
                
                try {
                    stmt.execute("ALTER TABLE cleaning_services ADD COLUMN available_days VARCHAR(50) DEFAULT 'Mon,Tue,Wed,Thu,Fri'");
                    System.out.println("Added available_days column");
                } catch (SQLException e) {
                    System.out.println("Note: " + e.getMessage());
                }
                
                try {
                    stmt.execute("ALTER TABLE cleaning_services ADD COLUMN available_start_time TIME DEFAULT '09:00:00'");
                    System.out.println("Added available_start_time column");
                } catch (SQLException e) {
                    System.out.println("Note: " + e.getMessage());
                }
                
                try {
                    stmt.execute("ALTER TABLE cleaning_services ADD COLUMN available_end_time TIME DEFAULT '17:00:00'");
                    System.out.println("Added available_end_time column");
                } catch (SQLException e) {
                    System.out.println("Note: " + e.getMessage());
                }
            }
        }
    }
    
    private static String[] getCleanerUsernames() {
        List<String> cleanerUsernames = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE account_type = 'Cleaner'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                cleanerUsernames.add(rs.getString("username"));
            }
            
            if (cleanerUsernames.isEmpty()) {
                UserDAO userDAO = new UserDAO();
                userDAO.initializeDemoAccounts();
                cleanerUsernames.add("cleaner123");
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving cleaner usernames: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cleanerUsernames.toArray(new String[0]);
    }
    
    public static void main(String[] args) {
        System.out.println("Starting random service generation...");
        
        String[] cleanerUsernames = getCleanerUsernames();
        
        if (cleanerUsernames.length == 0) {
            System.out.println("No cleaner accounts found. Please create cleaner accounts first.");
            return;
        }
        
        System.out.println("Found " + cleanerUsernames.length + " cleaner accounts.");
        System.out.println("Cleaner usernames: ");
        for (String username : cleanerUsernames) {
            System.out.println("- " + username);
        }
        
        int totalServicesWanted = 100;
        int servicesPerCleaner = totalServicesWanted / cleanerUsernames.length;
        
        int extraServices = totalServicesWanted % cleanerUsernames.length;
        
        System.out.println("Generating " + servicesPerCleaner + " services per cleaner");
        if (extraServices > 0) {
            System.out.println("Plus " + extraServices + " extra services for " + cleanerUsernames[0]);
        }
        
        boolean success = generateRandomServices(cleanerUsernames, servicesPerCleaner);
        
        if (success && extraServices > 0) {
            String[] firstCleanerOnly = {cleanerUsernames[0]};
            success = generateRandomServices(firstCleanerOnly, extraServices);
        }
        
        if (success) {
            System.out.println("Successfully completed random service generation.");
            System.out.println("Total services created: " + totalServicesWanted);
        } else {
            System.out.println("Failed to complete random service generation.");
        }
    }
}