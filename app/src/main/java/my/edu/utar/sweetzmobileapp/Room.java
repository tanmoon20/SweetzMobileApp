/*
package my.edu.utar.sweetzmobileapp;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Room {
    String room_code;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final int RANDOM_STRING_LENGTH = 4;
    String room_title;
    String room_desc;
    String room_pwd;

    User author;
    ArrayList<User> members;
    //quiz

    //database
    final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final static CollectionReference roomReference = db.collection("privateRoom");

    public Room(){
        this.room_code = "";
        this.room_pwd = "";
        this.room_desc = "";
        this.room_title = "";
        this.author = new User();
        this.members = new ArrayList<User>();
    }
    public Room(String room_title, String room_desc, String room_pwd){
        */
/*generateUniqueRandomCode();*//*

        room_code = generateRandomCode();
        this.room_title = room_title;
        this.room_desc = room_desc;
        this.room_pwd = room_pwd;
        this.author = new User();
        this.members = new ArrayList<User>();

        roomReference.document(room_code).set(this).addOnSuccessListener(documentReference -> {
            this.room_code =room_code;
            Log.i("#######Tag#######", "the data is created!");

        }).addOnFailureListener(e->{
            e.printStackTrace();
        });
    }

    //return arraylist of all private room
    public static void readAllRooms(final RoomCallback<ArrayList<Room>> callback) {
        final ArrayList<Room> roomList = new ArrayList<Room>();

        roomReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Map<String, Object> data = snapshot.getData();
                    Room room = new Room();
                    room.setRoom_code(snapshot.getId());
                    room.setRoom_desc(snapshot.getString("roomDesc"));
                    room.setRoom_pwd(snapshot.getString("roomPwd"));
                    roomList.add(room);
                }

                // Notify the callback with the populated roomList
                callback.onSuccess(roomList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                e.printStackTrace();

                // Notify the callback of failure
                callback.onFailure("Error reading rooms");
            }
        });
    }



    public static Room readARoom(String room_code, final RoomCallback<String> callback) {
        final Room room = new Room();

        roomReference.document(room_code).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> roomData = documentSnapshot.getData();
                    room.setRoom_desc(roomData.get("roomDesc").toString());
                    room.setRoom_code(room_code);
                    room.setRoom_title(roomData.get("roomName").toString());
                    room.setRoom_pwd(roomData.get("roomPwd").toString());
                    Log.i("room created", room_code);
                } else {
                    // Handle the case where the room document doesn't exist
                    callback.onFailure("Room not found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                // Handle failure to retrieve the room document
                callback.onFailure("Error reading room");
            }
        });
        */
/*FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("privateRoomTesting").document(room_code);

        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the document data
                            Map<String, Object> documentData = documentSnapshot.getData();

                            // Assuming you have a field called "subcollectionRef" that contains a reference to the subcollection
                            DocumentReference subcollectionReference = (DocumentReference) documentData.get("members");

                            // Check if the subcollection reference exists
                            if (subcollectionReference != null) {
                                // Now you can read data from the subcollection
                                subcollectionReference.collection("subcollection_name")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot subdocumentSnapshot : queryDocumentSnapshots) {
                                                    // Access individual documents within the subcollection
                                                    String subdocumentId = subdocumentSnapshot.getId();
                                                    Map<String, Object> subdocumentData = subdocumentSnapshot.getData();

                                                    Log.i("#######success#######", subdocumentId);
                                                    // Use the subdocument data as needed
                                                    callback.onSuccess(subdocumentSnapshot.getString("user_email"));
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle errors when reading the subcollection
                                                Log.w("#######error#######", "Error reading subcollection", e);
                                            }
                                        });
                            } else {
                                // Handle the case where the subcollection reference is null
                                Log.d("#######error#######", "Subcollection reference is null");
                            }
                        } else {
                            // Handle the case where the document doesn't exist
                            Log.d("#######error#######", "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors when reading the document
                        Log.w("#######error#######", "Error reading document", e);
                    }
                });*//*


        */
/*roomReference.document(room_code).collection("members").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Access individual documents
                    String documentId = documentSnapshot.getId();
                    Map<String, Object> data = documentSnapshot.getData();

                    Log.i("#######member#######", data.get(documentId).toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("#######Tag#######", "You fail");
            }
        });*//*

        return room;
    }

    public interface RoomCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public boolean ifPrivate(){
        if(room_code == null && room_pwd == null){
            return false;
        }
        return true;
    }

    public boolean authentication(String inputRoomCode, String inputRoomPwd){
        return this.room_code.equals(inputRoomCode) && this.room_pwd.equals(inputRoomPwd);
    }

    //using StringBuilder because String is immutable, if we modify it, new String object will be created, less efficient
    private String generateRandomCode(){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<RANDOM_STRING_LENGTH;i++){
            char randomChar = CHARACTERS.charAt(random.nextInt());
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    //database
*/
/*    private boolean isCodeUnique(DatabaseReference reference, String random_code){
        final boolean[] isUnique = new boolean[1];
        // Query Firebase Database Room table to check code uniqueness
        databaseReference.orderByChild("room_code").equalTo(random_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  //for each record
                if (snapshot.exists()) {
                    isUnique[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return isUnique[0];
    };

    //need a callback function to handle asynchromous operations, it will notify our code if a operation is finished and result is available
    interface UniqueCodeCheckCallback {
        void onResult(boolean isUnique);
    }
    //using static so we can class.generateCode, not instance.generateCode*//*


    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_desc() {
        return room_desc;
    }

    public void setRoom_desc(String room_desc) {
        this.room_desc = room_desc;
    }

    public String getRoom_pwd() {
        return room_pwd;
    }

    public void setRoom_pwd(String room_pwd) {
        this.room_pwd = room_pwd;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }
}
*/
