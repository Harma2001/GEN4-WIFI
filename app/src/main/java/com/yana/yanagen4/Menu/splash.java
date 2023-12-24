package com.yana.yanagen4.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.yana.yanagen4.MainActivity;
import com.yana.yanagen4.R;


public class splash extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView imageView = findViewById(R.id.logoo);

        // Create a handler.
        handler = new Handler();

        // Add a runnable to the handler's postDelayed method.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(splash.this, MainActivity.class);
                startActivity(i);

            }
        }, 2000);


    }
}