//package com.androidcode.rentalsystem;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    private EditText usernameEditText, passwordEditText;
//    private Button loginButton;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//        setContentView(R.layout.activity_main);
//
//
//
//        usernameEditText = findViewById(R.id.username);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.loginButton);
//
//        loginButton.setOnClickListener(v -> login());
//    }
//
//    private void login() {
//        final String username = usernameEditText.getText().toString().trim();
//        final String password = passwordEditText.getText().toString().trim();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.20.201/rental_system/login.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
//                            if (status.equals("success")) {
//                                String userId = jsonObject.getString("user_id");
//                                String name = jsonObject.getString("name");
//                                String email = jsonObject.getString("email");
//                                String mobileno = jsonObject.getString("mobileno");
//
//                                String query = "DELETE FROM Configuration";
//                                DBClass.execNonQuery(query);
//
//
//
//
//                                query = "INSERT INTO Configuration(CName, CValue) ";
//                                query += "VALUES('id', '" + userId.replace("'", "''") + "')";
//                                DBClass.execNonQuery(query);
//
//                                query = "INSERT INTO Configuration(CName, CValue) ";
//                                query += "VALUES('name', '" + name.replace("'", "''") + "')";
//                                DBClass.execNonQuery(query);
//
//                                query = "INSERT INTO Configuration(CName, CValue) ";
//                                query += "VALUES('email', '" + email.replace("'", "''") + "')";
//                                DBClass.execNonQuery(query);
//
//
//                                query = "INSERT INTO Configuration(CName, CValue) ";
//                                query += "VALUES('mobileno', '" + mobileno.replace("'", "''") + "')";
//                                DBClass.execNonQuery(query);
//                                // Start UserHomeActivity and pass user ID
//                                Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
//                                intent.putExtra("user_id", userId);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(MainActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("username", username);
//                params.put("password", password);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//
//    public void gotoRegister(View view) {
//        Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
//        startActivity(intent);
//    }
//}
package com.androidcode.rentalsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.url + "login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Server Response", response); // Log the response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.optString("status", "fail");

                            if (status.equals("success")) {
                                String userId = jsonObject.getString("user_id");
                                String id = jsonObject.optString("user_id", "");
                                String name = jsonObject.optString("name", "");
                                String email = jsonObject.optString("email", "");
                                String mobileno = jsonObject.optString("mobileno", "");

                                String query = "DELETE FROM Configuration";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('id', '" + id.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('name', '" + name.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('email', '" + email.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('mobileno', '" + mobileno.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                // Start UserHomeActivity and pass user ID
                                Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                                intent.putExtra("user_id", userId);
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void gotoRegister(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
