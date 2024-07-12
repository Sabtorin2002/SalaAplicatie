package com.example.salaaplicatie.firstSteps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salaaplicatie.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private EditText emailReset;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailReset = findViewById(R.id.etEmailReset);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> {
            String email = emailReset.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ResetPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ResetPassword.this, "Unable to send reset mail", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}