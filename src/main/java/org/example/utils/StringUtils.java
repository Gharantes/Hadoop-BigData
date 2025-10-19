package org.example.utils;

import java.text.Normalizer;

public class StringUtils {
    public static String fixString(String str) {
        str = str.toLowerCase();
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        str = str.replaceAll("[^a-z0-9]", " ");
        str = str.trim();
        return str;
    }
    public static boolean isDigit(String str) {
        return str.matches("\\d+");
    }
}
