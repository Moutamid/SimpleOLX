package com.moutamid.simpleolx;

import static com.moutamid.simpleolx.Constants.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail;
    private EditText loginPwd;
    private Button signinbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPwd = findViewById(R.id.login_pwd);
        signinbtn = findViewById(R.id.signinbtn);

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
    }

    public void switchToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void signin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPwd.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be of 6 characters", Toast.LENGTH_SHORT).show();
        } else {

            auth().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth().getCurrentUser();
                    checkAdminStatus(auth().getCurrentUser().getEmail());
                    if (user != null && user.isEmailVerified()) {
                        checkAdminStatus(user.getEmail());
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

        public void checkAdminStatus(String email) {
            DatabaseReference adminRef = Constants.databaseReference().child("Admins");

            adminRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = dataSnapshot.exists();
                    Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
                    intent.putExtra("isAdmin", isAdmin);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Error checking admin status", Toast.LENGTH_SHORT).show();
                }
            });
        }
}