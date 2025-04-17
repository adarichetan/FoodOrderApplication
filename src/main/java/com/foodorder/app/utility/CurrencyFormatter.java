package com.foodorder.app.utility;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private CurrencyFormatter() {
    }
    private static final NumberFormat defaultFormatter;

    static {
        Locale india = new Locale("en", "IN");
        defaultFormatter = NumberFormat.getCurrencyInstance(india);
    }

    public static String format(double amount) {
        return defaultFormatter.format(amount);
    }
}
