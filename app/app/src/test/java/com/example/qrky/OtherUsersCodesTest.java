package com.example.qrky;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import android.widget.GridView;
import android.widget.ImageButton;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for OtherUsersCodes
 *
 * @author Franco Bonilla
 */
public class OtherUsersCodesTest {
    private OtherUsersCodes fragment;
    private GridView gridView;
    private ImageButton backBttn;
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        fragment = new OtherUsersCodes();
        gridView = mock(GridView.class);
        backBttn = mock(ImageButton.class);
        db = mock(FirebaseFirestore.class);

        fragment.gridOfCodes = gridView;
        fragment.backBttn = backBttn;
        fragment.qrkyDB = db;

        fragment.getTestData();
    }

    @Test
    public void testGetCodeNames() {
        List<String> testDataNames = new ArrayList<>(Arrays.asList("CookieMonster", "Elmo", "DiegoZombie"));
        assertEquals(testDataNames, fragment.getCodeNames());
    }

    @Test
    public void testGetCodeScores() {
        List<Integer> testDataScores = new ArrayList<>(Arrays.asList(100, 340, 8));
        assertEquals(testDataScores, fragment.getCodeScores());
    }

    @Test
    public void testGetCodeDrawings() {
        List<List<String>> testDataDrawings = new ArrayList<>();
        List<String> codeDrawing = new ArrayList<>(Arrays.asList("U     U", "   ~   ", "  ___  "));
        List<String> codeDrawing2 = new ArrayList<>(Arrays.asList(">     <", "   -   ", "  UUU  "));
        List<String> codeDrawing3 = new ArrayList<>(Arrays.asList("T     T", "   O   ", "  MWM  "));
        testDataDrawings.add(codeDrawing);
        testDataDrawings.add(codeDrawing2);
        testDataDrawings.add(codeDrawing3);
        assertEquals(testDataDrawings, fragment.getCodeDrawings());
    }

    @Test
    public void testGetLibrarySize() {
        assertEquals(3, fragment.getLibrarySize());
    }
}
