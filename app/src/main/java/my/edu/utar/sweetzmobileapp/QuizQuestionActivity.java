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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class QuizQuestionActivity extends HeaderFooterActivity {
    int questionNum = 1;
    TextView questionNumber;
    EditText editQuestion, editCorrect, editWrongA, editWrongB, editWrongC;
    ImageButton preBtn, nextBtn;
    Button done;
    Quiz quiz;

    Boolean isPublic, isQuit=false;
    String question, correct, toastMessage="created", userId, username;
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
        wrong.clear();
        setContentView(R.layout.activity_quiz_question);
        questionNumber = findViewById(R.id.question_num);
        editCorrect = findViewById(R.id.edit_correct);
        editQuestion = findViewById(R.id.editQuestion);
        editWrongA = findViewById(R.id.editWrongA);
        editWrongB = findViewById(R.id.editWrongB);
        editWrongC = findViewById(R.id.editWrongC);
        preBtn = findViewById(R.id.imageButton6);
        nextBtn = findViewById(R.id.imageButton3);
        done = findViewById(R.id.question_done_btn);
        questionNumber.setText("Question "+questionNum);
        Intent intent = getIntent();
        quiz = (Quiz) intent.getSerializableExtra("quiz");
        isPublic = intent.getBooleanExtra("isPublic", true);
        preBtn.setVisibility(GONE);
        userId = Login.currentUserId;
        username = Login.currentUser.getUsername();

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
                    if(isPublic){
                        fm2.insertPublicQuizQuestion(userId, quiz.getQuizId(), "question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString()); //need to use method of checking size to determine how many options has been filled in
                    }else{
                        fm2.insertPrivateRoomQuizQuestion(userId, quiz.getRoomCode(),quiz.getQuizId(),"question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString());
                    }
                    Toast.makeText(QuizQuestionActivity.this, "Question "+questionNum+" "+toastMessage+" successfully", Toast.LENGTH_SHORT).show();
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
                    if(!question.isEmpty()&&!correct.isEmpty()&&wrong.size()>=1){
                        if(isPublic){
                            fm2.insertPublicQuizQuestion(userId, quiz.getQuizId(), "question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString()); //need to use method of checking size to determine how many options has been filled in
                        }else{
                            fm2.insertPrivateRoomQuizQuestion(userId, quiz.getRoomCode(),quiz.getQuizId(),"question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString());
                        }
                        Toast.makeText(QuizQuestionActivity.this, "Question "+questionNum+" "+toastMessage+" successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(QuizQuestionActivity.this, "Insufficient field filled! Question "+questionNum+" not "+toastMessage+" successfully", Toast.LENGTH_SHORT).show();
                    }
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
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountDownLatch doneLatch = new CountDownLatch(1);
                Thread doneThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                            if(isPublic){
                                fm2.insertPublicQuizQuestion(userId, quiz.getQuizId(), "question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString()); //need to use method of checking size to determine how many options has been filled in
                            }else{
                                Log.i("this is first run", "run");

                                fm2.insertPrivateRoomQuizQuestion(userId, quiz.getRoomCode(),quiz.getQuizId(),"question"+questionNum, correct, question,editWrongA.getText().toString() , editWrongB.getText().toString(), editWrongC.getText().toString());
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(QuizQuestionActivity.this, "Question "+questionNum+" "+toastMessage+" successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        doneLatch.countDown();
                    }
                });
                doneThread.start();
                try{
                    doneLatch.await();
                    Log.i("this is last run", "run");

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread checkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("this is second run", "run");
                        onBackPressed();
                    }
                });
                checkThread.start();

                try{
                    Log.i("this is third run", "run");
                    checkThread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

/*                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });*/
                //insert
            }
        });

    }

    private class NextThread extends Thread{
        @Override
        public void run() {
            wrong.clear();
            if(isPublic){
                fm.getPublicRoomQuizSpecificQuestion(quiz.getQuizId(), "question"+questionNum,new NextCallback());
            }else {
                fm.getPrivateRoomQuizSpecificQuestion(quiz.getRoomCode(),quiz.getQuizId(), "question"+questionNum,new NextCallback());
            }
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
                            questionNumber.setText("Question "+questionNum);
                            toastMessage = "created";
                        }
                    });
                }else {
                    correct = result[0];
                    question = result[1];
                    wrong.add(result[2]);
                    wrong.add(result[3]);
                    wrong.add(result[4]);
                    toastMessage = "updated";
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
        CheckThread checkThread = new CheckThread();
        checkThread.start();

        try {
            checkThread.join(); // Wait for the CheckThread to finish.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isQuit) {
            Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private class CheckThread extends Thread{
        @Override
        public void run() {
            //should be using lastQuestion == null or not
            if(isPublic){
                fm.getLastPublicQuestion(quiz.getQuizId(),new CheckCallback());
            }else {
                fm.getLastPrivateQuestion(quiz.getRoomCode(),quiz.getQuizId(),new CheckCallback());
            }
        }

        private class CheckCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                    if(result[0].equals("notFound")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(isPublic){
                                    createEmptyQuizDialog("Empty Quiz", "Empty public quiz will be deleted. Do you really want to exit?");
                                }else{
                                    createEmptyQuizDialog("Empty Quiz", "Empty private will not be deleted, but do you really want to exit?");
                                }
                            }
                        });
                    }else{
                        questionNum = Integer.parseInt(result[0].substring(8,9));
                        Toast.makeText(QuizQuestionActivity.this, questionNum+" questions in the new quiz", Toast.LENGTH_SHORT).show();
                        isQuit = true;
                    }

            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }



    public void createEmptyQuizDialog(String title, String message){

        Context context = QuizQuestionActivity.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isPublic){
                    fm2.deletePublicQuiz(userId, quiz.getQuizId());
                    onBackPressed();
/*                    Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                    startActivity(intent);*/
                }else{
                    /*fm2.deletePrivateQuiz(quiz.getRoomCode(), quiz.getQuizId());*/
                    Intent intent = new Intent(QuizQuestionActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                isQuit = true;

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*public void Crea*/
}