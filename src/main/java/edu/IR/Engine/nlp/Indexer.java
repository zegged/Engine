package edu.IR.Engine.nlp;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Indexer {


    //terms:
    public static Map<String, List<TermData>> terms = new TreeMap<>();
    List<String> lastDictionaryToView ;

    //docs:
    public static List<String> docs = new ArrayList<>();


    public static Map<String,TermPointers> termsPointers = new LinkedHashMap<>();


    int nPostingShards = 0;
    String m_strPostingFolderPath;

    public Indexer(String path) {
        m_strPostingFolderPath = path;
        lastDictionaryToView=new ArrayList<>();
        System.out.println("Indexer init");
    }


    public static Map<String, Integer> mergeMaps(Map<String,Integer> visitCounts1, Map<String,Integer> visitCounts2){
        //https://coderwall.com/p/oflatw/merging-multiple-maps-using-java-8-streams

        Map<String, Integer> totalTFCounts = Stream.concat(visitCounts1.entrySet().stream(), visitCounts2.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), // The key
                        entry -> entry.getValue(), // The value
                        // The "merger" as a method reference
                        Integer::sum
                        )
                );
        //System.out.println("Total:");
        //System.out.println(totalTFCounts);

        return totalTFCounts;

    }


    private void dumpPosting(String fileNameToSave, FileWriter raf, Map<String, List<TermData>> terms) throws IOException {

        for (Map.Entry<String,List<TermData>> entry : terms.entrySet()) {
            String term = entry.getKey();
            List<TermData> value = entry.getValue();
            String lineToAdd = term + ":" + value;
            raf.write(lineToAdd);
            raf.write(System.lineSeparator());
//            raf.writeBytes(lineToAdd);
//            raf.writeBytes(System.lineSeparator());
        }

    }

    private void dumpPostingSavePointers(String fileNameToSave, RandomAccessFile raf) throws IOException {
        for (Map.Entry<String,List<TermData>> entry : Indexer.terms.entrySet()) {
            String term = entry.getKey();
            List<TermData> value = entry.getValue();

            System.out.println(term + " : " + value);
            String lineToAdd = term + " : " + value;

            Long plStart = raf.getFilePointer();
            System.out.println("ptr before : " + plStart);

            raf.writeBytes(lineToAdd);

            Long plEnd = raf.getFilePointer();
            // add newline after ptr
            raf.writeBytes(System.lineSeparator());
            System.out.println("ptr after : " + plEnd);

            //dump buffer
            if(Indexer.termsPointers.containsKey(term)){
                //term exists
                TermPointers points = Indexer.termsPointers.get(term);
                points.add(fileNameToSave,plStart,plEnd);
            }
            else{
                //new term
                TermPointers newTermToAdd = new TermPointers(term);
                newTermToAdd.add(fileNameToSave,plStart,plEnd);
                Indexer.termsPointers.put(term,newTermToAdd);
            }

            System.out.println("result");
            System.out.println(Indexer.termsPointers.get(term));

        }

    }

    private boolean OpenFileToWrite(String filename, Map<String, List<TermData>> terms){

        File file = new File(filename);
        if (!file.exists()) {
            // Create a new file if not exists.
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                //RandomAccessFile raf = new RandomAccessFile(file, "rw");
                dumpPosting(filename,writer, terms);
                // Closing the resources.
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("file already exists");
            return false;
        }

        return true;
    }

    public void findTerm(String fileName, Long start, Long end) throws IOException {
        // Using file pointer creating the file.
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found!");
        }

        System.out.println("File opened");
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        raf.seek(start);
        String line = raf.readLine();
        System.out.println(line);

    }


    // ARCHIVE
    public void writeToFile(String data) throws Exception {
        //System.out.println(data);
        String fileName = "c:\\posting\\test.txt";
        RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
        FileChannel channel = stream.getChannel();
        String value = data;
        byte[] strBytes = value.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);
        stream.close();
        channel.close();

    }


    public void addTerms(DocumentTerms documentTerms, Integer docID) {
        // ADD TERMS TO INDEXER
        final HashMap<String, Integer> dictionary = documentTerms.dictionary;
        for (String strSortedTerm : documentTerms.sortedTerms){
            //for (Map.Entry<String,Integer> entry : dictionary.entrySet()) {
            //String term = entry.getKey();
            //Integer value = entry.getValue();

            String term = strSortedTerm;
            Integer value = dictionary.get(term);


            //System.out.println(term + " -> " + value);
            if (Indexer.terms.containsKey(term)){
                Indexer.terms.get(term).add(new TermData(docID,value));
            }
            else{
                List<TermData> termList = new ArrayList<>();
                termList.add(new TermData(docID,value));
                Indexer.terms.put(term,termList);
            }
        }
    }

    public void addDocument(DocumentData documentData) {
        String docData = documentData.docID + "^" + documentData.mostPopularTerm + "^" + documentData.mostPopulatFrequency ;
        Indexer.docs.add(docData);
    }

    public boolean isMemoryFull() {
        if (Indexer.docs.size() % 5000==0){
            System.out.println("Dumping 5K Documents.");
            return true;
        }
        return false;
    }




    public void savePosting() {
        String strPostingShardPath = getPath(0,nPostingShards++);
        OpenFileToWrite(strPostingShardPath, Indexer.terms);

        // clear dictionary
        // Get keys and values
        this.lastDictionaryToView.clear();
        for (Map.Entry<String, List<TermData>> entry : terms.entrySet()) {
            String k = entry.getKey();
            this.lastDictionaryToView.add(k);
        }
        Indexer.terms.clear();
    }


    public List<String> getDictionaryForView(){
        return this.lastDictionaryToView;
    }

    public void deleteDictionary(){
        this.lastDictionaryToView.clear();
    }

    private String getPath(Integer prefix , Integer num) {
        String path = m_strPostingFolderPath + "post" + prefix + "-" + num + ".txt";
        return path;
    }


    public void merge() throws IOException {
        Integer iteration = 0;
        String path1 = getPath(iteration,1);
        while (checkFilesExists(path1)){
            mergePostingFile(iteration);
            iteration++;
            path1 = getPath(iteration,1);
        }


    }

    //merge for last 2 files
    public void merge_two_last_files(String path1,String path2,String mergeFile) throws IOException {
        System.out.println("merging: " + path1 + " w/ " + path2);
        BufferedReader firstFile = new BufferedReader(new FileReader(path1));
        BufferedReader secondFile = new BufferedReader(new FileReader(path2));
        List<TermMerge> ans = new ArrayList<>();
        String line1 = firstFile.readLine();
        String line2 = secondFile.readLine();
        SortedMap<String, List<TermData>> sm = new TreeMap<String, List<TermData>>();
        while (line1!=null) {
            String term , value;
            Integer index;
            index = line1.indexOf(':');
            term = line1.substring(0, index);
            value = line1.substring(index + 1);
            TermPosting termPosting1 = new TermPosting(value);
            TermMerge newTermMerge =  new TermMerge(term,termPosting1);
            newTermMerge.concatenateLists();
            sm.put(term,newTermMerge.m_termData);
            //ans.add(newTermMerge);
            line1 = firstFile.readLine();

        }
        while (line2!=null) {
            String term , value;
            Integer index;
            index = line2.indexOf(':');
            term = line2.substring(0, index);
            value = line2.substring(index + 1);
            TermPosting termPosting1 = new TermPosting(value);
            TermMerge newTermMerge =  new TermMerge(term,termPosting1);
            newTermMerge.concatenateLists();
            sm.put(term,newTermMerge.m_termData);
            //ans.add(newTermMerge);
            line2 = secondFile.readLine();
        }
        firstFile.close();
        secondFile.close();
        System.out.println("reformat");
//        for (TermMerge termMerge : ans){
//            termMerge.concatenateLists();
//            sm.put(termMerge.m_term,termMerge.m_termData);
//        }
        //TODO: retuen or save ans
        saveMerge(mergeFile,sm);


    }

    public void mergePostingFile(Integer Iteration) throws IOException {
        System.out.println("merging");
        String path = m_strPostingFolderPath;
        Integer i = 0;
        Integer mergeIdx=0;
        String path1 = getPath(Iteration,i+0);
        String path2 = getPath(Iteration,i+1);

        String strPostingShardPath = getPath(Iteration+1,0);
        String path3=getPath(Iteration,i+2);
        if(checkFilesExists(path3)==false){
            merge_two_last_files(path1,path2,strPostingShardPath);
            deleteFile(path1);
            deleteFile(path2);
        }
        else {
            while (checkFilesExists(path1)){
                if ( checkFilesExists(path2) ){
                    mergeTwoFiles(path1,path2,strPostingShardPath);
                    deleteFile(path1);
                    deleteFile(path2);
                }
                else{
                    //last odd . just copy
                    mergeLastFile(path1, strPostingShardPath);
                }
                i+=2;
                mergeIdx++;
                path1 = getPath(Iteration,i+0);
                path2 = getPath(Iteration,i+1);
                strPostingShardPath = getPath(Iteration+1,mergeIdx);
            }
        }
    }

