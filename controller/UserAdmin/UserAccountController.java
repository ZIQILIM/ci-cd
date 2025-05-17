package controller.UserAdmin;

import entity.*;
import entity.UserAdmin.UserAccount;
import entity.UserAdmin.UserProfile;
import java.util.List;
import java.util.ArrayList;

public class UserAccountController {
    private UserDAO userDAO;
    
    public UserAccountController() {
        this.userDAO = new UserDAO();
        userDAO.initializeDemoAccounts();
    }
    
    // From CreateUserAccountController
    public boolean createUserAccount(String username, String password, String email, String name) {  // var1 = username var2 = password var3 = email var4 = name
        if (userDAO.findByUsername(username) != null) return false;
        if (userDAO.findByEmail(email) != null) return false;
        
        UserAccount newAccount = new UserAccount(username, password, "", name, email);
        newAccount.setAddress("");
        newAccount.setPhoneNumber("");
        
        return userDAO.createUser(newAccount);
    }
    
    // From CreateUserProfileController
    public boolean updateUserProfile(UserAccount user) {
        return userDAO.updateUser(user);
    }
    
    // From LoginController
    public UserAccount validateLogin(String username, String password, String profileType) {   // var1 = username var2 = password var3 = profileType
        UserAccount account = userDAO.validateLogin(username, password, profileType);
        
        // Check if account is suspended
        if (account != null && account.getStatus() != null && 
            account.getStatus().startsWith("Account Sus")) {
            return null; // Return null to indicate login failure
        }
        
        return account;
    }
    
    // From SearchUserAccountController
    public UserAccount searchUserAccount(String username) {   // var1 = username
        return userDAO.findByUsername(username);
    }
    
    // From SuspendUserAccountController
    public boolean suspendAccount(String username, String status) {   // var1 = username  var2 = status 
        UserAccount user = userDAO.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        return userDAO.updateUser(user);
    }
    
    // From UpdateUserAccountController
    public boolean deleteUser(String username) {   // var1 = username
        return userDAO.deleteUser(username);
    }
    
    public boolean updateUser(UserAccount user) {   // var1  = user
        return userDAO.updateUser(user);
    }
    
    // From UpdateUserProfileController
    public boolean updateUserProfile(String username, String address, String phoneNumber) {    // var1 = username var2 = address var3 = phonenumber
        UserAccount user = userDAO.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        
        return userDAO.updateUser(user);
    }
    
    // From UserAccountViewController
    public List<UserAccount> getAllUserAccounts() {
        return userDAO.getAllUsers();
    }
    
    public List<UserAccount> searchUserAccounts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUserAccounts();
        }
        
        return userDAO.searchUsers(searchTerm);
    }
    
    public UserAccount getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    // From ViewUserProfileController
    public ArrayList<UserProfile> viewUserProfile() {
        ArrayList<UserProfile> profiles = new ArrayList<>();
        for (UserAccount user : userDAO.getAllUsers()) {
            if (!user.isProfileSuspended()) {
                UserProfile profile = new UserProfile(
                    user.getUserId(),
                    user.getUsername(),
                    user.getAddress(),
                    user.getPhoneNumber()
                );
                profiles.add(profile);
            }
        }
        return profiles;
    }
    
    public boolean suspendUserProfile(String username) {
        UserAccount user = userDAO.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        user.setProfileSuspended(true);
        user.setStatus("Profile Suspended");
        
        return userDAO.updateUser(user);
    }
    
    public UserAccount findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}