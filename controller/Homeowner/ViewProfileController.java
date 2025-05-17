package controller.Homeowner;

import java.util.List;

import entity.CleaningServiceDAO;
import entity.Cleaner.CleanerAccount;
import entity.Cleaner.CleaningService;
import entity.UserDAO;
import entity.UserAdmin.UserAccount;

public class ViewProfileController {
    private UserDAO userDAO;
    private CleaningServiceDAO serviceDAO;
    
    public ViewProfileController() {
        this.userDAO = new UserDAO();
        this.serviceDAO = new CleaningServiceDAO();
    }
    
    public CleanerAccount getCleanerInfo(String cleanerId) {
        UserAccount user = userDAO.findByUsername(cleanerId);
        
        if (user == null || !user.getAccountType().equals("Cleaner")) {
            return null;
        }
        
        // Convert UserAccount to CleanerAccount
        CleanerAccount cleanerAccount = new CleanerAccount(
            user.getUsername(),
            user.getPassword(),
            user.getName(),
            user.getEmail()
        );
        
        cleanerAccount.setAddress(user.getAddress());
        cleanerAccount.setPhoneNumber(user.getPhoneNumber());
        
        return cleanerAccount;
    }
    
    public List<CleaningService> getCleanerServices(String cleanerId) {
        return serviceDAO.getServicesByCleanerUsername(cleanerId);
    }
}