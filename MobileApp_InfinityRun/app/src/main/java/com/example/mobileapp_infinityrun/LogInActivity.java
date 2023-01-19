package com.example.mobileapp_infinityrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class LogInActivity extends AppCompatActivity {


    String usernameDatabase, mailDatabase, passwordDatabase;
    private RequestQueue queue;
    TextView username;
    TextView password;

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

                //Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(LogInActivity.this, Map.class);
                // send username to Map
                //intent.putExtra("username", username.getText().toString());
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
                                // send username to Map
                                intent.putExtra("username", username.getText().toString());
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


    public void GetDatabase(View view) {
        HttpURLConnection connection = null;



        try{
            URL url = new URL("https://infinityrun.azurewebsites.net/api/User/" + username.getText().toString() + "&" + password.getText().toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        /************************************************
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://infinityrun.azurewebsites.net/api/User/"+username.getText().toString()+"&"+password.getText().toString();
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject user = response.getJSONObject("data");

                            System.out.println("-----------------------------------------------------------------------");
                            if(user == null){
                                Toast.makeText(LogInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                System.out.println("TEST111111111111111111111111111111111111111111111111111111111111");
                                Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogInActivity.this, Map.class);
                                // send username to Map
                                intent.putExtra("username", username.getText().toString());
                                startActivity(intent);
                            }
                            //JSONArray jsonArray = response.getJSONArray("data");

                            /*
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                usernameDatabase = data.getString("username");
                                mailDatabase = data.getString("mail");
                                passwordDatabase = data.getString("password");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

         */
    }

}