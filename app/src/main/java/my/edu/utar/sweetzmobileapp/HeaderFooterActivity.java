package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class HeaderFooterActivity extends AppCompatActivity {

    private String headerTitle;
    private UserLoginManager userLoginManager;

    protected boolean userAllowed = false;
    protected boolean isGuest = false;

    public HeaderFooterActivity(String title)
    {
        this.headerTitle = title;
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

        //disable footer
        disableFooter();

        displayTitle();

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

        setupUI(findViewById(R.id.parentContainer));
    }

    protected void goCreateRoom(){
        Intent intent = new Intent(getApplicationContext(),CreateQuizActivity.class);
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

    private void disableFooter(){
        BottomNavigationView navigationView = findViewById(R.id.footer);
        Menu menu = navigationView.getMenu();


        if(headerTitle.compareTo("Create Room") == 0)
        {
            MenuItem menuItem = menu.findItem(R.id.createQuiz);
            menuItem.setOnMenuItemClickListener((itm)->{
                itm.setEnabled(false);
                return true;
            });
        }
        else if(headerTitle.compareTo("Public") == 0)
        {
            MenuItem menuItem = menu.findItem(R.id.searchQuizPublic);
            menuItem.setOnMenuItemClickListener((itm)->{
                itm.setEnabled(false);
                return true;
            });
        }
        else if(headerTitle.compareTo("Private") == 0)
        {
            MenuItem menuItem = menu.findItem(R.id.searchQuizPrivate);
            menuItem.setOnMenuItemClickListener((itm)->{
                itm.setEnabled(false);
                return true;
            });
        }
        else if(headerTitle.compareTo("Setting") == 0)
        {
            MenuItem menuItem = menu.findItem(R.id.settings);
            menuItem.setOnMenuItemClickListener((itm)->{
                itm.setEnabled(false);
                return true;
            });
        }
    }

    public void displayTitle()
    {
        TextView titletv = findViewById(R.id.titletv);
        titletv.setText(headerTitle);
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
                    hideSoftKeyboard(HeaderFooterActivity.this);
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


    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
}