package com.androidcode.rentalsystem;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_splash_screen);
        createDatabase();

        final View logo = findViewById(R.id.logo);
        final View revealView = findViewById(R.id.reveal_view);

        logo.post(new Runnable() {
            @Override
            public void run() {
                // Get the center of the logo
                int cx = (logo.getLeft() + logo.getRight()) / 2;
                int cy = (logo.getTop() + logo.getBottom()) / 2;

                // Get the final radius for the circular reveal
                float finalRadius = (float) Math.hypot(revealView.getWidth(), revealView.getHeight());

                // Create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, 0f, finalRadius);
                anim.setInterpolator(new AccelerateInterpolator());
                anim.setDuration(2500);

                // Make the reveal view visible and start the animation
                revealView.setVisibility(View.VISIBLE);
                anim.start();
            }
        });

        // Delay to move to the next activity or action after splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Add the intent to start the next activity here

                 Intent intent ;
                String query = "SELECT * FROM Configuration";
                if (DBClass.checkIfRecordExist(query)) {
                    intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                }
                else
                {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                 startActivity(intent);
                 finish(); // close this activity
            }
        }, SPLASH_DURATION);
    }
    public void createDatabase() {
        String query;
        DBClass.database = openOrCreateDatabase(DBClass.dbname, MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS Configuration(CName VARCHAR, CValue VARCHAR);";
        DBClass.execNonQuery(query);

    }
}





//package com.androidcode.rentalsystem;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class SplashScreenActivity  extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//        setContentView(R.layout.activity_splash_screen);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
//
//
//
//
//
//
//
//
//                    intent = new Intent(getApplicationContext(), MainActivity.class);
//
//
//                startActivity(intent);
//                finish();
//            }
//        },3000);
//    }
//
//
//
//
//}