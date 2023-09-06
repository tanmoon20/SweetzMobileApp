package my.edu.utar.sweetzmobileapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private String username;
    private String user_pwd;
    private String user_email;
    private boolean isGuest;

    public static User currentUser;

    public User() {
        this.username = "";
        this.user_email = "";
        this.user_pwd = "";
        this.isGuest = false;
    }

    public User(String user_email, String user_pwd, String username, boolean isGuest) {
        this.username = username;
        this.user_pwd = user_pwd;
        this.user_email = user_email;
        this.isGuest = isGuest;
    }

    public boolean isGuest() { return isGuest; }

    public void setGuest(boolean guest) { isGuest = guest; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getUser_pwd() { return user_pwd; }

    public void setUser_pwd(String user_pwd) { this.user_pwd = user_pwd; }

    public String getUser_email() { return user_email; }

    public void setUser_email(String user_email) { this.user_email = user_email; }

    // Fetch user data from Firestore and update the username
    public void fetchUserDataFromFirestore() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

            userRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.w("User", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        this.username = user.getUsername();
                    }
                }
            });
        }
    }
}