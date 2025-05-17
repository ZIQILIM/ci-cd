package boundary.Cleaner;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.Cleaner.CleaningServiceController;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class ViewCleaningServicePage extends JFrame {
    private CleaningServiceController controller;
    private UserAccount cleanerAccount;
    private JTable servicesTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public ViewCleaningServicePage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new CleaningServiceController();
        initializeUI();
        loadServices();
    }
    
    private void initializeUI() {
        setTitle("View Cleaning Services");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("View Cleaning Services");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
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
        
        String[] columnNames = {"ID", "Service Name", "Description", "Price", "Duration", "Available Days", "Hours", "Status"};
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
        
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(this::goBackButtonOnAction);
        
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadServices() {
        tableModel.setRowCount(0);
        
        List<CleaningService> services = controller.getServicesByCleanerUsername(cleanerAccount.getUsername());
        
        for (CleaningService service : services) {
            Object[] rowData = {
                service.getServiceId(),
                service.getTitle(),
                service.getDescription(),
                "$" + String.format("%.2f", service.getPrice()),
                service.getServiceDuration() + " min",
                service.getAvailableDays(),
                service.getAvailableStartTime().format(TIME_FORMATTER) + " - " + 
                    service.getAvailableEndTime().format(TIME_FORMATTER),
                service.isAvailable() ? "Active" : "Inactive"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void viewServiceDetails(int serviceId) {
        boolean success = controller.viewService(serviceId);
        if (success) {
            // Get the service using the controller instead of directly accessing the DAO
            CleaningService service = controller.findServiceById(serviceId);
            
            JDialog dialog = new JDialog(this, "Service Details", true);
            dialog.setSize(500, 400);
            dialog.setLayout(new BorderLayout());
            
            JPanel detailsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
            detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            detailsPanel.add(new JLabel("Service Name:"));
            detailsPanel.add(new JLabel(service.getTitle()));
            
            detailsPanel.add(new JLabel("Description:"));
            detailsPanel.add(new JLabel(service.getDescription()));
            
            detailsPanel.add(new JLabel("Price:"));
            detailsPanel.add(new JLabel("$" + String.format("%.2f", service.getPrice())));
            
            detailsPanel.add(new JLabel("Duration:"));
            detailsPanel.add(new JLabel(service.getServiceDuration() + " minutes"));
            
            detailsPanel.add(new JLabel("Available Days:"));
            detailsPanel.add(new JLabel(service.getAvailableDays()));
            
            detailsPanel.add(new JLabel("Hours:"));
            detailsPanel.add(new JLabel(service.getAvailableStartTime().format(TIME_FORMATTER) + 
                " - " + service.getAvailableEndTime().format(TIME_FORMATTER)));
            
            detailsPanel.add(new JLabel("Status:"));
            detailsPanel.add(new JLabel(service.isAvailable() ? "Active" : "Inactive"));
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            
            dialog.add(detailsPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Service not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void goBackButtonOnAction(ActionEvent e) {
        dispose();
        CleanerPage cleanerPage = new CleanerPage(cleanerAccount);
        cleanerPage.setVisible(true);  
    }
}