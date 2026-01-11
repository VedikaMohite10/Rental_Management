package com.androidcode.rentalsystem;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyPropertiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PropertyAdapter propertyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_my_properties);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch the properties listed by the user and set to the adapter
        fetchUserProperties();
    }

    private void fetchUserProperties() {
        // Fetch properties from the server and set to the adapter
        List<Property> properties = getPropertiesFromServer();

        // Assuming you have the user ID stored somewhere

        String userId = "user_id"; // Replace with the actual user ID

        propertyAdapter = new PropertyAdapter(this, properties);
        recyclerView.setAdapter(propertyAdapter);
    }


    private List<Property> getPropertiesFromServer() {
        // Fetch properties from the server
        return null;
    }
}