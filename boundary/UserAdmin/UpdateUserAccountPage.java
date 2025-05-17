package boundary.UserAdmin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


import controller.UserAdmin.UserAccountController;
import entity.UserAdmin.UserAccount;

// Boundary class for user account updates
public class UpdateUserAccountPage extends JFrame {
    private UserAccountController controller;
    private UserAccount userToUpdate;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> accountTypeComboBox;  
    private JComboBox<String> statusComboBox;       
    private JButton updateButton;
    private JButton cancelButton;

    private final Color PRIMARY_COLOR    = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    private final String[] ACCOUNT_TYPES = {"User Admin", "Cleaner", "Homeowner", "Platform Manager"};
    private final String[] STATUS_OPTIONS = {"Active", "Inactive"};

    public UpdateUserAccountPage(UserAccount adminAccount, UserAccount userToUpdate) {
        this.userToUpdate  = userToUpdate;
        this.controller    = new UserAccountController();

        initUI();
        populateFields();
    }

    private void initUI() {
        setTitle("Update User Account");
        setSize(500, 450); 
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Update User Account", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        main.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        accountTypeComboBox = new JComboBox<>(ACCOUNT_TYPES);
        form.add(accountTypeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        form.add(statusComboBox, gbc);

        main.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(BACKGROUND_COLOR);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        updateButton = new JButton("Update User");
        updateButton.addActionListener(e -> updateUser());
        buttons.add(cancelButton);
        buttons.add(updateButton);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(updateButton);
    }

    private void populateFields() {
        usernameField.setText(userToUpdate.getUsername());
        nameField.setText(userToUpdate.getName());
        emailField.setText(userToUpdate.getEmail());
        
        String accountType = userToUpdate.getAccountType();
        for (int i = 0; i < ACCOUNT_TYPES.length; i++) {
            if (ACCOUNT_TYPES[i].equals(accountType)) {
                accountTypeComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        String status = userToUpdate.getStatus();
        if (status == null || status.equals("Active")) {
            statusComboBox.setSelectedIndex(0); // Active
        } else if (status.equals("Inactive")) {
            statusComboBox.setSelectedIndex(1); // Inactive
        } else if (status.startsWith("Profile Suspended") || status.startsWith("Account Suspended")) {
            // For suspended profiles shows the suspension status but disable the dropdown
            statusComboBox.addItem(status);
            statusComboBox.setSelectedItem(status);
            statusComboBox.setEnabled(false); 
            statusComboBox.setToolTipText("Suspended accounts can only be reactivated through the Show Suspended Profiles page.");
        }
    }

    private void updateUser() {
        String newUsername = usernameField.getText().trim();
        String password    = new String(passwordField.getPassword()).trim();
        String name        = nameField.getText().trim();
        String email       = emailField.getText().trim();
        String accountType = (String) accountTypeComboBox.getSelectedItem();
        String status      = (String) statusComboBox.getSelectedItem();

        if (newUsername.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserAccount clash = controller.getUserByUsername(newUsername);
        if (clash != null && !clash.getUsername().equals(userToUpdate.getUsername())) {
            JOptionPane.showMessageDialog(this,
                "Invalid username, username has been taken.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailRegex = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        userToUpdate.setUsername(newUsername);
        userToUpdate.setPassword(password);
        userToUpdate.setName(name);
        userToUpdate.setEmail(email);
        userToUpdate.setAccountType(accountType);
        userToUpdate.setStatus(status);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean success = controller.updateUser(userToUpdate);
        setCursor(Cursor.getDefaultCursor());

        if (success) {
            JOptionPane.showMessageDialog(this,
                "User account updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update user account.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showPromptMessage(boolean executed) {
        setVisible(true);
    }
}