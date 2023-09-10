package my.edu.utar.sweetzmobileapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Collection;
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
    public void insertPrivateRoom(String userId, String roomID,String roomName, String roomDesc, String roomPwd, String author){
        Map<String, Object> data = new HashMap<>();
        data.put("roomDesc", roomDesc);
        data.put("roomName", roomName);
        data.put("roomPwd", roomPwd);
        Map<String, Object> authorInfo = new HashMap<>();
        authorInfo.put("username", author);
        db.collection("privateRoom").document(roomID)
                .set(data);
        db.collection("privateRoom").document(roomID).collection("author").document("author").set(authorInfo);
        db.collection("users").document(userId).collection("privateRoom").document(roomID).collection("author").document("author").set(authorInfo);
        db.collection("users").document(userId).collection("privateRoom").document(roomID)
                .set(data);
    }

    public void insertPrivateRoomQuiz(String userId, String roomID, String quizID, String quizTitle, String quizDesc, String author){
        Map<String, Object> data = new HashMap<>();
        data.put("description",quizDesc );
        data.put("lastUpdate", FieldValue.serverTimestamp());
        data.put("playCount", 0);
        data.put("title",quizTitle);

        Map<String, Object> authorInfo = new HashMap<>();
        authorInfo.put("username", author);

        db.collection("privateRoom").document(roomID).collection("quiz").document(quizID)
                .set(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Fail to set
                        Log.e("FIRESTORE 2 : ", "FAILED TO INSERT");
                    }
                });
        db.collection("privateRoom").document(roomID).collection("author").document("author").set(authorInfo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FIRESTORE 2 : ", "FAILED TO INSERT");
            }
        });

        db.collection("users").document(userId).collection("privateRoom").document(roomID).collection("author").document("author").set(authorInfo);
        db.collection("users").document(userId).collection("privateRoom").document(roomID).collection("quiz").document(quizID).set(data);

    }
    //
    //
    public void insertPrivateRoomQuizQuestion(String userId, String roomID, String quizID,String questionID
            , String correct, String title, String A, String B, String C){
        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);
        data.put("lastUpdate", FieldValue.serverTimestamp());

        db.collection("privateRoom").document(roomID).collection("quiz")
                .document(quizID).collection("question").document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Fail to set
                        //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
                    }
                });
        db.collection("users").document(userId).collection("privateRoom").document(roomID).collection("quiz")
                .document(quizID).collection("question").document(questionID)
                .set(data);
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
    public void insertPublicQuiz(String quizID, String quizTitle, String quizDesc, String author, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("description",quizDesc );
        data.put("lastUpdate", FieldValue.serverTimestamp());
        data.put("playCount", 0);
        data.put("title",quizTitle);
        Map<String, Object> authorInfo = new HashMap<>();
        authorInfo.put("username", author);
        db.collection("publicRoom").document(quizID)
                .set(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Fail to set
                        //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
                    }
                });
        db.collection("publicRoom").document(quizID).collection("author").document("author").set(authorInfo);
        db.collection("users").document(userId).collection("publicRoom").document(quizID).set(data);
        db.collection("users").document(userId).collection("publicRoom").document(quizID).collection("author").document("author").set(authorInfo);
    }
    //
    //
    public void insertPublicQuizQuestion(String userId, String quizID,String questionID
            , String correct, String title, String A, String B, String C){
        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);
        data.put("lastUpdate", FieldValue.serverTimestamp());


        db.collection("publicRoom")
                .document(quizID).collection("question").document(questionID)
                .set(data);
        db.collection("users").document(userId).collection("publicRoom")
                .document(quizID).collection("question").document(questionID)
                .set(data);

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

    public void insertNewPrivateQuizPlay(String roomID, String quizID, int playNum){
        Map<String, Object> data = new HashMap<>();
        data.put("playCount", playNum);

        db.collection("privateRoom")
                .document(roomID)
                .collection("quiz")
                .document(quizID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }

    public void manipulatePublicQuizQuestion(String userID, String quizID, String questionID
            , String correct, String title, String A, String B, String C){

        //This method is used to change the public quiz question under the user collection
        //in the firestore firebase collection

        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);
/*
        db.collection("publicRoom")
                .document(quizID)
                .collection("question")
                .document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });*/
        db.collection("user")
                .document(userID)
                .collection("publicRoom")
                .document(quizID)
                .collection("question")
                .document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });

    }

    public void manipulatePrivateQuizQuestion(String userID,String roomID, String quizID, String questionID
            , String correct, String title, String A, String B, String C){

        //This method is used to change the private quiz question under the user collection
        //in the firestore firebase collection

        Map<String, Object> data = new HashMap<>();
        data.put("correct", correct);
        data.put("title", title);
        data.put("wrongA", A);
        data.put("wrongB", B);
        data.put("wrongC", C);
/*
        db.collection("privateRoom")
                .document(roomID)
                .collection("quiz")
                .document(quizID)
                .collection("question")
                .document(questionID)
                .set(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });*/
        db.collection("user")
                .document(userID)
                .collection("privateRoom")
                .document(roomID)
                .collection("quiz")
                .document(quizID)
                .collection("question")
                .document(questionID)
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
    public void manipulatePrivateRoom(String userID,String roomID,String roomName, String roomDesc){
        //
        //This method is used to change the roomName and the room Description in the firestore firebase
        //
        Map<String, Object> data = new HashMap<>();
        data.put("roomName",roomName);
        data.put("roomDesc",roomDesc);
        db.collection("user")
                .document(userID)
                .collection("privateRoom")
                .document(roomID)
                .set(data)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //Fail to set
                //Log.e("FIRESTORE 2 : ", "FAILED TO INSERT")
            }
        });
    }

    public void deletePublicQuiz(String userId, String quizId){
        CollectionReference authorCollectionRef = db.collection("publicRoom").document(quizId).collection("author");
        CollectionReference membersCollectionRef = db.collection("publicRoom").document(quizId).collection("users");
        CollectionReference questionCollectionRef = db.collection("publicRoom").document(quizId).collection("question");
        CollectionReference authorPublicCollectionRef = db.collection("users").document(userId).collection("publicRoom").document(quizId).collection("author");
        CollectionReference quizPublicCollectionRef = db.collection("users").document(userId).collection("publicRoom").document(quizId).collection("quiz");
        deleteCollection(authorCollectionRef);
        deleteCollection(membersCollectionRef);
        deleteCollection(questionCollectionRef);
        deleteCollection(authorPublicCollectionRef);
        deleteCollection(quizPublicCollectionRef);

        db.collection("users").document(userId).collection("publicRoom").document(quizId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i("Deleted room code", quizId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Deletion failed", "room code: "+quizId);
            }
        });
        db.collection("publicRoom").document(quizId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i("Deleted room code", quizId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Deletion failed", "room code: "+quizId);
            }
        });
    }
    private void deleteCollection(CollectionReference collectionRef) {
        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the documents and delete them
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to delete the collection
                    }
                });
    }


}


