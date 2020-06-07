package com.example.payungistation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportPage extends AppCompatActivity {
    private Button btnReport;
    private EditText txtEmail, txtMessage;
    private String email, problem;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnReport   = findViewById(R.id.btnReport);
        txtEmail    = findViewById(R.id.txtEmail);
        txtMessage  = findViewById(R.id.txtMessage);

        db      = FirebaseFirestore.getInstance();

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email       = txtEmail.getText().toString();
                problem     = txtMessage.getText().toString();
                Date date   = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MM dd hh:mm:ss yyyy");
                String tanggal = formatter.format(date);
                DocumentReference documentReference = db.collection("Reports").document(tanggal);
                Map<String, Object> mapper = new HashMap<>();
                mapper.put("email", email);
                mapper.put("problem", problem);
                documentReference.set(mapper).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReportPage.this, "Report has sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReportPage.this, "Report failed to be sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
