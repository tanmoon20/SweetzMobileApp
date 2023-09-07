package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class user_privateRoom_quiz extends HeaderFooterActivity {

    //Previous page is linked to showOwnerOfQuiz.java > displayRoom method
    // This page is used to display all the private quiz list
    // done owned by the current user !
    // Upon click it should map to EditPrivateRoomQuiz.java to display all the question
    // This page is a mimic of MainActivity Code


    Button createQuizBtn;
    private MusicManager musicManager;
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
    Room myRoom = new Room();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPublic = true;

    public user_privateRoom_quiz()
    {
        super("Own Private Room");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_private_room_quiz);

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_privateRoom_quiz.this, showOwnerOfQuiz.class);
                startActivity(intent);
                finish();
            }
        });

        Button deleteCurrentRoomBtn = findViewById(R.id.deleteRoom);
        deleteCurrentRoomBtn.setOnClickListener(view -> {
            deleteRoom();
        });

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
            Intent intent = new Intent(this, EditPrivateRoomQuiz.class);
            intent.putExtra("private","Private");
            quiz.setRoomCode(myRoom.getRoomCode()); //Room code is set here

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
        public Integer getCount = 0;

        public void run(){
            CollectionReference quizes;
            myRoom = (Room)getIntent().getSerializableExtra("room");
            quizes = db.collection("user")
                    .document("user1")
                    .collection("privateRoom")
                        .document(myRoom.getRoomCode())
                        .collection("quiz");

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
                                    Log.d("Start","start");
                                    quizes.document(quiz.getQuizId())
                                            .collection("author")
                                            .document("author")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        quiz.setAuthor(documentSnapshot.getString("username"));
                                                        getCount += 1;
                                                        if(getCount == quizList.size())
                                                        {
                                                            for (Quiz quiz: quizList)
                                                            {
                                                                displayRow(quiz);
                                                            }
                                                        }
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

    private void deleteRoom() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .document("user1")
                .collection("privateRoom")
                .document(myRoom.getRoomCode())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Room deleted successfully, you can handle UI updates or navigation if needed.
                        Toast.makeText(user_privateRoom_quiz.this, "Room Deleted!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the current activity after deleting the room.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to delete the room.
                        Toast.makeText(user_privateRoom_quiz.this, "Room fail to delete", Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(this, showOwnerOfQuiz.class);
        startActivity(intent);
        finish();
    }
}