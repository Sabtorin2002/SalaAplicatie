package com.example.salaaplicatie.firstSteps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.helpers.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText repassword;
    EditText numeUser;

    Button btnregister;
    Button btnsignIn;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.etUsername);
        numeUser= findViewById(R.id.etUserUsername);

        password = findViewById(R.id.etPassword);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        repassword = findViewById(R.id.etRePassword);
        repassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnregister = findViewById(R.id.btnRegister);
        btnsignIn = findViewById(R.id.btnSignIn);
        firebaseHelper = new FirebaseHelper();

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String repass = repassword.getText().toString().trim();
                String nume= numeUser.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty() || repass.isEmpty())
                    Toast.makeText(RegisterActivity.this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
                else if (!pass.equals(repass)) {
                    Toast.makeText(RegisterActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                } else {
                    // Utilizează FirebaseAuthHelper pentru a înregistra utilizatorul
                    firebaseHelper.registerUser(RegisterActivity.this, email, pass,nume);
                }
            }
        });
        btnsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}