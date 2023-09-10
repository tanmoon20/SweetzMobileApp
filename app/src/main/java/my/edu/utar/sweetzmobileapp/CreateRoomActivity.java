package my.edu.utar.sweetzmobileapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class CreateRoomActivity extends HeaderFooterActivity{
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final int RANDOM_STRING_LENGTH = 4;
     EditText editRoomName, editRoomCode, editRoomDesc, editRoomPwd, joinRoomPwd;
     ImageButton camera_btn;
     Button joinRoomBtn, createRoomBtn;
     Room room = new Room();
     String roomName, roomCode, roomDesc, roomPwd, author="sweetz", username="sweetz", userId="user1";
     final private FirestoreManager fm = new FirestoreManager();

     final private FirestoreManager2 fm2 = new FirestoreManager2();
     public CreateRoomActivity()
     {
         super("Create Room");
     }
     final private Handler firestoreHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        editRoomCode = findViewById(R.id.edit_room_code);
        joinRoomPwd = findViewById(R.id.join_room_pwd);
        editRoomName = findViewById(R.id.edit_room_title);
        editRoomDesc = findViewById(R.id.edit_room_desc);
        editRoomPwd = findViewById(R.id.edit_room_pwd);
        camera_btn = findViewById(R.id.camera_btn);
        joinRoomBtn = findViewById(R.id.join_room_btn);
        createRoomBtn = findViewById(R.id.create_room_btn);

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateRoomActivity.this, QRScanner.class);
                startActivity(intent);

            }
        });

        joinRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCode = editRoomCode.getText().toString();
                roomPwd = joinRoomPwd.getText().toString();
                Log.i("####RoomCode#####", roomCode);
                JoinThread joinThread = new JoinThread(roomCode, roomPwd);
                joinThread.start();

            }
        });

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(editRoomName) || isEmpty(editRoomPwd)){
                    Toast.makeText(CreateRoomActivity.this, "Please fill in title and password",Toast.LENGTH_LONG).show();
                }else{
                    roomCode = generateRandomCode();
                    roomDesc = isEmpty(editRoomDesc)?"":editRoomDesc.getText().toString();
                    roomName = editRoomName.getText().toString();
                    roomPwd = editRoomPwd.getText().toString();
                    room.setRoomCode(roomCode);
                    room.setTitle(roomName);
                    room.setDesc(roomDesc);
                    room.setAuthor(author);
                    fm2.insertPrivateRoom(roomCode,roomName, roomDesc, roomPwd, room.getAuthor());
                    Intent intent = new Intent(CreateRoomActivity.this, MainActivity.class);
                    intent.putExtra("room",room);
                    intent.putExtra("private","Private");
                    startActivity(intent);
                    }
                }
            });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

     //dialog for room not found
     public void createDialog(String title, String message){
         Context context = CreateRoomActivity.this;
         AlertDialog.Builder builder = new AlertDialog.Builder(context);

         builder.setTitle(title)
                 .setMessage(message);

         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

             }
         });
         AlertDialog alertDialog = builder.create();
         alertDialog.show();
     }

    private String generateRandomCode(){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<RANDOM_STRING_LENGTH;i++){
            char randomChar = CHARACTERS.charAt(random.nextInt(62));
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

    private class JoinThread extends Thread {
        FirestoreManager fm = new FirestoreManager();
        private String roomCode, roomPwd;

        public JoinThread(String roomCode, String roomPwd) {

            this.roomCode = roomCode;
            this.roomPwd = roomPwd;
        }

        //after detect is private, check if room is found
        @Override
        public void run() {
            fm.getPrivateRoomInfo(roomCode, new JoinFirestoreCallback());
        }

        //try to get the roomInfo from firebase
        private class JoinFirestoreCallback implements FirestoreManager.FirestoreCallback {

            @Override
            public void onCallback(String[] result) {

                Handler firestoreHandler = new Handler();
                if (result == null) { //if no info can be get
                    firestoreHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createDialog("Room not found!", "The room " + roomCode + " is not found!");
                        }
                    });
                } else {  //else check the password
                    if (roomPwd.equals(result[3])) {
                        fm2.insertPrivateRoomMember(roomCode, userId, username);
                        fm.getPrivateRoomInfo(roomCode, new JoinFirestoreCallback2());
                    } else { //if password is incorrect
                        firestoreHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                createDialog("Try again", "room code or password incorrect!");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCallbackError(Exception e) {
            }
        };

        private class JoinFirestoreCallback2 implements FirestoreManager.FirestoreCallback {
            Room room = new Room();
            Handler handler = new Handler();

            @Override
            public void onCallback(String[] result) {
                room.setRoomCode(result[0]);
                room.setTitle(result[1]);
                room.setDesc(result[2]);
                room.setAuthor(result[4]);
                fm2.insertPrivateRoomMember(roomCode, "user2","username");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CreateRoomActivity.this, MainActivity.class);
                        intent.putExtra("room",room);
                        intent.putExtra("private","Private");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }
 }