package com.moutamid.simpleolx.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static String db_path = "SimpleOlx";
      public static String NOTIFICATIONAPIURL = "https://fcm.googleapis.com/fcm/send";


    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("SimpleOlx");
        db.keepSynced(true);
        return db;
    }

    public static DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference(db_path).child("users");
    public static DatabaseReference CategoryReference = FirebaseDatabase.getInstance().getReference(db_path).child("categories");
    public static DatabaseReference ChatReference = FirebaseDatabase.getInstance().getReference(db_path).child("chats");
    public static DatabaseReference ChatListReference = FirebaseDatabase.getInstance().getReference(db_path).child("chats_list");
    public static DatabaseReference LocationReference = FirebaseDatabase.getInstance().getReference(db_path).child("location");
}
