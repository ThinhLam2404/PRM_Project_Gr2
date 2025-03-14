package com.app.cookbook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.app.cookbook.prefs.DataStoreManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    public static final String FIREBASE_URL = "https://prm392-dotai-default-rtdb.firebaseio.com/";
    private FirebaseDatabase mFirebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        Log.d("Firebase", "Database URL: " + mFirebaseDatabase.getReference().toString());
        DataStoreManager.init(getApplicationContext());
    }

    public DatabaseReference locationDatabaseReference() {
        return mFirebaseDatabase.getReference("/locations");
    }

    public DatabaseReference foodDatabaseReference() {
        return mFirebaseDatabase.getReference("/hotels");
    }

    public DatabaseReference hotelDatabaseReference() {
        return mFirebaseDatabase.getReference("/hotels");
    }

    public DatabaseReference destinationDatabaseReference() {
        return mFirebaseDatabase.getReference("/destinations");
    }


    public DatabaseReference feedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }




    public DatabaseReference requestFoodDatabaseReference() {
        return mFirebaseDatabase.getReference("/request");
    }

    public DatabaseReference foodDetailDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("destinations/" + foodId);
    }

    public DatabaseReference ratingFoodDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("/destinations/" + foodId + "/rating");
    }

    public DatabaseReference countFoodDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("/destinations/" + foodId + "/count");
    }
    public DatabaseReference userDatabaseReference() {
        return mFirebaseDatabase.getReference("/user");
    }
}
