package edu.IR.Engine.nlp;

import java.util.*;
public class DocumentTerms {
    HashMap<String, Integer> dictionary;
    List<String> sortedTerms;
    int term_frequency;

    public DocumentTerms(){

        dictionary = new LinkedHashMap<String, Integer>();
        term_frequency=1;
    }

    public void add(String term) {
        if (dictionary.containsKey(term)){
            term_frequency = dictionary.get(term) + 1;
            dictionary.put(term,term_frequency);
        }
        else{
            term_frequency=1;
            dictionary.put(term,1);
        }
    }

    public int checkMostPopular() {
        // STATISTICS
        return this.term_frequency;

    }

    public void deleteLastTerm(String s){
        dictionary.remove(s);
    }

    public void sort() {
        List sortedKeys=new ArrayList(dictionary.keySet());
        Collections.sort(sortedKeys);
        sortedTerms=sortedKeys;
    }
}
