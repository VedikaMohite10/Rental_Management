package com.androidcode.rentalsystem;

import android.os.Bundle;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, mobileEditText, passwordEditText;
    private Button saveButton;
    private String userId;
    String query;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_profile);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        mobileEditText = findViewById(R.id.mobile);
        passwordEditText = findViewById(R.id.password);
        saveButton = findViewById(R.id.saveButton);

        // Retrieve user ID from Intent extras
       // userId = getIntent().getStringExtra("user_id");
        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        userId = DBClass.getSingleValue(query);

        // Load user profile data and set to EditText fields
        loadUserProfile();

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        String url = DBClass.url + "get_user_profile.php?user_id=" + userId; // Replace with your server URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("UserProfileActivity", "JSON Response: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String username = jsonObject.getString("username");
                            String email = jsonObject.getString("email");
                            String mobile = jsonObject.getString("mobileno");
                            String password = jsonObject.getString("password");

                            usernameEditText.setText(username);
                            emailEditText.setText(email);
                            mobileEditText.setText(mobile);
                            passwordEditText.setText(password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("UserProfileActivity", "JSON Parsing error: " + e.getMessage());
                            Toast.makeText(UserProfileActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UserProfileActivity", "Volley error: " + error.getMessage());
                        Toast.makeText(UserProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveUserProfile() {
        String url = DBClass.url+"rental_system/update_user_profile.php"; // Replace with your server URL

        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Clear the input fields
                        usernameEditText.setText("");
                        emailEditText.setText("");
                        mobileEditText.setText("");
                        passwordEditText.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("username", username);
                params.put("email", email);
                params.put("mobileno", mobile);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
