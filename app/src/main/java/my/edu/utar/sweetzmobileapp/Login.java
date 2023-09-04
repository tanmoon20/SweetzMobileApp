package my.edu.utar.sweetzmobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
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
        String user_email = name.getText().toString();
        String user_pwd = password.getText().toString();

        if (user_email.isEmpty() || user_pwd.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Email and password are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(user_email, user_pwd)
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
                                "Authentication failed: email or password is null",
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
        String user_email = name.getText().toString();
        String user_pwd = password.getText().toString();

        if (user_email.isEmpty() || user_pwd.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Email and password are required for registration",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.fetchSignInMethodsForEmail(user_email)
                .addOnSuccessListener(signInMethodsResult -> {
                    if (signInMethodsResult.getSignInMethods() != null && !signInMethodsResult.getSignInMethods().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "User is already registered. Please login.",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Username");

                        final EditText usernameInput = new EditText(this);
                        builder.setView(usernameInput);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String username = usernameInput.getText().toString().trim();
                                if (!username.isEmpty()) {
                                    createUserWithEmailAndPassword(user_email, user_pwd, username);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Username cannot be empty",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Error checking user registration status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void createUserWithEmailAndPassword(String email, String password, String username)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        String userId = user.getUid();
                        User newUser = new User(email, password, username);
                        sendEmailVerification(user);
                        storeUserInfo(userId, email, password, username);

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

    private void storeUserInfo(String userId, String user_email, String user_pwd, String username) {
        User newUser = new User(user_email, user_pwd, username); 

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