package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PrivateRoomActivity extends HeaderFooterActivity {

    public PrivateRoomActivity(){super("Private");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_class);

        displayRoom();
    }

    public void displayRoom(){
        LinearLayout ll = findViewById(R.id.room_container);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,30,30,60);
        cardView.setLayoutParams(params);

        LinearLayout playCountContatner = cardView.findViewById(R.id.playCountContainer);
        playCountContatner.setVisibility(View.GONE);

//        ImageView btnPlay = cardView.findViewById(R.id.btnPlay);
//        btnPlay.setVisibility(View.GONE);

        ll.addView(cardView, 0);
    }
}