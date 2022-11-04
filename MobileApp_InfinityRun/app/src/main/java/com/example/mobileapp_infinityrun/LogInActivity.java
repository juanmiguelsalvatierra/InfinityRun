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

public class LogInActivity extends AppCompatActivity {


    String usernameDatabase, mailDatabase, passwordDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginButton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDatabase(v);
                Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LogInActivity.this, Map.class);
                intent.putExtra("username", username.getText().toString());
                startActivity(intent);
                //if (username.getText().toString().equals(username) && password.getText().toString().equals("") || username.getText().toString().equals(mail) && password.getText().toString().equals("")) {
                //    Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //    Intent intent = new Intent(LogInActivity.this, Map.class);
                //    startActivity(intent);
                //} else
                //    Toast.makeText(LogInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton signupbtn = (MaterialButton) findViewById(R.id.signupButton);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    public void GetDatabase(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://";
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
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
    }
}