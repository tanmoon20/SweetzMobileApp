package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.Date;

public class showOwnerOfQuiz extends HeaderFooterActivity {
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
    private ArrayList<Quiz> quizList2 = new ArrayList<Quiz>();
    private  String [] roomIDList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public showOwnerOfQuiz() {
        super("EDIT");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_owner_of_quiz);
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
            Intent intent = new Intent(this, editPage.class);
            Log.e("Testing number 100 : ","I am gonig to "+quiz.getTitle());
            intent.putExtra("quiz",quiz);
            startActivity(intent);
        });

        ll.addView(cardView);

    }

    public void getQuizList() {
        Handler mHandler = new Handler();
        QuizThread myQuizThread = new QuizThread(mHandler);
        myQuizThread.start();
    }

    private class QuizThread extends Thread{
        private Handler mHandler;

        public QuizThread(Handler mHandler){
            this.mHandler = mHandler;
        }

        public void run(){
            // userID is already picked
            db.collection("user")
                    .document("user1")
                    .collection("publicRoom")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Quiz quizTemp = new Quiz();

                                    if(document.exists()){
                                        quizTemp.setQuizId(document.getId());
                                        Log.e("GATE2",document.getData().get("description").toString());
                                        quizTemp.setTitle(document.getData().get("title").toString());
                                        quizTemp.setDesc(document.getData().get("description").toString());
                                        quizTemp.setNumPlay(document.getData().get("playCount").hashCode());

                                        Log.e("ShowOwnerOfQuiz : ",
                                                "title : " + document.getData().get("title").toString()
                                                +" description : "+document.getData().get("description").toString()
                                                + " playCount :" + document.getData().get("playCount").hashCode());

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

                                for (Quiz quiz: quizList){  // remember to have an author or it will not work
                                    db.collection("user")
                                            .document("user1")
                                            .collection("publicRoom")
                                            .document(quiz.getQuizId())
                                            .collection("author")
                                            .document("author")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        quiz.setAuthor(documentSnapshot.getString("username"));
                                                        displayRow(quiz); // <-- this will not run if no author
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

            //===============================================//
            ArrayList<String> tmp = new ArrayList<>();
                    db.collection("user")
                            .document("user1")
                            .collection("privateRoom")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                        tmp.add(document.getId()); // get all the privateRoom ID
                                    Log.e("sdalfkjsd",document.getId());
                                    }

                                }
                            processPrivateRoomIds(tmp);
                        }
                    });
            Log.e("thaslfsdjkf",tmp.toString());
            //===============================================//

        }
    }
    private void processPrivateRoomIds(ArrayList<String> roomIds) {
        String[] roomIDList = roomIds.toArray(new String[0]);
        //Extra
        Log.e("showOwner : ","privateRoom ID"+ Arrays.toString(roomIDList));
        for(String roomID : roomIDList){

            db.collection("user")
                    .document("user1")
                    .collection("privateRoom")
                    .document(roomID)
                    .collection("quiz")
                    .orderBy("lastUpdate", Query.Direction.DESCENDING)
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

                                        Log.e("ShowOwnerOfQuiz : ",
                                                "title : " + document.getData().get("title").toString()
                                                        +" description : "+document.getData().get("description").toString()
                                                        + " playCount :" + document.getData().get("playCount").hashCode());

                                        String author;

                                        Timestamp timestamp = (Timestamp) document.getData().get("lastUpdate");
                                        Date date = timestamp.toDate();
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        String lastUpdateDate = formatter.format(date);
                                        quizTemp.setLastUpdate(lastUpdateDate);

                                        quizList2.add(quizTemp);
                                    }
                                    else
                                    {
                                        Log.d("Public Quiz's Info", "No such document");
                                    }

                                }

                                for (Quiz quiz: quizList2){
                                    db.collection("user")
                                            .document("user1")
                                            .collection("privateRoom")
                                            .document(roomID)
                                            .collection("quiz")
                                            .document(quiz.getQuizId())
                                            .collection("author")
                                            .document("author")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        quiz.setAuthor(documentSnapshot.getString("username"));
                                                        quiz.setRoomCode(roomID);
                                                        Log.e("Testing number 99",": "+quiz.getQuizId());
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