package boundary.Cleaner;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import boundary.Login.EnhancedLoginPage;
import controller.Homeowner.ShortlistController;
import entity.UserAdmin.UserAccount;

public class CleanerPage extends JFrame {
    private JButton logoutButton;
    private UserAccount userAccount;
    private JLabel welcomeLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public CleanerPage() {
        this.userAccount = null;
        initializeUI();
    }
    
    public CleanerPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        initializeUI();
        updateUserInfo();
    }
    
    private void initializeUI() {
        setTitle("Cleaner Dashboard");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        welcomeLabel = new JLabel("Welcome, Cleaner User");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> switchToLoginPage());
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(BACKGROUND_COLOR);
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton myServicesButton = createMenuButton("My Cleaning Services");
        myServicesButton.addActionListener(e -> openMyServicesPage());
        
        JButton searchServicesButton = createMenuButton("Search Services");
        searchServicesButton.addActionListener(e -> openSearchServicesPage());
        
        JButton viewBookingsButton = createMenuButton("My Bookings");
        viewBookingsButton.addActionListener(e -> openBookingsPage());
        
        JButton totalShortlistedButton = createMenuButton("Total Shortlisted");
        totalShortlistedButton.addActionListener(e -> showShortlistCount());
        
        JButton trackViewsButton = createMenuButton("Track View Statistics");
        trackViewsButton.addActionListener(e -> openTrackViewInformationPage());
        
        JButton matchHistoryButton = createMenuButton("Match History");
        matchHistoryButton.addActionListener(e -> openMatchHistoryPage());
        
        contentPanel.add(myServicesButton);
        contentPanel.add(searchServicesButton);
        contentPanel.add(viewBookingsButton);
        contentPanel.add(totalShortlistedButton);
        contentPanel.add(trackViewsButton);
        contentPanel.add(matchHistoryButton);
        
        add(contentPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel statusLabel = new JLabel("Profile Status: Active");
        statusLabel.setForeground(TEXT_COLOR);
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
        
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
    
    // Method to open the new Track View Information page
    private void openTrackViewInformationPage() {
        TrackViewInformationPage trackViewPage = new TrackViewInformationPage(userAccount);
        trackViewPage.displayViewStats();
    }
    
    // Method to open the Match History page
    private void openMatchHistoryPage() {
        CleanerConfirmedMatchesPage matchHistoryPage = new CleanerConfirmedMatchesPage(userAccount);
        matchHistoryPage.displayPage();
    }
    
    // Updated method to show detailed shortlist information
    private void showShortlistCount() {
        ShortlistController controller = new ShortlistController();
        int count = controller.getShortlistCount(userAccount.getUsername());
        List<String> homeowners = controller.getHomeownersWhoShortlisted(userAccount.getUsername());
        
        if (count == 0) {
            JOptionPane.showMessageDialog(this,
                "You haven't been shortlisted by any homeowners yet.",
                "Shortlist Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a more detailed message with the list of homeowners
        StringBuilder message = new StringBuilder();
        message.append("You have been shortlisted by ").append(count).append(" homeowner(s):\n\n");
        
        for (String homeowner : homeowners) {
            message.append("- ").append(homeowner).append("\n");
        }
        
        // Create a scrollable text area for the message
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Shortlist Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openMyServicesPage() {
        MyCleaningServicesPage servicesPage = new MyCleaningServicesPage(userAccount);
        servicesPage.setVisible(true);
    }
    
    private void openSearchServicesPage() {
        SearchCleaningServicePage searchPage = new SearchCleaningServicePage(userAccount);
        searchPage.displaySearchPage();
    }
    
    // Add this method to the CleanerPage class in the correct location with other methods
    private void openBookingsPage() {
        ViewCleanerBookingsPage bookingsPage = new ViewCleanerBookingsPage(userAccount);
        bookingsPage.displayBookingsPage();
    }
    
    private void updateUserInfo() {
        if (userAccount != null) {
            String name = userAccount.getName();
            if (name != null && !name.isEmpty()) {
                welcomeLabel.setText("Welcome, " + name);
            } else {
                welcomeLabel.setText("Welcome, " + userAccount.getUsername());
            }
        }
    }
    
    public void switchToLoginPage() {
        dispose(); 
        SwingUtilities.invokeLater(() -> {
            EnhancedLoginPage loginPage = new EnhancedLoginPage(); 
            loginPage.displayLoginPage();
        });
    }
    
    public void displayCleanerPage() {
        setVisible(true);
    }
}