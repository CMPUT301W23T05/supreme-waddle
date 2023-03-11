package com.example.qrky;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;


// US 02.03.01: As a player, I want to be able to browse QR codes that other players have scanned.
// Artist: Franco Bonilla
// Actors: Player (primary)
// Goal: Player sees the code library of other players
// Trigger: Player clicks on an OtherUser
// Precondition: N/A
// Postcondition: Player sees OtherUser's code library
// Basic Flow:
//     1. Player clicks on an OtherUser (trigger)
//     2. System gets code library from database
//     3. System makes card for each code in library
//     4. System displays cards in a grid (3 columns)
//     5. Player sees OtherUser's code library (goal)
public class OtherUsersCodesViewModel extends ViewModel {
    List<String> codeNames = new ArrayList<>();
    List<Integer> codeScores = new ArrayList<>();
    List<List<String>> codeDrawings = new ArrayList<>();  // 0 = eyes, 1 = nose, 2 = mouth

    // TODO: get code library from database

    // TODO: Move to testing class
    public void addTestData() {
        codeNames.add("CookieMonster");
        codeScores.add(100);
        List<String> codeDrawing = new ArrayList<String>();
        codeDrawing.add("U     U");
        codeDrawing.add("   ~   ");
        codeDrawing.add("  ___  ");
        codeDrawings.add(codeDrawing);


        codeNames.add("Elmo");
        codeScores.add(340);
        List<String> codeDrawing2 = new ArrayList<String>();
        codeDrawing2.add(">     <");
        codeDrawing2.add("   -   ");
        codeDrawing2.add("  UUU  ");
        codeDrawings.add(codeDrawing2);

        codeNames.add("DiegoZombie");
        codeScores.add(8);
        List<String> codeDrawing3 = new ArrayList<String>();
        codeDrawing3.add("T     T");
        codeDrawing3.add("   O   ");
        codeDrawing3.add("  MWM  ");
        codeDrawings.add(codeDrawing3);
    }

}