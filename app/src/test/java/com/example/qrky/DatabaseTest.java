package com.example.qrky;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest {
    private byte[] bytes= {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    @Test
    public void testDatabaseStore() {
        Database testDatabase = new Database();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Assert.assertNotNull(testDatabase);
        testDatabase.goSaveLibrary(true, "06827919", new GeoPoint(53.5444, -113.4909), (bytes));
//        Assert.assertNotNull(db.collection("QR Codes").document("
    }
}
