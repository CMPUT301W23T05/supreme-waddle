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
import java.util.Dictionary;
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
                mDb.collection("QR Codes").document(mCode).update("photo", FieldValue.arrayUnion(path[0]));
            }).addOnProgressListener(taskSnapshot -> {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        Log.d("uploadImage",  "Progress: " + progress + "% uploaded");
                    });

        }



    }
    /**
     * This method takes a QR Code hash and converts it to a name
     * @param str
     * @return name: a 6 word name for a QR Code
     */
    private String makeName(String str) {
        String[] strArr = str.split("");
        String[] wordArr = new String[6];
        String binary = "";
        if (strArr[0].equals("0")) {
            binary += "0000";
        } else if (strArr[0].equals("1")) {
            binary += "0001";
        } else if (strArr[0].equals("2")) {
            binary += "0010";
        } else if (strArr[0].equals("3")) {
            binary += "0011";
        } else if (strArr[0].equals("4")) {
            binary += "0100";
        } else if (strArr[0].equals("5")) {
            binary += "0101";
        } else if (strArr[0].equals("6")) {
            binary += "0110";
        } else if (strArr[0].equals("7")) {
            binary += "0111";
        } else if (strArr[0].equals("8")) {
            binary += "1000";
        } else if (strArr[0].equals("9")) {
            binary += "1001";
        } else if (strArr[0].equals("a")) {
            binary += "1010";
        } else if (strArr[0].equals("b")) {
            binary += "1011";
        } else if (strArr[0].equals("c")) {
            binary += "1100";
        } else if (strArr[0].equals("d")) {
            binary += "1101";
        } else if (strArr[0].equals("e")) {
            binary += "1110";
        } else if (strArr[0].equals("f")) {
            binary += "1111";
        }
        if (strArr[1].equals("4")) {
            binary += "01";
        } else if (strArr[1].equals("5")) {
            binary += "01";
        } else if (strArr[1].equals("6")) {
            binary += "01";
        } else if (strArr[1].equals("7")) {
            binary += "01";
        } else if (strArr[0].equals("8")) {
            binary += "10";
        } else if (strArr[0].equals("9")) {
            binary += "10";
        } else if (strArr[0].equals("a")) {
            binary += "10";
        } else if (strArr[0].equals("b")) {
            binary += "10";
        } else if (strArr[0].equals("c")) {
            binary += "11";
        } else if (strArr[0].equals("d")) {
            binary += "11";
        } else if (strArr[0].equals("e")) {
            binary += "11";
        } else if (strArr[0].equals("f")) {
            binary += "11";
        } else {
            binary += "00";
        }

        strArr = binary.split("");
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
