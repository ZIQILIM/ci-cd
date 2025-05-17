package boundary.UserAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

public class SearchUserAccountPage { // Boundary class for searching user accounts
    private UserAccountController controller; 
    
    private JTextField searchField; 
    private JButton searchButton; 
    private JTable resultsTable; 
    private DefaultTableModel tableModel; 
    private JPanel parentPanel;
    private JLabel totalUsersLabel; 
    
    public SearchUserAccountPage(JTextField searchField, JButton searchButton, 
                               JTable resultsTable, DefaultTableModel tableModel,
                               JPanel parentPanel, JLabel totalUsersLabel) {
        this.controller = new UserAccountController();
        this.searchField = searchField;
        this.searchButton = searchButton;
        this.resultsTable = resultsTable;
        this.tableModel = tableModel;
        this.parentPanel = parentPanel;
        this.totalUsersLabel = totalUsersLabel;
        
        this.searchButton.addActionListener(e -> searchUser());
        
        this.searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUser();
                }
            }
        });
    }
    
    private void searchUser() {
        String username = searchField.getText().trim();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(parentPanel, 
                "Please enter a username to search", 
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        UserAccount user = controller.searchUserAccount(username);
        
        displayUser(user);
    }
    
    private void displayUser(UserAccount user) {
        tableModel.setRowCount(0);
        
        if (user != null) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getAccountType(),
                user.getName(),
                user.getEmail(),
                user.getStatus() != null ? user.getStatus() : "Active",
                "N/A" 
            };
            tableModel.addRow(row);
            
            totalUsersLabel.setText("Search Results: 1");
        } else {
            JOptionPane.showMessageDialog(parentPanel, 
                "No user found with the specified username", 
                "Search Result", JOptionPane.INFORMATION_MESSAGE);
                
            totalUsersLabel.setText("Search Results: 0");
        }
    }
}