package com.moutamid.simpleolx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.helper.Config;

import java.util.HashMap;
import java.util.Objects;

public class MyAccountActivity extends AppCompatActivity {
    private EditText address_et;
    private EditText phone_et;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private Button updateProfileButton;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        phone_et = findViewById(R.id.phone_et);
        address_et = findViewById(R.id.address_et);
        fullNameEditText = findViewById(R.id.full_name_et);
        emailEditText = findViewById(R.id.email_et);
        newPasswordEditText = findViewById(R.id.new_pwd_et);
        updateProfileButton = findViewById(R.id.update_profile_btn);
        changePasswordButton = findViewById(R.id.change_pwd_btn);
        currentPasswordEditText = findViewById(R.id.current_pwd_et);
        Profile profile = (Profile) Stash.getObject("profile", Profile.class);
        if (profile != null) {
            fullNameEditText.setText(profile.name);
            phone_et.setText(profile.phone);
            address_et.setText(profile.address);
        }

        FirebaseUser currentUser = Constants.auth().getCurrentUser();
        if (currentUser != null) {
            emailEditText.setText(currentUser.getEmail());
        }

        updateProfileButton.setOnClickListener(view -> updateProfile());
        changePasswordButton.setOnClickListener(view -> changePassword());
    }

    private void updateProfile() {
        if (fullNameEditText.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        } else if (address_et.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
        } else if (phone_et.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show();
        } else {
            Config.showProgressDialog(this);
            final HashMap<Object, Object> datamap = new HashMap<Object, Object>();
            Profile profile= new Profile();
            profile.name= fullNameEditText.getText().toString().trim();
            profile.phone= phone_et.getText().toString().trim();
            profile.address= address_et.getText().toString().trim();
            profile.email= emailEditText.getText().toString().trim();

            datamap.put("user_name ", fullNameEditText.getText().toString().trim());
            datamap.put("phone", phone_et.getText().toString().trim());
            datamap.put("address", address_et.getText().toString().trim());
            datamap.put("email", emailEditText.getText().toString().trim());
            FirebaseDatabase.getInstance().getReference().child("SimpleOlx").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(datamap)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Stash.put("profile", profile);

                            Config.dismissProgressDialog();
                            finish();
                        } else {
                            Config.dismissProgressDialog();

                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        final String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter current and new passwords", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = Constants.auth().getCurrentUser();
        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(updatePasswordTask -> {
                                        if (updatePasswordTask.isSuccessful()) {
                                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void backPress(View view) {
        onBackPressed();
    }
}
