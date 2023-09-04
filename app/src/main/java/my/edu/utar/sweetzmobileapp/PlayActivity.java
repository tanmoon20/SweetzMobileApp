package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayActivity extends HeaderFooterActivity {

    FloatingActionButton mute;
    Quiz myQuiz;

    public PlayActivity(){
        super("Play");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        myQuiz = (Quiz) getIntent().getSerializableExtra("quiz");

//        Log.d("quiz",myQuiz.getTitle());

//        hide footer
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
    protected void onPause() {
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

    public void displayQuizTitle(){

        LinearLayout ll = findViewById(R.id.titleContainer);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,30,30,60);
        cardView.setLayoutParams(params);

        TextView titleTV = cardView.findViewById(R.id.title);
        titleTV.setText(myQuiz.getTitle());

        TextView descriptionTV = cardView.findViewById(R.id.description);
        descriptionTV.setText(myQuiz.getDesc());

        TextView playCountTV = cardView.findViewById(R.id.playCountTV);
        playCountTV.setText(playCountTV.getText().toString().replace("Num",myQuiz.getNumPlay()));

        TextView authorDateTV = cardView.findViewById(R.id.authorDateTV);
        String authorDate = authorDateTV.getText().toString();
        authorDate = authorDate.replace("Author", myQuiz.getAuthor());
        authorDate = authorDate.replace("Date", myQuiz.getLastUpdate());
        authorDateTV.setText(authorDate);

        ll.addView(cardView, 0);
    }

    public void displayQuestionAnswer(String title, String[] wrong, String correct){
        LinearLayout ll = findViewById(R.id.questionContainer);

        View cardView = getLayoutInflater().inflate(R.layout.qna_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,4,30,4);
        cardView.setLayoutParams(params);

        TextView questionTV = cardView.findViewById(R.id.question);
        questionTV.setText(title);

        Button ansABtn = cardView.findViewById(R.id.ansA);
        Button ansBBtn = cardView.findViewById(R.id.ansB);
        Button ansCBtn = cardView.findViewById(R.id.ansC);
        Button ansDBtn = cardView.findViewById(R.id.ansD);

        ansABtn.setTextColor(Color.BLACK);
        ansBBtn.setTextColor(Color.BLACK);
        ansCBtn.setTextColor(Color.BLACK);
        ansDBtn.setTextColor(Color.BLACK);

        Button[] ansBtn = new Button[4];
        ansBtn[0] = ansABtn;
        ansBtn[1] = ansBBtn;
        ansBtn[2] = ansCBtn;
        ansBtn[3] = ansDBtn;

        //shuffle answer
        String ans[] = new String[4];

        //shuffle num
        Integer[] num = {0, 1, 2, 3};
        List<Integer> numList = Arrays.asList(num);
        Collections.shuffle(numList);
        num = numList.toArray(new Integer[numList.size()]);

        //insert correct ans
        ans[num[0]] = correct;
        int correctNum = num[0];

        //isnert wroong ans
        ans[num[1]] = wrong[0];
        ans[num[2]] = wrong[1];
        ans[num[3]] = wrong[2];

        Button[] wrongList = new Button[3];
        int wrongListCount = 0;

        for(int i = 0; i < ansBtn.length; i++)
        {
            ansBtn[i].setText(ans[i]);

            //is wrong ans
            if(i != correctNum)
            {
                setWrong(ansBtn[i]);
                wrongList[wrongListCount++] = ansBtn[i];
            }
        }
        //is correct ans
        setCorrect(ansBtn[correctNum],wrongList);

        ll.addView(cardView);
    }

    public void setCorrect(Button btn, Button[] wrongBtnList){
        btn.setOnClickListener((v)->{
//            btn.setBackgroundColor(Color.GREEN);
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.correctGreen)));
            btn.setEnabled(false);

            for(Button wrongBtn:wrongBtnList){
                wrongBtn.setEnabled(false);
            }
        });
    }

    public void setWrong(Button btn){
        btn.setOnClickListener((v)->{
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wrongRed)));
            btn.setEnabled(false);

        });
    }

    public void goBack(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    private class QuestionThread extends Thread implements FirestoreManager.FirestoreCallback{

        private FirestoreManager questionFM = new FirestoreManager();
        private Handler mHandler;

        public QuestionThread(Handler mHandler){
            this.mHandler = mHandler;
        }

        public void run() {
            questionFM.getPublicRoomQuizAllQuestion(myQuiz.getQuizId(), QuestionThread.this);
        }

        @Override
        public void onCallback(String[] result) {
            //shuffle question
            List<String> resultList = Arrays.asList(result);
            Collections.shuffle(resultList);
            result = resultList.toArray(new String[resultList.size()]);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            for(String question : result){
                db.collection("publicRoom")
                        .document(myQuiz.getQuizId())
                        .collection("question")
                        .document(question)
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

                                    displayQuestionAnswer(title, wrong, correct);

                                } else {
                                    Log.e("Public Quiz's Ans ", "Query fail");
                                }
                            }
                        });
            }
        }

        @Override
        public void onCallbackError(Exception e) {
            Log.e("Public Quiz's Id ", "Firebase Manager fail");
        }
    }
}