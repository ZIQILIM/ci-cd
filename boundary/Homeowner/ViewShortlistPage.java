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

public class ViewShortlistPage extends JFrame {
    private ShortlistController controller;
    private UserAccount homeownerAccount;
    
    private JTextField searchField;
    private JButton searchButton;
    private JTable cleanerTable;
    private DefaultTableModel tableModel;
    private JButton viewProfileButton;
    private JButton removeButton;
    private JButton backButton;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public ViewShortlistPage(UserAccount homeownerAccount) {
        this.homeownerAccount = homeownerAccount;
        this.controller = new ShortlistController();
        initializeUI();
        loadShortlist();
    }
    
    private void initializeUI() {
        setTitle("My Shortlisted Cleaners");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel headerLabel = new JLabel("My Shortlisted Cleaners");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchCleaners();
                }
            }
        });
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchCleaners());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        
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
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        viewProfileButton = new JButton("View Profile");
        viewProfileButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewProfileButton.setFocusPainted(false);
        viewProfileButton.addActionListener(e -> viewSelectedCleanerProfile());
        
        removeButton = new JButton("Remove from Shortlist");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> removeFromShortlist());
        
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(viewProfileButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadShortlist() {
        tableModel.setRowCount(0);
        
        List<CleanerAccount> cleaners = controller.getShortlist(homeownerAccount.getUsername());
        
        if (cleaners.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your shortlist is empty. Add cleaners to your shortlist to compare them later.",
                "Empty Shortlist",
                JOptionPane.INFORMATION_MESSAGE);
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
    
    private void searchCleaners() {
        String criteria = searchField.getText().trim();
        List<CleanerAccount> matchingCleaners = controller.searchShortlist(criteria, homeownerAccount.getUsername());
        
        // Clear the table and update with search results
        tableModel.setRowCount(0);
        
        if (matchingCleaners.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No matching cleaners found in your shortlist.",
                "No Results",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (CleanerAccount cleaner : matchingCleaners) {
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
            JOptionPane.showMessageDialog(this,
                "Please select a cleaner from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected cleaner's email which we use to find their account
        String cleanerEmail = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Get all cleaners from the shortlist
        List<CleanerAccount> cleaners = controller.getShortlist(homeownerAccount.getUsername());
        
        // Find the selected cleaner by email
        for (CleanerAccount cleaner : cleaners) {
            if (cleaner.getEmail().equals(cleanerEmail)) {
                CleanerProfilePage profilePage = new CleanerProfilePage(homeownerAccount, cleaner.getUsername());
                profilePage.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadShortlist(); 
                    }
                });
                profilePage.displayProfile();
                break;
            }
        }
    }
    
    private void removeFromShortlist() {
        int selectedRow = cleanerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a cleaner from the table.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected cleaner's email
        String cleanerEmail = (String) tableModel.getValueAt(selectedRow, 1);
        String cleanerName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Get all cleaners from the shortlist
        List<CleanerAccount> cleaners = controller.getShortlist(homeownerAccount.getUsername());
        
        // Find the selected cleaner by email
        for (CleanerAccount cleaner : cleaners) {
            if (cleaner.getEmail().equals(cleanerEmail)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove " + cleanerName + " from your shortlist?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.removeCleanerFromShortlist(
                        homeownerAccount.getUsername(),
                        cleaner.getUsername());
                        
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            cleanerName + " has been removed from your shortlist.",
                            "Removal Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadShortlist(); // Refresh the list
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to remove " + cleanerName + " from your shortlist.",
                            "Removal Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            }
        }
    }
    
    public void displayPage() {
        setVisible(true);
    }
}