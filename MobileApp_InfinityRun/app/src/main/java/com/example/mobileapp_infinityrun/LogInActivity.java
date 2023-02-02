package com.example.mobileapp_infinityrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity {


    String usernameDatabase, mailDatabase, passwordDatabase;
    private RequestQueue queue;
    TextView username;
    TextView password;
    List<MarkerOptions> placeList = new ArrayList<>();

    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);

        queue = Volley.newRequestQueue(this);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginButton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
                //getRouteFromDatabase();

                //Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(LogInActivity.this, Map.class);
                // send username to Map
                //intent.putExtra("placeList", (Serializable) placeList);
                //intent.putExtra("username", username.getText().toString());
                //intent.putExtra("id", id);
                //startActivity(intent);
                //GetDatabase(v);
            }
        });

        MaterialButton signupbtn = (MaterialButton) findViewById(R.id.signupButton);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the sign up page
                Intent intent = new Intent(LogInActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

    }


    private void checkCredentials() {
        String url = "https://infinityrun.azurewebsites.net/api/User/" + username.getText().toString() + "&" + password.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response == null) {
                                Toast.makeText(LogInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogInActivity.this, Map.class);
                                id = response.getString("_id");

                                intent.putExtra("placeList", (Serializable) placeList);
                                intent.putExtra("username", username.getText().toString());
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Toast.makeText(LogInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogInActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void getRouteFromDatabase() {
        // Get the route from the database
        String url = "https://infinityrun.azurewebsites.net/api/Route/" + id + "?coach=false";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response == null) {
                                // Toast message that no route is available
                                Toast.makeText(LogInActivity.this, "No route is available", Toast.LENGTH_SHORT).show();
                            } else {
                                // get route array from response
                                JSONArray tmp = response.getJSONArray("routePoints");
                                // loop through the array
                                for (int i = 0; i < tmp.length(); i++) {
                                    // get the object from the array
                                    JSONArray point = tmp.getJSONArray(i);
                                    double lat = point.getDouble(0);
                                    double lng = point.getDouble(1);
                                    Log.d("route", String.valueOf(lat));
                                    Log.d("route", String.valueOf(lng));
                                    Log.d("route", String.valueOf(point));
                                    placeList.add(new MarkerOptions().position(new LatLng(lat, lng)).title("Place " + (i + 1))
                                            .icon(bitmapDescriptor(getApplicationContext(), R.drawable.ic_baseline_circle_24)));
                                }
                            }


                        } catch (JSONException e) {
                            Toast.makeText(LogInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogInActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
        queue.add(jsonObjectRequest);
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId){
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}