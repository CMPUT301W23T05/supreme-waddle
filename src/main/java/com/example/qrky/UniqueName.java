package com.example.qrky;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UniqueName extends Activity {
    public String makeName(String str) {
        String[] strArr = str.split("");
        String[] wordArr = new String[6];
        String result = null;

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
            result = wordArr[i];
        }

        return result;
    }
}
