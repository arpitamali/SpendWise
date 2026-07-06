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

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnRegister;

    TextView tvLogin;

    DBHelper DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        DB = new DBHelper(this);

        // Open Register Page
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);

                startActivity(intent);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Empty Validation
                if(name.isEmpty() || email.isEmpty() || password.isEmpty()) {

                    Toast.makeText(RegisterActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();

                }

                // Email Validation
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    etEmail.setError("Enter valid email");

                }

                // Password Validation
                else if(!isValidPassword(password)) {

                    etPassword.setError(
                            "Password must contain:\n" +
                                    "1 Uppercase\n" +
                                    "1 Lowercase\n" +
                                    "1 Number\n" +
                                    "1 Special Character\n" +
                                    "Minimum 8 Characters"
                    );

                }

                else {

                    Boolean checkUser = DB.checkEmail(email);

                    if(checkUser == false) {

                        Boolean insert =
                                DB.insertData(email, password);

                        if(insert == true) {
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences(
                                            "LoginPrefs",
                                            MODE_PRIVATE
                                    );

                            SharedPreferences.Editor editor =
                                    sharedPreferences.edit();

                            editor.putString(
                                    "name",
                                    name
                            );

                            editor.apply();

                            Toast.makeText(RegisterActivity.this,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    LoginActivity.class
                            );

                            startActivity(intent);
                            finish();

                        }
                        else {

                            Toast.makeText(RegisterActivity.this,
                                    "Registration Failed",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                    else {

                        Toast.makeText(RegisterActivity.this,
                                "User already exists",
                                Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

    }

    // Strong Password Validation Function
    private boolean isValidPassword(String password) {

        String passwordPattern =
                "^(?=.*[0-9])" +
                        "(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*[@#$%^&+=!])" +
                        "(?=\\S+$).{8,}$";

        return password.matches(passwordPattern);
    }
}