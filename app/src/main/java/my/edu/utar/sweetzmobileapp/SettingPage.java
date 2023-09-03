package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPage extends HeaderFooterActivity {
    private Switch musicBoolean;
    private MusicManager musicManager;

    FloatingActionButton login;
    TextView text;

    public SettingPage() {
        super("Setting");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        musicBoolean = findViewById(R.id.musicBoolean);
        musicManager = MusicManager.getInstance();

        login = findViewById(R.id.login);
        text = findViewById(R.id.fab_text);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        musicBoolean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    musicBoolean.getThumbDrawable().setTint(Color.rgb(254,142,252));
                    musicManager.togglePlayback();
                } else {
                    musicBoolean.getThumbDrawable().setTint(getResources().getColor(android.R.color.white));
                    if (musicManager.isPlaying()) {
                        musicManager.togglePlayback();
                    } else {
                        Log.d("MusicManager", "MediaPlayer is not playing");
                    }
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
    }
}