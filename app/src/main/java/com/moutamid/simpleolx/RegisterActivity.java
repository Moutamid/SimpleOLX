package com.moutamid.simpleolx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.simpleolx.helper.Config;

import java.util.HashMap;
import java.util.Stack;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmail, regName, regPhone, regAddress;
    private EditText regPwd;
    private Button signupbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmail = findViewById(R.id.reg_email);
        regName = findViewById(R.id.reg_name);
        regPwd = findViewById(R.id.reg_pwd);
        regPhone = findViewById(R.id.reg_phone);
        regAddress = findViewById(R.id.reg_address);
        signupbtn = findViewById(R.id.signupbtn);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void switchToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        } else if (regName.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        } else if (regAddress.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
        } else if (regPhone.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show();
        } else {
            createNewUser(email, password);

        }
    }

    private void createNewUser(String email, String password) {
        Config.showProgressDialog(RegisterActivity.this);
        Constants.auth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Constants.auth().getCurrentUser();
                        String fullName = regName.getText().toString().trim();
                        FirebaseUser currentUser = Constants.auth().getCurrentUser();
                        if (currentUser != null) {
                            final HashMap<Object,Object> datamap = new HashMap<Object, Object>();
                            Profile profile= new Profile();
                            profile.name= regName.getText().toString().trim();
                            profile.phone= regPhone.getText().toString().trim();
                            profile.address= regAddress.getText().toString().trim();
                            profile.email= regEmail.getText().toString().trim();
                            datamap.put("user_name ", regName.getText().toString().trim());
                            datamap.put("phone", regPhone.getText().toString().trim());
                            datamap.put("address", regAddress.getText().toString().trim());
                            datamap.put("email", regEmail.getText().toString().trim());
                            FirebaseDatabase.getInstance().getReference().child("SimpleOlx").child("Users").child(currentUser.getUid()).setValue(datamap)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Stash.put("profile", profile);
                                            Config.dismissProgressDialog();
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Config.dismissProgressDialog();

                                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                    } else {
                        Config.dismissProgressDialog();
                        Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Config.showProgressDialog(RegisterActivity.this);

                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Config.showProgressDialog(RegisterActivity.this);

                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Config.showProgressDialog(RegisterActivity.this);

                        Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


