package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.DatabaseConnection;

public class RandomDataGenerator {
    
    private static final String[] STREET_NUMBERS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", 
                                                   "15", "20", "25", "30", "40", "50", "100", "123", "234", "345", "456"};
    
    private static final String[] STREET_NAMES = {"Main", "Park", "Oak", "Pine", "Maple", "Cedar", "Elm", "Washington", 
                                                 "Lake", "Hill", "River", "Meadow", "Forest", "Beach", "Sunset", "Spring", 
                                                 "Valley", "Highland", "Orchard"};
    
    private static final String[] STREET_TYPES = {"St", "Ave", "Blvd", "Dr", "Ln", "Rd", "Way", "Place", "Court", "Circle"};
    
    private static final String[] CITIES = {"Singapore", "Woodlands", "Jurong", "Tampines", "Bedok", "Hougang", "Ang Mo Kio", 
                                           "Queenstown", "Bukit Timah", "Clementi", "Punggol", "Pasir Ris", "Sengkang", 
                                           "Bukit Batok", "Yishun", "Toa Payoh", "Sembawang"};
    
    private static final String[] POSTAL_CODES = {"100000", "200000", "300000", "400000", "500000", "600000", "700000", 
                                                 "800000", "900000"};
    
    private static final String[] PHONE_PREFIXES = {"8", "9", "6"};
    
    private static Random random = new Random();
    
    public static String generateRandomAddress() {
        String streetNumber = STREET_NUMBERS[random.nextInt(STREET_NUMBERS.length)];
        String streetName = STREET_NAMES[random.nextInt(STREET_NAMES.length)];
        String streetType = STREET_TYPES[random.nextInt(STREET_TYPES.length)];
        String city = CITIES[random.nextInt(CITIES.length)];
        
        String postalBase = POSTAL_CODES[random.nextInt(POSTAL_CODES.length)];
        String postalCode = String.valueOf(Integer.parseInt(postalBase) + random.nextInt(99999));
        
        return streetNumber + " " + streetName + " " + streetType + ", " + city + " " + postalCode + ", Singapore";
    }
    
    public static String generateRandomPhoneNumber() {
        String prefix = PHONE_PREFIXES[random.nextInt(PHONE_PREFIXES.length)];
        
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 7; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
    
    public static boolean updateAllUsersWithRandomData() {
        String sql = "SELECT username FROM users";
        List<String> usernames = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        String updateSql = "UPDATE users SET address = ?, phone_number = ? WHERE username = ?";
        int updatedCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                for (String username : usernames) {
                    String address = generateRandomAddress();
                    String phoneNumber = generateRandomPhoneNumber();
                    
                    pstmt.setString(1, address);
                    pstmt.setString(2, phoneNumber);
                    pstmt.setString(3, username);
                    
                    updatedCount += pstmt.executeUpdate();
                }
                
                conn.commit(); 
                System.out.println("Successfully updated " + updatedCount + " user records with random data.");
                return true;
                
            } catch (SQLException e) {
                conn.rollback(); 
                System.out.println("Error updating users with random data: " + e.getMessage());
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
    
    public static void main(String[] args) {
        System.out.println("Starting random data generation...");
        boolean success = updateAllUsersWithRandomData();
        if (success) {
            System.out.println("Successfully completed random data generation.");
        } else {
            System.out.println("Failed to complete random data generation.");
        }
    }
}