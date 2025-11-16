package org.example.parsers;

import org.apache.commons.lang3.tuple.Pair;
import org.example.CsvEntry;
import org.example.enums.CsvColumnEnum;
import org.example.enums.DescriptionLengthEnum;
import org.example.enums.EntryTypeEnum;
import org.example.utils.StringUtils;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrequencyParser {
    Map<
            EntryTypeEnum,
            Map<
                    Year,
                    Map<
                            CsvColumnEnum,
                            Map<String, Integer>>>>
            frequencyMap = new HashMap<>();

    public FrequencyParser findMostFrequent(CsvParser reader) {
        for (CsvEntry entry : reader.entries) {
            setDefault(entry);
            ArrayList<Pair<CsvColumnEnum, String>> pairList = new ArrayList<>();

            pairList.add(Pair.of(CsvColumnEnum.RATING, entry.rating));
            pairList.add(Pair.of(CsvColumnEnum.DURATION, entry.durationEnum != null ? entry.durationEnum.name() : ""));
            pairList.add(Pair.of(CsvColumnEnum.TITLE_LENGTH, String.valueOf(entry.title.split(" ").length)));
            pairList.add(Pair.of(CsvColumnEnum.DESCRIPTION_LENGTH, DescriptionLengthEnum.from(entry.description).name()));
            for (String category : entry.categories) {
                pairList.add(Pair.of(CsvColumnEnum.CATEGORIES, category));
            }
            for (String word : StringUtils.fixString(entry.title).split(" ")) {
                if (StringUtils.hasDigit(word)) { continue; }
                if (StringUtils.isStopword(word)) { continue; }
                pairList.add(Pair.of(CsvColumnEnum.TITLE, word));
            }
            for (String word : StringUtils.fixString(entry.description).split(" ")) {
                if (StringUtils.hasDigit(word)) { continue; }
                if (StringUtils.isStopword(word)) { continue; }
                pairList.add(Pair.of(CsvColumnEnum.DESCRIPTION, word));
            }
            for (Pair<CsvColumnEnum, String> pair : pairList) {
                addToFrequencyMap(getMap(entry), pair);
            }
        }
        return this;
    }
    private Map<CsvColumnEnum, Map<String, Integer>> getMap(CsvEntry entry) {
        return frequencyMap.get(entry.type).get(entry.release_year);
    }
    private void setDefault(CsvEntry entry) {
        frequencyMap.putIfAbsent(entry.type, new HashMap<>());
        frequencyMap.get(entry.type).putIfAbsent(entry.release_year, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.RATING, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.DURATION, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.CATEGORIES, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.TITLE_LENGTH, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.DESCRIPTION_LENGTH, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.TITLE, new HashMap<>());
        getMap(entry).putIfAbsent(CsvColumnEnum.DESCRIPTION, new HashMap<>());
    }
    private void addToFrequencyMap(
            Map<CsvColumnEnum, Map<String, Integer>> map,
            Pair<CsvColumnEnum, String> pair
    ) {
        Integer currentRatingQtd = map.get(pair.getKey()).getOrDefault(pair.getValue(), 0);
        map.get(pair.getKey()).put(pair.getValue(), currentRatingQtd + 1);
    }
}
