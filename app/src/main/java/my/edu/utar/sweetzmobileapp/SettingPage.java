package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingPage extends HeaderFooterActivity {
    private Switch musicBoolean;
    private MusicManager musicManager;

    public SettingPage() {
        super("Setting");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        musicBoolean = findViewById(R.id.musicBoolean);
        musicManager = MusicManager.getInstance();

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


    }
}