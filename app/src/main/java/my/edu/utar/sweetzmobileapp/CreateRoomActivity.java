package my.edu.utar.sweetzmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateRoomActivity extends HeaderFooterActivity {

    FloatingActionButton mute;
    TextView text;

     public CreateRoomActivity()
     {
         super("Create Room");
     }

     //mute button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        mute = findViewById(R.id.mute);
        text = findViewById(R.id.fab_text);

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager musicManager = MusicManager.getInstance();
                musicManager.toggleMute();

                if (musicManager.isMuted()) {
                    mute.setImageResource(R.drawable.music_off);
                } else {
                    mute.setImageResource(R.drawable.music_on);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager musicManager = MusicManager.getInstance();
        if (musicManager.isMuted()) {
            musicManager.toggleMute();
        }
    }
}