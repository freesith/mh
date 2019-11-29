package com.freesith.manhole;

import java.util.List;
import java.util.Set;

public class Util {

    public static String join(List list, CharSequence delimiter) {
        if (list == null) {
            return "";
        }
        int size = list.size();
        if (list.size() == 1) {
            return String.valueOf(list.get(0));
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                builder.append(list.get(i));
            } else {
                builder.append(delimiter);
                builder.append(list.get(i));
            }
        }
        return builder.toString();
    }


    public static String setToSelection(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (String caseName : set) {
            if (!first) {
                builder.append(",");
            } else {
                first = false;
            }
            builder.append("'").append(caseName).append("'");
        }
        return builder.toString();
    }


}
