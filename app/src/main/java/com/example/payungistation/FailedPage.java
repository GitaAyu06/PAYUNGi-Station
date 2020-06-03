package com.example.payungistation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

public class FailedPage extends AppCompatActivity {

    String errMessage = "Error has occured";
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txt    = findViewById(R.id.txt);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b != null){
            errMessage = (String) b.get("errMessage");
        }
        txt.setText(errMessage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FailedPage.this, HomePage.class));
            }
        }, 7000);
    }
}
