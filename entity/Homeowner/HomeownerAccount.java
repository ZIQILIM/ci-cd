package entity.Homeowner;

import entity.UserAdmin.UserAccount;

public class HomeownerAccount extends UserAccount {
    
    public HomeownerAccount(String username, String password) {
        super(username, password, "Homeowner");
    }
    
    public HomeownerAccount(String username, String password, String name, String email) {
        super(username, password, "Homeowner", name, email);
    }
    
    public boolean validateLogin(String username, String password) {
        return super.validateLogin(username, password, "Homeowner");
    }
}