package entity.UserAdmin;

public class UserAdminAccount extends UserAccount {
    
    public UserAdminAccount(String username, String password) {
        super(username, password, "User Admin");
    }
    
    public UserAdminAccount(String username, String password, String name, String email) {
        super(username, password, "User Admin", name, email);
    }

    public boolean validateLogin(String username, String password) {
        return super.validateLogin(username, password, "User Admin");
    }
}