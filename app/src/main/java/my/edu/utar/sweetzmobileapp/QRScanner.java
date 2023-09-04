package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class QRScanner extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private ScannerLiveView camera;
    private TextView tv;
    FirestoreManager fm = new FirestoreManager();
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
        tv = findViewById(R.id.qr_result_textview);

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
                Toast.makeText(QRScanner.this, data, Toast.LENGTH_LONG).show();  //roomCode no need password
                if(data.length()==4 && !data.contains(" ")){
                    Toast.makeText(QRScanner.this, "this is a private room", Toast.LENGTH_LONG).show();  //roomCode no need password
                    //start private room activity
                } else if (data.substring(0,3).equals("quiz")) {
                    Toast.makeText(QRScanner.this, "Public room", Toast.LENGTH_LONG).show();  //roomCode no need password

/*                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra("quiz",quiz);
                    startActivityForResult(intent, 0);*/
                } else{
                    String[] dataArray = data.split("\\s+");
                    Toast.makeText(QRScanner.this, dataArray[0], Toast.LENGTH_LONG).show();  //roomCode no need password
                    Toast.makeText(QRScanner.this, dataArray[1], Toast.LENGTH_LONG).show();  //roomCode no need password

                    JoinQuizThread joinQuizThread = new JoinQuizThread(dataArray[0], dataArray[1]);
                    joinQuizThread.start();
                    //quizCode need password of room
                    //get the room of quiz
                    //get the password
                    //dialog
                    //granted access dialog
                    //if
                    //start quiz activity
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
    public void createPwdDialog(String roomCode){
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
                JoinThread joinThread = new JoinThread(roomCode, roomPwd);
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
        private FirestoreManager.FirestoreCallback firestoreCallback;
        private String roomCode, roomPwd;

        public JoinThread(String roomCode, String roomPwd) {

            this.firestoreCallback = new joinFirestoreCallback();
            this.roomCode = roomCode;
            this.roomPwd = roomPwd;
        }

        @Override
        public void run() {
            fm.getPrivateRoomInfo(roomCode, firestoreCallback);
        }


        private class joinFirestoreCallback implements FirestoreManager.FirestoreCallback {

            @Override
            public void onCallback(String[] result) {

                Handler firestoreHandler = new Handler();
                if (result == null) {
                    firestoreHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createDialog("Room not found!", "The room " + roomCode + " is not found!");
                        }
                    });
                } else {

                    if (roomPwd.equals(result[2])) {
                        firestoreHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                createDialog("Granted access", "welcome");
                                //change to intent
                                //put extra result[0] is the room code
                            }
                        });
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
            Log.i("Tag", roomCode+" "+quizId);
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
                            fm.getPrivateRoomMembers2(roomCode, "user5", new JoinQuizFirestoreCallback2("user1", roomCode));
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
            FirestoreManager fm = new FirestoreManager();
            String userId, roomCode;
            public JoinQuizFirestoreCallback2(String userId, String roomCode){
                this.userId = userId;
                this.roomCode = roomCode;
            }


            @Override
            public void onCallback(String[] result) {
                if(result[0].equals("not found")){
                    createPwdDialog(roomCode);
                }else{
                    //intent
                    createDialog("Successful", "You are the user!");
                }
            }

            @Override
            public void onCallbackError(Exception e) {

            }
        }
    }
}