package boundary.Homeowner;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.*;

import controller.Homeowner.PastServiceController;
import entity.ServiceRecord;
import entity.UserAdmin.UserAccount;
import utility.DateLabelFormatter;

public class PastServicesPage extends JFrame {
    private UserAccount userAccount;
    private PastServiceController controller;

    private JComboBox<String> serviceDropdown;
    private JDatePickerImpl fromDatePicker;
    private JDatePickerImpl toDatePicker;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public PastServicesPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.controller = new PastServiceController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Past Services");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        List<String> availableServices = controller.getAvailableServices(userAccount.getUsername());
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

        java.util.Calendar toCalendar = java.util.Calendar.getInstance();
        toModel.setValue(toCalendar.getTime());
        toModel.setSelected(true);

        gbc.gridx = 3;
        gbc.gridy = 1;
        filterPanel.add(toDatePicker, gbc);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());

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
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(backButton);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        return resultsPanel;
    }

    private void performSearch() {
        String selectedService = (String) serviceDropdown.getSelectedItem();
        List<String> serviceIds = null;
        if (!"All Services".equals(selectedService)) {
            serviceIds = controller.getServiceIdsByName(selectedService);
        }

        java.util.Date fromDate = (java.util.Date) fromDatePicker.getModel().getValue();
        java.util.Date toDate = (java.util.Date) toDatePicker.getModel().getValue();

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this,
                "Please select both From and To dates.",
                "Missing Date Range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

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

        showPastServices(serviceIds, fromLocalDate, toLocalDate);
    }

    public void showPastServices(List<String> serviceIds, LocalDate dateFrom, LocalDate dateTo) {
        tableModel.setRowCount(0);

        List<ServiceRecord> services = controller.getPastServices(
            userAccount.getUsername(),
            serviceIds,
            dateFrom,
            dateTo
        );

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

    public void displayPage() {
        setVisible(true);
    }
}
