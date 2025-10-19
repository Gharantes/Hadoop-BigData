package org.example;

import org.apache.hadoop.io.Text;

public class WordCounter {
    private int total = 0;
    public void add(Movie movie) {
        this.total += movie.getDescriptionWordCount();
    }
    public Text asText() {
        Text text = new Text();
        String s = "[Total de Palavras nas Descrições]: " + this.total + " palavras\n";
        text.set(s);
        return text;
    }
}
