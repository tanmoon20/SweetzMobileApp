package my.edu.utar.sweetzmobileapp;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizQuestionActivity extends HeaderFooterActivity {
    int questionNum = 1;
    TextView questionNumber;
    EditText editQuestion, editCorrect, editWrongA, editWrongB, editWrongC;
    ImageButton preBtn, nextBtn;
    Quiz quiz;
    Boolean isPublic;
    String question, correct;
    ArrayList<String> wrong = new ArrayList<>();
    FirestoreManager2 fm2 = new FirestoreManager2();
    FirestoreManager fm = new FirestoreManager();
    Handler handler = new Handler();

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
        questionNumber.setText("Question "+questionNum);
        Intent intent = getIntent();
        quiz = (Quiz) intent.getSerializableExtra("quiz");
        isPublic = intent.getBooleanExtra("isPublic", true);
        preBtn.setVisibility(GONE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question = editQuestion.getText().toString();
                correct = editCorrect.getText().toString();
                if(!editWrongA.getText().toString().isEmpty()){
                    wrong.add(editWrongA.getText().toString());
                }else if(!editWrongB.getText().toString().isEmpty()){
                    wrong.add(editWrongB.getText().toString());
                }else if(!editWrongC.getText().toString().isEmpty()){
                    wrong.add(editWrongC.getText().toString());
                }
                if(!question.isEmpty()&&!correct.isEmpty()&&wrong.size()>=1){
                    fm2.insertPublicQuizQuestion(quiz.getQuizId(), "question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString()); //need to use method of checking size to determine how many options has been filled in
                    //maybe user input optionA and C, not B
                    //then get wrong A and B C using editText.getText so the options can be get
                    //whether it is "" or not

                    //fetch info of next question
                    questionNum++;
                    if(questionNum>1){
                        preBtn.setVisibility(View.VISIBLE);
                    }
                    NextThread nextThread = new NextThread();
                    nextThread.start();
                    //if empty then setText("")

                }else{
                    Toast.makeText(QuizQuestionActivity.this, "You need to fill in question, correct option and at least one wrong option", Toast.LENGTH_SHORT).show();
                }

            }
        });


        preBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(questionNum>=1){
                    question = editQuestion.getText().toString();
                    correct = editCorrect.getText().toString();
                    if(!editWrongA.getText().toString().isEmpty()){
                        wrong.add(editWrongA.getText().toString());
                    }else if(!editWrongB.getText().toString().isEmpty()){
                        wrong.add(editWrongB.getText().toString());
                    }else if(!editWrongC.getText().toString().isEmpty()){
                        wrong.add(editWrongC.getText().toString());
                    }
                    fm2.insertPublicQuizQuestion(quiz.getQuizId(), "question" + questionNum, correct, question, editWrongA.getText().toString(), editWrongB.getText().toString(), editWrongC.getText().toString()); //need to use method of checking size to determine how many options has been filled in
                    //maybe user input optionA and C, not B
                    questionNum--;
                    questionNumber.setText("Question "+questionNum);
                    NextThread nextThread = new NextThread();
                    nextThread.start();
                }
                if(questionNum==1){
                    preBtn.setVisibility(View.GONE);
                }
            }
        });

    }

    private class NextThread extends Thread{
        @Override
        public void run() {
            fm.getPublicRoomQuizSpecificQuestion(quiz.getQuizId(), "question"+(questionNum-1),new NextCallback());
        }

        private class NextCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                if(result[0].equals("notFound")){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            editQuestion.setText("");
                            editCorrect.setText("");
                            editWrongA.setText("");
                            editWrongB.setText("");
                            editWrongC.setText("");
                            Toast.makeText(QuizQuestionActivity.this, "Question "+questionNum+" created successfully", Toast.LENGTH_SHORT).show();
                            questionNumber.setText("Question "+questionNum);
                        }
                    });
                }else {
                    wrong.clear();
                    correct = result[0];
                    question = result[1];
                    wrong.add(result[2]);
                    wrong.add(result[3]);
                    wrong.add(result[4]);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            editQuestion.setText(question);
                            editCorrect.setText(correct);
                            editWrongA.setText(wrong.get(0));
                            editWrongB.setText(wrong.get(1));
                            editWrongC.setText(wrong.get(2));
                            questionNumber.setText("Question "+questionNum);
                        }
                    });
                }
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }

    @Override
    public void onBackPressed() {
/*        CheckThread checkThread = new CheckThread();
        checkThread.start();*/
        boolean isQuit = false;
        CheckThread checkThread = new CheckThread();
        checkThread.start();
        isQuit = checkThread.getIsQuit();
        if(isQuit) super.onBackPressed();
    }
    private class CheckThread extends Thread{
        private boolean isQuit = false;
        @Override
        public void run() {
            //should be using lastQuestion == null or not
            Log.i("function", "check thread run");
            if(isPublic){
                Log.i("function", "if empty public run");
                fm.getPublicRoomQuizSpecificQuestion(quiz.getQuizId(), "question1", new CheckCallback());
            }else {
                fm.getPrivateRoomQuizSpecificQuestion(quiz.getRoomCode(), quiz.getQuizId(), "question1", new CheckCallback());
            }
        }

        private class CheckCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                    if(result[0].equals("notFound")&&question.isEmpty()){
                        Log.i("function", "if empty run");
                        isQuit = createEmptyQuizDialog("Empty Quiz", "Do you really want to exit?");
                        Log.i("function", "result return "+isQuit);
                    }

            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }

        public boolean getIsQuit(){
            return isQuit;
        }
    }


    public boolean createEmptyQuizDialog(String title, String message){
        Log.i("function", "create dialog");

        Context context = QuizQuestionActivity.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message);
        final boolean[] isQuit = {false};

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isPublic){
                    fm2.deletePublicQuiz(quiz.getQuizId());
                    Log.i("function", "delete run");

/*                    Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                    startActivity(intent);*/
                }else{
                    fm2.deletePrivateQuiz(quiz.getRoomCode(), quiz.getQuizId());
                    Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                Log.i("function", "change isQuite run");

                isQuit[0] = true;

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return isQuit[0];
    }

    /*public void Crea*/
}