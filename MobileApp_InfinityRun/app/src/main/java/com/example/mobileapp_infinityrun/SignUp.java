package com.example.mobileapp_infinityrun;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    String usernameDatabase, mailDatabase, passwordDatabase;
    EditText username, email, password1, password2;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        queue = Volley.newRequestQueue(this);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);

        MaterialButton signupbtn = (MaterialButton) findViewById(R.id.signupButton);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null && email.getText().toString().contains("@") && password1.getText().toString().equals(password2.getText().toString())) {
                    usernameDatabase = username.getText().toString();
                    mailDatabase = email.getText().toString();
                    passwordDatabase = password1.getText().toString();
                    sendData();

                }
                // Check if ther is a user with the same username
                else if (username.getText().toString().equals("admin")) {
                    Toast.makeText(SignUp.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
                // Check if the passwords match
                else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                // Check if the password is at least 8 characters long
                //else if (password1.getText().toString().length() < 8) {
                //    Toast.makeText(SignUp.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                //}
                // Check if the email is valid
                else if (!email.getText().toString().contains("@")) {
                    Toast.makeText(SignUp.this, "Invalid email", Toast.LENGTH_SHORT).show();
                }
                // Check if email is already in use
                else if (email.getText().toString().equals(" ")) {
                    Toast.makeText(SignUp.this, "Email already in use", Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().toString().equals("") && !username.getText().toString().equals(""))
                    Toast.makeText(SignUp.this, "Enter a email address", Toast.LENGTH_SHORT).show();
                else if (!password1.getText().toString().equals(password2.getText().toString()))
                    Toast.makeText(SignUp.this, "Those passwords didn’t match. Try again.", Toast.LENGTH_SHORT).show();
                else if (username.getText().toString().equals("") && !email.getText().toString().equals(""))
                    Toast.makeText(SignUp.this, "Enter a Username", Toast.LENGTH_SHORT).show();
                //else if (email.getText().toString().equals(""))
                //    Toast.makeText(SignUp.this, "There is already an account with this email address", Toast.LENGTH_SHORT).show();
                else if(email.getText().toString().equals("") && username.getText().toString().equals("") && password1.getText().toString().equals("") && password2.getText().toString().equals(""))
                    Toast.makeText(SignUp.this, "You have to fill in everything", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SignUp.this, "Register not successful", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginButton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username.getText().toString());
            jsonObject.put("mail", email.getText().toString());
            jsonObject.put("password", password1.getText().toString());

        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }

        String url = "https://infinityrun.azurewebsites.net/api/User";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Data sent", "Success");
                        Toast.makeText(SignUp.this, "Register Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, LogInActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Data sent", "Error: " + error.getMessage());
                Toast.makeText(SignUp.this, "Register not successful", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

}