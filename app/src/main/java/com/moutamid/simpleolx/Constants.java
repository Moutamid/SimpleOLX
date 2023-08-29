package com.moutamid.simpleolx;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("https://sweet-nutrition-default-rtdb.firebaseio.com/SimpleOlx");
        db.keepSynced(true);
        return db;
    }
}
