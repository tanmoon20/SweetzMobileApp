package my.edu.utar.sweetzmobileapp;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class FirestoreManager {
    final private FirebaseFirestore db;
    private DocumentReference docRef;
    //
    //
    //
    public FirestoreManager(){
    //NOTE : REMEMBER TO DECLARE FirestoreManager() IN YOUR OWN ACTIVITY !!
    //
    //
        db = FirebaseFirestore.getInstance();
    }
    //
    //
    public void getUser(String username, final FirestoreCallback callback){
        // NOTE: You are expecting to get a string array with 2 index with the first one being
        //       username and the second one is the user_pwd
        //       to modify the code to return more stuff you need to change the "userInfo[] = new String [size]
        //       then find the userInfo[1] in this method, add another line Eg. userInfo[2] = documentSnapshot.getString("new_field");
        //
        //
        String userInfo[] = new String [2];
        docRef = db.collection("user").document(username);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userInfo[0] = documentSnapshot.getString("username");
                            userInfo[1] = documentSnapshot.getString("user_pwd");
                            callback.onCallback(userInfo);
                            //
                            //For Debugging, uncomment the bellow line
                            //Log.i("FIREMANAGER","username : "+userInfo[0] + " password : "+userInfo[1]);
                            //
                        } else {
                            Log.i("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.i("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    };

    public void getList(String mainCollection, final FirestoreCallback callback){
        //This method is to get all the document from the highest collection in the firestore
        //The tables in the collection will need another method to retrieve it
        //Note: this is dynamic, any number of the document should not have any problem using this
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection(mainCollection)
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPrivateRoomInfo(String specificRoomID, final FirestoreCallback callback){
        //This method is to get the specific private room
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            tmpList.add(documentSnapshot.getString("roomName"));
                            tmpList.add(documentSnapshot.getString("roomDesc"));
                            tmpList.add(documentSnapshot.getString("roomPwd"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPrivateRoomAuthor(String specificRoomID, final FirestoreCallback callback){
        //This method is to get the specific private room's author, this method only accounts for one author
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("author").document("author")
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            tmpList.add(documentSnapshot.getString("username"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPrivateRoomMembers(String specificRoomID, final FirestoreCallback callback){
        //This method is to get all the members of the privateRoom
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("user")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPrivateRoomMembers2(String specificRoomID, String username, final FirestoreCallback callback){
        //This method is to get the member's username under privateRoom> roomID, user > username
        //Test case: specificRoomID = 1234, username = user1
        //fm.getPrivateRoomMembers2("1234","user3",this);
        //OUTPUT FROM TEST : my.edu.utar.sweetzmobileapp I/987987 :: [blackpink]
        //Take note that i have convert the array to string already, you will just need to access it
        //like this result[0] , result[1], there should be no "[" and "]"
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("user").document(username)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("username"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPrivateRoomAllQuiz(String specificRoomID, final FirestoreCallback callback){
        //This method is to get all the QuizId
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("quiz")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPrivateRoomQuizInfo(String specificRoomID, String quizID, final FirestoreCallback callback){
        //This method is to get the quiz info under privateRoom> roomID > quiz >
        //Test case: specificRoomID = 1234, quizID = quiz1
        //fm.getPrivateRoomQuizInfo("1234","quiz1",this);
        //OUTPUT : [sweetz, Fri Sep 01 00:00:00 GMT+08:00 2023, 0, sweetz]
        //Take note that i have convert the array to string already, you will just need to access it
        //like this result[0] , result[1], there should be no "[" and "]"
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("quiz").document(quizID)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("description"));
                            tmpList.add(documentSnapshot.getTimestamp("lastUpdate").toDate().toString()); //note this guy is timestamp data type
                            tmpList.add(documentSnapshot.getLong("playCount").toString()); // note this guy is int, long data type should do the job
                            tmpList.add(documentSnapshot.getString("title"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPrivateRoomAllQuestion(String specificRoomID, String quizID, final FirestoreCallback callback){
        //This method is to get all the stored question in the firestore
        //
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("quiz").document(quizID).collection("question")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPrivateRoomQuizSpecificQuestion(String specificRoomID, String quizID,String questionID, final FirestoreCallback callback){
        //This method is to get the particular question
        //Test case: specificRoomID = 1234, quizID = quiz1
        //fm.getPrivateRoomQuizSpecificQuestion("1234","quiz1","question1",this);
        //OUTPUT :
        //Take note that i have convert the array to string already, you will just need to access it
        //like this result[0] , result[1], there should be no "[" and "]"
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("privateRoom").document(specificRoomID).collection("quiz").document(quizID).collection("question").document(questionID)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("correct"));
                            tmpList.add(documentSnapshot.getString("title")); //note this guy is timestamp data type
                            tmpList.add(documentSnapshot.getString("wrongA")); // note this guy is int, long data type should do the job
                            tmpList.add(documentSnapshot.getString("wrongB"));
                            tmpList.add(documentSnapshot.getString("wrongC"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPublicRoomAllQuiz(final FirestoreCallback callback){
        //This method is to get all the QuizId in the public room
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("publicRoom")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPublicRoomSpecificQuizInfo(String quizID, final FirestoreCallback callback){
        //This method is to get the QuizId's info from public quiz
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("publicRoom").document(quizID)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("description"));
                            tmpList.add(documentSnapshot.getTimestamp("lastUpdate").toDate().toString()); //note this guy is timestamp data type
                            tmpList.add(documentSnapshot.getLong("playCount").toString()); // note this guy is int, long data type should do the job
                            tmpList.add(documentSnapshot.getString("title"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPublicRoomSpecificQuizAuthor(String quizID, final FirestoreCallback callback){
        //This method is to get the particular public quiz author
        //Test case:
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("publicRoom").document(quizID).collection("author").document("author")
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("username"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }

    public void getPublicRoomQuizAllQuestion(String quizID, final FirestoreCallback callback){
        //This method is to get all the question stored in the particular quiz
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("publicRoom").document(quizID).collection("question")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getPublicRoomQuizSpecificQuestion( String quizID,String questionID, final FirestoreCallback callback){
        //This method is to get the set of data of the question from a particular quiz
        //Test case:  quizID = quiz1, question ID = "question1"
        //fm.getPublicRoomQuizSpecificQuestion("quiz1","question1",this);
        //OUTPUT :
        //Take note that i have convert the array to string already, you will just need to access it
        //like this result[0] , result[1], there should be no "[" and "]"
        //
        ArrayList<String> tmpList = new ArrayList<>();
        db.collection("publicRoom").document(quizID).collection("question").document(questionID)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //
                            //If any more attribute needed to be retrieve, you just modify here
                            //Add one more line referring to the line below
                            tmpList.add(documentSnapshot.getString("correct"));
                            tmpList.add(documentSnapshot.getString("title")); //note this guy is timestamp data type
                            tmpList.add(documentSnapshot.getString("wrongA")); // note this guy is int, long data type should do the job
                            tmpList.add(documentSnapshot.getString("wrongB"));
                            tmpList.add(documentSnapshot.getString("wrongC"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //query fail
                Log.e("FIREMANAGER : ","QUERY FAILED !");
            }
        });
    }
    public void getUserAllPublicQuizQuestion(String userID,String quizID, final FirestoreCallback callback){
        //This method is to get all the QuizId
        //Test case: specificRoomID = 1234
        //
        //
        Log.e("getUserAllPublicQuizQ","touched");
        ArrayList<String> tmpList = new ArrayList<>();
        tmpList.add("publicRoom"); // <-to indicate what list is this
        db.collection("user")
                .document(userID)
                .collection("publicRoom")
                .document(quizID)
                .collection("question")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

    public void getUserAllPrivateQuizQuestion(String userID,String roomID,String quizID, final FirestoreCallback callback){
        //This method is to get all the QuizId
        //Test case: specificRoomID = 1234
        //
        //
        ArrayList<String> tmpList = new ArrayList<>();
        tmpList.add("privateRoom"); // <-to indicate what list is this
        db.collection("user")
                .document(userID)
                .collection("privateRoom")
                .document(roomID)
                .collection("quiz")
                .document(quizID)
                .collection("question")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tmpList.add(document.getId());
                            }
                            String[] tmp = tmpList.toArray(new String[0]); //Convert back to array
                            callback.onCallback(tmp); //need to parse array into it
                        } else {

                        }
                    }
                });
    }

/*    public void getLastQuiz(String roomType, String roomCode){
        ArrayList<String> tmplist = new ArrayList<>();
        if(roomType.equals("privateRoom")){

            Query query = db.collection("privateRoom").document(roomCode).collection("quiz").orderBy("lastUpdate", Query.Direction.DESCENDING).limit(1);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            });
        }
    }*/

    public interface FirestoreCallback {
        void onCallback(String [] result);
        void onCallbackError(Exception e);
    }

}

