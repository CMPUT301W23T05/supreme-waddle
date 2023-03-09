package com.example.qrky;

import static com.google.firebase.firestore.FieldValue.arrayUnion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final FirebaseFirestore mDb;
    public Database() {
        mDb = FirebaseFirestore.getInstance();

    }
    @ExperimentalGetImage
    public void goSaveLibrary(boolean isLocationRequired, String mCode, GeoPoint mGeoPoint, ImageProxy Photo) {
        Bitmap bitmap = getImageBitmap(Photo);
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
            map.put("photo", bitmap);
            map.put("timestamp", Timestamp.now());
            map.put("playerID", arrayUnion("playerID"));
            Log.d("TAG", "goSaveLibrary: ");
            mDb.collection("QR Codes").document(hexString.toString()).set(map);
        }
    }
    @ExperimentalGetImage
    private Bitmap getImageBitmap(ImageProxy image) {
        Image mediaImage = image.getImage();
        if (mediaImage != null) {
            ByteBuffer buffer = mediaImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        }
        else {
            return null;
        }
    }
}
