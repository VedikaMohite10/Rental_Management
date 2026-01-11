package com.androidcode.rentalsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> propertyList;
    private Context context;
    private boolean isWishlist;

    public PropertyAdapter(Context context, List<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
        this.isWishlist = false;
    }

    public PropertyAdapter(Context context, List<Property> propertyList, boolean isWishlist) {
        this.context = context;
        this.propertyList = propertyList;
        this.isWishlist = isWishlist;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.titleTextView.setText(property.getTitle());
        holder.locationTextView.setText("City : " + property.getCity());
        holder.priceTextView.setText("Price : " + property.getPrice());
        holder.typeTextView.setText("Type : " + property.getType());

        final Uri uri = Uri.parse(DBClass.url + "productpics/" + property.getPic());
        Picasso.get().load(uri).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PropertyDetailsActivity.class);
                intent.putExtra("property", property);
                context.startActivity(intent);
            }
        });

        if (isWishlist) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Remove from Wishlist")
                            .setMessage("Are you sure you want to remove this property from your wishlist?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removeFromWishlist(property.getPropertyId(), position);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });
        } else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Property Options")
                            .setMessage("Do you want to update or delete this property?")
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, AddPropertyActivity.class);
                                    intent.putExtra("property_id", property.getPropertyId());
                                    intent.putExtra("property", property);
                                    intent.putExtra("user_id", DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'"));
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteProperty(property.getPropertyId(), position);
                                }
                            })
                            .setNeutralButton("Cancel", null)
                            .show();
                    return true;
                }
            });
        }
    }

    private void removeFromWishlist(final int propertyId, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.url + "remove_from_wishlist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            propertyList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Property removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to remove property from wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("property_id", String.valueOf(propertyId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void deleteProperty(final int propertyId, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.url + "delete_property.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if (status.equals("success")) {
                                propertyList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("property_id", String.valueOf(propertyId));
                params.put("user_id", DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, locationTextView, priceTextView, typeTextView, descriptionTextView;
        ImageView imageView;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            locationTextView = itemView.findViewById(R.id.location);
            priceTextView = itemView.findViewById(R.id.price);
            typeTextView = itemView.findViewById(R.id.type);
            descriptionTextView = itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.imgIcon);
        }
    }
}
