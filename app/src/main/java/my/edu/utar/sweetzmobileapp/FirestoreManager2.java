package my.edu.utar.sweetzmobileapp;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirestoreManager2 {
    private FirebaseFirestore db;

    public FirestoreManager2(){
        db = FirebaseFirestore.getInstance();
    }

    //=====================================================================//
    //INSERT SECTION
    //=====================================================================//
    //
    //
    public void insertPrivateRoom(String roomID,String roomName, String roomDesc, String roomPwd){
        Map<String, Object> data = new HashMap<>();
        data.put("roomDesc", roomDesc);
        data.put("roomName", roomName);
        data.put("roomPwd", roomPwd);

        db.collection("privateRoom").document(roomID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPrivateRoomAuthor(String roomID,String username){
        Map<String, Object> data = new HashMap<>();
        data.put("username",username);
        db.collection("privateRoom").document(roomID).collection("author").document("author")
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPrivateRoomQuiz(String roomID, String quizID, String quizTitle, String quizDesc){
        Map<String, Object> data = new HashMap<>();
        data.put("description",quizDesc );
        data.put("lastUpdate", FieldValue.serverTimestamp());
        data.put("playCount", 0);
        data.put("title",quizTitle);

        db.collection("privateRoom").document(roomID).collection("quiz").document(quizID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPrivateRoomQuizQuestion(String roomID, String quizID,String questionID
            , String correct, String title, String A, String B, String C){
        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);


        db.collection("privateRoom").document(roomID).collection("quiz")
                .document(quizID).collection("question").document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPrivateRoomMember(String roomID, String userID, String username){
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        db.collection("privateRoom").document(roomID).collection("user").document(userID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPublicQuiz(String quizID, String quizTitle, String quizDesc){
        Map<String, Object> data = new HashMap<>();
        data.put("description",quizDesc );
        data.put("lastUpdate", FieldValue.serverTimestamp());
        data.put("playCount", 0);
        data.put("title",quizTitle);

        db.collection("publicRoom").document(quizID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertPublicQuizQuestion(String quizID,String questionID
            , String correct, String title, String A, String B, String C){
        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);


        db.collection("publicRoom")
                .document(quizID).collection("question").document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //
    public void insertUser(String userID,String userEmail, String userPwd, String username){
        Map<String, Object> data = new HashMap<>();
        data.put("user_email",userEmail);
        data.put("user_pws",userPwd );
        data.put("username",username );

        db.collection("user").document(userID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
    //
    //

    public void insertNewPublicQuizPlay(String quizID, int playNum){
        Map<String, Object> data = new HashMap<>();
        data.put("playCount", playNum);

        db.collection("publicRoom").document(quizID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("increase 3",Integer.toString(playNum));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }
}
