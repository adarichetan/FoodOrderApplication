package com.foodorder.app.utility;

import java.util.List;

public class DataFormatter {
    private static final int CHAR_LIMIT = 18;
    private static final String COLUMN_FORMAT = "%-" + CHAR_LIMIT + "s";

    public static <T extends Formattable> void printTable(List<T> items) {
        if (items == null || items.isEmpty()) return;

        T sampleItem = items.get(0);
        System.out.println(ColourCodes.BRIGHT_BLUE + sampleItem.getTitle() + ColourCodes.RESET);

        printBorder(sampleItem.getColumns().size());
        printRow(sampleItem.getColumns(), ColourCodes.YELLOW);
        printBorder(sampleItem.getColumns().size());

        for (T item : items) {
            printRow(item.getValues(), "");
        }

        printBorder(sampleItem.getColumns().size());
    }

    private static void printRow(List<String> values, String valueColor) {
        StringBuilder sb = new StringBuilder();
        sb.append(ColourCodes.WINE_RED).append("|").append(ColourCodes.RESET);

        for (String value : values) {
            sb.append(" ")
                    .append(valueColor).append(String.format(COLUMN_FORMAT, value)).append(ColourCodes.RESET)
                    .append(ColourCodes.WINE_RED).append(" |").append(ColourCodes.RESET);
        }

        System.out.println(sb);
    }

    private static void printBorder(int columns) {
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < columns; i++) {
            sb.append("-".repeat(CHAR_LIMIT + 2)).append("+");
        }
        System.out.println(ColourCodes.WINE_RED+sb+ColourCodes.RESET);
    }
}
