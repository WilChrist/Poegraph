package com.wilchrist.poegraph;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtility {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtility firebaseUtility;

    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RC_SIGN_IN = 123;
    private static Activity caller;

    public static boolean isAdmin;

    public static ArrayList<Poem> mPoems;
    public static String poemCollectionName;

    //create a private constructor to avoid this class being instantiated
    private FirebaseUtility() {
    }


    public static void openFbReference(String ref, final Activity callerActivity) {
        if (firebaseUtility == null) {
            firebaseUtility = new FirebaseUtility();
            mFirebaseDatabase = FirebaseDatabase.getInstance();

            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    String userName="";

                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtility.signIn();
                        Toast.makeText(callerActivity.getBaseContext(), "Welcome back! ".concat(userName), Toast.LENGTH_LONG).show();
                    } else {
                        String userId = firebaseAuth.getUid();
                        userName=firebaseAuth.getCurrentUser().getDisplayName();
                        checkAdmin(userId);
                    }

                }
            };
        }
        mPoems = new ArrayList<Poem>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static FirebaseUser getCurrentlyConnectedUser(){
        return mFirebaseAuth.getCurrentUser();
    }

    public static boolean isConnectedUserOwnerOfPoem(Poem poem){
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String poemUserGuid = "";
        poemUserGuid = poem.getUserId();
        if(firebaseUser!= null){
            if(firebaseUser.getUid().equals(poem.getUserId())){
                return true;
            }
        }
        return false;
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtility.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
