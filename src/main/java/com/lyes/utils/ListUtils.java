package com.lyes.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {

    public static List<String> union(List<String> list1, List<String> list2) {
        HashSet<String> set = new HashSet<String>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<String>(set);
    }
}
