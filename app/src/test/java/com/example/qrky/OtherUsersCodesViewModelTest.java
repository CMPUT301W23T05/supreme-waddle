package com.example.qrky;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for OtherUsersCodesViewModel.
 *
 * @author Franco Bonilla
 */
public class OtherUsersCodesViewModelTest {
    private OtherUsersCodesViewModel otherUsersCodesVM;

    @Before
    public void setUp() {
        otherUsersCodesVM = new OtherUsersCodesViewModel();
    }


    @Test
    public void testGetCodeNames() {
        List<String> testDataNames = new ArrayList<>(Arrays.asList("CookieMonster", "Elmo", "DiegoZombie"));
        assertEquals(testDataNames, otherUsersCodesVM.getCodeNames());
    }

    @Test
    public void testGetCodeScores() {
        List<Integer> testDataScores = new ArrayList<>(Arrays.asList(100, 340, 8));
        assertEquals(testDataScores, otherUsersCodesVM.getCodeScores());
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
        assertEquals(testDataDrawings, otherUsersCodesVM.getCodeDrawings());
    }

//    @After
//    public void tearDown() throws Exception {
//    }
}
