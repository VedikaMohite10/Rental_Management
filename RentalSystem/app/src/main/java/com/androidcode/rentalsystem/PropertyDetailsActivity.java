package com.androidcode.rentalsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PropertyDetailsActivity extends AppCompatActivity {

    private TextView titleTextView, locationTextView, priceTextView, typeTextView, descriptionTextView, cityTextView, depositTextView, dateTextView, numberTextView;
    private ImageView propertyImageView;
    private EditText etxtNumber;
    private Button btnAddToWishlist;
    private String userId;
    String query;
    private Property property;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_details);

        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        userId = DBClass.getSingleValue(query);

        etxtNumber = findViewById(R.id.etxtNumber);
        checkAndRequestPermissions();

        titleTextView = findViewById(R.id.title);
        locationTextView = findViewById(R.id.location);
        priceTextView = findViewById(R.id.price);
        typeTextView = findViewById(R.id.type);
        descriptionTextView = findViewById(R.id.description);
        propertyImageView = findViewById(R.id.property_image);
        cityTextView = findViewById(R.id.city);
        depositTextView = findViewById(R.id.deposit);
        dateTextView = findViewById(R.id.date);
        numberTextView = findViewById(R.id.number);
        btnAddToWishlist = findViewById(R.id.btnAddToWishlist);

        // Get the property details passed from the previous activity
        property = (Property) getIntent().getSerializableExtra("property");

        // Set the property details with labels
        titleTextView.setText(property.getTitle());
        locationTextView.setText("Location: " + property.getLocation());
        priceTextView.setText("Rent: " + property.getPrice());
        typeTextView.setText("Type: " + property.getType());
        descriptionTextView.setText("Description: " + property.getDescription());
        cityTextView.setText("City: " + property.getCity());
        depositTextView.setText("Deposit: " + property.getDeposit());
        dateTextView.setText("Available From: " + property.getDate());
        numberTextView.setText("Contact Number: " + property.getNumber());

        etxtNumber.setText(property.getNumber());

        // Load the image using Picasso
        String imageUrl = DBClass.url + "productpics/" + property.getPic();
        Picasso.get().load(imageUrl).into(propertyImageView);

        btnAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishlist(property.getPropertyId(),userId);
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS
        };

        boolean permissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        if (!permissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions are required to proceed", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    public void btnPlaceCall(View view) {
        String mobileno = etxtNumber.getText().toString();
        String dial = "tel:" + mobileno;
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
        if (ContextCompat.checkSelfPermission(PropertyDetailsActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "Permission to make calls not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnPlaceWhatsapp(View view) {
        String mobileno = "+91 " + etxtNumber.getText().toString();
        Uri uri = Uri.parse("smsto:" + mobileno);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        smsIntent.putExtra("sms_body", "Hello, I'm interested in the property you have listed.");

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "Messaging app is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add property to wishlist
    public void addToWishlist(int propertyId,String userId) {
        String url = DBClass.url + "add_to_wishlist.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            Toast.makeText(PropertyDetailsActivity.this, "Property added to wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PropertyDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(PropertyDetailsActivity.this, "Error adding property to wishlist", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("property_id", String.valueOf(propertyId));
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
