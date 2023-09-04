package my.edu.utar.sweetzmobileapp;

public class User {

    private String user_email;
    private String password;
    private String username; // Add username field

    public User(String user_email, String password, String username) {
        this.user_email = user_email;
        this.password = password;
        this.username = username;
    }

    public String getUser_email() { return user_email; }

    public void setUser_email(String user_email) { this.user_email = user_email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

}
