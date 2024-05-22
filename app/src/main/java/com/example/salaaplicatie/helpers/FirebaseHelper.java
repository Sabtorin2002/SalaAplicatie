package com.example.salaaplicatie.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.salaaplicatie.firstSteps.LoginActivity;
import com.example.salaaplicatie.home.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void registerUser(Activity activity, String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        addUserToFirestore(user, username);
                        Toast.makeText(activity, "Registration successful", Toast.LENGTH_SHORT).show();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    } else {
                        Toast.makeText(activity, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToFirestore(FirebaseUser user, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("username", username);
        userData.put("uid",user.getUid());

        db.collection("Users").document(user.getUid()).set(userData)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }

    public void uploadProfileImage(Uri fileUri, String userId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userImageRef = storageRef.child("profile_images/" + userId + ".jpg");
        userImageRef.putFile(fileUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return userImageRef.getDownloadUrl();
                }).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }
}
