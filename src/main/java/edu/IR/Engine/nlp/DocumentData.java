package edu.IR.Engine.nlp;

import java.util.List;

public class DocumentData {

    Integer docID;
    int docTF;
    String mostPopularTerm;
    Integer mostPopulatFrequency;
    int numOFsentences;
    int numofterms;
    String strID;
    List<String> list_of_best_terms;


    public DocumentData(Integer intID, String mostPopularTerm, int mostPopular_tf, int numOFsentences, int numofterms, String strID,List<String> list_of_best_terms) {
        docID=intID;
        this.mostPopularTerm=mostPopularTerm;
        this.mostPopulatFrequency=mostPopular_tf;
        this.numOFsentences=numOFsentences;
        this.numofterms=numofterms;
        this.strID = strID;
        this.list_of_best_terms=list_of_best_terms;
    }

    public String toString() {
        //return docID + "> |mostPopular:"+mostPopularTerm + " |freq:"+mostPopulatFrequency + " |numOfS:"+numOFsentences+ " |numOfterms:"+numofterms;
        String result="";
        for(String s:list_of_best_terms){
            result+=s+"^";
        }
        return docID+":"+mostPopularTerm+":"+mostPopulatFrequency+":"+numOFsentences+":"+numofterms+":"+strID+":"+result;
    }



}
