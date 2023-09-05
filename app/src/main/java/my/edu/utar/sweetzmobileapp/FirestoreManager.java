package my.edu.utar.sweetzmobileapp;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


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
                            tmpList.add(specificRoomID);
                            tmpList.add(documentSnapshot.getString("roomName"));
                            tmpList.add(documentSnapshot.getString("roomDesc"));
                            tmpList.add(documentSnapshot.getString("roomPwd"));

                            db.collection("privateRoom").document(specificRoomID).collection("author").document("author").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        tmpList.add(documentSnapshot.getString("username"));
                                        callback.onCallback(tmpList.toArray(new String[0]));
                                    }
                                }
                            });
                            //callback.onCallback(tmpList.toArray(new String[0]));
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                            String[] error = {"not found"};
                            callback.onCallback(error);
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
                            String[] error = {"not found"};
                            callback.onCallback(error);
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
                            tmpList.add(documentSnapshot.getId());
                            tmpList.add(documentSnapshot.getString("description"));
                            tmpList.add(documentSnapshot.getTimestamp("lastUpdate").toDate().toString()); //note this guy is timestamp data type
                            tmpList.add(documentSnapshot.getLong("playCount").toString()); // note this guy is int, long data type should do the job
                            tmpList.add(documentSnapshot.getString("title"));
                            callback.onCallback(tmpList.toArray(new String[0]));
                            Log.i("Timestamp", "Hi");

                            //
                            //For Debugging, uncomment the bellow line
                            //String [] tmpL = tmpList.toArray(new String[0]);
                            //Log.i("FIREMANAGER",Arrays.toString(tmpL));
                            //
                        } else {
                            Log.e("FIREMANAGER : ", "NO ATTRIBUTE FOUND!");
                            String[] error = {"not found"};
                            callback.onCallback(error);
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

    public void getPublicRoomSpecificQuizInfo(String quizID, final FirestoreCallback callback) {
        ArrayList<String> tmpList = new ArrayList<>();
        DocumentReference docRef = db.collection("publicRoom").document(quizID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Retrieve attributes from the main document
                    tmpList.add(documentSnapshot.getString("description"));
                    tmpList.add(documentSnapshot.getTimestamp("lastUpdate").toDate().toString());
                    tmpList.add(String.valueOf(documentSnapshot.getLong("playCount").toString()));
                    tmpList.add(documentSnapshot.getString("title"));

                    // Retrieve author information from the "author" subcollection
                    docRef.collection("author").document("author").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String author = task.getResult().getString("username");
                                tmpList.add(author);
                                Log.i("Author", author);
                                callback.onCallback(tmpList.toArray(new String[0]));
                            } else {
                                Log.e("Author Retrieval Error", "An error occurred while retrieving author information.");
                            }
                        }
                    });
                } else {
                    Log.e("Quiz Document Not Found", "No attributes found for the specified quiz ID.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("Query Failure", "Failed to query the Firestore database.");
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
                            Log.e("getUserAllPubliCQ", "before callback"+Arrays.toString(tmp));
                            callback.onCallback(tmp); //need to parse array into it
                        } else {
                            Log.e("getUserAllPubicquiz :", "Failed" );
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

    public void getLastPrivateQuiz(String roomCode, FirestoreCallback callback){
        ArrayList<String> tmplist = new ArrayList<>();

            Query query = db.collection("privateRoom").document(roomCode).collection("quiz").orderBy("lastUpdate", Query.Direction.DESCENDING).limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Retrieve the last document
                            DocumentSnapshot lastDocument = querySnapshot.getDocuments().get(0);

                            // Access the data in the last document
                            tmplist.add(lastDocument.getId());
                            Map<String, Object> data = lastDocument.getData();
                            tmplist.add(data.get("description").toString());
                            tmplist.add(data.get("lastUpdate").toString());
                            tmplist.add(data.get("playCount").toString());
                            tmplist.add(data.get("title").toString());
                            callback.onCallback(tmplist.toArray(new String[0]));
                        } else {
                            Log.i("Private", "No quiz in the room "+roomCode);
                            tmplist.add("quiz0");
                            callback.onCallback(tmplist.toArray(new String[0]));
                            // No documents found in the collection
                        }
                    } else {
                        // Handle errors
                        Log.i("Private", "not successful");

                        Exception exception = task.getException();
                        if (exception != null) {
                            // Handle the exception
                        }
                    }
                }
            });

    }

    public void getLastPublicQuiz(FirestoreCallback callback){
        ArrayList<String> tmplist = new ArrayList<>();
        Query query = db.collection("publicRoom").orderBy("lastUpdate", Query.Direction.DESCENDING).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Retrieve the last document
                        DocumentSnapshot lastDocument = querySnapshot.getDocuments().get(0);

                        // Access the data in the last document
                        tmplist.add(lastDocument.getId());
                        Map<String, Object> data = lastDocument.getData();
                        tmplist.add(data.get("description").toString());
                        tmplist.add(data.get("lastUpdate").toString());
                        tmplist.add(data.get("playCount").toString());
                        tmplist.add(data.get("title").toString());
                        callback.onCallback(tmplist.toArray(new String[0]));
                    } else {
                        Log.i("public", "No quiz in the public room yet");
                        tmplist.add("quiz0");
                        callback.onCallback(tmplist.toArray(new String[0]));
                        // No documents found in the collection
                    }
                } else {
                    // Handle errors
                    Log.i("Public room", "last index not successful");

                    Exception exception = task.getException();
                    if (exception != null) {
                        // Handle the exception
                    }
                }
            }
        });
    }



    public interface FirestoreCallback {
        void onCallback(String [] result);
        void onCallbackError(Exception e);
    }

}

