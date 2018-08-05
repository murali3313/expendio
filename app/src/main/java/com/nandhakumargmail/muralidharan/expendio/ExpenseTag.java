package com.nandhakumargmail.muralidharan.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ExpenseTag {
    public ExpenseTag(HashMap<String, List<String>> tags) {
        this.tags = tags;
    }

    private HashMap<String, List<String>> tags = new HashMap<>();

    public void put(String word, List<String> tagWords) {
        tags.put(word, tagWords);
    }

    public List<String> get(String word) {
        return this.tags.get(word);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return tags.isEmpty();
    }
}
