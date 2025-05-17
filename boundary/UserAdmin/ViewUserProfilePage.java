package boundary.UserAdmin;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserProfile;
import entity.UserAdmin.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ViewUserProfilePage extends JFrame {
    private UserAccountController controller;
    private JTable profileTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel totalUsersLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public ViewUserProfilePage() {
        controller = new UserAccountController();
        initializeUI();
        createPopupMenu();
        loadProfiles();
    }
    
    private void initializeUI() {
        setTitle("User Profiles");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("User Profiles", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel searchPanel = createSearchPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Search Users:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProfiles();
                }
            }
        });
        
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProfiles());
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadProfiles());
        
        searchControlsPanel.add(searchLabel);
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(refreshButton);
        
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        countPanel.setBackground(BACKGROUND_COLOR);
        
        totalUsersLabel = new JLabel("Total Users: 0");
        totalUsersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalUsersLabel.setForeground(TEXT_COLOR);
        
        countPanel.add(totalUsersLabel);
        
        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        searchPanel.add(countPanel, BorderLayout.EAST);
        
        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"User ID", "Username", "Address", "Phone Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        profileTable = new JTable(tableModel);
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileTable.setRowHeight(25);
        profileTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        profileTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // User ID
        profileTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Username
        profileTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Address
        profileTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Phone Number
        
        JScrollPane scrollPane = new JScrollPane(profileTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(backButton);
        return buttonPanel;
    }
    
    // popup menu for right-click actions
    private void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        viewDetailsItem.addActionListener(e -> viewSelectedUserDetails());
        
        JMenuItem suspendProfileItem = new JMenuItem("Suspend User Profile");
        suspendProfileItem.addActionListener(e -> suspendSelectedUserProfile());
        
        popupMenu.add(viewDetailsItem);
        popupMenu.add(suspendProfileItem);
        
        // popup menu to the table
        profileTable.setComponentPopupMenu(popupMenu);
        
        // mouse listener to handle row selection on right-click
        profileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = profileTable.rowAtPoint(point);
                if (row >= 0 && row < profileTable.getRowCount()) {
                    profileTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }
    
    private void viewSelectedUserDetails() {
        int selectedRow = profileTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1); // Username 
            
            // Get full user details
            UserAccount user = controller.findUserByUsername(username);
            
            if (user != null) {
                // Create and show details dialog
                JDialog dialog = new JDialog(this, "User Profile Details", true);
                dialog.setSize(400, 300);
                dialog.setLayout(new BorderLayout());
                
                JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                detailsPanel.add(new JLabel("Username:"));
                detailsPanel.add(new JLabel(user.getUsername()));
                
                detailsPanel.add(new JLabel("Address:"));
                detailsPanel.add(new JLabel(user.getAddress()));
                
                detailsPanel.add(new JLabel("Phone Number:"));
                detailsPanel.add(new JLabel(user.getPhoneNumber()));
                
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(closeButton);
                
                dialog.add(detailsPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table",
                "No User Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void suspendSelectedUserProfile() {
        int selectedRow = profileTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1); // Username column
            
            // Confirm before suspending profile
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to suspend user profile: " + username + "?",
                "Confirm Suspend Profile",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Suspend the profile
                boolean success = controller.suspendUserProfile(username);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "User profile '" + username + "' has been suspended.",
                        "Profile Suspended",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the profiles list
                    loadProfiles();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to suspend user profile: " + username,
                        "Suspend Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table",
                "No User Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadProfiles() {
        tableModel.setRowCount(0);
        
        // Get profiles from controller
        ArrayList<UserProfile> profiles = controller.viewUserProfile();
        
        for (UserProfile profile : profiles) {
            Object[] rowData = {
                profile.getUserId(),
                profile.getUsername(),
                profile.getAddress(),
                profile.getPhoneNumber()
            };
            tableModel.addRow(rowData);
        }
        
        // Update count
        totalUsersLabel.setText("Total Users: " + profiles.size());
    }
    
    private void searchProfiles() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            loadProfiles();
            return;
        }
        
        tableModel.setRowCount(0);
        
        // Get all profiles
        ArrayList<UserProfile> allProfiles = controller.viewUserProfile();
        ArrayList<UserProfile> matchingProfiles = new ArrayList<>();
        
        // Filter profiles
        for (UserProfile profile : allProfiles) {
            if (profile.getUsername().toLowerCase().contains(searchTerm) ||
                profile.getAddress().toLowerCase().contains(searchTerm) ||
                profile.getPhoneNumber().toLowerCase().contains(searchTerm)) {
                matchingProfiles.add(profile);
            }
        }
        
        // Add matching profiles to table
        for (UserProfile profile : matchingProfiles) {
            Object[] rowData = {
                profile.getUserId(),
                profile.getUsername(),
                profile.getAddress(),
                profile.getPhoneNumber()
            };
            tableModel.addRow(rowData);
        }
        
        totalUsersLabel.setText("Search Results: " + matchingProfiles.size());
    }
    
    public void displayUserProfilePage() {
        setVisible(true);
    }
}