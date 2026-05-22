package com.example.spendwise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    DBHelper DB;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        DB = new DBHelper(this);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Open Register Page
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);

                startActivity(intent);

            }
        });

        // Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Empty Validation
                if(email.isEmpty() || password.isEmpty()) {

                    Toast.makeText(LoginActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();

                }

                // Email Validation
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    etEmail.setError("Enter valid email");

                }

                else {

                    Boolean checkLogin =
                            DB.checkEmailPassword(email, password);

                    if(checkLogin == true) {
                        // SharedPreferences

                        SharedPreferences sharedPreferences =
                                getSharedPreferences(
                                        "LoginPrefs",
                                        MODE_PRIVATE
                                );

                        SharedPreferences.Editor editor =
                                sharedPreferences.edit();

                        // Save Login Data

                        editor.putString(
                                "email",
                                email
                        );

                        editor.putBoolean(
                                "isLoggedIn",
                                true
                        );

                        editor.apply();

                        Toast.makeText(
                                LoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent =
                                new Intent(
                                        LoginActivity.this,
                                        MainActivity.class
                                );

                        startActivity(intent);

                        finish();

                    }
                    else {

                        Toast.makeText(LoginActivity.this,
                                "Invalid Email or Password",
                                Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

    }
}