package com.androidcode.rentalsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UserHomeActivity extends AppCompatActivity {

    CardView cardView1, cardView2, cardView3, cardView4, cardView5,cardView6;
    private String userId;
    private TextView userIdTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_home);


        // Retrieve user ID from Intent extras
        userId = getIntent().getStringExtra("user_id");

        // Display user ID in TextView



        cardView1 = findViewById(R.id.card_view1);
        cardView2 = findViewById(R.id.card_view2);
        cardView3 = findViewById(R.id.card_view3);
        cardView4 = findViewById(R.id.card_view4);
        cardView5 = findViewById(R.id.card_view5);
        cardView6 = findViewById(R.id.card_view6);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserHomeActivity.this,AddPropertyActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, SearchActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = "DELETE FROM Configuration";
                DBClass.execNonQuery(query);
                Intent intent = new Intent(UserHomeActivity.this, SplashScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, MyUploadedProperties.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);            }
        });
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, WishlistActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);            }
        });


    }
    private void openProfile() {
        Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}