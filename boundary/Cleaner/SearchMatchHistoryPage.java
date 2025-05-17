package boundary.Cleaner;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Properties;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import controller.Cleaner.CleaningServiceController;
import entity.Cleaner.CleaningService;
import entity.Homeowner.BookingService;
import entity.UserAdmin.UserAccount;
import utility.DateLabelFormatter;

public class SearchMatchHistoryPage extends JFrame {
    private CleaningServiceController controller;
    private UserAccount cleanerAccount;
    
    private JComboBox<ServiceItem> serviceComboBox;
    private JDatePickerImpl fromDatePicker;
    private JDatePickerImpl toDatePicker;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    public SearchMatchHistoryPage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new CleaningServiceController();
        initializeUI();
        
        // Load services for the dropdown
        loadServices();
        
        // Set default date range to last month to today
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        UtilDateModel toModel = (UtilDateModel) toDatePicker.getModel();
        toModel.setValue(calendar.getTime()); // Today's date for "To Date"

        // Clone the calendar to set the "From Date" to one month ago
        java.util.Calendar fromCalendar = (java.util.Calendar) calendar.clone();
        fromCalendar.add(java.util.Calendar.MONTH, -1); // Go back one month
        UtilDateModel fromModel = (UtilDateModel) fromDatePicker.getModel();
        fromModel.setValue(fromCalendar.getTime());

        // Make sure the models are marked as selected
        fromModel.setSelected(true);
        toModel.setSelected(true);
    }
    
    private void initializeUI() {
        setTitle("Match History");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Match History");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.CENTER);
        
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
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
        
        serviceComboBox = new JComboBox<>();
        serviceComboBox.addItem(new ServiceItem(0, "All Services"));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        filterPanel.add(serviceComboBox, gbc);
        
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
        fromDatePicker.setPreferredSize(new Dimension(180, 35)); // Make it wider
        
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
        toDatePicker.setPreferredSize(new Dimension(180, 35)); // Make it wider
        
        gbc.gridx = 3;
        gbc.gridy = 1;
        filterPanel.add(toDatePicker, gbc);
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchMatchHistory());
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
            "Match Results",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        String[] columnNames = {"Booking ID", "Date", "Time", "Service", "Homeowner", "Address", "Price"};
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
        scrollPane.setPreferredSize(new Dimension(850, 300));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(backButton);
        
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return resultsPanel;
    }
    
    private void loadServices() {
        List<CleaningService> services = controller.getServicesByCleanerUsername(cleanerAccount.getUsername());
        
        for (CleaningService service : services) {
            serviceComboBox.addItem(new ServiceItem(service.getServiceId(), service.getTitle()));
        }
    }
    
    private void searchMatchHistory() {
        // Get the filter values
        ServiceItem selectedService = (ServiceItem) serviceComboBox.getSelectedItem();
        
        java.util.Date fromDate = (java.util.Date) fromDatePicker.getModel().getValue();
        java.util.Date toDate = (java.util.Date) toDatePicker.getModel().getValue();
        
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this,
                "Please select both From and To dates.",
                "Missing Date Range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert dates to LocalDate
        LocalDate fromLocalDate = fromDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
            
        LocalDate toLocalDate = toDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
            
        if (fromLocalDate.isAfter(toLocalDate)) {
            JOptionPane.showMessageDialog(this,
                "From date must be before or equal to To date.",
                "Invalid Date Range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Prepare service ID filter
        List<Integer> serviceIds = null;
        if (selectedService.getId() != 0) { // 0 means "All Services"
            serviceIds = new ArrayList<>();
            serviceIds.add(selectedService.getId());
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        try {
            // Fetch and display results
            List<BookingService> matches = controller.getMatchHistory(
                cleanerAccount.getUsername(),
                fromLocalDate,
                toLocalDate,
                serviceIds
            );
            
            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No matches found for the selected criteria.\nTry expanding your date range or selecting 'All Services'.",
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (BookingService booking : matches) {
                    Object[] rowData = {
                        booking.getBookingId(),
                        booking.getBookingDate().format(DATE_FORMATTER),
                        booking.getStartTime().format(TIME_FORMATTER) + " - " +
                            booking.getEndTime().format(TIME_FORMATTER),
                        "Service ID: " + booking.getServiceId(),
                        booking.getHomeownerUsername(),
                        booking.getAddress(),
                        "$" + String.format("%.2f", booking.getTotalPrice())
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            // Add error handling
            JOptionPane.showMessageDialog(this,
                "Error searching matches: " + e.getMessage(),
                "Search Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Helper class to store service ID and title in the combo box
    private class ServiceItem {
        private int id;
        private String title;
        
        public ServiceItem(int id, String title) {
            this.id = id;
            this.title = title;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return title;
        }
    }
    
    // Display the match history search page and automatically perform search
    public void showMatchHistory() {
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> {
            searchButton.doClick();
        });
    }
}