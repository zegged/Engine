//package edu.IR.Engine.nlp;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.*;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Stream;
//
//public class ReadFile {
//    Map<String,String> stopword ;
//    public ReadFile(){
//        System.out.println("init ReadFile");
//    }
//
//    public String openFile(String path) throws IOException {
//        System.out.println("openFile " + path);
//        InputStream is = new FileInputStream("C:\\Users\\Razi\\Desktop\\ehzor\\testing\\testMonth");
//      //InputStream is = new FileInputStream("C:\\Users\\Razi\\Desktop\\ehzor\\corpus");
//
//        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
//
//        String line = buf.readLine();
//        StringBuilder sb = new StringBuilder();
//        sb.append("<FILE>\n");
//
//        while(line != null){
//            sb.append(line).append("\n");
//            line = buf.readLine();
//        }
//        sb.append("</FILE>\n");
//
//        String fileAsString = sb.toString();
//        //System.out.println("Contents (before Java 7) : " + fileAsString);
//
//        //Stop words razy---------------------------------------------------------------
//
//
//        File pathofstopword=new File("\\\\stop_words.txt");
//        String []stops=(readStopword(pathofstopword));
//        stopword = new HashMap<>();// why save stop?
//        for(int i=0;i<stops.length;i++)
//        {
//            stopword.put(stops[i],"");
//        }
//        //-----------------------------------------------------------------------
//        //String path="C:\\Users\\Razi\\Desktop\\ehzor";
//        //String mainPath = path;
////        List<String> filesPaths = new ArrayList<>();
////        try (Stream<Path> paths = Files.walk(Paths.get(path+"\\corpus"))) {
////
////            paths.filter(Files::isRegularFile)
////                    .forEach(path1 -> filesPaths.add(path1.toString()));
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //return fileAsString;
//        return fileAsString;
//    }
//
//
//    IRDocument[] parseXML(String singleFileXML) throws Exception {
//
//
//        //Get Document Builder
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        //Build Document
//        Document document = builder.parse(new InputSource(new StringReader(singleFileXML)));
//
//        //Normalize the XML Structure; It's just too important !!
//        document.getDocumentElement().normalize();
//
//        //Here comes the root node
//        Element root = document.getDocumentElement();
//        //System.out.println(root.getNodeName());
//
//        //Get all docs in file
//        NodeList nList = document.getElementsByTagName("DOC");
//        //System.out.println("============================");
//        //System.out.println(((Document) nList).getDocumentElement().getNodeName());
//
//        //System.out.println("loading " + nList.getLength() + 1 + " documents"); //NOT TRUE. counting all elements
//        IRDocument[] allDocs = new IRDocument[nList.getLength()];
//
//        for (int temp = 0; temp < nList.getLength(); temp++)
//        {
//            Node node = nList.item(temp);
//            //System.out.println("");    //Just a separator
//            if (node.getNodeType() == Node.ELEMENT_NODE)
//            {
//                //Print each employee's detail
//                Element eElement = (Element) node;
//                String id = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
//                //System.out.println("Document id : "	+ id);
//
//                String text = eElement.getElementsByTagName("TEXT").item(0).getTextContent();
//                //System.out.println("Text " + text);
//
//                NodeList headerList = eElement.getElementsByTagName("HEADER");
//                Element headerElement = (Element) headerList.item(0);
//                String date = headerElement.getElementsByTagName("DATE1").item(0).getTextContent();
//                //System.out.println("Date : "	+ date);
//
//                Element titleElement = (Element) headerElement.getElementsByTagName("H3").item(0);
//                String title = titleElement.getElementsByTagName("TI").item(0).getTextContent();
//                //System.out.println("Title : "	+ title);
//
//                allDocs[temp] = new IRDocument(id,title,date,text);
//            }
//        }
//
//
//        //IRDocument mydoc = new IRDocument("id","title","date","text");
//        System.out.println("loaded documents " + allDocs.length);
//
//        return allDocs;
//    }
//
//
//    //razy-----
//    /**
//     * this method gets path to the stop word text file and return a string array with all of the words
//     * @param S the path to the stop word file
//     * @return the array of stop words as strings
//     */
//    public String [] readStopword(File S){
//        //make a string Array from all the StopWords
//        File file = new File("C:\\Users\\Razi\\IdeaProjects\\Engine\\stop_words.txt");
//        String everything="";
//        try {
//            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
//                StringBuilder sb = new StringBuilder();
//                String line = br.readLine();
//
//                while (line != null) {
//                    sb.append(line);
//                    sb.append(System.lineSeparator());
//                    line = br.readLine();
//                }
//                everything = sb.toString();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String []stopwords=everything.split("\\s+");
//        return  stopwords;
//    }
//
//
//}
package edu.IR.Engine.nlp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadFile {

    public List<String> getAllFiles(String pathToCorpus) throws IOException {

        List<String> ans = Files.walk(Paths.get(pathToCorpus))
                .filter(Files::isRegularFile)
                .map(x -> x.toString()).collect(Collectors.toList());
        return ans;
    }

    public ReadFile(){
        System.out.println("init ReadFile");
    }

    public String openFile(String path) throws IOException {
        System.out.println("openFile " + path);
        InputStream is = new FileInputStream(path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        //sb.append("<script th:inline=\"javascript\">");
        sb.append("<FILE>\n");
        while(line != null){
            if (line.contains("<F P")) {
                if (line.contains("</F>")){
                    line = buf.readLine();
                }
                else {
                    while (!line.contains("</F>")) {
                        line = buf.readLine();
                    }
                    line = buf.readLine();
                }
            }
            else if(line.contains("<FIG")){
                if (line.contains("</FIG>")){
                    line = buf.readLine();
                }
                else {
                    while (!line.contains("</FIG>")) {
                        line = buf.readLine();
                    }
                    line = buf.readLine();
                }
            }
            else {
                if (line.contains("&")) { //FB396073
                    line = line.replaceAll("&", "");
                }
                if (line.contains("<3>") || line.contains("</3>")) { //DUE TO FB496073
                    line = line.replaceAll("<3>", "");
                    line = line.replaceAll("</3>", "");
                }
                if (line.contains("<DATELINE>") || line.contains("</DATELINE>")) {
                    line = line.replaceAll("<DATELINE>", "<TEXT>");
                    line = line.replaceAll("</DATELINE>", "</TEXT>");
                }
                if (line.contains("<P>") || line.contains("</P>")) { // LA010189
                    line = line.replaceAll("<P>", "");
                    line = line.replaceAll("</P>", "");
                }

                //if (line.contains("<GRAPHIC>") || line.contains("</GRAPHIC>")) { // LA010189
                //line = line.replaceAll("<GRAPHIC>", "<TEXT>");
                //line = line.replaceAll("</GRAPHIC>", "</TEXT>");
                //}

                sb.append(line).append("\n");
                line = buf.readLine();
            }
        }
        sb.append("</FILE>\n");
        //sb.append("</script>");

        String fileAsString = sb.toString();
        //System.out.println("Contents (before Java 7) : " + fileAsString);

        return fileAsString;
    }


    IRDocument[] parseXML(String singleFileXML) throws Exception {


        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();



        //Build Document
        Document document = builder.parse(new InputSource(new StringReader(singleFileXML)));

        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        //Here comes the root node
        Element root = document.getDocumentElement();
        //System.out.println(root.getNodeName());

        //Get all docs in file
        NodeList nList = document.getElementsByTagName("DOC");
        //System.out.println("============================");
        //System.out.println(((Document) nList).getDocumentElement().getNodeName());

        //System.out.println("loading " + nList.getLength() + 1 + " documents"); //NOT TRUE. counting all elements
        IRDocument[] allDocs = new IRDocument[nList.getLength()];

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node node = nList.item(temp);
            //System.out.println("");    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                //Print each employee's detail
                Element eElement = (Element) node;
                String id = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
                //System.out.println("Document id : "	+ id);

                NodeList nodeList =  eElement.getElementsByTagName("TEXT");
                String text;
                if (nodeList.getLength()>0) { // LA011290 - no <text>
                    text = eElement.getElementsByTagName("TEXT").item(0).getTextContent();
                }
                else{
                    text = "";
                }
                //System.out.println("Text " + text);

                //NodeList headerList = eElement.getElementsByTagName("HEADER");
                //Element headerElement = (Element) headerList.item(0);
                //String date = headerElement.getElementsByTagName("DATE1").item(0).getTextContent();
                //System.out.println("Date : "	+ date);
                //REMOVED DUE TO FILE: 70

                //Element titleElement = (Element) headerElement.getElementsByTagName("H3").item(0);
                //String title = titleElement.getElementsByTagName("TI").item(0).getTextContent();
                //System.out.println("Title : "	+ title);
                //REMOVED DUE TO FILE: 3

                allDocs[temp] = new IRDocument(id,"","",text);
            }
        }


        //IRDocument mydoc = new IRDocument("id","title","date","text");
        System.out.println("loaded documents " + allDocs.length);

        return allDocs;
    }
//    public String [] readStopword(File S){
//        //make a string Array from all the StopWords
//        //File file = new File("C:\\Users\\Razi\\IdeaProjects\\Engine\\stop_words.txt");
//        String everything="";
//        try {
//            try(BufferedReader br = new BufferedReader(new FileReader(S))) {
//                StringBuilder sb = new StringBuilder();
//                String line = br.readLine();
//
//                while (line != null) {
//                    sb.append(line);
//                    sb.append(System.lineSeparator());
//                    line = br.readLine();
//                }
//                everything = sb.toString();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String []stopwords=everything.split("\\s+");
//        return  stopwords;
//    }


}
