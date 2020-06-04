package com.example.payungistation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class TopupPage extends AppCompatActivity {
    private Button btnBack, btnBank, btnEmoney, btnAgent, btnPulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnBack = findViewById(R.id.btnback);
        btnBank = findViewById(R.id.btnBank);
        btnEmoney = findViewById(R.id.btnEmoney);
        btnAgent = findViewById(R.id.btnAgent);
        btnPulse = findViewById(R.id.btnPulse);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopupPage.this,HomePage.class);
                startActivity(intent);
            }
        });

        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopupPage.this,BankPage.class);
                startActivity(intent);
            }
        });

        btnEmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopupPage.this,EmoneyPage.class);
                startActivity(intent);
            }
        });

        btnAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopupPage.this,AgentPage.class);
                startActivity(intent);
            }
        });

        btnPulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopupPage.this,PulsePage.class);
                startActivity(intent);
            }
        });
    }
}
