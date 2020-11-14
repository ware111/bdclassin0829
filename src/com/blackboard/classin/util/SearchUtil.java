package com.blackboard.classin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchUtil {
    public static List<String> getDifferListByMap(List<String> listA, List<String> listB) {
        List<String> differList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (String strA : listA) {
            map.put(strA, 1);
        }
        for (String strB : listB) {
            Integer value = map.get(strB);
            if (value != null) {
                map.put(strB, ++value);
                continue;
            }
            map.put(strB, 1);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                differList.add(entry.getKey());
            }
        }
        return differList;
    }
}
