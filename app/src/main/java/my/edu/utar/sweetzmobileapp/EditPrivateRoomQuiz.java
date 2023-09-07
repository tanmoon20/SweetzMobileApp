package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditPrivateRoomQuiz extends HeaderFooterActivity {

    // This page is used to display the quiz questions
    // Previous page is user_privateRoom_quiz for displaying rows of quiz list
    // Current page is the deepest layer of the navigation
    // This is a mimic of editPage.java modified


    FloatingActionButton mute;
    Quiz myQuiz;
    private ArrayList<String> questionIDArrayList = new ArrayList<>();
    private ArrayList<EditText> titleList = new ArrayList<>();
    private ArrayList<EditText> listOfQuestion = new ArrayList<>();
    private Button deleteQuizButton;
    private int listOfQuestionINDEX = 0;
    public EditPrivateRoomQuiz() {
        super("Edit");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        myQuiz = (Quiz) getIntent().getSerializableExtra("quiz");

        BottomNavigationView bottomNavigationView = findViewById(R.id.footer);
        bottomNavigationView.setVisibility(View.GONE);

        mute = findViewById(R.id.mute);
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

        displayQuizTitle();
        getQnAList();



    }
    @Override
    protected void onPause() { /*This is for muting*/
        super.onPause();
        MusicManager musicManager = MusicManager.getInstance();
        if (musicManager.isMuted()) {
            musicManager.toggleMute();
        }
    }


    public void getQnAList() {
        Handler mHandler = new Handler();
        QuestionThread myQuesThread = new QuestionThread(mHandler);
        myQuesThread.start();
    }

    public void displayQuizTitle(){  /*Dont need to touch*/

        LinearLayout ll = findViewById(R.id.titleContainer);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card2, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,30,30,60);
        cardView.setLayoutParams(params);

        TextView titleTV = cardView.findViewById(R.id.title);
        titleTV.setText(myQuiz.getTitle());

        TextView descriptionTV = cardView.findViewById(R.id.description);
        descriptionTV.setText(myQuiz.getDesc());

        TextView playCountTV = cardView.findViewById(R.id.playCountTV);
        String playCount = Integer.toString(myQuiz.getNumPlay());
        playCountTV.setText(playCountTV.getText().toString().replace("Num",playCount));

        TextView authorDateTV = cardView.findViewById(R.id.authorDateTV);
        String authorDate = authorDateTV.getText().toString();
        authorDate = authorDate.replace("Author", myQuiz.getAuthor());
        authorDate = authorDate.replace("Date", myQuiz.getLastUpdate());
        authorDateTV.setText(authorDate);


        ll.addView(cardView, 0);
        deleteQuizButton = findViewById(R.id.deleteButton);
        deleteQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentQuiz();
            }
        });
    }

    public void displayQuestionAnswer(String questionID,String title, String[] wrong, String correct){
        LinearLayout ll = findViewById(R.id.questionContainer);
        int [] indexCurSetQuestion = new int[4];

        View cardView = getLayoutInflater().inflate(R.layout.edit_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,4,30,4);
        cardView.setLayoutParams(params);

        EditText questionTV = cardView.findViewById(R.id.question);
        questionTV.setText(title);

        EditText ansABtn = cardView.findViewById(R.id.ansA);
        EditText ansBBtn = cardView.findViewById(R.id.ansB);
        EditText ansCBtn = cardView.findViewById(R.id.ansC);
        EditText ansDBtn = cardView.findViewById(R.id.ansD);

        ansABtn.setTextColor(Color.BLACK);
        ansBBtn.setTextColor(Color.BLACK);
        ansCBtn.setTextColor(Color.BLACK);
        ansDBtn.setTextColor(Color.BLACK);

        listOfQuestion.add(ansABtn); // <- this must store the correct answer
        listOfQuestion.add(ansBBtn);
        listOfQuestion.add(ansCBtn);
        listOfQuestion.add(ansDBtn);

        listOfQuestion.get(listOfQuestion.size()-4).setText(correct); // the last one which is every 1/4 of option that is correct
        listOfQuestion.get(listOfQuestion.size()-3).setText(wrong[0]);
        listOfQuestion.get(listOfQuestion.size()-2).setText(wrong[1]);
        listOfQuestion.get(listOfQuestion.size()-1).setText(wrong[2]); // option d

        titleList.add(questionTV);
        questionIDArrayList.add(questionID);

        ll.addView(cardView);
        Button deleteQuestion = findViewById(R.id.buttonToDelete);
        deleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisQuestion(questionID);
                finish();
                startActivity(getIntent());
            }
        });

    }
    // remove set correct and set wrong method

    public void goBack(View view) { // This is the done button
        FirestoreManager2 fm2 = new FirestoreManager2();
        String [] tmp4 = new String[4];
        for(int i = 0; i<titleList.size();i++){
            for(int j = 0; j< 4;j++){
                tmp4[j] = listOfQuestion.get(listOfQuestionINDEX++).getText().toString();
            }
            fm2.manipulatePrivateQuizQuestion("user1", myQuiz.getRoomCode(),
                    myQuiz.getQuizId(),questionIDArrayList.get(i),tmp4[0],titleList.get(i).getText().toString(),tmp4[1],tmp4[2],tmp4[3]);
        }

        Intent intent = new Intent(this, user_privateRoom_quiz.class);
        startActivity(intent);
        finish();
    }

    private class QuestionThread extends Thread implements FirestoreManager.FirestoreCallback{

        private FirestoreManager questionFM = new FirestoreManager();
        private Handler mHandler;

        public QuestionThread(Handler mHandler){
            this.mHandler = mHandler;
        }

        public void run() {
            questionFM.getUserAllPrivateQuizQuestion("user1", myQuiz.getRoomCode(), myQuiz.getQuizId(), QuestionThread.this);
        }

        @Override
        public void onCallback(String[] result) {
            // remove total length assign for checking the play count
            // remove shuffle code here
            List<String> resultList = new ArrayList<>(Arrays.asList(result));
            Log.e("testing 105 ", resultList.toString());
            String checking = resultList.get(0);
            resultList.remove(0);
            result = resultList.toArray(new String[resultList.size()]);
            Log.e("testing 102 : ",Arrays.toString(result)+ "-"+checking+"-");
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            for(String question : result){
                db.collection("user")
                        .document("user1")
                        .collection(checking) // the list return will get private/public room string at the first index of the array list
                        .document(myQuiz.getRoomCode())
                        .collection("quiz")
                        .document(myQuiz.getQuizId())
                        .collection("question")
                        .document(question)//this would get the "question1" question id
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {

                                    String correct;
                                    String[] wrong = new String[3];

                                    correct = documentSnapshot.getString("correct");
                                    wrong[0] = documentSnapshot.getString("wrongA");
                                    wrong[1] = documentSnapshot.getString("wrongB");
                                    wrong[2] = documentSnapshot.getString("wrongC");
                                    String title = documentSnapshot.getString("title");

                                    displayQuestionAnswer(question,title, wrong, correct);

                                } else {
                                    Log.e("Public Quiz's Ans ", "Query fail");
                                }
                            }
                        });
            }

        }

        @Override
        public void onCallbackError(Exception e) { //Nothing to see here
            Log.e("Public Quiz's Id ", "Firebase Manager fail");
        }
    }
    //remove firestore to increase play count here
    public void deleteCurrentQuiz(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .document("user1")
                .collection("privateRoom")
                .document(myQuiz.getRoomCode())
                .collection("quiz")
                .document(myQuiz.getQuizId())
                .delete();
        Intent intent = new Intent(this, user_privateRoom_quiz.class);
        startActivity(intent);
        finish();
    }

    public void deleteThisQuestion(String questionID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .document("user1")
                .collection("privateRoom")
                .document(myQuiz.getRoomCode())
                .collection("quiz")
                .document(myQuiz.getQuizId())
                .collection("question")
                .document(questionID)
                .delete();
        Intent intent = new Intent(this, EditPrivateRoomQuiz.class);
        startActivity(intent);
        finish();
    }
}