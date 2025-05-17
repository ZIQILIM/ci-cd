package boundary.Homeowner;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import boundary.Login.EnhancedLoginPage;
import controller.Homeowner.PastServiceController;
import entity.ServiceRecord;
import entity.UserAdmin.UserAccount;
import utility.DateLabelFormatter;

public class HomeownerPage extends JFrame {
    private JButton logoutButton;
    private UserAccount userAccount;
    private JLabel welcomeLabel;
    private JComboBox<String> serviceDropdown;
    private JDatePickerImpl fromDatePicker;
    private JDatePickerImpl toDatePicker;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    
    // Controller
    private PastServiceController pastServiceController;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    // Constructor
    public HomeownerPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.pastServiceController = new PastServiceController();
        initializeUI();
        updateUserInfo();
    }
    
    private void initializeUI() {
        setTitle("Homeowner Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        welcomeLabel = new JLabel("Welcome, Homeowner User");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchToLoginPage();
            }
        });
        
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(BACKGROUND_COLOR);
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton searchCleanerButton = createMenuButton("Search Cleaners");
        searchCleanerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSearchCleanerPage();
            }
        });
        
        JButton viewShortlistButton = createMenuButton("View Shortlist");
        viewShortlistButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openViewShortlistPage();
            }
        });
        
        JButton viewBookingsButton = createMenuButton("View Bookings");
        viewBookingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openViewBookingsPage();
            }
        });
        
        JButton viewHistoryButton = createMenuButton("View Booking History");
        viewHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openViewHistoryPage();
            }
        });
        
        JButton viewPastServicesButton = createMenuButton("View Past Services");
        viewPastServicesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPastServicesPage();
            }
        });

        JButton serviceHistoryButton = createMenuButton("Service History");
        serviceHistoryButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
                openServiceHistoryPage();
            }
        });

        
        contentPanel.add(searchCleanerButton);
        contentPanel.add(viewShortlistButton);
        contentPanel.add(viewBookingsButton);
        contentPanel.add(viewHistoryButton);
        contentPanel.add(viewPastServicesButton);
        contentPanel.add(serviceHistoryButton);
        
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(emptyPanel);
        
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
    
    // Method to open the Match History page
    private void openServiceHistoryPage() {
        SearchServiceHistoryPage serviceHistoryPage = new SearchServiceHistoryPage(userAccount);
        serviceHistoryPage.showServiceHistory();
    }

    // Method to open the Past Services page
    private void openPastServicesPage() {
        JFrame pastServicesFrame = new JFrame("Past Services");
        pastServicesFrame.setSize(800, 600);
        pastServicesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Past Cleaning Services");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.CENTER);
        
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);
        
        pastServicesFrame.setContentPane(mainPanel);
        pastServicesFrame.setLocationRelativeTo(null);
        pastServicesFrame.setVisible(true);
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Filters",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel serviceLabel = new JLabel("Service:");
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        serviceLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(serviceLabel, gbc);
        
        // Get available services from the controller
        List<String> availableServices = pastServiceController.getAvailableServices(userAccount.getUsername());
        String[] serviceArray = availableServices.toArray(new String[0]);
        
        serviceDropdown = new JComboBox<>(serviceArray);
        serviceDropdown.insertItemAt("All Services", 0);
        serviceDropdown.setSelectedIndex(0);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        filterPanel.add(serviceDropdown, gbc);
        
        JLabel fromDateLabel = new JLabel("From Date:");
        fromDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fromDateLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        filterPanel.add(fromDateLabel, gbc);
        
        UtilDateModel fromModel = new UtilDateModel();
        Properties fromProperties = new Properties();
        fromProperties.put("text.today", "Today");
        fromProperties.put("text.month", "Month");
        fromProperties.put("text.year", "Year");
        JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromModel, fromProperties);
        fromDatePicker = new JDatePickerImpl(fromDatePanel, new DateLabelFormatter());
        
        // Set default from date (3 months ago)
        java.util.Calendar fromCalendar = java.util.Calendar.getInstance();
        fromCalendar.add(java.util.Calendar.MONTH, -3);
        fromModel.setValue(fromCalendar.getTime());
        fromModel.setSelected(true);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        filterPanel.add(fromDatePicker, gbc);
        
        JLabel toDateLabel = new JLabel("To Date:");
        toDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        toDateLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 2;
        gbc.gridy = 1;
        filterPanel.add(toDateLabel, gbc);
        
        UtilDateModel toModel = new UtilDateModel();
        Properties toProperties = new Properties();
        toProperties.put("text.today", "Today");
        toProperties.put("text.month", "Month");
        toProperties.put("text.year", "Year");
        JDatePanelImpl toDatePanel = new JDatePanelImpl(toModel, toProperties);
        toDatePicker = new JDatePickerImpl(toDatePanel, new DateLabelFormatter());
        
        // Set default to date (today)
        java.util.Calendar toCalendar = java.util.Calendar.getInstance();
        toModel.setValue(toCalendar.getTime());
        toModel.setSelected(true);
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        filterPanel.add(toDatePicker, gbc);
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedService = (String) serviceDropdown.getSelectedItem();
                List<String> serviceIds = null;
                if (!"All Services".equals(selectedService)) {
                    serviceIds = pastServiceController.getServiceIdsByName(selectedService);
                }
                
                java.util.Date fromDate = (java.util.Date) fromDatePicker.getModel().getValue();
                java.util.Date toDate = (java.util.Date) toDatePicker.getModel().getValue();
                
                LocalDate fromLocalDate = fromDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                    
                LocalDate toLocalDate = toDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                
                // Call controller to get past services
                showPastServices(serviceIds, fromLocalDate, toLocalDate);
            }
        });
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        filterPanel.add(searchButton, gbc);
        
        return filterPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(BACKGROUND_COLOR);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Service Results",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        String[] columnNames = {"Booking ID", "Service", "Cleaner", "Date", "Time", "Status", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        scrollPane.setPreferredSize(new Dimension(750, 300));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(buttonPanel);
                if (window != null) {
                    window.dispose();
                }
            }
        });
        
        buttonPanel.add(backButton);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return resultsPanel;
    }
    
    // Method to display past services based on filters
    public void showPastServices(List<String> serviceIds, LocalDate dateFrom, LocalDate dateTo) {
        tableModel.setRowCount(0);
        
        // Call controller to get past services
        List<ServiceRecord> services = pastServiceController.getPastServices(
            userAccount.getUsername(),
            serviceIds,
            dateFrom,
            dateTo
        );
        
        // Display results in the table
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No services found for the selected criteria.",
                "No Results",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (ServiceRecord service : services) {
                Object[] rowData = {
                    service.getBookingId(),
                    service.getServiceName(),
                    service.getCleanerName(),
                    service.getBookingDate().format(DATE_FORMATTER),
                    service.getStartTime() + " - " + service.getEndTime(),
                    service.getStatus(),
                    "$" + String.format("%.2f", service.getPrice())
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    // Method to open the Search Cleaner page
    private void openSearchCleanerPage() {
        SearchCleanerPage searchPage = new SearchCleanerPage(userAccount);
        searchPage.displayCleanerHomePage();
    }
    
    // Method to open the View Shortlist page
    private void openViewShortlistPage() {
        ViewShortlistPage shortlistPage = new ViewShortlistPage(userAccount);
        shortlistPage.displayPage();
    }
    
    // Method to open the View Bookings page - Modified to only show active bookings
    private void openViewBookingsPage() {
        ViewBookingsPage bookingsPage = new ViewBookingsPage(userAccount) {
            // Override the loadBookings method to filter out completed bookings
        };
        bookingsPage.displayBookingsPage();
    }
    
    // Method to open the View History page
    private void openViewHistoryPage() {
        ViewHistoryPage historyPage = new ViewHistoryPage(userAccount);
        historyPage.displayHistoryPage();
    }
    
    // Update user info
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
    
    // Logout method
    private void switchToLoginPage() {
        dispose();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EnhancedLoginPage loginPage = new EnhancedLoginPage();
                loginPage.displayLoginPage();
            }
        });
    }
    
    public void displayHomePage() {
        setVisible(true);
    }
}