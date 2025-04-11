package com.foodorder.app.utility;

import java.util.List;

public class DataFormatter {
    private static final int CHAR_LIMIT = 15;
    private static final int EXTRA_SPACING = 8;
    private static final String COLUMN_FORMAT = "%-" + (CHAR_LIMIT + EXTRA_SPACING) + "s";

    public static <T extends Formattable> void printTable(List<T> items) {
        if (items == null || items.isEmpty()) return;

        T sampleItem = items.get(0);
        System.out.println(ColourCodes.BLUE + sampleItem.getTitle() + ColourCodes.RESET);


        printRow(sampleItem.getColumns(), ColourCodes.PURPLE);

        for (T item : items) {
            printRow(item.getValues(), "");
        }

        int totalWidth = (CHAR_LIMIT + EXTRA_SPACING) * sampleItem.getColumns().size();
        System.out.println(ColourCodes.BLUE + "-".repeat(totalWidth) + ColourCodes.RESET);
    }

    private static void printRow(List<String> values, String color) {
        StringBuilder sb = new StringBuilder(color);
        for (String value : values) {
            sb.append(String.format(COLUMN_FORMAT, value));
        }
        sb.append(ColourCodes.RESET);
        System.out.println(sb);
    }
}
