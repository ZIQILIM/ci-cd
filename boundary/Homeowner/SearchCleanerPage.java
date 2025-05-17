package boundary.Homeowner;

import boundary.Cleaner.CleanerProfilePage;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.Homeowner.BookingController;
import controller.Homeowner.ShortlistController;
import entity.Cleaner.CleanerAccount;
import entity.Cleaner.CleaningService;
import entity.Homeowner.SearchCriteria;
import entity.UserAdmin.UserAccount;

public class SearchCleanerPage extends JFrame {
    private BookingController controller;
    private UserAccount homeownerAccount;
    private JTextField locationField;
    private JTextField serviceTypeField;
    private JTextField minPriceField;
    private JButton searchButton;
    private JButton backButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel resultsCountLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    // Store search results for booking
    private List<Map<String, Object>> searchResults;
    
    public SearchCleanerPage(UserAccount homeownerAccount) {
        this.homeownerAccount = homeownerAccount;
        this.controller = new BookingController();
        this.searchResults = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Search Cleaners");
        setSize(800, 470); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel headerLabel = new JLabel("Find a Cleaner");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        
        JPanel resultsPanel = createResultsPanel();
        contentPanel.add(resultsPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel criteriaPanel = new JPanel(new GridBagLayout());
        criteriaPanel.setBackground(BACKGROUND_COLOR);
        criteriaPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Criteria",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            PRIMARY_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 12));
        locationLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        criteriaPanel.add(locationLabel, gbc);
        
        locationField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        criteriaPanel.add(locationField, gbc);
        
        JLabel serviceTypeLabel = new JLabel("Service Type:");
        serviceTypeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        serviceTypeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        criteriaPanel.add(serviceTypeLabel, gbc);
        
        serviceTypeField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        criteriaPanel.add(serviceTypeField, gbc);

        JLabel priceRangeLabel = new JLabel("Price Range ($):");
        priceRangeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceRangeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        criteriaPanel.add(priceRangeLabel, gbc);
        
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pricePanel.setBackground(BACKGROUND_COLOR);
        
        pricePanel.add(new JLabel("Min:"));
        minPriceField = new JTextField(5);
        pricePanel.add(minPriceField);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        criteriaPanel.add(pricePanel, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        criteriaPanel.add(Box.createHorizontalGlue(), gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.0;
        criteriaPanel.add(buttonPanel, gbc);
        
        searchPanel.add(criteriaPanel);
        
        return searchPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(BACKGROUND_COLOR);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Results",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            PRIMARY_COLOR));
        
        resultsCountLabel = new JLabel("Enter search criteria and click Search");
        resultsCountLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        resultsPanel.add(resultsCountLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Cleaner Name", "Location", "Service Title", "Price", "Duration", "Available Days"};
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
        
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    bookSelectedService();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewProfileItem = new JMenuItem("View Cleaner Profile");
        viewProfileItem.addActionListener(e -> viewCleanerProfile());
        
        JMenuItem viewDetailsItem = new JMenuItem("View Service Details");
        viewDetailsItem.addActionListener(e -> viewServiceDetails());
        
        JMenuItem bookServiceItem = new JMenuItem("Book This Service");
        bookServiceItem.addActionListener(e -> bookSelectedService());
        
        JMenuItem addToShortlistItem = new JMenuItem("Add to Shortlist");
        addToShortlistItem.addActionListener(e -> addToShortlist());
        
        popupMenu.add(viewProfileItem);
        popupMenu.add(viewDetailsItem);
        popupMenu.add(bookServiceItem);
        popupMenu.add(addToShortlistItem);
        
        resultsTable.setComponentPopupMenu(popupMenu);
        
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = resultsTable.rowAtPoint(point);
                if (row >= 0 && row < resultsTable.getRowCount()) {
                    resultsTable.setRowSelectionInterval(row, row);
                }
            }
        });
        
        return resultsPanel;
    }
    
    private void performSearch() {
        tableModel.setRowCount(0);
        
        SearchCriteria criteria = new SearchCriteria();
        criteria.setLocation(locationField.getText().trim());
        criteria.setServiceType(serviceTypeField.getText().trim());
        
        try {
            String minPrice = minPriceField.getText().trim();
            if (!minPrice.isEmpty()) {
                criteria.setMinPrice(Float.parseFloat(minPrice));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid minimum price.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        searchResults = controller.handleSearch(criteria);
        displayResults(searchResults);
    }
    
    private void displayResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            resultsCountLabel.setText("No cleaning services found for your search criteria.");
            showMessage("No cleaning services found for your search criteria.");
            return;
        }
        
        int totalServices = 0;
        for (Map<String, Object> result : results) {
            @SuppressWarnings("unchecked")
            List<CleaningService> services = (List<CleaningService>) result.get("services");
            totalServices += services.size();
            
            CleanerAccount cleaner = (CleanerAccount) result.get("cleaner");
            
            for (CleaningService service : services) {
                Object[] rowData = {
                    cleaner.getName(),
                    cleaner.getAddress(),
                    service.getTitle(),
                    "$" + String.format("%.2f", service.getPrice()),
                    service.getServiceDuration() + " min",
                    service.getAvailableDays()
                };
                tableModel.addRow(rowData);
            }
        }
        
        resultsCountLabel.setText("Found " + totalServices + " matching services from " + results.size() + " cleaners");
    }
    
    private void viewCleanerProfile() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a cleaner from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        CleanerAccount selectedCleaner = findSelectedCleaner(selectedRow);
        
        if (selectedCleaner == null) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving cleaner details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open cleaner profile page
        CleanerProfilePage profilePage = new CleanerProfilePage(homeownerAccount, selectedCleaner.getUsername());
        profilePage.displayProfile();
    }
    
