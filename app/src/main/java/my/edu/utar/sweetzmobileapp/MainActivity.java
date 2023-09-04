package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends HeaderFooterActivity {
    private MusicManager musicManager;
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();

    public MainActivity()
    {
        super("Public");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make instance MusicManager
        musicManager = MusicManager.getInstance();
        if(!musicManager.isPlaying()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    musicManager.togglePlayback();
                }
            }, 2000); // <-- This is delay the music because the phone need to load first
        }

//        FirestoreManager firestoreQuiz = new FirestoreManager();
//        firestoreQuiz.getPublicRoomAllQuiz(this);

        try {
            getQuizList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        displayRow();
//        displayRow();
//        displayRow();

    }

    public void displayRow(Quiz quiz){
        LinearLayout ll = findViewById(R.id.quiz_title_container);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,4,30,4);
        cardView.setLayoutParams(params);

        TextView titleTV = cardView.findViewById(R.id.title);
        titleTV.setText(quiz.getTitle());

        TextView descriptionTV = cardView.findViewById(R.id.description);
        descriptionTV.setText(quiz.getDesc());

        TextView playCountTV = cardView.findViewById(R.id.playCountTV);
        playCountTV.setText(playCountTV.getText().toString().replace("Num",quiz.getNumPlay()));

        TextView authorDateTV = cardView.findViewById(R.id.authorDateTV);
        String authorDate = authorDateTV.getText().toString();
        authorDate = authorDate.replace("Author", quiz.getAuthor());
        authorDate = authorDate.replace("Date", quiz.getLastUpdate());
        authorDateTV.setText(authorDate);

        ll.addView(cardView);

    }

    public void getQuizList() throws InterruptedException {



//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        CollectionReference quizes = db.collection("publicRoom");
//        quizes.orderBy("lastUpdate", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Quiz quizTemp = new Quiz();
//
//                                if(document.exists()){
//                                    quizTemp.setQuizId(document.getId());
//                                    quizTemp.setTitle(document.getData().get("title").toString());
//                                    quizTemp.setDesc(document.getData().get("description").toString());
//                                    quizTemp.setNumPlay(document.getData().get("playCount").toString());
//
//                                    String author;
//
//                                    Timestamp timestamp = (Timestamp) document.getData().get("lastUpdate");
//                                    Date date = timestamp.toDate();
//                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                                    String lastUpdateDate = formatter.format(date);
//                                    quizTemp.setLastUpdate(lastUpdateDate);
//
//                                    quizList.add(quizTemp);
//                                }
//                                else
//                                {
//                                    Log.d("Public Quiz's Info", "No such document");
//                                }
//
//                            }
//
//                            for (Quiz quiz: quizList){
//                                quizes.document(quiz.getQuizId())
//                                        .collection("author")
//                                        .document("author")
//                                        .get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                if (documentSnapshot.exists()) {
//
//                                                    quiz.setAuthor(documentSnapshot.getString("username"));
//                                                } else {
//                                                    Log.e("Public Quiz's Author : ", "NO AUTHOR FOUND!");
//                                                }
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(Exception e) {
//                                                //query fail
//                                                Log.e("Public Quiz's Author : ","QUERY FAILED !");
//                                            }
//                                        });
//                            }
//
//                        } else {
//                            Log.e("Public Quiz's Info : ", "Query fail");
//                        }
//                    }
//                });
        Handler mHandler = new Handler();
        QuizThread myQuizThread = new QuizThread(mHandler);
        myQuizThread.start();
        myQuizThread.join();
    }

    private class QuizThread extends Thread{
        private Handler mHandler;

        public QuizThread(Handler mHandler){
            this.mHandler = mHandler;
        }

        public void run(){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference quizes = db.collection("publicRoom");
            quizes.orderBy("lastUpdate", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Quiz quizTemp = new Quiz();

                                    if(document.exists()){
                                        quizTemp.setQuizId(document.getId());
                                        quizTemp.setTitle(document.getData().get("title").toString());
                                        quizTemp.setDesc(document.getData().get("description").toString());
                                        quizTemp.setNumPlay(document.getData().get("playCount").toString());

                                        String author;

                                        Timestamp timestamp = (Timestamp) document.getData().get("lastUpdate");
                                        Date date = timestamp.toDate();
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        String lastUpdateDate = formatter.format(date);
                                        quizTemp.setLastUpdate(lastUpdateDate);

                                        quizList.add(quizTemp);
                                    }
                                    else
                                    {
                                        Log.d("Public Quiz's Info", "No such document");
                                    }

                                }

                                for (Quiz quiz: quizList){
                                    quizes.document(quiz.getQuizId())
                                            .collection("author")
                                            .document("author")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        quiz.setAuthor(documentSnapshot.getString("username"));
                                                        displayRow(quiz);
                                                    } else {
                                                        Log.e("Public Quiz's Author : ", "NO AUTHOR FOUND!");
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(Exception e) {
                                                    //query fail
                                                    Log.e("Public Quiz's Author : ","QUERY FAILED !");
                                                }
                                            });
                                }


                            } else {
                                Log.e("Public Quiz's Info : ", "Query fail");
                            }
                        }
                    });
        }
    }
}