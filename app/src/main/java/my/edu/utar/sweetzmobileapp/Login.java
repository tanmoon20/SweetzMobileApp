package my.edu.utar.sweetzmobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;


public class Login extends AppCompatActivity {

    EditText name;
    EditText password;
    TextView guest;
    Button login;
    Button register;

    private UserLoginManager userLoginManager;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI(findViewById(R.id.loginContainer));

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        guest = findViewById(R.id.guest);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        userLoginManager = new UserLoginManager(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginManager.setLoginMode(true);
                startMainActivity(true);
            }
        });

    }

    private void loginUser() {
        String username = name.getText().toString();
        String pwd = password.getText().toString();

        if (username.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Username and password are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, pwd)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            if (userLoginManager.isGuest()) {
                                startMainActivity(true);
                            } else {
                                startMainActivity(false);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please verify your email address before logging in.",
                                    Toast.LENGTH_LONG).show();
                            firebaseAuth.signOut();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Authentication failed: Username or password is null",
                                Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void registerUser() {
        String username = name.getText().toString();
        String pwd = password.getText().toString();

        if (username.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Username and password are required for registration",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.fetchSignInMethodsForEmail(username)
                .addOnSuccessListener(signInMethodsResult -> {
                    if (signInMethodsResult.getSignInMethods() != null && !signInMethodsResult.getSignInMethods().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "User is already registered. Please login.",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        createUserWithEmailAndPassword(username, pwd);
                    }
                })
                .addOnFailureListener(e -> {
                    // Display error message
                    Toast.makeText(getApplicationContext(),
                            "Error checking user registration status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void createUserWithEmailAndPassword(String username, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        String userId = user.getUid();
                        User newUser = new User(username, password);
                        sendEmailVerification(user);
                        storeUserInfo(user.getUid(), username, password);

                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.child(userId).setValue(newUser);

                        progressDialog.dismiss();
                        loginUser();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Registration error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Registration successful. Please verify your email before logging in.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Email verification could not be sent. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void storeUserInfo(String userId, String username, String password) {
        User newUser = new User(username, password);

        DocumentReference userDocument = usersCollection.document(userId);
        userDocument.set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(),
                            "Registration successful. Please verify your email before logging in.",
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Error storing user information: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        userLoginManager.clear(); // Clear the login mode
    }

    private void startMainActivity(boolean go) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IS_GUEST", go);
        startActivity(intent);
        finish();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Login.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}