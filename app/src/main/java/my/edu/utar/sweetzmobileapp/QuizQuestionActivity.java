package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class QuizQuestionActivity extends HeaderFooterActivity {

    public QuizQuestionActivity(){
        super("Create Quiz Questions");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);
    }
}