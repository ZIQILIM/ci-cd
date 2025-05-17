package boundary.UserAdmin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;
import java.util.List;

public class ShowSuspendedProfilesPage extends JFrame {
    private UserAccountController controller;
    private UserAccount adminAccount;
    
    private JTabbedPane tabbedPane;
    private JTable accountsTable;
    private JTable profilesTable;
    private DefaultTableModel accountsTableModel;
    private DefaultTableModel profilesTableModel;
    private JButton backButton;
    private JButton refreshButton;
    
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    public ShowSuspendedProfilesPage(UserAccount adminAccount) {
        this.adminAccount = adminAccount;
        controller = new UserAccountController();
        initializeUI();
        loadSuspendedUsers();
    }
    
    private void initializeUI() {
        setTitle("Suspended User Profiles");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        
        JPanel accountsPanel = createAccountsPanel();
        tabbedPane.addTab("Suspended Accounts", accountsPanel);
        
        JPanel profilesPanel = createProfilesPanel();
        tabbedPane.addTab("Suspended Profiles", profilesPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSuspendedUsers());
        
        backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"User ID", "Username", "Account Type", "Name", "Email", "Status", "Password"};
        accountsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        accountsTable = new JTable(accountsTableModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsTable.setRowHeight(25);
        accountsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // User ID
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Username
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Account Type
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Name
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Email
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        accountsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Password
        
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        createAccountsContextMenu();
        
        return panel;
    }
    
    private JPanel createProfilesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        String[] columnNames = {"User ID", "Username", "Address", "Phone Number", "Status"};
        profilesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        profilesTable = new JTable(profilesTableModel);
        profilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profilesTable.setRowHeight(25);
        profilesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        profilesTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // User ID
        profilesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Username
        profilesTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Address
        profilesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone Number
        profilesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(profilesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        createProfilesContextMenu();
        
        return panel;
    }
    
    private void createAccountsContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem unsuspendItem = new JMenuItem("Unsuspend Account");
        unsuspendItem.addActionListener(e -> unsuspendSelectedAccount());
        
        popupMenu.add(unsuspendItem);
        
        accountsTable.setComponentPopupMenu(popupMenu);

        accountsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = accountsTable.rowAtPoint(point);
                if (row >= 0 && row < accountsTable.getRowCount()) {
                    accountsTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }
    
    private void createProfilesContextMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem unsuspendItem = new JMenuItem("Unsuspend Profile");
        unsuspendItem.addActionListener(e -> unsuspendSelectedProfile());
        
        popupMenu.add(unsuspendItem);
        
        profilesTable.setComponentPopupMenu(popupMenu);
        
        profilesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = profilesTable.rowAtPoint(point);
                if (row >= 0 && row < profilesTable.getRowCount()) {
                    profilesTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }
    
    private void loadSuspendedUsers() {
        accountsTableModel.setRowCount(0);
        profilesTableModel.setRowCount(0);
        
        List<UserAccount> allUsers = controller.getAllUserAccounts();
        
        for (UserAccount user : allUsers) {
            String status = user.getStatus();
            
            if (status != null && status.startsWith("Account Sus")) {
                Object[] rowData = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getAccountType(),
                    user.getName(),
                    user.getEmail(),
                    status,
                    user.getPassword()
                };
                accountsTableModel.addRow(rowData);
            }

            if (user.isProfileSuspended() || (status != null && status.startsWith("Profile Sus"))) {
                Object[] rowData = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getAddress(),
                    user.getPhoneNumber(),
                    status
                };
                profilesTableModel.addRow(rowData);
            }
        }
    }
    
    private void unsuspendSelectedAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) accountsTableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unsuspend the account for user: " + username + "?",
                "Confirm Unsuspend",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                UserAccount user = controller.getUserByUsername(username);
                if (user != null) {
                    user.setStatus("Active");
                    boolean success = controller.updateUser(user);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "User account has been unsuspended successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadSuspendedUsers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to unsuspend user account.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user to unsuspend.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void unsuspendSelectedProfile() {
        int selectedRow = profilesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) profilesTableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unsuspend the profile for user: " + username + "?",
                "Confirm Unsuspend",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                UserAccount user = controller.getUserByUsername(username);
                if (user != null) {
                    user.setProfileSuspended(false);
                    if (user.getStatus() != null && user.getStatus().startsWith("Profile Sus")) {
                        user.setStatus("Active");
                    }
                    boolean success = controller.updateUser(user);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "User profile has been unsuspended successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadSuspendedUsers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to unsuspend user profile.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user to unsuspend.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void displayPage() {
        setVisible(true);
    }
}