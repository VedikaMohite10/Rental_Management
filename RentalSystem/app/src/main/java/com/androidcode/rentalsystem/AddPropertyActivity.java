package com.androidcode.rentalsystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    private EditText titleEditText, locationEditText, priceEditText, descriptionEditText, cityEditText, depositEditText, dateEditText, numberEditText;
    private AutoCompleteTextView typeEditText;
    private Button addPropertyButton;
    private ImageView imageView;
    private Bitmap bitmap;
    private String user_id;

    String query;

    private Property property;

    private int property_id;
    private boolean isUpdateMode = false;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_IMAGE_REQUEST = 2;
    private Calendar myCalendar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_property);

        // Initialize views
        titleEditText = findViewById(R.id.title);
        locationEditText = findViewById(R.id.location);
        priceEditText = findViewById(R.id.price);
        typeEditText = findViewById(R.id.type);
        imageView = findViewById(R.id.imageView);
        descriptionEditText = findViewById(R.id.description);
        cityEditText = findViewById(R.id.city);
        depositEditText = findViewById(R.id.deposit);
        dateEditText = findViewById(R.id.date);
        numberEditText = findViewById(R.id.number);
        addPropertyButton = findViewById(R.id.addPropertyButton);


        // Get the property details passed from the previous activity
        property = (Property) getIntent().getSerializableExtra("property");

        // Retrieve user ID and property ID from intent
        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        user_id = DBClass.getSingleValue(query);
        property_id = getIntent().getIntExtra("property_id", -1);



        // Check if activity is in update mode
        if (property_id != -1) {
            isUpdateMode = true;
            fetchPropertyDetails(property_id);
        }

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

        // Set up calendar for date selection
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dateEditText.setFocusable(false);
        dateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new DatePickerDialog(AddPropertyActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(dateEditText.getWindowToken(), 0);
                    }
                    dateEditText.setFocusableInTouchMode(true);
                    dateEditText.setFocusable(true);
                    dateEditText.requestFocus();
                }
                return true;
            }
        });

        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateMode) {
                    updateProperty();
                } else {
                    addProperty();
                }
            }
        });

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, TAKE_IMAGE_REQUEST);
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private void addProperty() {
        final String title = titleEditText.getText().toString().trim();
        final String location = locationEditText.getText().toString().trim();
        final String city = cityEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String deposit = depositEditText.getText().toString().trim();
        final String type = typeEditText.getText().toString().trim();
        final String date = dateEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String number = numberEditText.getText().toString().trim();
        final String user_id = this.user_id;

        if (bitmap == null) {
            Toast.makeText(this, "Please select or take an image", Toast.LENGTH_SHORT).show();
            return;
        }

        final String imageString = convertBitmapToBase64(bitmap);
        String url = DBClass.url + "add_property.php?user_id=" + user_id; // Replace with your server URL


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                        // Clear the input fields
                        titleEditText.setText("");
                        locationEditText.setText("");
                        priceEditText.setText("");
                        typeEditText.setText("");
                        descriptionEditText.setText("");
                        cityEditText.setText("");
                        depositEditText.setText("");
                        dateEditText.setText("");
                        numberEditText.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPropertyActivity.this, "Error adding property: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("location", location);
                params.put("city", city);
                params.put("price", price);
                params.put("deposit", deposit);
                params.put("type", type);
                params.put("image", imageString);
                params.put("date", date);
                params.put("description", description);
                params.put("number", number);
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateProperty() {
        final String title = titleEditText.getText().toString().trim();
        final String location = locationEditText.getText().toString().trim();
        final String city = cityEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String deposit = depositEditText.getText().toString().trim();
        final String type = typeEditText.getText().toString().trim();
        final String date = dateEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String number = numberEditText.getText().toString().trim();
        final String user_id = this.user_id;

        final String imageString = bitmap != null ? convertBitmapToBase64(bitmap) : "";

        String url = DBClass.url + "update_property.php"; // Replace with your server URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddPropertyActivity.this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                        // Clear the input fields
                        titleEditText.setText("");
                        locationEditText.setText("");
                        priceEditText.setText("");
                        typeEditText.setText("");
                        descriptionEditText.setText("");
                        cityEditText.setText("");
                        depositEditText.setText("");
                        dateEditText.setText("");
                        numberEditText.setText("");
                        bitmap = null; // Clear bitmap
                        imageView.setImageDrawable(null); // Clear image view
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPropertyActivity.this, "Error updating property: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("property_id", String.valueOf(property_id));
                params.put("title", title);
                params.put("location", location);
                params.put("city", city);
                params.put("price", price);
                params.put("deposit", deposit);
                params.put("type", type);
                params.put("image", imageString);
                params.put("date", date);
                params.put("description", description);
                params.put("number", number);
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void fetchPropertyDetails(int propertyId) {
        String url = DBClass.url + "get_property_details.php?property_id=" + propertyId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the response and populate the fields
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("error")) {
                                Toast.makeText(AddPropertyActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            } else {
                                titleEditText.setText(jsonObject.getString("title"));
                                locationEditText.setText(jsonObject.getString("location"));
                                priceEditText.setText(jsonObject.getString("price"));
                                typeEditText.setText(jsonObject.getString("type"));
                                descriptionEditText.setText(jsonObject.getString("description"));
                                cityEditText.setText(jsonObject.getString("city"));
                                depositEditText.setText(jsonObject.getString("deposit"));
                                dateEditText.setText(jsonObject.getString("date"));
                                numberEditText.setText(jsonObject.getString("number"));

                                // Decode and set the image if available
                                String imageString = jsonObject.getString("pic");
                                if (!imageString.isEmpty()) {
                                    Log.d("AddPropertyActivity", "Image data received: " + imageString.substring(0, Math.min(imageString.length(), 100)) + "..."); // Log first 100 characters
                                    byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    Log.d("AddPropertyActivity", "No image data received.");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddPropertyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPropertyActivity.this, "Error fetching property details: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnSelectFileClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void btnTakeImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}

