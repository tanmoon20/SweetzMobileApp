package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HeaderFooterActivity extends AppCompatActivity {

    private String title;

    public HeaderFooterActivity(String title)
    {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        createRoomItem.setOnMenuItemClickListener((itm)->{
            goCreateRoom();
            return true;
        });

        homeItem.setOnMenuItemClickListener((itm)->{
            Log.i("home", "Home");
            goHome();
            return true;
        });
    }

    protected void goCreateRoom(){
        Intent intent = new Intent(this,CreateRoomActivity.class);
        startActivity(intent);
    }

    protected void goHome(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}