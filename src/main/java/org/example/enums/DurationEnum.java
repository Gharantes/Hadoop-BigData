package org.example.enums;

public enum DurationEnum {
    LESS_THAN_30_MINUTES,
    BETWEEN_30_MINUTES_AND_AN_HOUR,
    BETWEEN_ONE_AND_TWO_HOURS,
    MORE_THAN_TWO_HOURS,
    ONE_SEASON,
    TWO_SEASONS,
    THREE_SEASONS,
    FOUR_SEASONS,
    FIVE_OR_MORE_SEASONS;

    public static DurationEnum from(EntryTypeEnum type, String duration) {
        if (duration == null || duration.isEmpty()) {
            return null;
        }
        if (type.equals(EntryTypeEnum.MOVIE)) {
            int minutes = Integer.parseInt(duration.replace(" min", ""));
            if (minutes < 30) return LESS_THAN_30_MINUTES;
            if (minutes < 60) return BETWEEN_30_MINUTES_AND_AN_HOUR;
            if (minutes < 120) return BETWEEN_ONE_AND_TWO_HOURS;
            return MORE_THAN_TWO_HOURS;
        } else if (type.equals(EntryTypeEnum.TV_SHOW)) {
            int seasons = Integer.parseInt(duration.replace(" Seasons", "").replace(" Season", ""));
                if (seasons == 1) return ONE_SEASON;
                if (seasons == 2) return TWO_SEASONS;
                if (seasons == 3) return THREE_SEASONS;
                if (seasons == 4) return FOUR_SEASONS;
                return FIVE_OR_MORE_SEASONS;
        } else {
            throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }
    }
}
