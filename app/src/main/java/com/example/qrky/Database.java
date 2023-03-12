package com.example.qrky;

import static com.google.firebase.firestore.FieldValue.arrayUnion;


import static java.lang.Double.parseDouble;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {
    private final FirebaseFirestore mDb;
    private final StorageReference storageRef;
    public Database() {
        mDb = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    @ExperimentalGetImage
    public void goSaveLibrary(boolean isLocationRequired, String mCode, GeoPoint mGeoPoint, byte[] Photo) {
        final Map<String, Object> map = new HashMap<>();

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedHash = digest.digest(
                mCode.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        uploadImage(Photo, hexString.toString());

        mDb.collection("QR Codes").document(hexString.toString()).update("playerID", FieldValue.arrayUnion("playerID1"));
        mDb.collection("QR Codes").document(hexString.toString()).update("timestamp", Timestamp.now());



        if (isLocationRequired) {
            mDb.collection("QR Codes").document(hexString.toString()).update("location", mGeoPoint);
        }
        mDb.collection("QR Codes").document(hexString.toString()).update("name", makeName(hexString.toString()));
        mDb.collection("QR Codes").document(hexString.toString()).update("timestamp", Timestamp.now());
        mDb.collection("QR Codes").document(hexString.toString()).update("playerID", FieldValue.arrayUnion("playerID"));
        Log.d("TAG", "goSaveLibrary: ");
    }
    @ExperimentalGetImage
    public void uploadImage(byte[] Photo, String mCode) {

        if (Photo != null) {

            final String[] path = new String[1];
//            storage of image from https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
            StorageReference ref
                    = storageRef
                    .child(
                            "QRImages/"
                                    + UUID.randomUUID().toString());
            ref.putBytes(Photo)
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        // ...
                        Log.d("uploadImage", "onFailure: " + exception.getMessage());
                        path[0] = null;
                    }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                path[0] = ref.getPath();
                Log.d("uploadImage", "onSuccess: " + path[0]);
                mDb.collection("QR Codes").document(mCode).update("photo", (path[0]));
            }).addOnProgressListener(taskSnapshot -> {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        Log.d("uploadImage",  "Progress: " + progress + "% uploaded");
                    });

        }



    }
    private String makeName(String str) {
        String[] strArr = str.split("");
        String[] wordArr = new String[6];
        String result = "";

        if((strArr[0].equals("0"))){
            wordArr[0] = "Loud ";
        }else{
            wordArr[0] = "Quiet ";
        }
        if((strArr[1].equals("0"))){
            wordArr[1] = "Far";
        }else{
            wordArr[1] = "Near";
        }
        if((strArr[2].equals("0"))){
            wordArr[2] = "Fast";
        }else{
            wordArr[2] = "Slow";
        }
        if((strArr[3].equals("0"))){
            wordArr[3] = "Full";
        }else{
            wordArr[3] = "Empty";
        }
        if((strArr[4].equals("0"))){
            wordArr[4] = "Young";
        }else{
            wordArr[4] = "Old";
        }
        if((strArr[5].equals("0"))){
            wordArr[5] = "Strong";
        }else{
            wordArr[5] = "Weak";
        }
        for (int i = 0; i < 6; i++) {
            result += wordArr[i] + " ";
        }

        return result;
    }
}
