package edu.IR.Engine.nlp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class ReadFile {
    public ReadFile(){
        System.out.println("init ReadFile");
    }

    public String openFile(String path) throws IOException {
        System.out.println("openFile " + path);
        InputStream is = new FileInputStream("C:\\corpus\\corpus\\corpus\\FB396001\\FB396001");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        sb.append("<FILE>\n");

        while(line != null){
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        sb.append("</FILE>\n");

        String fileAsString = sb.toString();
        //System.out.println("Contents (before Java 7) : " + fileAsString);

        return fileAsString;
    }


    IRDocument[] parseXML(String singleFileXML) throws Exception {


        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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

                String text = eElement.getElementsByTagName("TEXT").item(0).getTextContent();
                //System.out.println("Text " + text);

                NodeList headerList = eElement.getElementsByTagName("HEADER");
                Element headerElement = (Element) headerList.item(0);
                String date = headerElement.getElementsByTagName("DATE1").item(0).getTextContent();
                //System.out.println("Date : "	+ date);

                Element titleElement = (Element) headerElement.getElementsByTagName("H3").item(0);
                String title = titleElement.getElementsByTagName("TI").item(0).getTextContent();
                //System.out.println("Title : "	+ title);

                allDocs[temp] = new IRDocument(id,title,date,text);
            }
        }


        //IRDocument mydoc = new IRDocument("id","title","date","text");
        System.out.println("loaded documents " + allDocs.length);

        return allDocs;
    }


}
