package org.example;

import org.apache.commons.csv.CSVRecord;
import org.example.enums.DurationEnum;
import org.example.enums.EntryTypeEnum;
import org.example.enums.DescriptionLengthEnum;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CsvEntry {
    public EntryTypeEnum type;
    public String title;
    public String director;
    public String cast;
    public String country;
    public String date_added;
    public Year release_year;
    public String rating;
    public String duration;
    public DurationEnum durationEnum;
    public String listed_in;
    public List<String> categories;
    public String description;
    DescriptionLengthEnum descriptionLength;
    public CsvEntry(CSVRecord record) {
        this.type = EntryTypeEnum.from(record.get(1));
        this.title = record.get(2);
        this.director = record.get(3);
        this.cast = record.get(4);
        this.country = record.get(5);
        this.date_added = record.get(6);
        if (record.get(7) != null && !Objects.equals(record.get(7), "")) {
            this.release_year = Year.of(Integer.parseInt(record.get(7)));
        };
        this.rating = record.get(8);
        this.duration = record.get(9);
        this.durationEnum = DurationEnum.from(this.type, this.duration);
        this.listed_in = record.get(10);
        this.categories = Arrays.stream(record.get(10).split(",")).map(v -> v.trim()).collect(Collectors.toList());
        this.description = record.get(11);
        this.descriptionLength = DescriptionLengthEnum.from(this.description);
    }
}
