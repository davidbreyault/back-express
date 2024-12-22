package com.david.express.model.dto;

import com.david.express.common.Constants;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class PaginatedResponseDto<T> {

    @JsonIgnore
    private List<T> data;

    @JsonIgnore
    private String key;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("totalItems")
    private long totalItems;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("timestamp")
    private long timestamp;

    public PaginatedResponseDto(String key, List<T> data, int currentPage, long totalItems, int totalPages) {
        this.key = key != null ? key : Constants.DEFAULT_JSON_KEY;
        this.data = data;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Getter personnalisé pour sérialiser dynamiquement la Map dans le JSON
     * @return  Un Map avec la clé dynamique (par exemple "notes") et la liste associée
     */
    @JsonAnyGetter
    public Map<String, Object> getDynamicData() {
        Map<String, Object> map = new HashMap<>();
        map.put(this.key, this.data);
        return map;
    }

    /**
     * Setter pour 'data' : si 'key' est null, attribution d'une valeur par défaut
     * Lombok ne génère pas de setters ou getters pour des méthodes déjà définies manuellement
     * @param data
     */
    public void setData(List<T> data) {
        if (this.key == null) {
            // Valeur par défaut si 'key' est null
            this.key = Constants.DEFAULT_JSON_KEY;
        }
        this.data = data;
    }
}
