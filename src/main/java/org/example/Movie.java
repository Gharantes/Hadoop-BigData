package org.example;

import org.apache.hadoop.io.Text;
import org.example.utils.StringUtils;

import java.sql.Array;

public class Movie {
    private final String title;
    private final String description;
    private final String[] descriptionWords;
    public String[] getDescriptionWords() { return this.descriptionWords; }
    private final int descriptionWordCount;
    public int getDescriptionWordCount() { return this.descriptionWordCount; }

    public Movie(String title, String description) {
        this.title = StringUtils.fixString(title);
        this.description = StringUtils.fixString(description);
        this.descriptionWords = this.description.split("\\s+");
        this.descriptionWordCount = this.descriptionWords.length;
    }

    /** Retorna "0" se a quantidade de palavras na descrição é a mesma.
     * Retorna "1" se a quantidade de palavras no filme2 é maior que a do atual.
     * Retorna "-1" se a quantidade de palvras no filme2 é menor que a do atual.
     * @return int
     */
    public int compare(Movie movie2) {
        return Integer.compare(movie2.descriptionWordCount, this.descriptionWordCount);
    }
    public Text asText(String base) {
        String str = "";
        str = str.concat("[" + base + "]\n");
        str = str.concat("Palavras: " + this.descriptionWordCount + "\n");
        str = str.concat("Titulo: " + this.title + "\n");
        str = str.concat("Descrição: " + this.description + "\n");
        Text text = new Text();
        text.set(str);
        return text;
    }
}
