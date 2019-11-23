package edu.IR.Engine.nlp;

//import javafx.util.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Engine {
    public static void main(String args[]) throws Exception {
        System.out.println("init main");

        //read corpus
        ReadFile readFile = new ReadFile();
        String pathToCorpus = "test"; //CORPUS folder
        String text = null;
        text = readFile.openFile(pathToCorpus);

        String postingFile = "c:\\posting\\test.txt";

        //xml to documents
        IRDocument[] fileDocs = readFile.parseXML(text);





        //parse documents
        Parse parser = new Parse();
        for (IRDocument doc : fileDocs){
            parser.parseDocument(doc);
        }



        // print interate terms in buffer
        //deleted
        /*
        for (Map.Entry<String,List<Pair<Integer,Integer>>> entry : Indexer.terms.entrySet()) {
            String term = entry.getKey();
            List<Pair<Integer,Integer>> value = entry.getValue();

            System.out.println(term + " : " + value);

            //dump buffer
            if(Indexer.termsPointers.containsKey(term)){
                Pair<Integer,Integer> points = Indexer.termsPointers.get(term);
                Integer begin = points.getKey();
                Integer offset = points.getValue();


            }
            else{

                //add to bottom of list
            }


        }


*/

        // print docs
        for (String doc : Indexer.docs){
            System.out.println(doc);
        }







        //docs to db
        //deleted
        /*
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
        }*/



        System.out.println("done");


    }
}
