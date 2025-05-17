package boundary.PlatformManager;

import java.awt.*;
import javax.swing.*;

import boundary.Login.EnhancedLoginPage;
import entity.UserAdmin.UserAccount;

public class PlatformManagerPage extends JFrame {
    private JButton logoutButton;
    private UserAccount userAccount;
    private JLabel welcomeLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);       
    
    public PlatformManagerPage() {
        this.userAccount = null;
        initializeUI();
    }
    
    public PlatformManagerPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        initializeUI();
        updateUserInfo();
    }
    
    private void initializeUI() {
        setTitle("Platform Manager Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        welcomeLabel = new JLabel("Welcome, Platform Manager");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            new EnhancedLoginPage().setVisible(true);
        });
        
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(BACKGROUND_COLOR);
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton viewCategoriesButton = createMenuButton("View Service Categories");
        viewCategoriesButton.addActionListener(e -> openCategoryListPage());
        
        JButton manageCategoriesButton = createMenuButton("Manage Service Categories");
        manageCategoriesButton.addActionListener(e -> openCategoryManagement());
        
        JButton searchCategoriesButton = createMenuButton("Search Categories");
        searchCategoriesButton.addActionListener(e -> openCategorySearchPage());

        JButton viewDailyReportButton = createMenuButton("View Daily Report");
        viewDailyReportButton.addActionListener(e -> openDailyReportPage());

        JButton viewWeeklyReportButton = createMenuButton("View Weekly Report");
        viewWeeklyReportButton.addActionListener(e -> openWeeklyReportPage());

        JButton viewMonthlyReportButton = createMenuButton("View Monthly Report");
        viewMonthlyReportButton.addActionListener(e -> openMonthlyReportPage());

        contentPanel.add(viewCategoriesButton);
        contentPanel.add(manageCategoriesButton);
        contentPanel.add(searchCategoriesButton);
        contentPanel.add(viewDailyReportButton);
        contentPanel.add(viewWeeklyReportButton);
        contentPanel.add(viewMonthlyReportButton);
        
        add(contentPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BACKGROUND_COLOR);
        JLabel statusLabel = new JLabel("System Status: Online");
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
    
    private void openCategoryManagement() {
        CategoryManagementPage categoryPage = new CategoryManagementPage(userAccount);
        categoryPage.setVisible(true);
    }
    
    private void openCategoryListPage() {
        CategoryListPage categoryPage = new CategoryListPage(userAccount);
        categoryPage.displayCategoryPage();
    }
    
    private void openCategorySearchPage() {
        CategorySearchPage searchPage = new CategorySearchPage(userAccount);
        searchPage.displaySearchPage();
    }
    
    private void openDailyReportPage() {
    viewDailyReportPage DailyReportPage = new viewDailyReportPage(userAccount, this);
        DailyReportPage.displayDailyReportPage();
    }

    private void openWeeklyReportPage() {
        this.setVisible(false);

        viewWeeklyReportPage weekly = new viewWeeklyReportPage(userAccount, this);
        weekly.setVisible(true);
    }

    private void openMonthlyReportPage() {
        viewMonthlyReportPage monthlyPage = new viewMonthlyReportPage(userAccount, this);
        monthlyPage.setVisible(true);
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
    
    public void displayPlatformManagerPage() {
        setVisible(true);
    }

    

}



