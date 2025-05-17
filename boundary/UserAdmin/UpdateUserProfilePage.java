package boundary.UserAdmin;

import javax.swing.*;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

import java.awt.*;
import java.awt.event.*;

// Boundary page for updating user profile information (address and phone)
public class UpdateUserProfilePage extends JFrame {
    private UserAccountController controller;
    private JTextField usernameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JButton searchButton;
    private JButton applyButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private UserAccount currentUser;

    public UpdateUserProfilePage() {
        controller = new UserAccountController();
        initUI();
    }

    private void initUI() {
        setTitle("Update User Profile");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);
        searchButton = new JButton("Search");
        gbc.gridx = 2;
        add(searchButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Address:"), gbc);
        addressField = new JTextField(25);
        addressField.setEnabled(false);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(15);
        phoneField.setEnabled(false);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(phoneField, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        add(messageLabel, gbc);
        gbc.gridwidth = 1;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton("Apply");
        applyButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        btnPanel.add(applyButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        add(btnPanel, gbc);

        searchButton.addActionListener(e -> searchUser());
        applyButton.addActionListener(e -> updateProfile());
        cancelButton.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
    }

    private void searchUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Enter a username to search.");
            addressField.setEnabled(false);
            phoneField.setEnabled(false);
            applyButton.setEnabled(false);
            return;
        }
        
        currentUser = controller.findUserByUsername(username);
        if (currentUser == null) {
            messageLabel.setText("Username not found");
            messageLabel.setForeground(Color.RED);
            addressField.setEnabled(false);
            phoneField.setEnabled(false);
            applyButton.setEnabled(false);
        } else {
            messageLabel.setText("User found: " + currentUser.getName());
            messageLabel.setForeground(new Color(0, 128, 0)); 
            
            addressField.setText(currentUser.getAddress());
            phoneField.setText(currentUser.getPhoneNumber());
            
            addressField.setEnabled(true);
            phoneField.setEnabled(true);
            applyButton.setEnabled(true);
        }
    }

    private void updateProfile() {
        if (currentUser == null) return; 
    
        String address = addressField.getText().trim(); 
        String phone = phoneField.getText().trim(); 
        
        boolean success = controller.updateUserProfile(currentUser.getUsername(), address, phone);
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "User profile updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            usernameField.setText("");
            addressField.setText("");
            phoneField.setText("");
            addressField.setEnabled(false);
            phoneField.setEnabled(false);
            applyButton.setEnabled(false);
            messageLabel.setText(" ");
            currentUser = null;
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update profile.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void switchToUserAdminPage(ActionEvent e, UserAccount userAccount) {
        dispose();
    }
    
    public void showMessage(boolean executed) {
        JOptionPane.showMessageDialog(this, 
            executed ? "Update successful" : "Update failed");
    }

    public void display() {
        setVisible(true);
    }
}