package org.example.enums;

public enum DescriptionLengthEnum {
    BETWEEN_1_AND_10,
    BETWEEN_11_AND_20,
    BETWEEN_21_AND_30,
    BETWEEN_31_AND_40,
    BETWEEN_40_AND_50,
    MORE_THAN_50;

    public static DescriptionLengthEnum from(String description) {
        int length = description.split(" ").length;
        if (length < 10) {
            return DescriptionLengthEnum.BETWEEN_1_AND_10;
        }
        if (length < 20) {
            return DescriptionLengthEnum.BETWEEN_11_AND_20;
        }
        if (length < 30) {
            return DescriptionLengthEnum.BETWEEN_21_AND_30;
        }
        if (length < 40) {
            return DescriptionLengthEnum.BETWEEN_31_AND_40;
        }
        if (length < 50) {
            return DescriptionLengthEnum.BETWEEN_40_AND_50;
        }
        return DescriptionLengthEnum.MORE_THAN_50;
    }
}
