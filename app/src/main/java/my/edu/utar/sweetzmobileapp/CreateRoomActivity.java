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

public class CreateRoomActivity extends HeaderFooterActivity implements FirestoreManager.FirestoreCallback{
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final int RANDOM_STRING_LENGTH = 4;
     EditText editRoomName, editRoomCode, editRoomDesc, editRoomPwd;
     ImageButton camera_btn;
     Button joinRoomBtn, createRoomBtn;

     ArrayList<String> roomList;
     String roomName, roomCode, roomDesc, roomPwd;
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
        editRoomName = findViewById(R.id.edit_room_title);
        editRoomDesc = findViewById(R.id.edit_room_desc);
        editRoomPwd = findViewById(R.id.edit_room_pwd);
        camera_btn = findViewById(R.id.camera_btn);
        joinRoomBtn = findViewById(R.id.join_room_btn);
        createRoomBtn = findViewById(R.id.create_room_btn);

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                QR qr = new QR(CreateRoomActivity.this, "1234");
                qr.createQRDialog();*/
                Intent intent = new Intent(CreateRoomActivity.this, QRScanner.class);
                startActivity(intent);
            }
        });

        joinRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCode = editRoomCode.getText().toString();
                FirestoreManager.FirestoreCallback firestoreCallback = new FirestoreManager.FirestoreCallback() {
                    @Override
                    public void onCallback(String[] result) {
                        if(roomList.isEmpty()){
                            firestoreHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    createDialog("Room not found!","The room " + roomCode + " is not found!");
                                }
                            });
                        }else{
                            firestoreHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //change to intent
                                    //put extra
                                }
                            });
                        }
                    }

                    @Override
                    public void onCallbackError(Exception e) {
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        roomList = fm.getPrivateRoomInfo(roomCode, firestoreCallback);
                    }
                }).start();
            }
        });

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCode = generateRandomCode();
                roomDesc = editRoomDesc.getText().toString();
                roomName = editRoomName.getText().toString();
                roomPwd = editRoomPwd.getText().toString();
                FirestoreManager.FirestoreCallback firestoreCallback = new FirestoreManager.FirestoreCallback() {
                    @Override
                    public void onCallback(String[] result) {
                        Log.i("####log####","successful" + roomList.toString());
                        Toast.makeText(CreateRoomActivity.this,"Room "+roomName+" created successful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCallbackError(Exception e) {

                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fm2.insertPrivateRoom(roomCode,roomName, roomDesc, roomPwd);
                        roomList = fm.getPrivateRoomInfo(roomCode, firestoreCallback);
                        //intent
                    }
                }).start();
            }
        });

    }

     @Override
     public void onCallback(String[] result) {

     }

     @Override
     public void onCallbackError(Exception e) {

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
            char randomChar = CHARACTERS.charAt(random.nextInt(63));
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
 }