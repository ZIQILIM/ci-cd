package boundary.UserAdmin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

import java.util.List;

public class UserAccountViewPage extends JFrame { // Boundary class for viewing all user accounts in the system
    private UserAccountController controller;
    private UserAccount adminAccount;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton backButton;
    private JLabel totalUsersLabel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public UserAccountViewPage(UserAccount adminAccount) { 
        this.adminAccount = adminAccount; 
        controller = new UserAccountController();
        initializeUI();
        loadUsers();
    }
    
    private void initializeUI() {
        setTitle("User Accounts Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Registered User Accounts");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
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
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Search Users:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setToolTipText("Search by username, name, or email");
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUsers();
            }
        });
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUsers();
                }
            }
        });
        
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
        
        String[] columnNames = {"User ID", "Username", "Account Type", "Name", "Email", "Status", "Password"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        userTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // User ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Account Type
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Name
        userTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Email
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
        userTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Password
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        createPopupMenu();
        
        return tablePanel;
    }
    
    private void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem viewItem = new JMenuItem("View User Details");
        viewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSelectedUser();
            }
        });
        
        JMenuItem editItem = new JMenuItem("Edit User");
        editItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedUser();
            }
        });
        
        JMenuItem suspendAccountItem = new JMenuItem("Suspend User Account");
        suspendAccountItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                suspendSelectedUserAccount();
            }
        });
        
        JMenuItem deleteItem = new JMenuItem("Delete User");
        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedUser();
            }
        });
        
        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.add(suspendAccountItem);
        popupMenu.add(deleteItem);
        
        userTable.setComponentPopupMenu(popupMenu);
        
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = userTable.rowAtPoint(point);
                if (row >= 0 && row < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton createUserButton = new JButton("Create New User");
        createUserButton.setFont(new Font("Arial", Font.BOLD, 12));
        createUserButton.setFocusPainted(false);
        createUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCreateUserPage();
            }
        });
        
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(createUserButton);
        buttonPanel.add(backButton);
        
        return buttonPanel;
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        
        List<UserAccount> users = controller.getAllUserAccounts();
        
        for (UserAccount user : users) {
            Object[] rowData = {
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                user.getName(),
                user.getEmail(),
                user.getStatus() != null ? user.getStatus() : "Active",
                user.getPassword()
            };
            tableModel.addRow(rowData);
        }
        
        totalUsersLabel.setText("Total Users: " + users.size());
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        
        tableModel.setRowCount(0);
        
        List<UserAccount> users = controller.searchUserAccounts(searchTerm);
        
        for (UserAccount user : users) {
            Object[] rowData = {
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                user.getName(),
                user.getEmail(),
                user.getStatus() != null ? user.getStatus() : "Active",
                user.getPassword()
            };
            tableModel.addRow(rowData);
        }
        
        totalUsersLabel.setText("Search Results: " + users.size());
    }
    
    private void openCreateUserPage() {
        CreateUserAccountPage createUserPage = new CreateUserAccountPage(adminAccount);
        createUserPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadUsers();
            }
        });
        createUserPage.showInputForm(true);
    }
    
    private void viewSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            UserAccount selectedUser = controller.getUserByUsername(username);
            
            if (selectedUser != null) {
                JDialog dialog = new JDialog(this, "User Details", true);
                dialog.setSize(400, 350);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(this);
                
                JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
                detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                detailsPanel.add(new JLabel("User ID:"));
                detailsPanel.add(new JLabel(String.valueOf(selectedUser.getUserId())));
                
                detailsPanel.add(new JLabel("Username:"));
                detailsPanel.add(new JLabel(selectedUser.getUsername()));
                
                detailsPanel.add(new JLabel("Account Type:"));
                detailsPanel.add(new JLabel(selectedUser.getAccountType()));
                
                detailsPanel.add(new JLabel("Name:"));
                detailsPanel.add(new JLabel(selectedUser.getName()));
                
                detailsPanel.add(new JLabel("Email:"));
                detailsPanel.add(new JLabel(selectedUser.getEmail()));
                
                detailsPanel.add(new JLabel("Status:"));
                detailsPanel.add(new JLabel(selectedUser.getStatus() != null ? selectedUser.getStatus() : "Active"));
                
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(closeButton);
                
                dialog.add(detailsPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table",
                "No User Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            
            UserAccount selectedUser = controller.getUserByUsername(username);
            
            if (selectedUser != null) {
                UpdateUserAccountPage updatePage = new UpdateUserAccountPage(adminAccount, selectedUser);
                updatePage.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadUsers();
                    }
                });
                updatePage.showPromptMessage(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table",
                "No User Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void suspendSelectedUserAccount() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to suspend user account: " + username + "?",
                "Confirm Suspend Account",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                UserAccount user = controller.getUserByUsername(username);
                if (user != null) {
                    user.setStatus("Account Suspended");
                    boolean success = controller.updateUser(user);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "User account '" + username + "' has been suspended.",
                            "Account Suspended",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to suspend user account: " + username,
                            "Suspend Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table",
                "No User Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.deleteUser(username);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "User '" + username + "' has been deleted successfully.",
                        "User Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete user: " + username,
                        "Delete Failed",
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
    
    public void displayUserViewPage() {
        setVisible(true);
    }
}