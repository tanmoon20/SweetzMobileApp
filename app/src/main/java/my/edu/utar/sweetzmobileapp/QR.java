package my.edu.utar.sweetzmobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QR {
    Context context;
    String code;
    public QR(Context context, String code){
        this.context = context;
        this.code = code;
    }

    //generate the qr according to ur roomCode or quizId
    private Bitmap generateQR(){
        Bitmap bitmap = null;

        //adjust ur size here
        QRGEncoder qrgEncoder = new QRGEncoder(code, null, QRGContents.Type.TEXT, 300);
        try{
            bitmap = qrgEncoder.encodeAsBitmap();

        }catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void createQRDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.qr_image, null);
        builder.setView(view);
        builder.setPositiveButton("OK", null);

        // Find the ImageView by its ID within the inflated layout
        ImageView qr = (ImageView) view.findViewById(R.id.QR_ImageView);
        qr.setImageBitmap(generateQR());
        qr.setBackgroundResource(R.drawable.gradient_bg);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
