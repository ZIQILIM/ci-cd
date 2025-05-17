package boundary.Cleaner;

import boundary.Homeowner.BookServicePage;
import controller.Homeowner.ShortlistController;
import controller.Cleaner.TrackViewInformationController;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import controller.Homeowner.ViewProfileController;
import entity.Cleaner.CleanerAccount;
import entity.Cleaner.CleaningService;
import entity.UserAdmin.UserAccount;

public class CleanerProfilePage extends JFrame {
    private ViewProfileController controller;
    private TrackViewInformationController viewTrackController;
    private UserAccount homeownerAccount;
    private CleanerAccount cleanerAccount;
    private List<CleaningService> cleanerServices;
    
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel addressLabel;
    private JTable servicesTable;
    private DefaultTableModel tableModel;
    private JButton bookServiceButton;
    private JButton closeButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public CleanerProfilePage(UserAccount homeownerAccount, String cleanerId) {
        this.homeownerAccount = homeownerAccount;
        this.controller = new ViewProfileController();
        this.viewTrackController = new TrackViewInformationController();
        
        // Load cleaner data
        this.cleanerAccount = controller.getCleanerInfo(cleanerId);
        
        if (this.cleanerAccount == null) {
            JOptionPane.showMessageDialog(null,
                "Cleaner not found. The account may have been removed.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Load cleaner services
        this.cleanerServices = controller.getCleanerServices(cleanerId); 
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Cleaner Profile");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Cleaner Profile: " + cleanerAccount.getName());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel profilePanel = createProfilePanel();
        contentPanel.add(profilePanel, BorderLayout.NORTH);
        
        JPanel servicesPanel = createServicesPanel();
        contentPanel.add(servicesPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(BACKGROUND_COLOR);
        profilePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Contact Information",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel nameTitleLabel = new JLabel("Name:");
        nameTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameTitleLabel.setForeground(TEXT_COLOR);
        profilePanel.add(nameTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameLabel = new JLabel(cleanerAccount.getName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        profilePanel.add(nameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel emailTitleLabel = new JLabel("Email:");
        emailTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailTitleLabel.setForeground(TEXT_COLOR);
        profilePanel.add(emailTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailLabel = new JLabel(cleanerAccount.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        profilePanel.add(emailLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel phoneTitleLabel = new JLabel("Phone:");
        phoneTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneTitleLabel.setForeground(TEXT_COLOR);
        profilePanel.add(phoneTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneLabel = new JLabel(cleanerAccount.getPhoneNumber());
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        profilePanel.add(phoneLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel addressTitleLabel = new JLabel("Address:");
        addressTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressTitleLabel.setForeground(TEXT_COLOR);
        profilePanel.add(addressTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressLabel = new JLabel(cleanerAccount.getAddress());
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        profilePanel.add(addressLabel, gbc);
        
        return profilePanel;
    }
    
    private JPanel createServicesPanel() {
        JPanel servicesPanel = new JPanel(new BorderLayout());
        servicesPanel.setBackground(BACKGROUND_COLOR);
        servicesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Available Services",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        // Create table model
        String[] columnNames = {"Service Name", "Description", "Price", "Duration", "Available Days", "Hours"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate table with services
        if (cleanerServices != null && !cleanerServices.isEmpty()) {
            for (CleaningService service : cleanerServices) {
                Object[] rowData = {
                    service.getTitle(),
                    service.getDescription(),
                    "$" + String.format("%.2f", service.getPrice()),
                    service.getServiceDuration() + " min",
                    service.getAvailableDays(),
                    service.getAvailableStartTime().toString() + " - " + service.getAvailableEndTime().toString()
                };
                tableModel.addRow(rowData);
            }
        }
        
        servicesTable = new JTable(tableModel);
        servicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        servicesTable.setRowHeight(25);
        servicesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        servicesTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int selectedRow = servicesTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < cleanerServices.size()) {
                // Only increment view count and process booking on double-click
                if (e.getClickCount() == 2) {
                    CleaningService selectedService = cleanerServices.get(selectedRow);
                    viewTrackController.incrementViewCount(selectedService.getServiceId());
                    bookSelectedService(selectedRow);
                }
            }
        }
    });
        
        JScrollPane scrollPane = new JScrollPane(servicesTable);
        servicesPanel.add(scrollPane, BorderLayout.CENTER);
        
        // If no services
        if (cleanerServices == null || cleanerServices.isEmpty()) {
            JLabel noServicesLabel = new JLabel("This cleaner has no services available.");
            noServicesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noServicesLabel.setHorizontalAlignment(JLabel.CENTER);
            noServicesLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
            servicesPanel.add(noServicesLabel, BorderLayout.NORTH);
        }
        
        return servicesPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton addToShortlistButton = new JButton("Add to Shortlist");
        addToShortlistButton.setFont(new Font("Arial", Font.BOLD, 14));
        addToShortlistButton.setFocusPainted(false);
        addToShortlistButton.addActionListener(e -> addToShortlist());
        
        bookServiceButton = new JButton("Book Selected Service");
        bookServiceButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookServiceButton.setFocusPainted(false);
        bookServiceButton.addActionListener(e -> {
            int selectedRow = servicesTable.getSelectedRow();
            if (selectedRow >= 0) {
                CleaningService selectedService = cleanerServices.get(selectedRow);
                viewTrackController.incrementViewCount(selectedService.getServiceId());
                
                bookSelectedService(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a service to book.",
                    "No Service Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addToShortlistButton);
        buttonPanel.add(bookServiceButton);
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    private void bookSelectedService(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= cleanerServices.size()) {
            return;
        }
        
        CleaningService selectedService = cleanerServices.get(selectedRow);
        
        // Create and show booking page
        BookServicePage bookingPage = new BookServicePage(
            homeownerAccount, 
            cleanerAccount,
            selectedService);
        
        bookingPage.setVisible(true);
    }
    
    // Add to shortlist method
    private void addToShortlist() {
        // Create shortlist controller
        ShortlistController shortlistController = new ShortlistController();
        
        // Check if cleaner is already in shortlist
        if (shortlistController.isCleanerInShortlist(homeownerAccount.getUsername(), cleanerAccount.getUsername())) {
            JOptionPane.showMessageDialog(this,
                cleanerAccount.getName() + " is already in your shortlist.",
                "Already Shortlisted",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Add to shortlist
        boolean success = shortlistController.addCleanerToShortlist(
            homeownerAccount.getUsername(), 
            cleanerAccount.getUsername());
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                cleanerAccount.getName() + " has been added to your shortlist.",
                "Shortlist Updated",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to add " + cleanerAccount.getName() + " to your shortlist.",
                "Shortlist Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void displayProfile() {
        setVisible(true);
    }
}