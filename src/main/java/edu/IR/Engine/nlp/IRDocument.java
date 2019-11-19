package edu.IR.Engine.nlp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class IRDocument {

    public String id;
    public String title;
    public String date;
    public String text;


    public Map<String, Integer> terms;
    public LinkedHashMap<String, Integer> sortedTerms;
    public LinkedHashMap<String, Integer> sortedValues;

    public String mostPopular; //most popular term
    public int mostPopular_tf; //most popular term frequency
    public int uniqueTermsInDocument;

    public IRDocument(String id, String title, String date, String text){
        this.id=id;
        this.title=title;
        this.date=date;
        this.text=text;
    }






}
