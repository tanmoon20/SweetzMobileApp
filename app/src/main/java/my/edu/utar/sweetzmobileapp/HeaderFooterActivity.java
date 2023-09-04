package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class HeaderFooterActivity extends AppCompatActivity {

    private String title;
    private UserLoginManager userLoginManager;

    protected boolean userAllowed = false;
    protected boolean isGuest = false;

    public HeaderFooterActivity(String title)
    {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLoginManager = new UserLoginManager(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getEmail() != null) {
                userAllowed = true;
                isGuest = userLoginManager.isGuest();
            }
        }

        isGuest = userLoginManager.isGuest();
    }

    public void setContentView (int layoutResID){
        RelativeLayout rl = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_header_footer,null);
        setContentView(rl);

        LinearLayout ll = (LinearLayout) rl.findViewById(R.id.llcontainer);
        getLayoutInflater().inflate(layoutResID, ll, true);

        BottomNavigationView navigationView = findViewById(R.id.footer);
        Menu menu = navigationView.getMenu();
        MenuItem createRoomItem = menu.findItem(R.id.createQuiz);
        MenuItem homeItem = menu.findItem(R.id.searchQuizPublic);

        TextView titletv = findViewById(R.id.titletv);
        titletv.setText(title);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.createQuiz:
                        goCreateRoom();
                        return true;
                    case R.id.searchQuizPublic:
                        goHome();
                        return true;
                    case R.id.searchQuizPrivate:
                        if (userAllowed || !isGuest) {
                            goPrivate();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You are not allowed to access Private Room",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(HeaderFooterActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    case R.id.settings:
                        goSetting();
                        return true;
                }
                return false;
            }
        });
    }

    protected void goCreateRoom(){
        Intent intent = new Intent(getApplicationContext(),CreateRoomActivity.class);
        startActivity(intent);
    }

    protected void goHome(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    protected void goSetting(){
        Intent intent = new Intent(getApplicationContext(),SettingPage.class);
        startActivity(intent);
    }

    protected void goPrivate(){
        Intent intent = new Intent(getApplicationContext(), PrivateRoomActivity.class);
        startActivity(intent);
    }
}