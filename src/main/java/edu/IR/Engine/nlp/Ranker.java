package edu.IR.Engine.nlp;

public class Ranker {

    public static final int N = 472525; //corpus size -> number of documents
    public static int avgDoc = 200; //Average length of document in corpus.???????
    double avgdl; //average document length


    public Ranker(double avgdl) {
        this.avgdl = avgdl;
    }
    /**
     * Uses BM25 to compute a weight for a term in a document.
     *
     * @param tf The term frequency in the document
     * @param numberOfDocuments number of documents
     * @param docLength the document's length
     * @param averageDocumentLength average document length
     * @return the score assigned to a document with the given
     * <p>
     * tf and docLength, and other preset parameters
     * הנוסחה של bm25
     */

    private double k_1 = 2.0;//1.2d ???????????????????????????????
    private double k_3 = 8d;
    public static final double b = 0.75;


    public final double score(double tf, double numberOfDocuments, double docLength, double averageDocumentLength, double queryFrequency, double documentFrequency) {
        numberOfDocuments=N;
        double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
        double first = (((k_1 + 1d) * tf) / (K + tf));    //first part
        double second = (((k_3 + 1) * queryFrequency) / (k_3 + queryFrequency));    //second part
        double weight = first * second;
        // multiply the weight with idf
        double idf = weight * Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));
        return idf;

    }


//
//    public double BM25(int docLength, int ri, int ni, int R, int fi, int qfi)
//    {
//        double logNumerator = (ri+0.5)/(R-ri+0.5);
//        double logDenominator = (ni - ri + 0.5) / (N - ni - R + ri + 0.5);
//        if (logDenominator < 0)
//            logDenominator = 0.0000001;
//        double K = K1 * ((1 - b) + b * (docLength / avgdl));
//
//        //double log = Math.Log10(logNumerator / logDenominator);
//        double first = ((K1 + 1) * fi) / (K + fi);
//        double second = ((K2 + 1) * qfi) / (K2 + qfi);
//
//        //double result = log * first * second;
//
//
//        return 0.0;
//    }

}
