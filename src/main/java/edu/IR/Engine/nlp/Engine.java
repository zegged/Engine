package edu.IR.Engine.nlp;

import javafx.util.Pair;

import java.util.*;

public class Engine {
    public static void main(String args[]) throws Exception {
        System.out.println("init main");

        //read corpus
        ReadFile readFile = new ReadFile();
        String pathToCorpus = "test"; //CORPUS folder
        String text = null;
        text = readFile.openFile(pathToCorpus);

        //xml to documents
        IRDocument[] fileDocs = readFile.parseXML(text);



        //posting(terms) cache memory
        Map<String, Integer> db = new LinkedHashMap<String, Integer>();
        //term^documentFrequency^termFrequency^ DocID-tf ^ DocID-tf ^


        // term, List: doc,tf
        Map<String, List<Pair<Integer,Integer>>> termSorted = new TreeMap<>();
        //Documents data



        //parse documents
        Parse parser = new Parse();
        for (IRDocument doc : fileDocs){
            //IRDocument unsortedDoc = parser.parseDocument(doc);
            //db = Indexer.mergeMaps(db, unsortedDoc.terms);
            parser.parseDocument(doc);
            parser.sortDocument(doc);
        }

        //add to docs metadata
        List<String> docsMetadata = new ArrayList<>();
        for (IRDocument doc : fileDocs){
            String line;
            line = doc.id.concat("^").concat(doc.mostPopular).concat("^").concat(String.valueOf(doc.mostPopular_tf)).concat("\n");
            docsMetadata.add(line);
        }
        System.out.println(docsMetadata);

        System.out.println("Size: " + db.size());


        String pathToPosting = "";
        Indexer indexer = new Indexer(pathToPosting);


        //docs to db
        String dataToFile = "";
        for (Map.Entry<String, Integer> entry : db.entrySet()) {
            String id = entry.getKey();
            Integer value = entry.getValue();
            String sValue = String.valueOf(value);
            String expression = id.concat("^").concat(sValue).concat("\n");
            //System.out.println(expression);
            dataToFile = dataToFile.concat(expression);
            //System.out.println(dataToFile);
            //System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
        }

        System.out.println("data:");
        System.out.println(dataToFile);
        indexer.writeToFile(dataToFile);

        //DocumentParsed unsortedDoc = parser.parseDocument(fileDocs[0]);
        //DocumentParsed unsortedDoc2 = parser.parseDocument(fileDocs[1]);

        //Indexer.mergeMaps(unsortedDoc.getTerms(),unsortedDoc2.getTerms());

        //sort terms
        //unsortedDoc.sort();

        //DocumentParsed sortedDoc = unsortedDoc;

        //calc max occ
        //sortedDoc.computeMaxOccurrences();



        //add document data to indexer



    }
}
