package com.moutamid.simpleolx;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyAccountActivity extends AppCompatActivity {
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private Button updateProfileButton;
    private Button changePasswordButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        fullNameEditText = findViewById(R.id.full_name_et);
        emailEditText = findViewById(R.id.email_et);
        newPasswordEditText = findViewById(R.id.new_pwd_et);
        updateProfileButton = findViewById(R.id.update_profile_btn);
        changePasswordButton = findViewById(R.id.change_pwd_btn);
        currentPasswordEditText = findViewById(R.id.current_pwd_et);

        auth = Constants.auth();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            fullNameEditText.setText(currentUser.getDisplayName());
            emailEditText.setText(currentUser.getEmail());
        }

        updateProfileButton.setOnClickListener(view -> updateProfile());
        changePasswordButton.setOnClickListener(view -> changePassword());
    }

    private void updateProfile() {
        String fullName = fullNameEditText.getText().toString().trim();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
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

        FirebaseUser currentUser = auth.getCurrentUser();
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
}
