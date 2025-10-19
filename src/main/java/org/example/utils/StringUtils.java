package org.example.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringUtils {
    public static String fixString(String str) {
        str = str.toLowerCase(); // A -> a
        /* Remove Acentos */
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        /* Remove caractéres especias.
        * Regra adicional: Caso exista um apóstrofo que está entre duas letras, não apaga ele.
        * Isso porque tem coisa como "don't" e "actor's" que devem ser consideradas 1
        * palavra só ainda.
        * */
        str = str.replaceAll("[^a-z0-9']+", " ");
        str = str.replaceAll("(?<![a-z0-9])'|'(?![a-z0-9])", "");
        // Remove espaço vazio no começo e final da linha.
        str = str.trim();
        return str;
    }
    /** Retorna TRUE se for somente números. **/
    public static boolean isDigit(String str) {
        return str.matches("\\d+");
    }
    /** Retorna TRUE se tiver qualquer número. **/
    public static boolean hasDigit(String str) {
        return str.matches(".*\\d.*");
    }
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "and", "the", "of", "to", "with", "is", "in", "his",
            "her", "an", "for", "on", "their", "when", "this", "from",
            "as", "by", "he", "that", "who", "but", "at", "into"
    ));
    /** Retorna TRUE se for qualquer uma das palavras acima. **/
    public static boolean isStopword(String str) {
        return STOPWORDS.contains(str);
    }
}
