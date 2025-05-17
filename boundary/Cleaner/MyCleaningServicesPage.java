package boundary.Cleaner;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import controller.Cleaner.CleaningServiceController;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;
import entity.CleaningServiceDAO;

import java.util.List;

public class MyCleaningServicesPage extends JFrame {
    private CleaningServiceController controller;
    private UserAccount cleanerAccount;
    private CleaningServiceDAO serviceDAO;
    
    private JTable servicesTable;
    private DefaultTableModel tableModel;
    private JButton createButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);  
    
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public MyCleaningServicesPage(UserAccount cleanerAccount) {
        this.cleanerAccount = cleanerAccount;
        this.controller = new CleaningServiceController();
        this.serviceDAO = new CleaningServiceDAO();
        initializeUI();
        loadServices();
    }
    
    private void initializeUI() {
        setTitle("My Cleaning Services");
        setSize(1000, 500); // Increased width for categories column
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("My Cleaning Services");
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
        
        String[] columnNames = {"ID", "Service Name", "Description", "Price", "Duration", "Available Days", "Hours", "Categories"};
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
        
        TableColumnModel columnModel = servicesTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);   // ID
        columnModel.getColumn(1).setPreferredWidth(150);  // Name
        columnModel.getColumn(2).setPreferredWidth(200);  // Description
        columnModel.getColumn(3).setPreferredWidth(80);   // Price
        columnModel.getColumn(4).setPreferredWidth(80);   // Duration
        columnModel.getColumn(5).setPreferredWidth(120);  // Available Days
        columnModel.getColumn(6).setPreferredWidth(120);  // Hours
        columnModel.getColumn(7).setPreferredWidth(150);  // Categories
        
        JScrollPane scrollPane = new JScrollPane(servicesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Service");
        JMenuItem deleteItem = new JMenuItem("Delete Service");
        
        editItem.addActionListener(e -> editSelectedService());
        deleteItem.addActionListener(e -> deleteSelectedService());
        
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        
        servicesTable.setComponentPopupMenu(popupMenu);
        
        servicesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = servicesTable.rowAtPoint(point);
                if (row >= 0 && row < servicesTable.getRowCount()) {
                    servicesTable.setRowSelectionInterval(row, row);
                }
            }
        });
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        createButton = new JButton("Create New Service");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCreateServiceForm();
            }
        });
        
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadServices() {
        tableModel.setRowCount(0); 
        
        List<CleaningService> services = controller.getServicesByCleanerUsername(cleanerAccount.getUsername());
        
        for (CleaningService service : services) {
            List<String> categoryNames = serviceDAO.getServiceCategoryNames(service.getServiceId());
            String categoriesStr = String.join(", ", categoryNames);
            
            Object[] rowData = {
                service.getServiceId(),
                service.getTitle(),
                service.getDescription(),
                "$" + String.format("%.2f", service.getPrice()),
                service.getServiceDuration() + " min",
                service.getAvailableDays(),
                service.getAvailableStartTime().format(TIME_FORMATTER) + " - " + 
                    service.getAvailableEndTime().format(TIME_FORMATTER),
                categoriesStr 
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void openCreateServiceForm() {
        CreateCleaningServicePage servicePage = new CreateCleaningServicePage(cleanerAccount);
        servicePage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadServices(); 
            }
        });
        servicePage.setVisible(true);
    }
    
    private void editSelectedService() {
        int selectedRow = servicesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int serviceId = (int) tableModel.getValueAt(selectedRow, 0);
            
            UpdateCleaningServicePage updatePage = new UpdateCleaningServicePage(cleanerAccount, serviceId);
            updatePage.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadServices(); 
                }
            });
            updatePage.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a service to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteSelectedService() {
        int selectedRow = servicesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int serviceId = (int) tableModel.getValueAt(selectedRow, 0);
            String serviceName = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the service \"" + serviceName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                CleaningServiceController deleteController = new CleaningServiceController();
                boolean success = deleteController.deleteService(serviceId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Service successfully deleted.",
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    loadServices();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete service. Please try again.",
                        "Delete Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a service to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
}