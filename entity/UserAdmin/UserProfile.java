package entity.UserAdmin;

// entity holding user profile information
public class UserProfile {
    private int userId;
    private String username;
    private String address;
    private String phoneNumber;

    public UserProfile(int userId, String username, String address, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
}