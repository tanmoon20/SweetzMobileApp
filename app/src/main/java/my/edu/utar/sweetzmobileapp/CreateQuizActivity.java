package my.edu.utar.sweetzmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateQuizActivity extends HeaderFooterActivity {
    TextView editName, editRoomCode, editDesc;
    Button enterBtn;
    String roomCode, quizName, quizDesc, quizCode, author = "user1";
    Integer numPlay = 0;
    Boolean isPublic;
    FirestoreManager2 firestoreManager2 = new FirestoreManager2();
    FirestoreManager fm = new FirestoreManager();

    Quiz quiz;

    public CreateQuizActivity() {
        super("Create Quiz");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        editRoomCode = findViewById(R.id.edit_enter_code);
        editName = findViewById(R.id.edit_quiz_name);
        editDesc = findViewById(R.id.edit_quiz_desc);
        enterBtn = findViewById(R.id.create_quiz_btn);

        Intent intent = getIntent();
        isPublic = getIntent().getBooleanExtra("isPublic", true);
        if(isPublic){
            roomCode = null;
            Log.i("room:", "Public");
            Log.i("room:", "000");
            editRoomCode.setHint("No code is needed for public");
        }else{
            roomCode = intent.getStringExtra("roomCode");
            editRoomCode.setText(roomCode);
            Log.i("room:", "Private");
            //save it to firestore and set the quizId
        }
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizName = editName.getText().toString();
                quizDesc = editDesc.getText().toString();

                //disable user input in both public and private(private will get roomCode from intent)
                if(isPublic){
                    new IDPublicThread().start();
                }else{
                    //save it to firestore and set the quizId
                    new IDPrivateThread().start();
                }


                //create the new question object
                quiz = new Quiz();
                quiz.setQuizId(quizCode);
                quiz.setAuthor(author);
                quiz.setDesc(quizDesc);
                quiz.setTitle(quizName);
                quiz.setNumPlay(numPlay);

                //should intent to the activity that create question, but which is it

                Intent goIntent = new Intent(CreateQuizActivity.this, QuizQuestionActivity.class);
                intent.putExtra("quiz",quiz);
                startActivity(goIntent);
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
                firestoreManager2.insertPrivateRoomQuiz(roomCode,quizCode, quizName, quizDesc);
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
                firestoreManager2.insertPublicQuiz(quizCode,quizName, quizDesc);
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }
}
