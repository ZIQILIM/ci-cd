package boundary.Cleaner;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import controller.Cleaner.CleaningServiceController;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class SearchCleaningServicePage extends JFrame {
    private CleaningServiceController controller;
    private UserAccount cleanerAccount;
    
    private JTextField serviceIdField;
    private JTextField titleField;
    private JButton searchButton;
    private JButton backButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public SearchCleaningServicePage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new CleaningServiceController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Search Cleaning Services");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Search Cleaning Services");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel criteriaPanel = new JPanel(new GridBagLayout());
        criteriaPanel.setBackground(BACKGROUND_COLOR);
        criteriaPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Criteria",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel idLabel = new JLabel("Service ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        criteriaPanel.add(idLabel, gbc);
        
        serviceIdField = new JTextField(10);
        serviceIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        criteriaPanel.add(serviceIdField, gbc);
        
        JLabel titleLabel = new JLabel("Service Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        criteriaPanel.add(titleLabel, gbc);
        
        titleField = new JTextField(20);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        criteriaPanel.add(titleField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchCleaningServices());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> goBack());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(backButton);
        
        searchPanel.add(criteriaPanel, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);
        
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
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        String[] columnNames = {"ID", "Service Name", "Description", "Price", "Duration", "Available Days", "Hours"};
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
        
        TableColumnModel columnModel = resultsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);   // ID
        columnModel.getColumn(1).setPreferredWidth(150);  // Name
        columnModel.getColumn(2).setPreferredWidth(200);  // Description
        columnModel.getColumn(3).setPreferredWidth(80);   // Price
        columnModel.getColumn(4).setPreferredWidth(80);   // Duration
        columnModel.getColumn(5).setPreferredWidth(120);  // Available Days
        columnModel.getColumn(6).setPreferredWidth(120);  // Hours
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        scrollPane.setPreferredSize(new Dimension(850, 200));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultsPanel;
    }
    
    private void searchCleaningServices() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get search criteria
        int serviceId = 0;
        String title = titleField.getText().trim();
        
        // Parse service ID if provided
        try {
            String idText = serviceIdField.getText().trim();
            if (!idText.isEmpty()) {
                serviceId = Integer.parseInt(idText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid Service ID (numeric value).",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<CleaningService> services = controller.searchCleaningService(serviceId, title);
        
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No cleaning services found for your search criteria.",
                "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (CleaningService service : services) {
                Object[] rowData = {
                    service.getServiceId(),
                    service.getTitle(),
                    service.getDescription(),
                    "$" + String.format("%.2f", service.getPrice()),
                    service.getServiceDuration() + " min",
                    service.getAvailableDays(),
                    service.getAvailableStartTime().format(TIME_FORMATTER) + " - " +
                        service.getAvailableEndTime().format(TIME_FORMATTER)
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void goBack() {
        dispose();
    }
    
    public void displaySearchPage() {
        setVisible(true);
    }
}