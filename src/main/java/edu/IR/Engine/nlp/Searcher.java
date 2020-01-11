package edu.IR.Engine.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Searcher {

    StanfordCoreNLP stanfordCoreNLP;
    Map<String, String> mapTerms;
    Map<Integer, String> mapDocs;

    public Searcher() {
        System.out.println("init searcher");
        mapTerms = new HashMap<>();
        mapDocs = new HashMap<>();
        stanfordCoreNLP = Pipeline.getPipeline();
    }


    //todo:
    //pointer to each term
    //

    public void buildSVD() {

        //TODO: get all terms from post

    }

    public Map<String, Double> runSingleQuery(String str) throws Exception {
        TermSearch termSearch = getTerm(str);
        List<DocumentData> documentData = getDocStats(termSearch);
        Ranker ranker = new Ranker(termSearch, documentData);
        Map<String, Double> map = ranker.get_all_ranked_document();
        return map;
    }

    private List<CoreSentence> breakSentences(String text) {
        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> sentences = coreDocument.sentences();
        return sentences;
    }

    public Map<String, Double> runQuery(String query) throws Exception {


        Map<String, Double> fullMap = new HashMap<>();

        //parse query in NLP
        CoreDocument coreDocument = new CoreDocument(query);
        List<CoreSentence> sentences = breakSentences(query);
        List<CoreLabel> coreLabelList = sentences.get(0).tokens();
        Integer querySize = coreLabelList.size();

        for (CoreLabel coreLabel : coreLabelList) {
            String token = coreLabel.originalText();
            //String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            //String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            Map<String, Double> map = runSingleQuery(token);
            //invert map to merge by String
//            Map<String, Double> invMap = map.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

            map.forEach((k, v) -> fullMap.merge(k, v, (v1, v2) -> v1 + v2));
            //fullMap.putAll(map);
        }
        //invert map to sort by Double
        //before sort
//        Map<Double, String> sigmaMap = fullMap.entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//        Map<Double, String> ascSortedMap = new TreeMap();
//        ascSortedMap.putAll(sigmaMap);

        return fullMap;
    }

    public void writeQueryResult(Map<String, Double> scores, Integer queryID) throws IOException {
        boolean append = true;
        FileWriter fw = new FileWriter("d:\\documents\\users\\razyal\\Documents\\posting\\yesStem\\Qresults.txt", append);
        BufferedWriter bw = null;
        bw = new BufferedWriter(fw);



        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            Double score = entry.getValue();
            String docID = entry.getKey();
            bw.write(queryID + " 0 " + docID + " " + score + " 0.0 mt \n");


        }
        bw.close();
    }

    public void rank(List<DocumentData> documentData) {
        //Ranker ranker = new Ranker()

        for (DocumentData documentData1 : documentData) {
            double documentScore = bm25(documentData1);

        }

    }

    public double bm25(DocumentData documentData) {

        Integer D = documentData.numofterms; // length of the document D in words


        return 0.0;
    }


    public List<DocumentData> getDocStats(TermSearch termSearch) throws Exception {
        //TODO: make separate func
        List<DocumentData> documentDataList = new ArrayList<>();
        for (TermData termData : termSearch.termData) {
            int docID = termData.document;
            int docTF = termData.frequency;

           // DocumentData documentData = searchDocument(docID);
            DocumentData documentData = getDoc(docID);
            documentData.docTF = docTF;
            documentDataList.add(documentData);
        }
        return documentDataList;
    }


    public DocumentData getDoc(Integer doc) {

//        if (mapTerms.containsKey(doc)){
//            return new TermSearch(doc, mapDocs.get(doc));
//        }
        String value1 = mapDocs.get(doc);
        //System.out.println("doc found");
        //System.out.println(line);

        //Integer intID, String mostPopularTerm, int mostPopular_tf,int numOFsentences,int numofterms
        String[] stats = value1.split(":");

        String mostPopularTerm = stats[0];
        Integer mostPopular_tf = Integer.valueOf(stats[1]);
        Integer numOFsentences = Integer.valueOf(stats[2]);
        Integer numofterms = Integer.valueOf(stats[3]);
        String strID = stats[4];

        DocumentData documentData = new DocumentData(doc, mostPopularTerm, mostPopular_tf, numOFsentences, numofterms, strID);
        return documentData;
    }


    public TermSearch getTerm(String term) {

        if (mapTerms.containsKey(term)) {
            return new TermSearch(term, mapTerms.get(term));
        }

        return new TermSearch("TERM-NOT-FOUND", "");
    }

    public TermSearch searchTerm(String term) throws Exception { //DELETE?

        System.out.println("searching for term " + term);

        //String path1 = getPath("final");

        String path1 = "C:\\posting\\post.txt";

        BufferedReader firstFile = new BufferedReader(new FileReader(path1));

        // List<TermStats> dic = new ArrayList<>();

        String line;

        //dicNumTerms=0;
        //numUniq=0;
        while ((line = firstFile.readLine()) != null) {
            Integer index1 = line.indexOf(':');
            String term1 = line.substring(0, index1);
            if (term1.equals(term)) {
                String value1 = line.substring(index1 + 1);

                ///TODO: return termSearch
                TermSearch termSearch = new TermSearch(term1, value1);


                return termSearch;
            }
//            TermStats termStats = new TermStats(term1, value1);
//            dic.add(termStats);
//            //dicNumTerms++;
//            if (termStats.tf==1){
//                numUniq++;
//            }

        }
//        StringBuilder stringBuilder = new StringBuilder();
//        for (TermStats termStats : dic){
//            stringBuilder.append(termStats).append(System.lineSeparator());
//        }
//        String path = getPath("dic");
//        System.out.println(stringBuilder);
//        firstFile.close();
//        writeToFile(stringBuilder.toString(),path);


        ////////////TERM-NOT-FOUND///////////////////
        return new TermSearch("TERM-NOT-FOUND", "");
    }


    public DocumentData searchDocument(int doc) throws Exception {

        //System.out.println("searching for doc " + doc);

        //String path1 = getPath("final");

        String path1 = "C:\\posting\\documents.txt";

        BufferedReader firstFile = new BufferedReader(new FileReader(path1));

        List<TermStats> dic = new ArrayList<>();
        String line;

        //dicNumTerms=0;
        //numUniq=0;


        //skip 2?
        //todo: fix to 1
        line = firstFile.readLine();
        line = firstFile.readLine();

        while ((line = firstFile.readLine()) != null) {
            Integer index1 = line.indexOf(':');
            String doc1 = line.substring(0, index1);
            if (doc1.equals(String.valueOf(doc))) {
                String value1 = line.substring(index1 + 1);
                //System.out.println("doc found");
                //System.out.println(line);

                //Integer intID, String mostPopularTerm, int mostPopular_tf,int numOFsentences,int numofterms
                String[] stats = value1.split(":");

                String mostPopularTerm = stats[0];
                Integer mostPopular_tf = Integer.valueOf(stats[1]);
                Integer numOFsentences = Integer.valueOf(stats[2]);
                Integer numofterms = Integer.valueOf(stats[3]);
                String strID = stats[4];

                DocumentData documentData = new DocumentData(doc, mostPopularTerm, mostPopular_tf, numOFsentences, numofterms, strID);
                return documentData;

                //TermSearch termSearch = new TermSearch(term1, value1);


                //return termSearch;
            }
//            TermStats termStats = new TermStats(term1, value1);
//            dic.add(termStats);
//            //dicNumTerms++;
//            if (termStats.tf==1){
//                numUniq++;
//            }

        }
//        StringBuilder stringBuilder = new StringBuilder();
//        for (TermStats termStats : dic){
//            stringBuilder.append(termStats).append(System.lineSeparator());
//        }
//        String path = getPath("dic");
//        System.out.println(stringBuilder);
//        firstFile.close();
//        writeToFile(stringBuilder.toString(),path);


        ////////////TERM-NOT-FOUND///////////////////
        //  return new TermSearch("TERM-NOT-FOUND", "");
        //Integer intID, String mostPopularTerm, int mostPopular_tf,int numOFsentences,int numofterms
        return new DocumentData(0, "", 0, 0, 0, "");
    }


    public void loadDictionary() throws IOException {

        //String path1 = getPath("final");
        String path1 = "d:\\documents\\users\\razyal\\Documents\\posting\\yesStem\\post.txt";

        FileReader fileReader = new FileReader(path1);
        BufferedReader firstFile = new BufferedReader(fileReader);

        String line;
        while ((line = firstFile.readLine()) != null) {
            Integer index1 = line.indexOf(':');
            String term1 = line.substring(0, index1);
            String value1 = line.substring(index1 + 1);

            mapTerms.put(term1, value1);


        }
        firstFile.close();
        fileReader.close();
    }

    public void loadDocuments() throws IOException {
        //String path1 = getPath("final");
        String path1 = "d:\\documents\\users\\razyal\\Documents\\posting\\yesStem\\documents.txt";

        FileReader fileReader = new FileReader(path1);
        BufferedReader firstFile = new BufferedReader(fileReader);

        String line;
        //todo: fix this
        line = firstFile.readLine();
        line = firstFile.readLine();


        while ((line = firstFile.readLine()) != null) {
            Integer index1 = line.indexOf(':');
            String term1 = line.substring(0, index1);
            String value1 = line.substring(index1 + 1);

            mapDocs.put(Integer.valueOf(term1), value1);


        }
        firstFile.close();
        fileReader.close();

    }

    public void runFileQueries(String path1) throws Exception {
//        String path1 = "d:\\documents\\users\\razyal\\Documents\\posting\\yesStem\\queries.txt";

        ReadFile readFile = new ReadFile();
        String text = readFile.openQueryFile(path1);


        //FileReader fileReader = new FileReader(path1);
        //BufferedReader firstFile = new BufferedReader(fileReader);

        String line;
      //  line = firstFile.readLine();
//        while ((line = firstFile.readLine()) != null) {
//
//        }


        IRQuery[] fileDocs = readFile.parseQueryFile(text);


        for (IRQuery irQuery : fileDocs){
            Map<String, Double> scores =  runQuery(irQuery.title);

            //add id
            //save to stringFile

            writeQueryResult(scores, irQuery.id);

        }


    }
}
