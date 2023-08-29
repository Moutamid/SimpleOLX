package com.moutamid.simpleolx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail;
    private EditText loginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPwd = findViewById(R.id.login_pwd);
    }

    private void login() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPwd.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be of 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth auth = Constants.auth();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    checkAdminStatus(auth.getCurrentUser().getEmail());
                } else {
                    Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

        private void checkAdminStatus(String email) {
            DatabaseReference adminRef = Constants.databaseReference().child("Admins");

            adminRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = dataSnapshot.exists();
                    if (isAdmin) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, SellerHomeActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Error checking admin status", Toast.LENGTH_SHORT).show();
                }
            });
        }
}