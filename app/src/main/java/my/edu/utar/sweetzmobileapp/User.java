package my.edu.utar.sweetzmobileapp;

public class User {
    String username;
    String user_pwd;
    String user_email;
    boolean isGuest;

    public User() {
        this.username = "";
        this.user_email = "";
        this.user_pwd = "";
        this.isGuest = false;
    }

    public User(String username, String user_pwd, String user_email, boolean isGuest) {
        this.username = username;
        this.user_pwd = user_pwd;
        this.user_email = user_email;
        this.isGuest = false;
    }

    public boolean isGuest() { return isGuest; }

    public void setGuest(boolean guest) { isGuest = guest; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
