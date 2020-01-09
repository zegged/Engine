package edu.IR.Engine.nlp;

public class DocumentData {

    Integer docID;
    int docTF;
    String mostPopularTerm;
    Integer mostPopulatFrequency;
    int numOFsentences;
    int numofterms;
    String strID;


    public DocumentData(Integer intID, String mostPopularTerm, int mostPopular_tf,int numOFsentences,int numofterms, String strID) {
        docID=intID;
        this.mostPopularTerm=mostPopularTerm;
        this.mostPopulatFrequency=mostPopular_tf;
        this.numOFsentences=numOFsentences;
        this.numofterms=numofterms;
        this.strID = strID;
    }

    public String toString() {
        //return docID + "> |mostPopular:"+mostPopularTerm + " |freq:"+mostPopulatFrequency + " |numOfS:"+numOFsentences+ " |numOfterms:"+numofterms;
        return docID+":"+mostPopularTerm+":"+mostPopulatFrequency+":"+numOFsentences+":"+numofterms+":"+strID;
    }



}
