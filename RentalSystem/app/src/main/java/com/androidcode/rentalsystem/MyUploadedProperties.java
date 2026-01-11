package com.androidcode.rentalsystem;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MyUploadedProperties extends AppCompatActivity {


    private RecyclerView propertiesRecyclerView;
    private PropertyAdapter propertyAdapter;
    private ArrayList<Property> propertyList;
    private String user_id;
    String query;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_my_uploaded_properties);

        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertyList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(this, propertyList, false); // Updated to pass 'false' for isWishlist
        propertiesRecyclerView.setAdapter(propertyAdapter);
        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        user_id = DBClass.getSingleValue(query);
        //user_id = getIntent().getStringExtra("user_id");
        searchProperties(user_id);

    }

    private void searchProperties(final String user_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.url+"my_uploaded_properties.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            propertyList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int propertyId= Integer.parseInt(jsonObject.getString("id"));
                                String title = jsonObject.getString("title");
                                String location = jsonObject.getString("location");
                                String price = jsonObject.getString("price");
                                String type = jsonObject.getString("type");
                                String description = jsonObject.getString("description");
                                String city = jsonObject.getString("city");
                                String deposit = jsonObject.getString("deposit");
                                String date = jsonObject.getString("date");
                                String number = jsonObject.getString("number");
                                String pic = jsonObject.getString("pic");
                                Property property = new Property(propertyId,title, location, price, type, description, city, deposit, date, number, pic);
                                propertyList.add(property);
                            }
                            propertyAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MyUploadedProperties.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyUploadedProperties.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}