//    public void mergeTwoFiles(String path1, String path2, String mergeFile) throws IOException {
//        BufferedReader firstFile = new BufferedReader(new FileReader(path1));
//        BufferedReader secondFile = new BufferedReader(new FileReader(path2));
//        String st;
//        while ((st = firstFile.readLine()) != null)
//            System.out.println(st);
//    }


//////////////


    public void mergeTwoFiles(String path1, String path2, String mergeFile) throws IOException {
        System.out.println("merging: " + path1 + " w/ " + path2);
        BufferedReader firstFile = new BufferedReader(new FileReader(path1));
        BufferedReader secondFile = new BufferedReader(new FileReader(path2));
        List<TermMerge> ans = new ArrayList<>();
       // Map< String,Integer> map = new HashMap< String,Integer>();
       // Map<String, List<TermData>> stringListMap = new LinkedHashMap<>();
        String strAns = "";
        String line1, line2;
        line1="";
        line2="";
        line1 = firstFile.readLine();
        line2 = secondFile.readLine();
        while (line1!=null  && line2!=null  ) {
            String term1, term2, value1, value2;
            Integer index1, index2;
            index1 = line1.indexOf(':');
            index2 = line2.indexOf(':');
            term1 = line1.substring(0, index1);
            value1 = line1.substring(index1 + 1);
            term2 = line2.substring(0, index2);
            value2 = line2.substring(index2 + 1);
            TermPosting termPosting1 = new TermPosting(value1);
            TermPosting termPosting2 = new TermPosting(value2);
            // INTERSECT
            if (term1.equals(term2)){
                TermMerge newTermMerge =  new TermMerge(term1,termPosting1,termPosting2);
//                stringListMap.put(newTermMerge.m_term,newTermMerge.m_termData);
                ans.add(newTermMerge);
            }
            else {
                if(term1.compareTo(term2)<0){
                    TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
                    //stringListMap.put(newTermMerge.m_term,newTermMerge.m_termData);
                    ans.add(newTermMerge);

                }
                else{
                    TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
                    //stringListMap.put(newTermMerge.m_term,newTermMerge.m_termData);
                    ans.add(newTermMerge);
                }
            }
            line1 = firstFile.readLine();
            line2 = secondFile.readLine();

        }
        String line1T = firstFile.readLine();
        while (line1T!=null) {
            Integer index1 = line1T.indexOf(':');
            String term1 = line1T.substring(0, index1);
            String value1 = line1T.substring(index1 + 1);
            TermPosting termPosting1 = new TermPosting(value1);
            TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
            //stringListMap.put(newTermMerge.m_term,newTermMerge.m_termData);
            ans.add(newTermMerge);
            line1T=firstFile.readLine();
        }

        String line2T = secondFile.readLine();
        while (secondFile.readLine()!=null  ) {
            Integer index2 = line2T.indexOf(':');
            String term2 = line2T.substring(0, index2);
            String value2 = line2T.substring(index2 + 1);
            TermPosting termPosting2 = new TermPosting(value2);
            TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
            //----------
            //stringListMap.put(newTermMerge.m_term,newTermMerge.m_termData);
            ans.add(newTermMerge);
            //---------
            line2T=secondFile.readLine();
        }

        firstFile.close();
        secondFile.close();
        Map<String, List<TermData>> stringListMap = new LinkedHashMap<>();
        System.out.println("reformat");

        for (TermMerge termMerge : ans){
            termMerge.concatenateLists();
            stringListMap.put(termMerge.m_term,termMerge.m_termData);
        }

        //TODO: retuen or save ans
        saveMerge(mergeFile,stringListMap);
    }


    public void saveMerge(String filePath, Map<String, List<TermData>> terms) {
        OpenFileToWrite(filePath, terms);
        // TODO: clear old files
    }

    public void deleteFile(String path){
        File f= new File(path);           //file to be delete
        if(f.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }
    }



    private void mergeLastFile(String path1, String mergeFile) throws IOException {
        System.out.println("merging: " + path1);
        File file1 = new File(path1);
        File file2 = new File(mergeFile);
        file1.renameTo(file2);
    }

    private boolean checkFilesExists(String path1) {
        File file = new File(path1);
        boolean exists1 = file.exists();
        return exists1;
    }

    private boolean OpenFileToMerge(String filename, Map<String, List<TermData>> terms){

        File file = new File(filename);

        if (!file.exists()) {
            // Create a new file if not exists.
            try {
                file.createNewFile();

                RandomAccessFile raf = new RandomAccessFile(file, "rw");

                dumpMerge(filename,raf, terms);

                // Closing the resources.
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("file already exists");
            return false;
        }
        return true;
    }

    private void dumpMerge(String fileNameToSave, RandomAccessFile raf, Map<String, List<TermData>> terms) throws IOException {

        for (Map.Entry<String,List<TermData>> entry : terms.entrySet()) {
            String term = entry.getKey();
            List<TermData> value = entry.getValue();
            String lineToAdd = term + " : " + value;
            raf.writeBytes(lineToAdd);
            raf.writeBytes(System.lineSeparator());
        }

    }



}
