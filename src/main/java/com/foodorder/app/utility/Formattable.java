package com.foodorder.app.utility;

import java.util.List;

public interface Formattable {
    List<String> getColumns();

    List<String> getValues();

    String getTitle();
}   