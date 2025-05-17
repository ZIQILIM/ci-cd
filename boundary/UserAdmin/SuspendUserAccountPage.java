package boundary.UserAdmin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

// Boundary class for suspending user accounts
public class SuspendUserAccountPage extends JFrame {
    private UserAccountController controller;
    
    private JTextField usernameField;
    private JComboBox<String> statusComboBox;
    private JButton searchButton;
    private JButton applyButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    
    private UserAccount foundUser;
    
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80);       
    
    public SuspendUserAccountPage() {
        controller = new UserAccountController();
        initUI();
    }
    
    private void initUI() {
        setTitle("Suspend User Account");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(usernameField, gbc);
        
        searchButton = new JButton("Search");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(searchButton, gbc);
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(statusLabel, gbc);
        
        String[] statuses = {"Active", "Inactive", "Suspended"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setEnabled(false); 
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(statusComboBox, gbc);
        
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        mainPanel.add(messageLabel, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        applyButton = new JButton("Apply");
        applyButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        searchButton.addActionListener(e -> searchUser());
        applyButton.addActionListener(e -> applyStatus());
        cancelButton.addActionListener(e -> dispose());
        
        setLocationRelativeTo(null);
    }
    
    private void searchUser() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            messageLabel.setText("Please enter a username");
            statusComboBox.setEnabled(false);
            applyButton.setEnabled(false);
            foundUser = null;
            return;
        }
        
        foundUser = controller.findUserByUsername(username);
        
        if (foundUser == null) {
            messageLabel.setText("Username not found");
            statusComboBox.setEnabled(false);
            applyButton.setEnabled(false);
        } else {
            messageLabel.setText("User found: " + foundUser.getName());
            messageLabel.setForeground(new Color(0, 128, 0)); 
            
            String currentStatus = foundUser.getStatus();
            if (currentStatus == null) {
                currentStatus = "Active"; 
            }
            statusComboBox.setSelectedItem(currentStatus);
            
            statusComboBox.setEnabled(true);
            applyButton.setEnabled(true);
        }
    }
    
    private void applyStatus() {
        if (foundUser == null) {
            return;
        }
        
        String newStatus = (String) statusComboBox.getSelectedItem();
        boolean success = controller.suspendAccount(foundUser.getUsername(), newStatus);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "User status has been updated to " + newStatus, 
                "Status Updated", 
                JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText("");
            statusComboBox.setEnabled(false);
            applyButton.setEnabled(false);
            messageLabel.setText(" ");
            messageLabel.setForeground(Color.RED);
            foundUser = null;
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update user status", 
                "Update Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void displayPage() {
        setVisible(true);
    }
    
    public void switchToUserAdminPage(ActionEvent event) {
        dispose();
    }
    
    public void showMessage(String executed) {
        JOptionPane.showMessageDialog(this, executed);
    }
}