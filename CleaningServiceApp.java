import javax.swing.SwingUtilities;

import javax.swing.UIManager;

import boundary.Login.EnhancedLoginPage;


public class CleaningServiceApp {
    public static void main(String[] args) {
        // Start the application with the login page
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
                
                EnhancedLoginPage loginPage = new EnhancedLoginPage();
                loginPage.displayLoginPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}