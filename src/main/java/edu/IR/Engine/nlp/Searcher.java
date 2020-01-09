package edu.IR.Engine.nlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    public Searcher(){
        System.out.println("init searcher");
    }


    //todo:
    //pointer to each term
    //

public void buildSVD(){

        //TODO: get all terms from post

}




public void runQuery(String str) throws Exception {


        //term - posting line (all docs and tf)
        TermSearch termSearch = searchTerm(str);




        //for every doc - get df.
        List<DocumentData> documentData =  getDocStats(termSearch);


        //Ranker ranker = new Ranker(termSearch,documentData);

        //rank(documentData);
}

public void rank(List<DocumentData> documentData){
        //Ranker ranker = new Ranker()

        for (DocumentData documentData1 : documentData){
           double documentScore =  bm25(documentData1);

        }

}

public double bm25(DocumentData documentData){

        Integer D =  documentData.numofterms; // length of the document D in words



        return 0.0;
}


public List<DocumentData> getDocStats(TermSearch termSearch) throws Exception {
    //TODO: make separate func
    List<DocumentData> documentDataList = new ArrayList<>();
    for (TermData termData: termSearch.termData){
        int docID = termData.document;
        int docTF = termData.frequency;
        DocumentData documentData =  searchDocument(docID);
        documentData.docTF=docTF;
        documentDataList.add(documentData);
    }
    return documentDataList;
}


    public TermSearch searchTerm(String term) throws Exception {

        System.out.println("searching for term " + term);

        //String path1 = getPath("final");

        String path1 = "C:\\posting\\post.txt";

        BufferedReader firstFile = new BufferedReader(new FileReader(path1));

       // List<TermStats> dic = new ArrayList<>();

        String line;

        //dicNumTerms=0;
        //numUniq=0;
        while ( (line= firstFile.readLine() )!=null) {
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

        System.out.println("searching for doc " + doc);

        //String path1 = getPath("final");

        String path1 = "C:\\posting\\documents.txt";

        BufferedReader firstFile = new BufferedReader(new FileReader(path1));

        List<TermStats> dic = new ArrayList<>();
        String line;

        //dicNumTerms=0;
        //numUniq=0;


        //skip 2?
        //todo: fix to 1
        line= firstFile.readLine();
        line= firstFile.readLine();

        while ( (line= firstFile.readLine() )!=null) {
            Integer index1 = line.indexOf(':');
            String doc1 = line.substring(0, index1);
            if (doc1.equals(String.valueOf(doc))) {
                String value1 = line.substring(index1 + 1);
                System.out.println("doc found");
                System.out.println(line);

                //Integer intID, String mostPopularTerm, int mostPopular_tf,int numOFsentences,int numofterms
                String[] stats = value1.split(":");

                String mostPopularTerm = stats[0];
                Integer mostPopular_tf = Integer.valueOf(stats[1]);
                Integer numOFsentences = Integer.valueOf(stats[2]);
                Integer numofterms = Integer.valueOf(stats[3]);

                DocumentData documentData = new DocumentData(doc,mostPopularTerm, mostPopular_tf, numOFsentences, numofterms);
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
        return new DocumentData(0,"",0,0,0);
    }



}
