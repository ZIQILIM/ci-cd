package boundary.UserAdmin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import boundary.Login.EnhancedLoginPage;
import entity.UserAdmin.UserAccount;

// Boundary class for User Admin
public class UserAdminPage extends JFrame {
    private JButton logoutButton;
    private JButton createUserButton;
    private UserAccount userAccount;
    private JLabel welcomeLabel;
    
    public UserAdminPage() {
        this.userAccount = null;
        initializeUI();
    }
    
    // Add constructor that accepts a UserAccount
    public UserAdminPage(UserAccount userAccount) {
        this.userAccount = userAccount;
        initializeUI();
        updateUserInfo();
    }
    
    private void initializeUI() {
        setTitle("User Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("Welcome, Admin");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        createUserButton = new JButton("Create User");
        createUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCreateUserPage(e);
            }
        });
        topButtonPanel.add(createUserButton);
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchToLoginPage();
            }
        });
        topButtonPanel.add(logoutButton);
        
        topPanel.add(topButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton viewUsersButton = new JButton("View User Accounts");
        viewUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUserAccountViewPage();
            }
        });

        JButton showSuspendedButton = new JButton("Show Suspended Profiles");
        showSuspendedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openShowSuspendedProfilesPage();
            }
        });
        
        JButton viewUserProfileButton = new JButton("View User Profile");
        viewUserProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openViewUserProfilePage();
            }
        });
        
        JButton updateUserProfileButton = new JButton("Update User Profile");
        updateUserProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUpdateUserProfilePage();
            }
        });
        
        contentPanel.add(viewUsersButton);
        contentPanel.add(showSuspendedButton);
        contentPanel.add(viewUserProfileButton);
        contentPanel.add(updateUserProfileButton);
        
        add(contentPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("System Status: Online");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private void updateUserInfo() {
        if (userAccount != null) {
            String name = userAccount.getName();
            if (name != null && !name.isEmpty()) {
                welcomeLabel.setText("Welcome, " + name);
            } else {
                welcomeLabel.setText("Welcome, " + userAccount.getUsername());
            }
        }
    }
    
    private void openCreateUserPage(ActionEvent e) {
        CreateUserAccountPage createUserPage = new CreateUserAccountPage(userAccount);
        createUserPage.showInputForm(true);
    }
    
    private void openUserAccountViewPage() {
        UserAccountViewPage viewPage = new UserAccountViewPage(userAccount);
        viewPage.displayUserViewPage();
    }
    
    private void openShowSuspendedProfilesPage() {
        ShowSuspendedProfilesPage suspendedPage = new ShowSuspendedProfilesPage(userAccount);
        suspendedPage.displayPage();
    }
    
    private void openViewUserProfilePage() {
        ViewUserProfilePage profilePage = new ViewUserProfilePage();
        profilePage.displayUserProfilePage();
    }
    
    private void openUpdateUserProfilePage() {
        UpdateUserProfilePage updatePage = new UpdateUserProfilePage();
        updatePage.display();
    }
    
    public void switchToLoginPage() {
        dispose(); 
        SwingUtilities.invokeLater(() -> {
            EnhancedLoginPage loginPage = new EnhancedLoginPage();
            loginPage.displayLoginPage();
        });
    }
}