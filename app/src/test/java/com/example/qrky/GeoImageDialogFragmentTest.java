package com.example.qrky;

import static com.example.qrky.CardDetailsFragment.ARG_TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Rule;
import org.junit.Test;

public class GeoImageDialogFragmentTest {

    private static final String ARG_TITLE = "title";


    @Test
    public void testGetLatLngFromGeoPoint() {
        GeoPoint geoPoint = new GeoPoint(37.4219983, -122.084);
        LatLng latLng = getLatLngFromGeoPoint(geoPoint);
        assertNotNull(latLng);
        assertEquals(37.4219983, latLng.latitude, 0);
        assertEquals(-122.084, latLng.longitude, 0);
    }

    private LatLng getLatLngFromGeoPoint(GeoPoint geoPoint) {
        if (geoPoint != null) {
            return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        }
        return null;
    }


}
