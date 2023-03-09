package com.example.qrky;

import static com.google.firebase.firestore.FieldValue.arrayUnion;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final FirebaseFirestore mDb;
    private final FirebaseStorage storage;
    public Database() {
        mDb = FirebaseFirestore.getInstance();
        storage  = FirebaseStorage.getInstance();

    }
    @ExperimentalGetImage
    public void goSaveLibrary(boolean isLocationRequired, String mCode, GeoPoint mGeoPoint, ImageProxy Photo) {
        String path = getImageBitmap(Photo);
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
        if (mDb.collection("QR Codes").document(hexString.toString()).get().isSuccessful(
        )) {
            mDb.collection("QR Codes").document(hexString.toString()).update("playerID", arrayUnion("playerID"));

        }
        else {
            map.put("hash", hexString.toString());
            if (isLocationRequired) {
                map.put("location", mGeoPoint);
            }
            map.put("photoPath", path);
            map.put("timestamp", Timestamp.now());
            map.put("playerID", arrayUnion("playerID"));
            Log.d("TAG", "goSaveLibrary: ");
            mDb.collection("QR Codes").document(hexString.toString()).set(map);
        }
    }
    @ExperimentalGetImage
    private String getImageBitmap(ImageProxy image) {
        final String[] path = new String[1];
        if (image != null) {
            Image mediaImage = image.getImage();
            if (mediaImage != null) {
                StorageReference storageRef = storage.getReference();
                ByteBuffer buffer = mediaImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);


                UploadTask uploadTask = storageRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        path[0] = storageRef.getPath();
                    }
                });
                return path[0];
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
