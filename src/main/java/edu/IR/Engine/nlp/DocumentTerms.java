package edu.IR.Engine.nlp;

import java.util.*;

public class DocumentTerms {
    HashMap<String, Integer> dictionary;
    List<String> sortedTerms;

    public DocumentTerms(){
        dictionary = new LinkedHashMap<String, Integer>();
    }

    public void add(String term) {
        Integer term_frequency;
        if (dictionary.containsKey(term)){
            term_frequency = dictionary.get(term) + 1;
            dictionary.put(term,term_frequency);
        }
        else{
            term_frequency=1;
            dictionary.put(term,1);
        }

        checkMostPopular(term,term_frequency);

    }

    private void checkMostPopular(String term, Integer term_frequency) {

    }

    public void sort() {
        List sortedKeys=new ArrayList(dictionary.keySet());
        //Collections.sort(sortedKeys);
        sortedTerms=sortedKeys;
    }
}
