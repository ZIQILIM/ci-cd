package boundary.Login;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import boundary.Cleaner.CleanerPage;
import boundary.Homeowner.HomeownerPage;
import boundary.PlatformManager.PlatformManagerPage;
import boundary.UserAdmin.UserAdminPage;
import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

// Boundary class
public class LoginPage extends JFrame {
    private UserAccountController controller;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> profileComboBox;
    private JButton loginButton;
    
    public LoginPage() {
        try {
            controller = new UserAccountController();
            initializeUI();
        } catch (Exception ex) {
            System.err.println("Error initializing LoginPage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void initializeUI() {
        try {
            setTitle("Login Page");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(4, 2, 10, 10));
            
            // Username
            add(new JLabel("Username:"));
            usernameField = new JTextField(20);
            add(usernameField);
            
            // Password
            add(new JLabel("Password:"));
            passwordField = new JPasswordField(20);
            add(passwordField);
            
            // Profile selection
            add(new JLabel("Profile:"));
            String[] profiles = {"User Admin", "Cleaner", "Homeowner", "Platform Manager"};
            profileComboBox = new JComboBox<>(profiles);
            add(profileComboBox);
            
            // Login button
            loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        handleLogin();
                    } catch (Exception ex) {
                        System.err.println("Error in login handler: " + ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(LoginPage.this, 
                            "An error occurred during login. Please try again.", 
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            add(new JLabel(""));
            add(loginButton);
            
            setLocationRelativeTo(null);
        } catch (Exception ex) {
            System.err.println("Error in initializeUI: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String profileType = (String) profileComboBox.getSelectedItem();
            
            UserAccount account = controller.validateLogin(username, password, profileType);
            
            if (account != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                
                // Redirect to the page based on account type
                String accountType = account.getAccountType();
                if (accountType.equals("User Admin")) {
                    switchToUserAdminPage(account);
                } else if (accountType.equals("Cleaner")) {
                    switchToCleanerPage(account);
                } else if (accountType.equals("Homeowner")) {
                    switchToHomeownerPage(account);
                } else if (accountType.equals("Platform Manager")) {
                    switchToPlatformManagerPage(account);
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", 
                                         "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error in handleLogin: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "An error occurred during login. Please try again.", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to switch to UserAdmin page
    public void switchToUserAdminPage(UserAccount userAccount) {
        try {
            dispose(); 
            UserAdminPage adminPage = new UserAdminPage(userAccount);
            adminPage.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error switching to UserAdminPage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Method to switch to Cleaner page
    public void switchToCleanerPage(UserAccount userAccount) {
        try {
            dispose(); 
            CleanerPage cleanerPage = new CleanerPage(userAccount);
            cleanerPage.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error switching to CleanerPage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Method to switch to Homeowner page
    public void switchToHomeownerPage(UserAccount userAccount) {
        try {
            dispose(); 
            HomeownerPage homeownerPage = new HomeownerPage(userAccount);
            homeownerPage.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error switching to HomeownerPage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Method to switch to PlatformManager page
    public void switchToPlatformManagerPage(UserAccount userAccount) {
        try {
            dispose(); 
            PlatformManagerPage managerPage = new PlatformManagerPage(userAccount);
            managerPage.setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error switching to PlatformManagerPage: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Display login page method
    public void displayLoginPage() {
        try {
            setVisible(true);
        } catch (Exception ex) {
            System.err.println("Error displaying login page: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}