package my.edu.utar.sweetzmobileapp;

import androidx.annotation.NonNull;
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

        TextView titletv = findViewById(R.id.titletv);
        titletv.setText(title);

        BottomNavigationView navigationView = findViewById(R.id.footer);
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
                        return true;
                    case R.id.settings:
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
}