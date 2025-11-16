package org.example.enums;

public enum EntryTypeEnum {
    MOVIE,
    TV_SHOW;

    public static EntryTypeEnum from(String type) {
        type = type.trim();
        if (type.equals("Movie")) {
            return MOVIE;
        } else if (type.equals("TV Show")) {
            return TV_SHOW;
        } else {
            throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }
    }
}
