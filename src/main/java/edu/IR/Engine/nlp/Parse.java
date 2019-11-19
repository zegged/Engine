package edu.IR.Engine.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Parse {
    StanfordCoreNLP stanfordCoreNLP;
    public Parse(){
        System.out.println("Parse init");
        stanfordCoreNLP = Pipeline.getPipeline();
    }

    public void parseDocument(IRDocument doc){
        System.out.println(doc.id);
        //System.out.println("breaking sentences");
        List<CoreSentence> sentences = breakSentences(doc.text);

        //dictionary
        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<String, Integer>();


        int index = 0; //word counter in sentence?
        for (CoreSentence sentence : sentences){
            //System.out.println(index++ + " : " + sentence.toString());
            List<CoreLabel> coreLabelList = sentence.tokens();

            for (CoreLabel coreLabel : coreLabelList){
                // PARSE HERE
                String token = coreLabel.originalText();
                String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);




                if (dictionary.containsKey(token)){
                    dictionary.put(token,dictionary.get(token)+1);
                }
                else{
                    dictionary.put(token,1);
                }
                //System.out.println(token + " = " + pos + " | " + ner);
            }



            //System.out.println("////////////////////////////////////////////");
        }


        //System.out.println(dictionary.toString());

        /// https://www.javacodegeeks.com/2017/09/java-8-sorting-hashmap-values-ascending-descending-order.html


        //Map<String, Integer> sorted = Indexer.sortDictionary(dictionary);


        //String id = doc.id;


        doc.terms=dictionary;

        //pos
        //stem
        //lemme
        //stop
        //named entity rec.

        //tracking words in sentence

        //return doc;

    }




private List<CoreSentence> breakSentences(String text){


    CoreDocument coreDocument = new CoreDocument(text);

    //System.out.println("annotate");
    stanfordCoreNLP.annotate(coreDocument);
    //System.out.println("annotate done");
    List<CoreSentence> sentences = coreDocument.sentences();

    /*
    for (CoreSentence sentence : sentences){
        System.out.println(sentence.toString());

    }

     */
    return sentences;
}

    public void sortDocument(IRDocument doc) {
        sortByValueDec(doc);
        sortByKey(doc);
        //System.out.println(doc.sortedTerms);
        //System.out.println(doc.sortedValues);
        findMaxOccurrences(doc);

    }

    private void findMaxOccurrences(IRDocument document){
        //System.out.println("in computeMaxOccurrences");
        /*document.sortedValues.forEach((term, frequency) -> {
            System.out.println(term + " => " + frequency);
        });*/

        Map.Entry<String, Integer> topOnList = document.sortedValues.entrySet().iterator().next();
        document.mostPopular = topOnList.getKey();
        document.mostPopular_tf = topOnList.getValue();

        System.out.println("MaxOccurrences: " + document.mostPopular + " -> " + document.mostPopular_tf);

    }

    private void sortByValueDec(IRDocument document){
        document.sortedValues = document.terms
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    private void sortByKey(IRDocument document){
        // https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html#ixzz65ZleZ7QA
        document.sortedTerms = document.terms
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));
    }







    private Map<String, Integer> sortDictionary(Map<String,Integer> dic){
        //https://www.javacodegeeks.com/2017/09/java-8-sorting-hashmap-values-ascending-descending-order.html
        System.out.println("map before sorting: " + dic);

        // let's sort this map by values first
        LinkedHashMap<String, Integer> sorted = dic
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println("map after sorting by values: " + sorted);

        // above code can be cleaned a bit by using method reference
        sorted = dic
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        // now let's sort the map in decreasing order of value
        sorted = dic
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println("map after sorting by values in descending order: "
                + sorted);

        return  sorted;
    }
}
