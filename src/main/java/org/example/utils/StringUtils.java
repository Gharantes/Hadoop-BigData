package org.example.utils;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringUtils {
    public static String fixString(String str) {
        str = str.toLowerCase();
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // keep ASCII letters, digits and apostrophes (for contractions/possessives),
        // replace other runs of chars with a single space
        str = str.replaceAll("[^a-z0-9']+", " ");

        // remove apostrophes that are NOT between two alphanumeric characters
        // i.e. remove leading/trailing/apostrophes next to non-alnum
        str = str.replaceAll("(?<![a-z0-9])'|'(?![a-z0-9])", "");

        str = str.trim();
        return str;
    }
    public static boolean isDigit(String str) {
        return str.matches("\\d+");
    }
    public static boolean hasDigit(String str) {
        return str.matches(".*\\d.*");
    }
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "and", "the", "of", "to", "with", "is", "in", "his",
            "her", "an", "for", "on", "their", "when", "this", "from",
            "as", "by", "he", "that", "who", "but", "at", "into"
    ));
    public static boolean isStopword(String str) {
        return STOPWORDS.contains(str);
    }
}
