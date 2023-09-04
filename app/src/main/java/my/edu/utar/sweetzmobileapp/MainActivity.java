package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Collection;
import java.util.Date;

public class MainActivity extends HeaderFooterActivity {
    private MusicManager musicManager;
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
    Room myRoom = new Room();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPublic = true;

    public MainActivity()
    {
        super("Public");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check public page or private page
        Intent intent = getIntent();
        String getPrivate = intent.getStringExtra("private");
        if(getPrivate == null)
        {
            isPublic = true;
        }
        else
        {
            isPublic = false;
            super.setHeaderTitle("Private Room");
        }

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

        //search function
        EditText searchText = findViewById(R.id.search_bar);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LinearLayout ll = findViewById(R.id.quiz_title_container);
                if (charSequence.toString().length() == 0) {
                    ll.removeAllViews();

                    for(Quiz quiz:quizList)
                    {
                        displayRow(quiz);
                    }
                } // This is used as if user erases the characters in the search field.
                else {
                    ll.removeAllViews();
                    String txtSearch = charSequence.toString().trim().toLowerCase();

                    for(Quiz quiz:quizList)
                    {
                        if(quiz.getTitle().toLowerCase().contains(txtSearch))
                        {
                            displayRow(quiz);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getQuizList();
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
        String playCount = Integer.toString(quiz.getNumPlay());
        playCountTV.setText(playCountTV.getText().toString().replace("Num",playCount));

        TextView authorDateTV = cardView.findViewById(R.id.authorDateTV);
        String authorDate = authorDateTV.getText().toString();
        authorDate = authorDate.replace("Author", quiz.getAuthor());
        authorDate = authorDate.replace("Date", quiz.getLastUpdate());
        authorDateTV.setText(authorDate);

        ImageButton shareBtn = cardView.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener((v)->{

        });

        cardView.setOnClickListener((v)->{
            Intent intent = new Intent(this, PlayActivity.class);

            if(!isPublic)
            {
                intent.putExtra("private","Private");
                quiz.setRoomCode(myRoom.getRoomCode());
            }

            intent.putExtra("quiz",quiz);
            startActivityForResult(intent, 0);
        });

        ll.addView(cardView);
    }

    public void getQuizList() {
        QuizThread myQuizThread = new QuizThread();
        myQuizThread.start();
    }

    private class QuizThread extends Thread{

        public void run(){
            CollectionReference quizes;
            if(isPublic)
            {
                quizes = db.collection("publicRoom");
            }
            else
            {
                myRoom = (Room)getIntent().getSerializableExtra("room");
                quizes = db.collection("privateRoom")
                                                .document(myRoom.getRoomCode())
                                                .collection("quiz");
            }
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
                                        quizTemp.setNumPlay(document.getData().get("playCount").hashCode());

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
                                    displayRow(quiz);
                                }


                            } else {
                                Log.e("Public Quiz's Info : ", "Query fail");
                            }
                        }
                    });
        }
    }

}