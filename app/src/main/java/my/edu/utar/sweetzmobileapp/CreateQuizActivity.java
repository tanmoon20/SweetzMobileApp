package my.edu.utar.sweetzmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateQuizActivity extends HeaderFooterActivity {
    TextView codeTv;
    EditText editName, editRoomCode, editDesc;
    Button enterBtn;
    String roomCode, quizName, quizDesc, quizCode, author = "user1";
    Boolean isPublic;
    FirestoreManager2 firestoreManager2 = new FirestoreManager2();
    FirestoreManager fm = new FirestoreManager();
    Quiz quiz = new Quiz();
    Handler handler = new Handler();

    public CreateQuizActivity() {
        super("Create Quiz");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        editRoomCode = findViewById(R.id.edit_enter_code);
        codeTv = findViewById(R.id.enter_code_textview);
        editName = findViewById(R.id.edit_quiz_name);
        editDesc = findViewById(R.id.edit_quiz_desc);
        enterBtn = findViewById(R.id.create_quiz_btn);
        /*author = Login.currentUser.getUsername;*/

        Intent intent = getIntent();
        isPublic = getIntent().getBooleanExtra("isPublic", true);
        if(isPublic){
            roomCode = null;
            editRoomCode.setHint("No code is needed");
            editRoomCode.setBackgroundColor(getResources().getColor(R.color.grey));
            codeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(CreateQuizActivity.this, "No code is needed for public quiz", Toast.LENGTH_LONG).show();
                }
            });

        }else{
            roomCode = intent.getStringExtra("roomCode");
            editRoomCode.setText(roomCode);
            //save it to firestore and set the quizId
        }
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizName = editName.getText().toString();
                quizDesc = editDesc.getText().toString();
                if(!quizName.isEmpty()){
                    quiz.setRoomCode(roomCode);
                    //disable user input in both public and private(private will get roomCode from intent)
                    if(!isPublic){
                        new IDPrivateThread().start();
                        //create the new quiz object
                        //should intent to the activity that create question, but which is it

                    }else{
                        Log.i("quizCode", "hello");
                        //save it to firestore and set the quizId
                        new IDPublicThread().start();
                    }

                    Intent goIntent = new Intent(CreateQuizActivity.this, QuizQuestionActivity.class);
                    goIntent.putExtra("quiz",quiz);
                    startActivity(goIntent);

                }else{
                    Toast.makeText(CreateQuizActivity.this, "Please specify the quiz name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class IDPrivateThread extends Thread{
        @Override
        public void run() {
            fm.getLastPrivateQuiz(roomCode, new LastQuizPrivateCallback());
        }

        private class LastQuizPrivateCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                quizCode = "quiz"+(Integer.parseInt(result[0].substring(4))+1);
                firestoreManager2.insertPrivateRoomQuiz(roomCode,quizCode, quizName, quizDesc, author);

                quiz.setQuizId(quizCode);
                quiz.setAuthor(author);
                quiz.setDesc(quizDesc);
                quiz.setTitle(quizName);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent goIntent = new Intent(CreateQuizActivity.this, QuizQuestionActivity.class);
                        goIntent.putExtra("quiz", quiz);
                        goIntent.putExtra("isPublic", isPublic);
                        goIntent.putExtra("roomCode", roomCode);
                        startActivity(goIntent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }

    private class IDPublicThread extends Thread{
        @Override
        public void run() {
            fm.getLastPublicQuiz(new LastQuizPublicCallback());
        }

        private class LastQuizPublicCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                quizCode = "quiz"+(Integer.parseInt(result[0].substring(4))+1);
                firestoreManager2.insertPublicQuiz(quizCode,quizName, quizDesc, author);
                quiz.setQuizId(quizCode);
                quiz.setAuthor(author);
                quiz.setDesc(quizDesc);
                quiz.setTitle(quizName);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("quizCode", "hello3");

                        Intent goIntent = new Intent(CreateQuizActivity.this, QuizQuestionActivity.class);
                        goIntent.putExtra("quiz", quiz);
                        goIntent.putExtra("roomCode", roomCode);
                        startActivity(goIntent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }
}
