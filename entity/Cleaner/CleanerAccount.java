package entity.Cleaner;

import entity.UserAdmin.UserAccount;

public class CleanerAccount extends UserAccount {
    
    public CleanerAccount(String username, String password) {
        super(username, password, "Cleaner");
    }
    
    public CleanerAccount(String username, String password, String name, String email) {
        super(username, password, "Cleaner", name, email);
    }
    
    public boolean validateLogin(String username, String password) {
        return super.validateLogin(username, password, "Cleaner");
    }
}