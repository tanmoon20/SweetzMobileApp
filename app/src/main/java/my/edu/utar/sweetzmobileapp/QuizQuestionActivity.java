package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class QuizQuestionActivity extends HeaderFooterActivity {
    int questionNum;
    TextView questionNumber;
    EditText editQuestion, editCorrect, editWrongA, editWrongB, editWrongC;
    ImageButton preBtn, nextBtn;
    public QuizQuestionActivity(){
        super("Create Quiz Questions");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);
        questionNumber = findViewById(R.id.question_num);
        editCorrect = findViewById(R.id.edit_correct);
        editQuestion = findViewById(R.id.editQuestion);
        editWrongA = findViewById(R.id.editWrongA);
        editWrongB = findViewById(R.id.editWrongB);
        editWrongC = findViewById(R.id.editWrongC);
        preBtn = findViewById(R.id.imageButton6);
        nextBtn = findViewById(R.id.imageButton3);
        Intent intent = getIntent();
        //every time next is clicked, clear all (or intent) and increase questionNum by one
        //prevent user from filling two options only?
        //insert the question to firebase everytime user click next
    }
}