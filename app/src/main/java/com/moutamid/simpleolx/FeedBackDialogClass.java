package com.moutamid.simpleolx;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;

public class FeedBackDialogClass extends Dialog {

    public Activity c;
    public Dialog d;
    private EditText editTextMessage;
    private EditText editTextEmail;
    private TextView buttonSubmit;



    public FeedBackDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.feedback);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSubmit = findViewById(R.id.buttonSubmit);


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextMessage.getText().toString();
                String email = editTextEmail.getText().toString();

                if (name.isEmpty() || email.isEmpty()) {
                    // Display an error message if the edit text fields are empty.
                    Toast.makeText(c, "Please enter email and Message.", Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    addDataToFirestore(name, email);
                }
            }
        });

    }

    private void addDataToFirestore(String message, String email) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("email", email);


        Constants.databaseReference().child("Support").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(c, "Feedback is successfully submitted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Something went wrong" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}