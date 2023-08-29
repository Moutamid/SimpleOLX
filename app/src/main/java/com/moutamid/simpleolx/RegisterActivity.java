package com.moutamid.simpleolx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmail;
    private EditText regPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmail = findViewById(R.id.reg_email);
        regPwd = findViewById(R.id.reg_pwd);
    }

    private void signup() {
        String email = regEmail.getText().toString().trim();
        String password = regPwd.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter name, email & password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be of 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            createNewUser(email, password);
        }
    }

    private void createNewUser(String email, String password) {
        FirebaseAuth auth = Constants.auth();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            sendVerificationEmail(user);
                        }
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


