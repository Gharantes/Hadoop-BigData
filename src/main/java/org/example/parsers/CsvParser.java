package org.example.parsers;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.CsvEntry;

import java.util.*;

public class CsvParser {
    public List<CsvEntry> entries = new ArrayList<>();


    public CsvParser read(CSVParser parser) {
        int i = 0;
        for (CSVRecord record : parser) {
            if (i == 0) {
                i+=1;
                continue;
            }
            if (record.size() < 12) {
                i+=1;
                continue;
            }
            i+=1;
            CsvEntry entry = new CsvEntry(record);
            entries.add(entry);
        }
        return this;
    }
}
