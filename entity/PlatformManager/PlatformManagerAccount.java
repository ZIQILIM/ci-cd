package entity.PlatformManager;

import entity.UserAdmin.UserAccount;

public class PlatformManagerAccount extends UserAccount {
    
    public PlatformManagerAccount(String username, String password) {
        super(username, password, "Platform Manager");
    }
    
    public PlatformManagerAccount(String username, String password, String name, String email) {
        super(username, password, "Platform Manager", name, email);
    }
    
    // Additional methods specific to PlatformManager
    public boolean validateLogin(String username, String password) {
        return super.validateLogin(username, password, "Platform Manager");
    }
}