    private void viewServiceDetails() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a service from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        String serviceTitle = (String) tableModel.getValueAt(selectedRow, 2);
        
        CleaningService selectedService = findSelectedService(selectedRow);
        
        if (selectedService == null) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving service details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a more detailed view of the service
        StringBuilder details = new StringBuilder();
        details.append("<html><body style='width: 300px'>");
        details.append("<h2>").append(serviceTitle).append("</h2>");
        details.append("<p><b>Cleaner:</b> ").append(cleanerName).append("</p>");
        details.append("<p><b>Price:</b> ").append(tableModel.getValueAt(selectedRow, 3)).append("</p>");
        details.append("<p><b>Duration:</b> ").append(tableModel.getValueAt(selectedRow, 4)).append("</p>");
        details.append("<p><b>Available Days:</b> ").append(tableModel.getValueAt(selectedRow, 5)).append("</p>");
        details.append("<p><b>Description:</b><br>").append(selectedService.getDescription()).append("</p>");
        details.append("<p><b>Available Hours:</b> ");
        details.append(selectedService.getAvailableStartTime().format(TIME_FORMATTER));
        details.append(" - ");
        details.append(selectedService.getAvailableEndTime().format(TIME_FORMATTER));
        details.append("</p>");
        details.append("</body></html>");
        
        // Display detailed information 
        JOptionPane.showMessageDialog(this,
            details.toString(),
            "Service Details",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void bookSelectedService() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a service from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected service info
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        String serviceTitle = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Find the selected service and cleaner in our search results
        CleaningService selectedService = findSelectedService(selectedRow);
        CleanerAccount selectedCleaner = findSelectedCleaner(selectedRow);
        
        if (selectedService == null || selectedCleaner == null) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving service details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BookServicePage bookingPage = new BookServicePage(
            homeownerAccount, 
            selectedCleaner, 
            selectedService);
        
        bookingPage.setVisible(true);
    }
    
    // Method to add the selected cleaner to the homeowner's shortlist
    private void addToShortlist() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a cleaner from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find the selected cleaner in our search results
        CleanerAccount selectedCleaner = findSelectedCleaner(selectedRow);
        
        if (selectedCleaner == null) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving cleaner details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create controller for shortlist operations
        ShortlistController shortlistController = new ShortlistController();
        
        // Check if cleaner is already in shortlist
        if (shortlistController.isCleanerInShortlist(homeownerAccount.getUsername(), selectedCleaner.getUsername())) {
            JOptionPane.showMessageDialog(this,
                cleanerName + " is already in your shortlist.",
                "Already Shortlisted",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Add to shortlist
        boolean success = shortlistController.addCleanerToShortlist(
            homeownerAccount.getUsername(), 
            selectedCleaner.getUsername());
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                cleanerName + " has been added to your shortlist.",
                "Shortlist Updated",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to add " + cleanerName + " to your shortlist.",
                "Shortlist Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method to find the selected service
    private CleaningService findSelectedService(int selectedRow) {
        if (selectedRow < 0 || searchResults == null) {
            return null;
        }
        
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        String serviceTitle = (String) tableModel.getValueAt(selectedRow, 2);
        
        for (Map<String, Object> result : searchResults) {
            CleanerAccount cleaner = (CleanerAccount) result.get("cleaner");
            if (cleaner.getName().equals(cleanerName)) {
                @SuppressWarnings("unchecked")
                List<CleaningService> services = (List<CleaningService>) result.get("services");
                for (CleaningService service : services) {
                    if (service.getTitle().equals(serviceTitle)) {
                        return service;
                    }
                }
            }
        }
        
        return null;
    }
    
    // Helper method to find the selected cleaner
    private CleanerAccount findSelectedCleaner(int selectedRow) {
        if (selectedRow < 0 || searchResults == null) {
            return null;
        }
        
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        
        for (Map<String, Object> result : searchResults) {
            CleanerAccount cleaner = (CleanerAccount) result.get("cleaner");
            if (cleaner.getName().equals(cleanerName)) {
                return cleaner;
            }
        }
        
        return null;
    }
    
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    public void displayCleanerHomePage() {
        setVisible(true);
    }
}