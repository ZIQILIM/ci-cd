package boundary.Cleaner;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.Homeowner.ShortlistController;
import entity.UserAdmin.UserAccount;

public class CleanerDashboardUI extends JFrame {
    private ShortlistController shortlistController;
    private UserAccount cleanerAccount;
    
    private JButton totalShortlistedButton;
    private JLabel welcomeLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public CleanerDashboardUI(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.shortlistController = new ShortlistController();
        initializeUI();
        updateUserInfo();
    }
    
    private void initializeUI() {
        setTitle("Shortlist Information");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        welcomeLabel = new JLabel("Shortlist Information");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Create the "Total Shortlisted" button
        totalShortlistedButton = createMenuButton("Show Shortlist Count");
        totalShortlistedButton.addActionListener(e -> showCount());
        
        contentPanel.add(totalShortlistedButton);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 248, 255));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));
        return button;
    }
    
    private void updateUserInfo() {
        if (cleanerAccount != null) {
            String name = cleanerAccount.getName();
            if (name != null && !name.isEmpty()) {
                welcomeLabel.setText("Shortlist Information for " + name);
            } else {
                welcomeLabel.setText("Shortlist Information for " + cleanerAccount.getUsername());
            }
        }
    }
    
    // Method to handle the "Total Shortlisted" button click
    private void showCount() {
        int count = shortlistController.getShortlistCount(cleanerAccount.getUsername());
        
        JOptionPane.showMessageDialog(this,
            "You have been shortlisted by " + count + " homeowner(s).",
            "Shortlist Count",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Method to display the dashboard
    public void displayDashboard() {
        setVisible(true);
    }
}