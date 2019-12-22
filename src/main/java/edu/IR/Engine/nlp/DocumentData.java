package edu.IR.Engine.nlp;

public class DocumentData {
    Integer docID;
    String mostPopularTerm;
    Integer mostPopulatFrequency;


    public DocumentData(Integer intID, String mostPopularTerm, int mostPopular_tf) {
        docID=intID;
        this.mostPopularTerm=mostPopularTerm;
        this.mostPopulatFrequency=mostPopular_tf;
    }

    public String toString() {

        return docID + "> |mostPopular:"+mostPopularTerm + " |freq:"+mostPopulatFrequency;
    }



}
