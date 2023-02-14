package com.david.express.common;

import java.util.*;

public class Utils {

    public static HashMap<String, Integer> sortHashMapByValue(HashMap<String, Integer> unsortedHashMap) {
        // Création d'une liste à partir du HashMap reçu en paramètre
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedHashMap.entrySet());
        // Tri de la liste
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        // Injection des données depuis la liste triée dans un nouveau HashMap
        HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
        for(Map.Entry<String, Integer> item : list) {
            sortedHashMap.put(item.getKey(), item.getValue());
        }
        return sortedHashMap;
    }
}
