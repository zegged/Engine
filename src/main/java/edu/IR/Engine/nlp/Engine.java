//
//package edu.IR.Engine.nlp;
//
////import javafx.util.Pair;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class Engine {
//    static Map<String,String> stopword ;
//    public static void main(String args[]) throws Exception {
//        System.out.println("init main");
//
//        //read corpus
//        ReadFile readFile = new ReadFile();
//        String pathToCorpus = "test"; //CORPUS folder
//        String text = null;
//        text = readFile.openFile(pathToCorpus);
//
//        //String postingFile = "c:\\posting\\test.txt";
//        //String postingFile = "C:\\Users\\Razi\\Desktop\\ehzor\\corpus\\FB396001\\FB396001";
//
//        //xml to documents
//        IRDocument[] fileDocs = readFile.parseXML(text);
//
//
//        //parse documents
//
//        File pathofstopword=new File("\\\\stop_words.txt");
//        String []stops=(readFile.readStopword(pathofstopword));
//        stopword = new HashMap<>();// why save stop?
//        for(int i=0;i<stops.length;i++)
//        {
//            stopword.put(stops[i],"");
//        }
//        boolean stamming=true;
//        Parse parser = new Parse(stopword,stamming);
//        for (IRDocument doc : fileDocs){
//            parser.parseDocument(doc);
//        }
//
//
//
//        // print interate terms in buffer
//        //deleted
//        /*
//        for (Map.Entry<String,List<Pair<Integer,Integer>>> entry : Indexer.terms.entrySet()) {
//            String term = entry.getKey();
//            List<Pair<Integer,Integer>> value = entry.getValue();
//
//            System.out.println(term + " : " + value);
//
//            //dump buffer
//            if(Indexer.termsPointers.containsKey(term)){
//                Pair<Integer,Integer> points = Indexer.termsPointers.get(term);
//                Integer begin = points.getKey();
//                Integer offset = points.getValue();
//
//
//            }
//            else{
//
//                //add to bottom of list
//            }
//
//
//        }
//
//
//*/
//
//        // print docs
//        for (String doc : Indexer.docs){
//            System.out.println(doc);
//        }
//
//
//
//
//
//
//
//        //docs to db
//        //deleted
//        /*
//        String dataToFile = "";
//        for (Map.Entry<String, Integer> entry : db.entrySet()) {
//            String id = entry.getKey();
//            Integer value = entry.getValue();
//            String sValue = String.valueOf(value);
//            String expression = id.concat("^").concat(sValue).concat("\n");
//            //System.out.println(expression);
//            dataToFile = dataToFile.concat(expression);
//            //System.out.println(dataToFile);
//            //System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
//        }*/
//
//
//
//        System.out.println("done");
//
//
//    }
//}

package edu.IR.Engine.nlp;
import java.io.File;
import java.util.*;
public class Engine {
    static Map<String,String> stopword ;
    public static void main(String args[]) throws Exception {
        System.out.println("init main");

        //read corpus
        ReadFile readFile = new ReadFile();
        String pathToCorpus = "C:\\Users\\Razi\\Desktop\\ehzor\\corpus\\"; //CORPUS folder
        //String postingFile = "C:\\Users\\Razi\\Desktop\\ehzor\\corpus\\FB396001\\FB396001";
        String postingFilePath = "C:\\Users\\Razi\\Desktop\\ehzor\\posting";
        Indexer indexer = new Indexer(postingFilePath);

        List<String> files = readFile.getAllFiles(pathToCorpus);

        Integer courpus_size = files.size();
        // FOR DEBUG ONLY
        Integer fileCounter = 0;
        for (int i =0; i<0; i++){
            files.remove(0);
            fileCounter++;
        }

        String text = null;
        File pathofstopword=new File("\\\\stop_words.txt");
        String []stops=(readFile.readStopword(pathofstopword));
        stopword = new HashMap<>();// why save stop?
        for(int i=0;i<stops.length;i++)
        {
            stopword.put(stops[i],"");
        }
        boolean stamming=true;
        Parse parser = new Parse(stopword,stamming);
//        for (IRDocument doc : fileDocs){
//            parser.parseDocument(doc);
//        }

        //Parse parser = new Parse();



        for (String filePath : files) {


            double percent = (0.0 +  ++fileCounter ) / courpus_size*100;
            System.out.println(String.format("%.2f", percent)  + "% File: " + fileCounter + " " + filePath);

            text = readFile.openFile(filePath);


            //xml to documents
            IRDocument[] fileDocs = readFile.parseXML(text);


            //parse documents

            System.out.println("parsing");
            int cnt=0;
            if (true) {
                for (IRDocument doc : fileDocs) {
                    cnt++;
                    if(cnt==20){
                        System.out.println("s");
                    }
                    ParseResult parseResult = parser.parseDocument(doc);
                    DocumentData documentData = parseResult.documentData;
                    DocumentTerms documentTerms = parseResult.documentTerms;
                    documentTerms.sort();
                    indexer.addTerms(documentTerms, documentData.docID);
                    indexer.addDocument(documentData);

                    if (indexer.isMemoryFull()) {
                        indexer.savePosting();
                    }

                }
            }

        }
        ///final dump
        indexer.savePosting();
        // merge sort - LIMITED to file size (logical,virtual,string,terms,lists)
        indexer.merge();

        System.out.println("Term Dictionary:");




        //String fileToSave = "c:\\posting\\test3.txt";



        // TEST FOR POINTERS - WORKS
        /*
        String term = "turn";
        String fileNameTerm = Indexer.termsPointers.get(term).m_pointers.get(0).first;
        Long plStart = Indexer.termsPointers.get(term).m_pointers.get(0).second;
        Long plEnd = Indexer.termsPointers.get(term).m_pointers.get(0).third;
        indexer.findTerm(fileNameTerm,plStart,plEnd);
        */



        // print docs
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Document Dictionary:");
        for (String doc : Indexer.docs){
            //System.out.println(doc);
        }




        //docs to db
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

        System.out.println("a".compareTo("b"));

    }




}
