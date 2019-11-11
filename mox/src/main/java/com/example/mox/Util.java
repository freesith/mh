package com.example.mox;

import java.util.List;

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
}
