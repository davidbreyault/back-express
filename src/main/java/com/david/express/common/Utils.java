package com.david.express.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class Utils {

    /**
     * Méthode utilitaire pour créer le Pageable avec les paramètres de tri.
     */
    public static Pageable createPaging(int page, int size, String[] sort) {
        // ?sort=column1,direction1 => array of 2 elements : [“column1”, “direction1”]
        // ?sort=column1,direction1&sort=column2,direction2 => array of 2 elements : [“column1, direction1”, “column2, direction2”]
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Tri selon plusieurs champs (sortOrder = "field, direction")
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(Sort.Direction.fromString(_sort[1]), _sort[0]));
            }
        } else {
            // Tri selon un seul champ (sortOrder = "field, direction")
            orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }

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
