package com.example.payungistation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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

public class ReturnPage extends AppCompatActivity  {
    private Button btnHome, btnHelp;
    private TextView textView;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private boolean firstDetected = true;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    SparseArray<Barcode> qrCode;
    long balance, fee;
    String rawValue;
    Date date, databaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnHome     = findViewById(R.id.btnHome);
        btnHelp     = findViewById(R.id.btnHelp);
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
                    rawValue = qrCode.valueAt(0).displayValue;

                    db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot element : list) {
                                    if ((element.getId()).equals(rawValue)) {
                                        balance = (long) element.get("Balance");
                                        add();
                                    }
                                }
                            }
                        }

                        private void add() {
                            db.collection("Borrow").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                               @Override
                               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                  if(!queryDocumentSnapshots.isEmpty()){
                                     List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                     for(DocumentSnapshot d : list){
                                        if ((d.getId()).equals(rawValue)) {
                                           DocumentReference documentReference2 = db.collection("Return").document(rawValue);
                                           Map<String, Object> map = new HashMap<>();
                                           date                    = Calendar.getInstance().getTime();
                                           databaseDate            = d.getDate("Tanggal Peminjamans");
                                           long price              = Math.abs(databaseDate.getTime() - date.getTime());
                                           long diff               = (price / (60000));
                                           fee                     = 5000 + (diff/30)*5000;
                                           long currentBalance     = balance - fee;
                                           setBalance(currentBalance);
                                           map.put("tanggalPeminjaman", databaseDate);
                                           map.put("tanggalDikembalikan", date);
                                           map.put("price", fee);
                                           DocumentReference documentReferenceKid = documentReference2.collection("pengembalian").document(databaseDate.toString());

                                           documentReferenceKid.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     textView.setText("Success");
                                                     startActivity(new Intent(getApplicationContext(), DonePage.class));
                                                  }
                                           });
                                           db.collection("Borrow").document(rawValue).delete();
                                           break;
                                        }
                                     }
                                  } else {
                                      String errMessage = "Failed to get data";
                                      Intent intent = new Intent(getApplicationContext(), FailedPage.class);
                                      intent.putExtra("errMessage", errMessage);
                                      startActivity(intent);
                                  }
                               }

                               private void setBalance(long currentBalance) {
                                  DocumentReference setVal = db.collection("Users").document(rawValue);
                                  Map<String, Object> value = new HashMap<>();
                                  value.put("Balance", currentBalance);
                                  setVal.update(value);
                               }
                            });
                        }
                    });
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnPage.this,HomePage.class);
                startActivity(intent);
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnPage.this,HelpPage.class);
                startActivity(intent);
            }
        });
    }
}
