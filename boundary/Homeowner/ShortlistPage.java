package boundary.Homeowner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import boundary.Cleaner.CleanerProfilePage;
import controller.Homeowner.ShortlistController;
import entity.Cleaner.CleanerAccount;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class ShortlistPage extends JFrame {
    private ShortlistController controller;
    private UserAccount homeownerAccount;
    
    private JTextField searchField;
    private JButton searchButton;
    private JTable cleanerTable;
    private DefaultTableModel tableModel;
    private JButton viewProfileButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public ShortlistPage(UserAccount homeownerAccount) {
        this.homeownerAccount = homeownerAccount;
        this.controller = new ShortlistController();
        initializeUI();
        // Load all shortlisted cleaners initially
        loadCleaners("");
    }
    
    private void initializeUI() {
        setTitle("My Shortlisted Cleaners");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("My Shortlisted Cleaners");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Search Criteria",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField(30);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> {
            String criteria = searchField.getText().trim();
            loadCleaners(criteria);
        });
        
        inputPanel.add(searchLabel);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);
        
        searchPanel.add(inputPanel, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(BACKGROUND_COLOR);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Matching Cleaners",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            PRIMARY_COLOR));
        
        String[] columnNames = {"Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cleanerTable = new JTable(tableModel);
        cleanerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cleanerTable.setRowHeight(25);
        cleanerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add double-click listener to view profile
        cleanerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedCleanerProfile();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cleanerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultsPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        viewProfileButton = new JButton("View Profile");
        viewProfileButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewProfileButton.setFocusPainted(false);
        viewProfileButton.addActionListener(e -> viewSelectedCleanerProfile());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(viewProfileButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadCleaners(String criteria) {
        tableModel.setRowCount(0);
        
        List<CleanerAccount> cleaners = controller.searchShortlist(criteria, homeownerAccount.getUsername());
        
        if (cleaners.isEmpty()) {
            showError("No matching cleaners found in your shortlist.");
        } else {
            for (CleanerAccount cleaner : cleaners) {
                Object[] rowData = {
                    cleaner.getName(),
                    cleaner.getEmail(),
                    cleaner.getPhoneNumber(),
                    cleaner.getAddress()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void viewSelectedCleanerProfile() {
        int selectedRow = cleanerTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a cleaner from the table.");
            return;
        }
        
        // Get the selected cleaner's email
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        String cleanerEmail = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Get matching cleaners from the controller
        List<CleanerAccount> cleaners = controller.searchShortlist("", homeownerAccount.getUsername());
        
        // Find the selected cleaner by email
        for (CleanerAccount cleaner : cleaners) {
            if (cleaner.getEmail().equals(cleanerEmail)) {
                CleanerProfilePage profilePage = new CleanerProfilePage(homeownerAccount, cleaner.getUsername());
                profilePage.displayProfile();
                break;
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displaySearchBar() {
        this.setVisible(true);
    }
    
    public void enterSearchCriteria(String criteria) {
        searchField.setText(criteria);
        loadCleaners(criteria);
    }
    
    public void displayMatchingCleaners(List<CleanerAccount> cleaners) {
        tableModel.setRowCount(0);
        
        for (CleanerAccount cleaner : cleaners) {
            Object[] rowData = {
                cleaner.getName(),
                cleaner.getEmail(),
                cleaner.getPhoneNumber(),
                cleaner.getAddress()
            };
            tableModel.addRow(rowData);
        }
    }
    
    public void showResults() {
        this.setVisible(true);
    }
}