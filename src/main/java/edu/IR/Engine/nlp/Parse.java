package edu.IR.Engine.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import javafx.util.Pair;

import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Parse {
    StanfordCoreNLP stanfordCoreNLP;



    //razy------------------------------------------------
    Map <String,String> Months=new HashMap<String, String>(){{
        put("january","01"); put("february","02"); put("march","03");put("april","04");put("may","05");
        put("june","06");put("july","07");put("august","08");put("september","09");put("october","10");
        put("november","11");put("december","12");put("jan", "01");put("feb","02");put("mar","03");
        put("apr","04");put("may","05");put("jun","06");put("jul","07");put("aug","08");put("sep","09");
        put("oct","10");put("nov","11");put("dec","03");}};
    //--------------------------------------------------


    public Parse(){
        System.out.println("Parse init");
        stanfordCoreNLP = Pipeline.getPipeline();
    }
    public static boolean isNumeric(String str) {
        if(str.contains(",")){
            return isNumeric(str.split(",")[0]);
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static int[] splitNumberWithPoint(String s){
        String[] arr=String.valueOf(s).split("\\.");
        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]); // 1
        intArr[1]=Integer.parseInt(arr[1]); // 9
        return intArr;
    }

    //thousand=K million=M billion=B
    public String Name_NumberHandle(String term,String prev){
        String number_to_save="";
        if(term.toLowerCase().equals("thousand")){
            number_to_save=prev+"K";
        }
        else if(term.toLowerCase().equals("billion")){
            number_to_save=prev+"B";
        }
        else{
            number_to_save=prev+"M";
        }
        return number_to_save;
    }

    //recive number like x,xxx and change it to k/m/b
    public String regular_NumberHandle(String term,String prev){
        String number_to_save="";
        number_to_save=term.split(",")[0]+"."+term.split(",")[1];
        if((isNumeric(term)&&term.length()<=7)){
            number_to_save=number_to_save+"K";
            //System.out.println(number_to_save);
        }
        else if((isNumeric(term)&&term.length()>=11)){
            number_to_save=number_to_save+"B";
            //System.out.println(number_to_save);
        }
        else{
            number_to_save=number_to_save+"M";
            //System.out.println(number_to_save);
        }
        char lastchar=number_to_save.charAt(number_to_save.length()-2);
        char lastchar2=number_to_save.charAt(number_to_save.length()-3);
        if(lastchar=='0'&&lastchar2!='0'){
            number_to_save=number_to_save.substring(0,number_to_save.length()-2)+
                    number_to_save.substring(number_to_save.length()-1);
        }
        if(lastchar=='0'&&lastchar2=='0'){
            number_to_save=number_to_save.substring(0,number_to_save.length()-3)+
                    number_to_save.substring(number_to_save.length()-1);
        }
        if(term.split(",")[1].equals("000") ){
            number_to_save=term.split(",")[0]+number_to_save.substring(number_to_save.length()-1);
        }
        return number_to_save;
    }

    //handle price 1)Price dollar
    //2) price function dollar ?????????????????????
    //3) $price
    public String priceHandle(String term,String prev){
        String price_to_save="";
        if(term.toLowerCase().equals("dollars")){
            price_to_save=prev+"Dollars";
        }
        if(prev.equals("$")){
            price_to_save=term+"Dollars";
        }
        return price_to_save;
    }


    public void parseDocument(IRDocument doc){

        // parse docID
        String stringID = doc.id.split("-",2)[1].trim();
        Integer intID = Integer.valueOf(stringID);
        System.out.println("doc: " + intID);


        // STATISTICS INIT
        String mostPopularTerm = ""; //most popular term
        int mostPopular_tf = 0; //most popular term frequency
        int uniqueTermsInDocument; //amount of unique terms


        // STANFORD NLP PARSE
        List<CoreSentence> sentences = breakSentences(doc.text);

        //temp dictionary for parse
        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<String, Integer>();



        for (CoreSentence sentence : sentences){
            //System.out.println(index++ + " : " + sentence.toString());
            List<CoreLabel> coreLabelList = sentence.tokens();
            String token="";
            for (CoreLabel coreLabel : coreLabelList){
                // PARSE HERE
                String prev= token;
                token = coreLabel.originalText();
                String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String term = token;
                //check term type (razy)---------------------
                //dates Months
                boolean check=Months.containsKey(term.toLowerCase());
                String date_to_save="";
                if((check==true&&isNumeric(prev))||(isNumeric(token)&&Months.containsKey(prev.toLowerCase()))){
                    if (check==true){
                        int sum = prev.chars().reduce(0, Integer::sum);
                        if(48<=sum&&sum<58){
                            date_to_save=Months.get(term.toLowerCase())+"-0"+prev;
                            System.out.println(date_to_save);
                        }
                        else{
                            date_to_save=Months.get(term.toLowerCase())+"-"+prev;
                            System.out.println(date_to_save);
                        }
                    }else {
                        int sum = term.chars().reduce(0, Integer::sum);
                        if(48<=sum&&sum<58){
                            date_to_save = Months.get(prev.toLowerCase()) + "-0" + term;
                            System.out.println(date_to_save);
                        }
                        else {
                            date_to_save = Months.get(prev.toLowerCase()) + "-" + term;
                            System.out.println(date_to_save);
                        }
                    }
                }


                //NUMBER K/M/B -> did not try to check uncorrect in put
                // number like 10,000,000 million
                String number_to_save="";
                if(term.contains(",")){
                    number_to_save=regular_NumberHandle(term,prev);
                }

                if((term.toLowerCase().equals("thousand")||term.toLowerCase().equals("million")
                        ||term.toLowerCase().equals("billion")&&isNumeric(prev))){
                    number_to_save=Name_NumberHandle(term,prev);
                }
                //should do it again (number with point and without , )
                //numbers like 1010.54 or 111992000.232
                /*
                if(term.contains(".")&& isNumeric(""+splitNumberWithPoint(term)[0])&&splitNumberWithPoint(term)[0]>=999){
                    System.out.println("WORKING");
                    int i=splitNumberWithPoint(term)[0];
                    int cnt=0;
                    while (i%10==0&&cnt<3){
                        i=i/10;
                        cnt++;
                    }
                    System.out.println("the number is : "+i);
                }
*/

//----------------------------------------------------------------------------------
                //percent
                String percent_to_save="";
                if(term.contains("%")||term.contains("percent")||term.contains("percentage")){
                    percent_to_save=prev+"%";
                }

                //price to save
                String price_to_save="";
                if(term.toLowerCase().equals("dollars")|| isNumeric(term) ){
                    price_to_save=priceHandle(term,prev);
                }



                if(number_to_save!=""){
                    System.out.println(number_to_save);
                }
                if(percent_to_save!=""){
                    System.out.println(percent_to_save);
                }
                if(price_to_save!=""){
                    System.out.println(price_to_save);
                }
                if(number_to_save==""&&percent_to_save==""&&price_to_save==""){
                    System.out.println(term);
                }


                // PARSE DONE
                // SAVE TERM IN TEMP DICTIONARY
                int term_frequency = 1;
                if (dictionary.containsKey(term)){
                    term_frequency = dictionary.get(term) + 1;
                    dictionary.put(term,term_frequency);
                }
                else{
                    dictionary.put(term,1);
                }

                // STATISTICS
                if (term_frequency>mostPopular_tf){
                    mostPopular_tf=term_frequency;
                    mostPopularTerm=term;
                }
            }



            //System.out.println("///////////////END OF DOC////////////////////");
        }


        // ADD TERMS TO INDEXER
        //deleteddd
        /*
        for (Map.Entry<String,Integer> entry : dictionary.entrySet()) {
            String term = entry.getKey();
            Integer value = entry.getValue();

            //System.out.println(term + " -> " + value);
            if (Indexer.terms.containsKey(term)){
                Indexer.terms.get(term).add(new Pair<Integer, Integer>(intID,value));
            }
            else{
                List<Pair<Integer,Integer>> termList = new ArrayList<>();
                termList.add(new Pair<Integer, Integer>(intID,value));
                Indexer.terms.put(term,termList);
            }
        }
*/

        // ADD DOC DATA TO INDEXER



        String docData = intID + "^" + mostPopularTerm + "^" + mostPopular_tf ;
        Indexer.docs.add(docData);
    }




private List<CoreSentence> breakSentences(String text){


    CoreDocument coreDocument = new CoreDocument(text);

    //System.out.println("annotate");
    stanfordCoreNLP.annotate(coreDocument);
    //System.out.println("annotate done");
    List<CoreSentence> sentences = coreDocument.sentences();

    /*
    for (CoreSentence sentence : sentences){
        System.out.println(sentence.toString());

    }

     */
    return sentences;
}

    public void sortDocument(IRDocument doc) {
        sortByValueDec(doc);
        sortByKey(doc);
    }



    private void sortByValueDec(IRDocument document){
        document.sortedValues = document.terms
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    private void sortByKey(IRDocument document){
        // https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html#ixzz65ZleZ7QA
        document.sortedTerms = document.terms
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));
    }







    private Map<String, Integer> sortDictionary(Map<String,Integer> dic){
        //https://www.javacodegeeks.com/2017/09/java-8-sorting-hashmap-values-ascending-descending-order.html
        System.out.println("map before sorting: " + dic);

        // let's sort this map by values first
        LinkedHashMap<String, Integer> sorted = dic
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println("map after sorting by values: " + sorted);

        // above code can be cleaned a bit by using method reference
        sorted = dic
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        // now let's sort the map in decreasing order of value
        sorted = dic
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println("map after sorting by values in descending order: "
                + sorted);

        return  sorted;
    }
}
