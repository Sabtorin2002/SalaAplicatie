package com.example.salaaplicatie.firstSteps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button btnLogIn;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etUsernameLogin);
        password = findViewById(R.id.etPasswordLogin);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnLogIn = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this,"Please complete all the fields", Toast.LENGTH_SHORT).show();
            }else {
                mAuth.signInWithEmailAndPassword(user,pass)
                        .addOnCompleteListener(this, task -> {
                           if(task.isSuccessful()){
                               Toast.makeText(LoginActivity.this,"Signed in succesfully",Toast.LENGTH_LONG).show();
                               Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                               startActivity(intent);
                           }else{
                               Toast.makeText(LoginActivity.this,"Invalid credentials",Toast.LENGTH_LONG).show();
                           }
                        });
            }
        });
    }
}