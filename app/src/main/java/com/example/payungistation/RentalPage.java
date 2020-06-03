package com.example.payungistation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalPage extends AppCompatActivity  {
    private Button btnHome;
    private TextView textView;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private boolean firstDetected = true;
    private boolean status;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    SparseArray<Barcode> qrCode;
    long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnHome     = findViewById(R.id.btnHome);
        surfaceView = findViewById(R.id.cameraPreview);
        textView    = findViewById(R.id.textView);
        db          = FirebaseFirestore.getInstance();
        mAuth       = FirebaseAuth.getInstance();
        balance     = 0l;

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource    = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(640,480).
                          setFacing(1).setAutoFocusEnabled(true).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
                try{
                    cameraSource.start(holder);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                qrCode = detections.getDetectedItems();

                if(qrCode.size() != 0 && firstDetected){
                    firstDetected = false;
                    db.collection("Borrow").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        //check is the user still borrowing another umbrella
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot element : list) {
                                    String s = element.getId();
                                    if (s.equals(qrCode.valueAt(0).displayValue)) {
                                        status = false;
                                        break;
                                    } else {
                                        status = true;
                                    }
                                }

                                if (status) {
                                    db.collection("Users").document(qrCode.valueAt(0).displayValue)
                                             .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            balance = documentSnapshot.getLong("Balance");
                                            if (balance >= 5000){
                                                DocumentReference documentReference = db.collection("Borrow").document(qrCode.valueAt(0).displayValue);
                                                Calendar calendar = Calendar.getInstance();
                                                Map<String, Date> map = new HashMap<>();
                                                map.put("Tanggal Peminjamans", calendar.getTime());
                                                documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        textView.setText("Take the umbrella out!");
                                                        startActivity(new Intent(getApplicationContext(), SuccessPage.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        String errMessage = "Failed to synchronize data";
                                                        Intent intent = new Intent(getApplicationContext(), FailedPage.class);
                                                        intent.putExtra("errMessage", errMessage);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }else{
                                                String errMessage = "Not enough balance! Top up your balance";
                                                Intent intent = new Intent(getApplicationContext(), FailedPage.class);
                                                intent.putExtra("errMessage", errMessage);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                } else {
                                    textView.setText("You still borrowing another umbrella. \n Return your umbrella and try again!");
                                }
                            }
                        }

                    });
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RentalPage.this,HomePage.class);
                startActivity(intent);
            }
        });
    }

}
