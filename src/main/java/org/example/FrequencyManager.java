package org.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.example.utils.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FrequencyManager {
    private final Map<Integer, ArrayList<String>> frequencyMap;
    private final List<Integer> sortedKeys;

    public FrequencyManager(Map<Integer, ArrayList<String>> frequencyMap) {
        this.frequencyMap = frequencyMap;
        this.sortedKeys = new ArrayList<>(frequencyMap.keySet());
        Collections.sort(this.sortedKeys);
    }
    public Text leastFrequent(Predicate<String> filterFn) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (Integer freq : this.sortedKeys) {
            List<String> list = this.frequencyMap
                    .get(freq)
                    .stream()
                    .filter(filterFn.negate())
                    .collect(Collectors.toList());
            if (list.isEmpty()) { continue; }
            stringBuilder
                    .append(freq)
                    .append(" vez(es): ")
                    .append(String.join(", ", list))
                    .append("\n");
            count += 1;
            if (count == 5) { break; }
        }
        return new Text(stringBuilder.toString());
    }

    public Text mostFrequent() {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (int i = sortedKeys.size() - 1; i >= 0; i--) {
            int freq = sortedKeys.get(i);
            for (String w : frequencyMap.get(freq)) {
                if (StringUtils.isStopword(w)) { continue; }
                stringBuilder
                        .append(w)
                        .append(" - ")
                        .append(freq)
                        .append(" vez(es)\n");
                count += 1;
                if (count == 5) { break; }
            }
            if (count == 5) { break; }
        }
        return new Text(stringBuilder.toString());
    }
}
