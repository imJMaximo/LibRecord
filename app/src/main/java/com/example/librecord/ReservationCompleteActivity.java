package com.example.librecord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ReservationCompleteActivity extends AppCompatActivity {
    ImageButton HomeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_complete);

        HomeButton = (ImageButton) findViewById(R.id.homeButton);

        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationCompleteActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}