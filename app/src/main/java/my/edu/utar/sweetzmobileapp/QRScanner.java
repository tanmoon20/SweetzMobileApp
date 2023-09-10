package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class QRScanner extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private UserLoginManager userLoginManager;
    String username = "sweetz";

    private ScannerLiveView camera;
    private TextView tv;
    FirestoreManager fm = new FirestoreManager();
    FirestoreManager2 fm2 = new FirestoreManager2();
    Intent intent;
    Quiz quiz = new Quiz();
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkPermission()){
            Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
        }else{
            requestPermissions();
        }
        setContentView(R.layout.qr_scanner);
        camera = findViewById(R.id.camera);
        handler = new Handler();

        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                Toast.makeText(QRScanner.this, "Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {

            }

            @Override
            public void onScannerError(Throwable err) {

            }

            //when the code is scanned, it should check which kind of activity should be intent
            //intent, the roomCode is data
            @Override
            public void onCodeScanned(String data) {
                if(data.length()==4 && !data.contains(" ")){
                    Toast.makeText(QRScanner.this, "Private", Toast.LENGTH_LONG).show();
                    JoinPrivateRoomThread joinPrivateRoomThread = new JoinPrivateRoomThread(data);
                    joinPrivateRoomThread.start();
                    //start private room activity
                } else if (data.substring(0,4).equals("quiz")) {
                    JoinPublicQuizThread joinPublicQuizThread = new JoinPublicQuizThread(data);
                    joinPublicQuizThread.start();
                } else{
                    String[] dataArray = data.split("\\s+");
                    JoinQuizThread joinQuizThread = new JoinQuizThread(dataArray[0], dataArray[1]);
                    joinQuizThread.start();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        decoder.setScanAreaPercent(1.0f);
        // below method will set decoder to camera.
        camera.setDecoder(decoder);
        camera.startScanner();
    }

    protected void onPause() {
        // on app pause the
        // camera will stop scanning.
        camera.stopScanner();
        super.onPause();
    }
    private boolean checkPermission() {
        int permissionResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        // Return true if permission is granted, false otherwise
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // this method is called when user
        // allows the permission to use camera.
        if (grantResults.length > 0) {
            boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (cameraaccepted && vibrateaccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied \n Camera permission has to be provided to scan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createDialog(String title, String message){
        Context context = QRScanner.this;
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
    public void createPwdDialog(String roomCode, String quizId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        builder.setTitle("Enter Password");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle OK button click here
                EditText passwordEditText = dialogView.findViewById(R.id.join_room_pwd_qr);
                String roomPwd = passwordEditText.getText().toString();
                JoinThread joinThread = new JoinThread(roomCode, roomPwd, quizId);
                joinThread.start();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //for joining room
    private class JoinThread extends Thread {
        FirestoreManager fm = new FirestoreManager();
        private String roomCode, roomPwd, quizId;

        public JoinThread(String roomCode, String roomPwd, String quizId) {
            this.quizId = quizId;
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
                        fm2.insertPrivateRoomMember(roomCode,"user4", username);
                        fm.getPrivateRoomQuizInfo(roomCode, quizId, new JoinFirestoreCallback2());
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


            @Override
            public void onCallback(String[] result) {
                quiz.setRoomCode(roomCode);
                quiz.setQuizId(quizId);
                quiz.setDesc(result[1]);
                quiz.setLastUpdate(result[2]);
                quiz.setNumPlay(Integer.parseInt(result[3]));
                quiz.setTitle(result[4]);
                quiz.setAuthor(result[5]);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(QRScanner.this, PlayActivity.class);
                        intent.putExtra("quiz", quiz);
                        intent.putExtra("private", "private");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }

    //for joining quiz
    private class JoinQuizThread extends Thread{
        String roomCode, quizId, roomPwd;
        FirestoreManager fm = new FirestoreManager();
        Handler handler = new Handler();

        public JoinQuizThread(String roomCode, String quizId){
            this.roomCode = roomCode;
            this.quizId = quizId;
        }

        @Override
        public void run() {
            fm.getPrivateRoomQuizInfo(roomCode, quizId, new JoinQuizFirestoreCallback());
        }

        private class JoinQuizFirestoreCallback implements FirestoreManager.FirestoreCallback {

            @Override
            public void onCallback(String[] result) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result[0].equals("not found")){
                            createDialog("Invalid QR", "Room or Quiz not found");
                        }else{
                            fm.getPrivateRoomMembers2(roomCode, "user4", new JoinQuizFirestoreCallback2("user4", roomCode));
                            //check if members, if no, createDialogPwd
                            //need get the current user
                        }
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }

        private class JoinQuizFirestoreCallback2 implements FirestoreManager.FirestoreCallback {
            String userId, roomCode;
            public JoinQuizFirestoreCallback2(String userId, String roomCode){
                this.userId = userId;
                this.roomCode = roomCode;
            }


            @Override
            public void onCallback(String[] result) {
                if(result[0].equals("not found")){
                    createPwdDialog(roomCode, quizId);
                }else{
                    //intent
                    fm.getPrivateRoomQuizInfo(roomCode, quizId, new JoinQuizFirestoreCallback3());
                }
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }

        private class JoinQuizFirestoreCallback3 implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                quiz.setRoomCode(roomCode);
                quiz.setQuizId(quizId);
                quiz.setDesc(result[1]);
                quiz.setLastUpdate(result[2]);
                quiz.setNumPlay(Integer.parseInt(result[3]));
                quiz.setTitle(result[4]);
                quiz.setAuthor(result[5]);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(QRScanner.this, PlayActivity.class);
                        intent.putExtra("quiz", quiz);
                        intent.putExtra("private", "private");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }

    private class JoinPublicQuizThread extends Thread{
        String quizId;
        Handler handler = new Handler();

        public JoinPublicQuizThread(String quizId){
            this.quizId = quizId;
        }

        @Override
        public void run() {
            fm.getPublicRoomSpecificQuizInfo(quizId,new PublicQuizFirestoreCallback());
        }

        private class PublicQuizFirestoreCallback implements FirestoreManager.FirestoreCallback{

            @Override
            public void onCallback(String[] result) {
                quiz.setQuizId(quizId);
                quiz.setDesc(result[0]);
                quiz.setLastUpdate(result[1]);
                quiz.setNumPlay(Integer.parseInt(result[2]));
                quiz.setTitle(result[3]);
                quiz.setAuthor(result[4]);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(QRScanner.this, PlayActivity.class);
                        intent.putExtra("quiz", quiz);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }
    private class JoinPrivateRoomThread extends Thread {
        FirestoreManager fm = new FirestoreManager();
        private String roomCode;

        Room room;
        public JoinPrivateRoomThread(String roomCode) {
            this.roomCode = roomCode;
            handler = new Handler();
            room = new Room();
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
                    room.setRoomCode(result[0]);
                    room.setTitle(result[1]);
                    room.setDesc(result[2]);
                    room.setAuthor(result[4]);
                    /*fm2.insertPrivateRoomMember(roomCode, "user2","username");*/

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(QRScanner.this, MainActivity.class);
                            intent.putExtra("room",room);
                            intent.putExtra("private","Private");
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCallbackError(Exception e) {
            }
        };
    }
}