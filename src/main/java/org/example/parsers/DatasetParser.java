package org.example.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.example.DatasetEntry;
import org.example.enums.CsvColumnEnum;
import org.example.enums.EntryTypeEnum;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatasetParser {
    public static List<DatasetEntry> results = new ArrayList<>();

    public DatasetParser first(FrequencyParser parser) {
        EntryTypeEnum[] types = {
                EntryTypeEnum.MOVIE,
                EntryTypeEnum.TV_SHOW
        };
        for (EntryTypeEnum type : types) {
            second(type, parser.frequencyMap.get(type));
        }
        return this;
    }
    private void second (
            EntryTypeEnum type,
            Map<Year, Map<CsvColumnEnum, Map<String, Integer>>> yearMap
    ) {
        List<Year> years = yearMap
                .keySet()
                .stream()
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .collect(Collectors.toList());
        for (Year year : years) {
            third(type, year, yearMap.get(year));
        }
    }
    private void third(
            EntryTypeEnum type,
            Year year,
            Map<CsvColumnEnum, Map<String, Integer>> csvColumnEnumMap
    ) {
        CsvColumnEnum[] columns = {
                CsvColumnEnum.TITLE,
                CsvColumnEnum.TITLE_LENGTH,
                CsvColumnEnum.RATING,
                CsvColumnEnum.DURATION,
                CsvColumnEnum.CATEGORIES,
                CsvColumnEnum.DESCRIPTION,
                CsvColumnEnum.DESCRIPTION_LENGTH
        };
        for (CsvColumnEnum column : columns) {
            fourth(type, year, column, csvColumnEnumMap.get(column));
        }

    }

    private void fourth(
            EntryTypeEnum type,
            Year year,
            CsvColumnEnum column,
            Map<String, Integer> stringIntegerMap
    ) {
        List<Pair<String, Integer>> res = stringIntegerMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        DatasetEntry datasetEntry = new DatasetEntry();
        datasetEntry.type = type;
        datasetEntry.year = year;
        datasetEntry.column = column;
        datasetEntry.top1 = !res.isEmpty() ? res.get(0) : null;
        datasetEntry.top2 = res.size() > 1 ? res.get(1) : null;
        datasetEntry.top3 = res.size() > 2 ? res.get(2) : null;
        datasetEntry.top4 = res.size() > 3 ? res.get(3) : null;
        datasetEntry.top5 = res.size() > 4 ? res.get(4) : null;
        results.add(datasetEntry);
    }
}
