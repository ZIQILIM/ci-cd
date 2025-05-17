package entity.UserAdmin;

// Entity class
public class UserAccount {
    private int userId;              
    
    private String username;
    private String password;
    private String accountType;
    private String name;
    private String email;
    private String status;
    
    private String address;
    private String phoneNumber;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public UserAccount(String username, String password, String accountType) {
        this(username, password, accountType, null, null);
    }
    
    public UserAccount(String username, String password, String accountType, 
                      String name, String email) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.name = name;
        this.email = email;
        this.status = "Active";
        this.address = "";      
        this.phoneNumber = "";   
    }
    
    public UserAccount(String username, String password, String accountType, 
                      String name, String email, String address, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.name = name;
        this.email = email;
        this.status = "Active";  
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public boolean validateLogin(String username, String password, String accountType) {
        return this.username.equals(username) && 
               this.password.equals(password) && 
               this.accountType.equals(accountType);
    }

    public UserAccount searchUserAccount(String username) {
    if (this.username.equals(username)) {
        return this;
    }
    return null;
}
    private boolean profileSuspended = false;
    public boolean isProfileSuspended() {
        return profileSuspended;
    }

    public void setProfileSuspended(boolean profileSuspended) {
        this.profileSuspended = profileSuspended;
    }
}