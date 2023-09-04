package my.edu.utar.sweetzmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends HeaderFooterActivity  {

    public Login(){ super("Login"); }

    EditText name;
    EditText password;
    TextView guest;
    Button login;
    Button register;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        guest = findViewById(R.id.guest);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.footer);

        if (userAllowed || isGuest) {
            navigationView.getMenu().findItem(R.id.createQuiz).setEnabled(true);
            navigationView.getMenu().findItem(R.id.searchQuizPublic).setEnabled(true);
            navigationView.getMenu().findItem(R.id.searchQuizPrivate).setEnabled(true);
            navigationView.getMenu().findItem(R.id.settings).setEnabled(true);
        } else {
            navigationView.getMenu().findItem(R.id.createQuiz).setEnabled(false);
            navigationView.getMenu().findItem(R.id.settings).setEnabled(false);
            navigationView.getMenu().findItem(R.id.searchQuizPublic).setEnabled(false);
            navigationView.getMenu().findItem(R.id.searchQuizPrivate).setEnabled(false);
        }

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
                startMainActivity();
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
                    if (user != null && user.getEmail() != null) {
                        startMainActivity();
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
                                "User is already registered. Please log in.",
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


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IS_GUEST", true);
        startActivity(intent);
        finish();
    }

}