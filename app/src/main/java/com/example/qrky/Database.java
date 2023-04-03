package com.example.qrky;


import static java.lang.Math.abs;

import android.util.Log;

import androidx.camera.core.ExperimentalGetImage;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is used to interact with the database
 */
public class Database {
    private final FirebaseFirestore mDb;
    private final StorageReference storageRef;
    public Database() {
        mDb = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /**
     * Saves a QR code to the database
     * @param isLocationRequired whether or not the location is required
     * @param mCode the code to be saved
     * @param mGeoPoint the recorded location of user who last scanned code
     * @param photoInputStream input stream of the photo taken
     */
    @ExperimentalGetImage
    public void goSaveLibrary(boolean isLocationRequired, String mCode, GeoPoint mGeoPoint,
                              InputStream photoInputStream) {

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
        Log.d("goSaveLibrary", "create new");
        try {
            mDb.collection("QR Codes").document(hexString.toString()).set(new HashMap<String, Object>(), SetOptions.merge());
        } catch (Exception e) {
            Log.d("goSaveLibrary", "error");
        }
        Log.d("goSaveLibrary", "before uploadImage ");
        uploadImage(photoInputStream, hexString.toString());

        if (isLocationRequired) {
            mDb.collection("QR Codes").document(hexString.toString()).update("location", mGeoPoint);
        }
        Log.d("goSaveLibrary", "before makeName");
        mDb.collection("QR Codes").document(hexString.toString()).update("name", makeName(extractFromHex(hexString.toString())));
        Log.d("goSaveLibrary", "after makeName");
        mDb.collection("QR Codes").document(hexString.toString()).update("timestamp", Timestamp.now());
        mDb.collection("QR Codes").document(hexString.toString()).update("playerID", FieldValue.arrayUnion(MainActivity.getuName()));
        mDb.collection("QR Codes").document(hexString.toString()).update("score", getScore(hexString.toString()));
        mDb.collection("Players").document(MainActivity.getuName()).update("codes", FieldValue.arrayUnion(hexString.toString()));
        updatePlayer(getScore(hexString.toString()));
    }

    private void updatePlayer(int score) {
        mDb.collection("QR Codes").whereArrayContains("playerID", MainActivity.getuName()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int size = task.getResult().size();
                Log.d("updatePlayer", "size: " + size);
                mDb.collection("Players").document(MainActivity.getuName()).update("totalCodes", size);
                int totalScore = 0;
                for (int i = 0; i < size; i++) {
                    try {
                        totalScore += Objects.requireNonNull(task.getResult().getDocuments().get(i).getLong("score")).intValue();
                    } catch (Exception e) {
                        Log.d("updatePlayer", "error: " + e);
                    }
                }
                mDb.collection("Players").document(MainActivity.getuName()).update("score", totalScore);
            }
        });
    }

    /**
     * This method is used to generate a score for the QR Code
     * @param mCode the hash of the QR Code
     * @return the calculated score of the QR Code
     */
    int getScore(String mCode) {
        int score;
        String[] halfwords = mCode.split("");
        String[] bytes = new String[halfwords.length / 2];
        for (int i = 0; i < halfwords.length; i += 2) {
            bytes[i / 2] = halfwords[i] + halfwords[i + 1];
            Log.d("getScore", "bytes: " + bytes[i / 2]);
        }
        Log.d("getScore", "firstBytes: " + bytes[0] + bytes[1] + bytes[2] + bytes[3]);
        Long firstBytes = Long.valueOf(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5], 16);
        Log.d("getScore", "firstBytesValue: " + firstBytes);
        Log.d("getScore", "lastBytes: " + bytes[bytes.length-4] + bytes[bytes.length-3] + bytes[bytes.length-2] + bytes[bytes.length-1]);
        Long lastBytes = Long.parseLong(bytes[bytes.length-4]+bytes[bytes.length-3]+bytes[bytes.length-2]+bytes[bytes.length-1], 16);
        Log.d("getScore", "firstBytesValue: " + firstBytes);
        long divisor = (abs(firstBytes) - abs(lastBytes)) % 1000;
        if (divisor == 0) {
            divisor = 1; // set a non-zero value for divisor
        }
        score = (int) ((Long.valueOf(bytes[6] + bytes[8] + bytes[10] + bytes[12] + bytes[14] , 16)
                + Long.valueOf(bytes[16] + bytes[18] + bytes[20] + bytes[22] + bytes[24] + bytes[26], 16)
                - Long.valueOf(bytes[7] + bytes[9] + bytes[11] + bytes[13] + bytes[15], 16)
                + Long.valueOf( bytes[17] + bytes[19] + bytes[21] + bytes[23] + bytes[25] + bytes[27], 16))
                % divisor) + 1;
        Log.d("getScore", "score: " + score);
        return abs(score);
    }



    /**
     * This method is used to upload an image to the firebase storage
     * @param photoInputStream byte array of photo to be uploaded
     * @param mCode the hash of the QR Code
     */
    @ExperimentalGetImage
    private void uploadImage(InputStream photoInputStream, String mCode) {

        if (photoInputStream != null) {

            final String[] path = new String[1];
//          storage of image from https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
            StorageReference ref
                    = storageRef
                    .child(
                            "QRImages/"
                                    + UUID.randomUUID().toString());
            ref.putStream(photoInputStream)
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
     * Gets important info for unique name from Hexadecimal hash of QR Code
     * @param str the string to be extracted from
     * @return the extracted string
     */
    private String extractFromHex(String str) {
        String[] strArr = str.split("");
        String binary = "";
        for (int i = 0; i < 4; i++) {
            switch (strArr[i]) {
                case "0":
                    binary += "00";
                    break;
                case "1":
                    binary += "01";
                    break;
                case "2":
                    binary += "02";
                    break;
                case "3":
                    binary += "03";
                    break;
                case "4":
                    binary += "10";
                    break;
                case "5":
                    binary += "11";
                    break;
                case "6":
                    binary += "12";
                    break;
                case "7":
                    binary += "13";
                    break;
                case "8":
                    binary += "20";
                    break;
                case "9":
                    binary += "21";
                    break;
                case "a":
                    binary += "22";
                    break;
                case "b":
                    binary += "23";
                    break;
                case "c":
                    binary += "30";
                    break;
                case "d":
                    binary += "31";
                    break;
                case "e":
                    binary += "32";
                    break;
                case "f":
                    binary += "33";
                    break;
            }
        }

        return binary;
    }
    /**
     * This method takes info extracted from a QR Code hash and converts it to a name
     * @param str the string to be converted
     * @return name: an 8 word name for a QR Code
     */
    String makeName(String str) {

        String[] wordArr = new String[8];
        String[] strArr;

        strArr = str.split("");
        String result = "";

        switch (strArr[0]) {
            case "0":
                wordArr[0] = "Friendly ";
                break;
            case "1":
                wordArr[0] = "Hostile ";
                break;
            case "2":
                wordArr[0] = "Calm ";
                break;
            case "3":
                wordArr[0] = "Angry ";
                break;
        }

        switch (strArr[1]) {
            case "0":
                wordArr[1] = "Lovely ";
                break;
            case "1":
                wordArr[1] = "Ugly ";
                break;
            case "2":
                wordArr[1] = "Pretty ";
                break;
            case "3":
                wordArr[1] = "Beautiful ";
                break;
        }

        switch (strArr[2]) {
            case "0":
                wordArr[2] = "Small ";
                break;
            case "1":
                wordArr[2] = "Big ";
                break;
            case "2":
                wordArr[2] = "Huge ";
                break;
            case "3":
                wordArr[2] = "Giant ";
                break;
        }


        switch (strArr[3]) {
            case "0":
                wordArr[3] = "Noisy ";
                break;
            case "1":
                wordArr[3] = "Quite ";
                break;
            case "2":
                wordArr[3] = "Silent ";
                break;
            case "3":
                wordArr[3] = "Loud ";
                break;
        }

        switch (strArr[4]) {
            case "0":
                wordArr[4] = "Young ";
                break;
            case "1":
                wordArr[4] = "Old ";
                break;
            case "2":
                wordArr[4] = "Baby ";
                break;
            case "3":
                wordArr[4] = "Senior ";
                break;
        }

        switch (strArr[5]) {
            case "0":
                wordArr[5] = "Tidy ";
                break;
            case "1":
                wordArr[5] = "Messy ";
                break;
            case "2":
                wordArr[5] = "Clean ";
                break;
            case "3":
                wordArr[5] = "Dirty ";
                break;
        }
        switch (strArr[6]) {
            case "0":
                wordArr[6] = "Fresh ";
                break;
            case "1":
                wordArr[6] = "Drippy ";
                break;
            case "2":
                wordArr[6] = "Iced-Out ";
                break;
            case "3":
                wordArr[6] = "Salty ";
                break;
        }
        switch (strArr[7]) {
            case "0":
                wordArr[7] = "Drake";
                break;
            case "1":
                wordArr[7] = "Duck";
                break;
            case "2":
                wordArr[7] = "Duckling";
                break;
            case "3":
                wordArr[7] = "Swan";
                break;
        }


        for (int i = 0; i < 8; i++) {
            result += wordArr[i];
        }

        return result;
    }
}
