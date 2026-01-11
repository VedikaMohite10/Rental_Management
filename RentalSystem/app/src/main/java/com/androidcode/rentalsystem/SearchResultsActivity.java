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

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView propertiesRecyclerView;
    private PropertyAdapter propertyAdapter;
    private ArrayList<Property> propertyList;

    private  String userId;
    String query;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_search_results);

        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertyList = new ArrayList<>();
        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        userId = DBClass.getSingleValue(query);
        propertyAdapter = new PropertyAdapter(this, propertyList);

        propertiesRecyclerView.setAdapter(propertyAdapter);

        // Get search parameters from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String city = extras.getString("city");
            String priceMin = extras.getString("price_min");
            String priceMax = extras.getString("price_max");
            String type = extras.getString("type");

            // Perform search using Volley
            searchProperties(city, priceMin, priceMax, type);
        } else {
            Toast.makeText(this, "No search parameters found", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchProperties(final String city, final String priceMin, final String priceMax, final String type) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.url + "search_properties.php",
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
                            Toast.makeText(SearchResultsActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchResultsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
                params.put("price_min", priceMin);
                params.put("price_max", priceMax);
                params.put("type", type);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
