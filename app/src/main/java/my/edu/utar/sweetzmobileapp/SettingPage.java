package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPage extends HeaderFooterActivity {
    private Button musicButton;
    private MusicManager musicManager;
    private ImageButton qr_btn;

    FloatingActionButton login;
    TextView text;

    public SettingPage() {
        super("Setting");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        musicManager = MusicManager.getInstance();


        login = findViewById(R.id.login);
        text = findViewById(R.id.fab_text);
        qr_btn = findViewById(R.id.quiz_camera_btn);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        musicButton = findViewById(R.id.musicBoolean);
        if(!musicManager.isPlaying()){
            musicButton.setBackgroundResource(R.drawable.red_settingmusicbutton);
            musicButton.setText("OFF");
            musicButton.setTextColor(Color.WHITE);
        }else{
            musicButton.setBackgroundResource(R.drawable.green_settingmusicbutton);
            musicButton.setText("ON");
            musicButton.setTextColor(Color.BLACK);
        }
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicManager.isPlaying()){
                    //if it is playing then you need to turn it off
                    musicButton.setBackgroundResource(R.drawable.red_settingmusicbutton);
                    musicButton.setText("OFF");
                    musicButton.setTextColor(Color.WHITE);
                    musicManager.togglePlayback();
                }else{
                    //if it is not then you need to turn it on
                    musicButton.setBackgroundResource(R.drawable.green_settingmusicbutton);
                    musicButton.setText("ON");
                    musicButton.setTextColor(Color.BLACK);
                    musicManager.togglePlayback();
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    text.setText("Sign Out");
                    auth.signOut();
                    Toast.makeText(getApplicationContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SettingPage.this, Login.class);
                    startActivity(intent);
                } else {
                    text.setText("Sign In / Register");
                    Intent intent = new Intent(SettingPage.this, Login.class);
                    startActivity(intent);
                }
            }
        });

        qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, QRScanner.class);
                startActivity(intent);
            }
        });
    }
}