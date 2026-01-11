package com.androidcode.rentalsystem;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView wishlistRecyclerView;
    private PropertyAdapter propertyAdapter;
    private ArrayList<Property> propertyList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wishlist);


        wishlistRecyclerView = findViewById(R.id.wishlistRecyclerView);
        wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertyList = new ArrayList<>();

        // Assuming you have the user ID stored somewhere
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            userId = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'");
        }

        propertyAdapter = new PropertyAdapter(this, propertyList, true); // Pass true for wishlist
        wishlistRecyclerView.setAdapter(propertyAdapter);

        getWishlist(userId);
    }

    private void getWishlist(final String user_id) {
        String url = DBClass.url + "get_wishlist.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            propertyList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int propertyId = jsonObject.getInt("propertyId");
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
                                Property property = new Property(propertyId, title, location, price, type, description, city, deposit, date, number, pic);
                                propertyList.add(property);
                            }
                            propertyAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WishlistActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WishlistActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
        requestQueue.add(request);
    }

}