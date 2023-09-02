package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlayActivity extends HeaderFooterActivity {

    public PlayActivity(){
        super("Play");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

//        hide footer
        BottomNavigationView bottomNavigationView = findViewById(R.id.footer);
        bottomNavigationView.setVisibility(View.GONE);

        displayQuizTitle();
        displayQuestionAnswer();
        displayQuestionAnswer();
        displayQuestionAnswer();
        displayQuestionAnswer();
        displayQuestionAnswer();
    }

    public void displayQuizTitle(){
        LinearLayout ll = findViewById(R.id.titleContainer);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,30,30,60);
        cardView.setLayoutParams(params);

//        ImageView btnPlay = cardView.findViewById(R.id.btnPlay);
//        btnPlay.setVisibility(View.GONE);

        ll.addView(cardView, 0);
    }

    public void displayQuestionAnswer(){
        LinearLayout ll = findViewById(R.id.questionContainer);

        View cardView = getLayoutInflater().inflate(R.layout.qna_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,4,30,4);
        cardView.setLayoutParams(params);

        ll.addView(cardView);
    }

    public void setCorrect(Button btn){
        btn.setOnClickListener((v)->{
            btn.setBackgroundColor(Color.GREEN);
        });
    }

    public void setWrong(Button btn){
        btn.setOnClickListener((v)->{
            btn.setBackgroundColor(Color.RED);
        });
    }

    public void goBack(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }
}