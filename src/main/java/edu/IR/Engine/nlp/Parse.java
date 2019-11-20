package edu.IR.Engine.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import javafx.util.Pair;

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

        // parse docID
        String stringID = doc.id.split("-",2)[1].trim();
        Integer intID = Integer.valueOf(stringID);
        System.out.println("doc: " + intID);


        // STATISTICS INIT
        String mostPopularTerm = ""; //most popular term
        int mostPopular_tf = 0; //most popular term frequency
        int uniqueTermsInDocument; //amount of unique terms


        // STANFORD NLP PARSE
        List<CoreSentence> sentences = breakSentences(doc.text);

        //temp dictionary for parse
        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<String, Integer>();



        for (CoreSentence sentence : sentences){
            //System.out.println(index++ + " : " + sentence.toString());
            List<CoreLabel> coreLabelList = sentence.tokens();

            for (CoreLabel coreLabel : coreLabelList){
                // PARSE HERE
                String token = coreLabel.originalText();
                String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);


                String term = token;


                // PARSE DONE
                // SAVE TERM IN TEMP DICTIONARY

                int term_frequency = 1;

                if (dictionary.containsKey(term)){
                    term_frequency = dictionary.get(term) + 1;
                    dictionary.put(term,term_frequency);
                }
                else{
                    dictionary.put(term,1);
                }

                // STATISTICS

                if (term_frequency>mostPopular_tf){
                    mostPopular_tf=term_frequency;
                    mostPopularTerm=term;
                }




            }



            //System.out.println("///////////////END OF DOC////////////////////");
        }


        // ADD TERMS TO INDEXER
        for (Map.Entry<String,Integer> entry : dictionary.entrySet()) {
            String term = entry.getKey();
            Integer value = entry.getValue();

            //System.out.println(term + " -> " + value);
            if (Indexer.terms.containsKey(term)){
                Indexer.terms.get(term).add(new Pair<Integer, Integer>(intID,value));
            }
            else{
                List<Pair<Integer,Integer>> termList = new ArrayList<>();
                termList.add(new Pair<Integer, Integer>(intID,value));
                Indexer.terms.put(term,termList);
            }
        }


        // ADD DOC DATA TO INDEXER



        String docData = intID + "^" + mostPopularTerm + "^" + mostPopular_tf ;
        Indexer.docs.add(docData);
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
