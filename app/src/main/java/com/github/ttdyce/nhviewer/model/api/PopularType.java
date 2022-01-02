package com.github.ttdyce.nhviewer.model.api;

import java.util.HashMap;
import java.util.Map;

public enum PopularType {
    none, allTime, month, week, today;

    @SuppressWarnings("UnusedAssignment")
    public static PopularType get(int which) {
        Map<Integer, PopularType> typeOptions = new HashMap<>();
        int index = 0;
        typeOptions.put(index++, none);
        typeOptions.put(index++, allTime);
        typeOptions.put(index++, month);
        typeOptions.put(index++, week);
        typeOptions.put(index++, today);

        if(typeOptions.containsKey(which))
            return typeOptions.get(which);
        else
            return none;
    }
}
