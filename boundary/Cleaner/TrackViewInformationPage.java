package boundary.Cleaner;

import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.Cleaner.CleaningServiceController;
import controller.Cleaner.TrackViewInformationController;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class TrackViewInformationPage extends JFrame {
    private TrackViewInformationController controller;
    private CleaningServiceController serviceController;
    private UserAccount cleanerAccount;
    
    // UI Components
    private JTable servicesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public TrackViewInformationPage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new TrackViewInformationController();
        this.serviceController = new CleaningServiceController();
        initializeUI();
        loadViewStats();
    }
    
    private void initializeUI() {
        setTitle("Service View Statistics");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Service View Statistics");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JLabel infoLabel = new JLabel("This page shows how many times each of your services has been viewed by homeowners");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"Service ID", "Service Name", "Price", "View Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        servicesTable = new JTable(tableModel);
        servicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        servicesTable.setRowHeight(25);
        servicesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(servicesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        refreshButton = new JButton("Refresh Stats");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadViewStats());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadViewStats() {
        tableModel.setRowCount(0);
        
        // Get all services for this cleaner
        List<CleaningService> services = serviceController.getServicesByCleanerUsername(cleanerAccount.getUsername());
        
        // Get view stats for all services
        Map<Integer, Integer> viewStats = controller.getViewStatsByUsername(cleanerAccount.getUsername());
        
        int totalViews = 0;
        
        // Populate the table with services and their view counts
        for (CleaningService service : services) {
            int serviceId = service.getServiceId();
            int viewCount = viewStats.getOrDefault(serviceId, 0);
            
            totalViews += viewCount;
            
            Object[] rowData = {
                serviceId,
                service.getTitle(),
                "$" + String.format("%.2f", service.getPrice()),
                viewCount
            };
            
            tableModel.addRow(rowData);
        }
        
        Object[] summaryRow = {
            "",
            "TOTAL VIEWS",
            "",
            totalViews
        };
        tableModel.addRow(summaryRow);
        
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "You don't have any services yet. Create services to track views.",
                "No Services",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void displayViewStats() {
        setVisible(true);
    }
}