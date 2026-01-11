package com.androidcode.rentalsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText cityEditText, priceMinEditText, priceMaxEditText;
    private AutoCompleteTextView typeEditText;
    private Button searchButton;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_search);

        userId = getIntent().getStringExtra("user_id");

        cityEditText = findViewById(R.id.city);
        priceMinEditText = findViewById(R.id.price_min);
        priceMaxEditText = findViewById(R.id.price_max);
        typeEditText = findViewById(R.id.type);
        searchButton = findViewById(R.id.searchButton);

        // Set up AutoCompleteTextView with property types
        String[] propertyTypes = {"cot Basis", "House", "Flat-1BHK", "Flat-2BHK", "PG hostel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, propertyTypes);
        typeEditText.setAdapter(adapter);

// Ensure AutoCompleteTextView shows dropdown on click
        typeEditText.setFocusable(false);
        typeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    typeEditText.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(typeEditText.getWindowToken(), 0);
                    }
                    typeEditText.setFocusableInTouchMode(true);
                    typeEditText.setFocusable(true);
                    typeEditText.requestFocus();
                }
                return true;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                String priceMin = priceMinEditText.getText().toString().trim();
                String priceMax = priceMaxEditText.getText().toString().trim();
                String type = typeEditText.getText().toString().trim();

                // Start SearchResultsActivity with search parameters
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("city", city);
                intent.putExtra("price_min", priceMin);
                intent.putExtra("price_max", priceMax);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }
}
