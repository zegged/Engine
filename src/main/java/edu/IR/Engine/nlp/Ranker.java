package edu.IR.Engine.nlp;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Ranker {

    public static final int N = 472525; //corpus size -> number of documents
    public static int avgDoc = 200; //Average length of document in corpus.???????
    double avgdl; //average document length
    TermSearch term;
    SortedMap<String, Double> all_doc_returns;
    List<DocumentData> list_of_all_relevant_doc;
    double tf = 0;
    double numberOfDocuments = 0;
    double docLength = 0;
    double averageDocumentLength = 0;
    double queryFrequency = 0;
    double documentFrequency = 0;


    public Ranker(TermSearch term, List<DocumentData> list_of_all_relevant_doc) {
        this.avgdl = avgdl;
        this.term = term;
        this.list_of_all_relevant_doc = list_of_all_relevant_doc;
        this.all_doc_returns = new TreeMap<String, Double>();
    }

    public Map get_all_ranked_document() {
        for (int i = 0; i < list_of_all_relevant_doc.size(); i++) {
            this.tf = list_of_all_relevant_doc.get(i).docTF;
            this.numberOfDocuments = N;
            this.docLength = list_of_all_relevant_doc.get(i).numofterms;
            this.averageDocumentLength = 200;
            this.documentFrequency = list_of_all_relevant_doc.size();
            double score = score(tf, numberOfDocuments, docLength, averageDocumentLength, 0, documentFrequency);
            all_doc_returns.put(list_of_all_relevant_doc.get(i).strID, score);
        }
        //sort the map - UPDATE: by string keys
        Map ascSortedMap = new TreeMap();
        ascSortedMap.putAll(all_doc_returns);
        return ascSortedMap;
    }

    /**
     * Uses BM25 to compute a weight for a term in a document.
     *
     * @param tf The term frequency in the document
     * @param numberOfDocuments number of documents
     * @param docLength the document's length
     * @param averageDocumentLength average document length
     * @param documentFrequency Number of documents with term t in it
     * @return the score assigned to a document with the given
     * <p>
     * tf and docLength, and other preset parameters
     * הנוסחה של bm25
     */

    // check the best parameters
    private double k_1 = 2.0;//1.2d ???????????????????????????????
    private double k_3 = 8d;//parameter to check term frequency
    public static final double b = 0.75;

    public final double score(double tf, double numberOfDocuments, double docLength, double averageDocumentLength, double queryFrequency, double documentFrequency) {
//        numberOfDocuments=N;
//        double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
//        double first = (((k_1 + 1d) * tf) / (K + tf));    //first part
//        double second = (((k_3 + 1) * queryFrequency) / (k_3 + queryFrequency));    //second part
//        double weight = first * second;
//        // multiply the weight with idf
//        double idf = weight * Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));
//        return idf;
        numberOfDocuments = N;
        double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
        double first = (((k_1 + 1d) * tf) / (K + tf));    //first part
        double idf = Math.log(numberOfDocuments / documentFrequency);
        double rank = first * idf;
        return rank;
    }

    public static void semantic() {
        try {
            Word2VecModel model = Word2VecModel.fromTextFile(new File("word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = model.forSearch();
            int results = 10;
            List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches("college", results);
            for (com.medallia.word2vec.Searcher.Match match : matches) {
                System.out.println(match.match()+"+"+match.distance());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.medallia.word2vec.Searcher.UnknownWordException e) {
            e.printStackTrace();

        }
    }
    public static void main(String args[]) throws Exception {
        semantic();
    }


}
