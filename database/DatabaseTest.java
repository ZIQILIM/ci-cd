package database;

import java.util.List;
import java.util.Scanner;

import entity.UserDAO;
import entity.Cleaner.CleanerAccount;
import entity.Homeowner.HomeownerAccount;
import entity.PlatformManager.PlatformManagerAccount;
import entity.UserAdmin.UserAccount;
import entity.UserAdmin.UserAdminAccount;

public class DatabaseTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Initializing demo accounts...");
        userDAO.initializeDemoAccounts();
        
        System.out.println("\nCurrent users before adding 100 records:");
        System.out.println("-----------------------------------------");
        displayAllUsers(userDAO);
        
        System.out.println("\nPress Enter to continue to add 100 users...");
        scanner.nextLine();
        
        addHundredUsers(userDAO);
        
        System.out.println("\nAfter adding 100 users - check MySQL Workbench now!");
        System.out.println("Press Enter after checking in MySQL Workbench...");
        scanner.nextLine();
        
        System.out.println("\nUpdated users in database:");
        System.out.println("-------------------------");
        displayAllUsers(userDAO);
        
        System.out.println("\nDatabase test complete!");
        scanner.close();
    }
    
    private static void displayAllUsers(UserDAO userDAO) {
        List<UserAccount> allUsers = userDAO.getAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("No users found in the database.");
        } else {
            for (UserAccount u : allUsers) {
                System.out.println(u.getUsername() + " | " + 
                                u.getAccountType() + " | " + 
                                u.getName() + " | " + 
                                u.getEmail());
            }
        }
        System.out.println("Total users: " + allUsers.size());
    }
    
    private static void addHundredUsers(UserDAO userDAO) {
        System.out.println("\nAdding 100 test users with realistic information...");
        int successCount = 0;
        
        String[] firstNames = {"John", "Mary", "James", "Patricia", "Robert", "Jennifer", "Michael", "Linda", 
                            "William", "Elizabeth", "David", "Susan", "Richard", "Jessica", "Joseph", "Sarah",
                            "Thomas", "Karen", "Charles", "Nancy", "Daniel", "Lisa", "Matthew", "Betty",
                            "Anthony", "Margaret", "Mark", "Sandra", "Donald", "Ashley", "Steven", "Emily"};
        
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
                            "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
                            "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White"};
        
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com", "aol.com"};
        
        for (int i = 1; i <= 100; i++) {
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[i % lastNames.length];
            String fullName = firstName + " " + lastName;
            
            String username = (firstName + lastName + i).toLowerCase();
            
            String domain = domains[i % domains.length];
            String email = (firstName + "." + lastName + i + "@" + domain).toLowerCase();

UserAccount newUser;
int accountType = i % 4;
switch (accountType) {
    case 0:
        newUser = new UserAdminAccount(
            username,
            "password123",
            fullName,
            email
        );
        break;
    case 1:
        newUser = new CleanerAccount(
            username,
            "password123",
            fullName,
            email
        );
        break;
    case 2:
        newUser = new HomeownerAccount(
            username,
            "password123",
            fullName,
            email
        );
        break;
    default:
        newUser = new PlatformManagerAccount(
            username,
            "password123",
            fullName,
            email
        );
        break;
}

            
            boolean added = userDAO.createUser(newUser);
            if (added) {
                successCount++;
                if (i % 10 == 0) {
                    System.out.println("Added " + i + " users so far...");
                }
            }
        }
        
        System.out.println("Successfully added " + successCount + " out of 100 users");
    }
}