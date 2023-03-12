package com.example.qrky;

import java.util.ArrayList;
import java.util.List;

public class FaceGenerator {
    private static final String CLOSED_EYES = "   ____\n  /    \\\n\\| _  _ |/\n@|  ||  |@\n/| ,`` ,|\\\n |      |\n | `--` |\n  \\____/\n";
    private static final String BRIGHT_EYES = "   ____\n  /    \\\n\\| O  O |/\n@|  ||  |@\n/|  --  |\\\n |      |\n |  ||  |\n  \\____/\n";
    private static final String MEAN_EYEBROWS = "   ____\n  /    \\\n\\| _  _ |/\n@|  ||  |@\n/| ^  ^ |\\\n |  --  |\n |______|\n  \\____/\n";
    private static final String NO_EYEBROWS = "   ____\n  /    \\\n\\| _  _ |/\n@|  ||  |@\n/| -  - |\\\n |      |\n |______|\n  \\____/\n";
    private static final String ROUND_FACE = "   .-. \n  (_\"_)\n (_   _)\n   \"-\"\n";
    private static final String SQUARE_FACE = "   ____\n  /    \\\n/|      |\\\n||      ||\n||      ||\n\\|______|/\n";
    private static final String BIG_NOSE = "   ,\"\"\".\n  / _  \\\n / / \\ \\\n \\ \\_/ /\n  `---'\n";
    private static final String NO_NOSE = "   ,\"\"\".\n  /    \\\n /      \\\n \\      /\n  `\"\"\"'\n";
    private static final String SMILE = "  _____\n /     \\\n| O   O |\n|   |   |\n| \\_/  |\n \\___/\n";
    private static final String FROWN = "  _____\n /     \\\n| O   O |\n|   |   |\n| ___  |\n \\___/\n";
    private static final String EARS = "    /\\   \n   /  \\\n  /    \\\n /      \\\n/________\\\n\\________/\n";

    public static List<String> makeFace(String str) {
        String[] strArr = str.split("");
        String[] wordArr = new String[6];
        List<String> result = new ArrayList<>();

        // Extract the last 6 bits from the input string
        for (int i = strArr.length - 6; i < strArr.length; i++) {
            wordArr[i - (strArr.length - 6)] = strArr[i];
        }

        // Generate the face based on the bits
        for (String s : wordArr) {
            switch (s) {
                case "0":
                    result.add(CLOSED_EYES);
                    break;
                case "1":
                    result.add(BRIGHT_EYES);
                    break;
                case "2":
                    result.add(MEAN_EYEBROWS);
                    break;
                case "3":
                    result.add(NO_EYEBROWS);
                    break;
                case "4":
                    result.add(ROUND_FACE);
                    break;
                case "5":
                    result.add(SQUARE_FACE);
                    break;
                case "6":
                    result.add(BIG_NOSE);
                    break;
                case "7":
                    result.add(NO_NOSE);
                    break;
                case "8":
                    result.add(SMILE);
                    break;
                case "9":
                    result.add(FROWN);
                    break;
                case "a":
                case "b":
                    result.add(EARS);
                    break;
                default:
                    break;
            }
        }

        return result;
    }
}