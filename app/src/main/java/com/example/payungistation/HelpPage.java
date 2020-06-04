package com.example.payungistation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class HelpPage extends AppCompatActivity {
    private Button btnBack, btnHelp1, btnHelp2, btnHelp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnBack = findViewById(R.id.btnback);
        btnHelp1 = findViewById(R.id.btnHelp1);
        btnHelp2 = findViewById(R.id.btnHelp2);
        btnHelp3 = findViewById(R.id.btnHelp3);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpPage.this,HomePage.class);
                startActivity(intent);
            }
        });

        btnHelp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpPage.this,Help1Page.class);
                startActivity(intent);
            }
        });

        btnHelp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpPage.this,Help2Page.class);
                startActivity(intent);
            }
        });

        btnHelp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpPage.this,Help3Page.class);
                startActivity(intent);
            }
        });
    }
}
