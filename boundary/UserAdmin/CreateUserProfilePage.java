package boundary.UserAdmin;

import javax.swing.*;

import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

import java.awt.*;


// Boundary page for creating/updating a user's profile (role)
public class CreateUserProfilePage extends JFrame {
    private UserAccountController controller;
    private JTextField usernameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JButton searchButton;
    private JComboBox<String> roleCombo;
    private JButton applyButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private UserAccount currentUser;

    private static final String[] ROLES = {"User Admin", "Cleaner", "Homeowner", "Platform Manager"};

    public CreateUserProfilePage() {
        controller = new UserAccountController();
        initUI();
    }

    public CreateUserProfilePage(String username) {
        controller = new UserAccountController();
        initUI();
        usernameField.setText(username);
        usernameField.setEditable(false);
        searchUser();
    }

    private void initUI() {
        setTitle("Create User Profile");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 350);
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

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(new JLabel("User Type:"), gbc);
        roleCombo = new JComboBox<>(ROLES);
        roleCombo.setEnabled(false);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(roleCombo, gbc);
        gbc.gridwidth = 1;

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        add(messageLabel, gbc);
        gbc.gridwidth = 1;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton("Apply");
        applyButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        btnPanel.add(applyButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        add(btnPanel, gbc);

        searchButton.addActionListener(e -> searchUser());
        applyButton.addActionListener(e -> applyChanges());
        cancelButton.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
    }

    private void searchUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Enter a username to search.");
            roleCombo.setEnabled(false);
            addressField.setEnabled(false);
            phoneField.setEnabled(false);
            applyButton.setEnabled(false);
            return;
        }
        
        currentUser = controller.findUserByUsername(username);
        if (currentUser == null) {
            messageLabel.setText("Invalid username. Please create account first.");
            roleCombo.setEnabled(false);
            addressField.setEnabled(false);
            phoneField.setEnabled(false);
            applyButton.setEnabled(false);
        } else {
            messageLabel.setText("User found: " + currentUser.getName());
            messageLabel.setForeground(new Color(0, 128, 0));
            
            addressField.setText(currentUser.getAddress());
            phoneField.setText(currentUser.getPhoneNumber());
            
            roleCombo.setEnabled(true);
            addressField.setEnabled(true);
            phoneField.setEnabled(true);
            applyButton.setEnabled(true);
        }
    }

    private void applyChanges() {
        if (currentUser == null) return;

        String newRole = (String)roleCombo.getSelectedItem();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        
        currentUser.setAccountType(newRole);
        currentUser.setAddress(address);
        currentUser.setPhoneNumber(phone);
        
        boolean ok = controller.updateUserProfile(currentUser);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update profile.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void display() {
        setVisible(true);
    }
}