package org.example;

import org.apache.commons.lang3.tuple.Pair;
import org.example.enums.CsvColumnEnum;
import org.example.enums.EntryTypeEnum;

import java.time.Year;

public class DatasetEntry {
    public EntryTypeEnum type;
    public Year year;
    public CsvColumnEnum column;
    public Pair<String, Integer> top1;
    public Pair<String, Integer> top2;
    public Pair<String, Integer> top3;
    public Pair<String, Integer> top4;
    public Pair<String, Integer> top5;
}
