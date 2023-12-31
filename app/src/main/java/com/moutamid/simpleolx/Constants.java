package com.moutamid.simpleolx;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    public static final String IS_ADMIN = "IS_ADMIN";

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("SimpleOlx");
        db.keepSynced(true);
        return db;
    }
}
