package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PrivateRoomActivity extends HeaderFooterActivity {

    public PrivateRoomActivity(){super("Private");}
    public ArrayList<Room> roomList = new ArrayList<Room>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_class);

        //retrieve data from firestore
        PrivateRoomActivity.roomThread myRoomThread = new roomThread();
        myRoomThread.start();

//        displayRoom();
    }

    public void displayRoom(Room room){
        LinearLayout ll = findViewById(R.id.room_container);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,30,30,60);
        cardView.setLayoutParams(params);

        LinearLayout playCountContatner = cardView.findViewById(R.id.playCountContainer);
        playCountContatner.setVisibility(View.GONE);

        TextView titleTV = cardView.findViewById(R.id.title);
        titleTV.setText(room.getTitle());

        TextView descriptionTV = cardView.findViewById(R.id.description);
        descriptionTV.setText(room.getDesc());

        TextView authorDateTV = cardView.findViewById(R.id.authorDateTV);
        String authorDate = authorDateTV.getText().toString();
        authorDate = authorDate.replace("Author\nDate", room.getAuthor());
        authorDateTV.setText(authorDate);

        ImageButton shareBtn = cardView.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener((v)->{

        });

        ll.addView(cardView, 0);
    }

    private class roomThread extends Thread{
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public void run(){
            //userIO
            String userId = "user1";

            CollectionReference privateRoomCollection = db.collection("user")
                    .document(userId)
                    .collection("privateRoom");

            privateRoomCollection.orderBy("roomDesc")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    Room roomTemp = new Room();
                                    roomTemp.setRoomId(document.getId());
                                    roomTemp.setDesc(document.getString("roomDesc"));
                                    roomTemp.setTitle(document.getString("roomName"));

                                    roomList.add(roomTemp);
                                }

                                //get author
                                for(Room room:roomList)
                                {
                                    privateRoomCollection
                                            .document(room.getRoomId())
                                            .collection("author")
                                            .document("author")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        room.setAuthor(documentSnapshot.getString("username"));
                                                        Log.d("display room", "room");
                                                        displayRoom(room);
                                                    } else {
                                                        Log.e("Private Room's Author", "NO AUTHOR FOUND!");
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(Exception e) {
                                                    //query fail
                                                    Log.e("Private Room's Author", "NO AUTHOR FOUND!");
                                                }
                                            });
                                }
                            }else {
                                Log.e("Private Room's Info : ", "Query fail");
                            }
                        }
                    });
        }
    }
}