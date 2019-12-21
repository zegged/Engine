package edu.IR.Engine.nlp;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    //docs:
    public static List<String> docs = new ArrayList<>();


    public static Map<String,TermPointers> termsPointers = new LinkedHashMap<>();


    int nPostingShards = 0;
    String m_strPostingFolderPath;

    public Indexer(String path) {
        m_strPostingFolderPath = path;
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


    private void dumpPosting(String fileNameToSave, RandomAccessFile raf, Map<String, List<TermData>> terms) throws IOException {

        for (Map.Entry<String,List<TermData>> entry : terms.entrySet()) {
            String term = entry.getKey();
            List<TermData> value = entry.getValue();
            String lineToAdd = term + ":" + value;
            raf.writeBytes(lineToAdd);
            raf.writeBytes(System.lineSeparator());
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

                RandomAccessFile raf = new RandomAccessFile(file, "rw");

                //System.out.println("poiner at: " + raf.getFilePointer());

                // writeBytes function to write a string
                // as a sequence of bytes.
                //raf.writeBytes("hello world");

                //System.out.println("poiner at: " + raf.getFilePointer());

                // To insert the next record in new line.
                //raf.writeBytes(System.lineSeparator());

                //System.out.println("poiner at: " + raf.getFilePointer());

                // Print the message
                //System.out.println(" line added. ");

                dumpPosting(filename,raf, terms);

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
        Indexer.terms.clear();
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

    public void mergePostingFile(Integer Iteration) throws IOException {
        System.out.println("merging");

        String path = m_strPostingFolderPath;




        Integer i = 0;
        Integer mergeIdx=0;

        String path1 = getPath(Iteration,i+0);
        String path2 = getPath(Iteration,i+1);
        String strPostingShardPath = getPath(Iteration+1,0);


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

//////////////


    public void mergeTwoFiles(String path1, String path2, String mergeFile) {


        System.out.println("merging: " + path1 + " w/ " + path2);


        //Path pathT1  = Paths.get(path1);
        //Path pathT2  = Paths.get(path2);

        //System.out.println("loading files");
        //ArrayList<String> lines1 = (ArrayList<String>) Files.readAllLines(pathT1);
        //ArrayList<String> lines2 = (ArrayList<String>) Files.readAllLines(pathT2);
        //System.out.println("merging");
        //File file1 = new File(path1);
        //File file2 = new File(path2);

        //RandomAccessFile raf1 = new RandomAccessFile(file1, "r");
        //RandomAccessFile raf2 = new RandomAccessFile(file2, "r");



        FileInputStream inputStream1 = null;
        FileInputStream inputStream2 = null;
        Scanner sc1 = null;
        Scanner sc2 = null;


        try {
            inputStream1 = new FileInputStream(path1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            inputStream2 = new FileInputStream(path2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sc1 = new Scanner(inputStream1, "UTF-8");
        sc2 = new Scanner(inputStream2, "UTF-8");



        List<TermMerge> ans = new ArrayList<>();

        String strAns = "";

        // reading lines from the files.
        // should wrap with a first if

        //boolean readRight = true;
        //boolean readLeft = true;



        /*

        int leftIndex = 0;
        int rightIndex = 0;
        int size = lines1.size() + lines2.size();
        for (int i = 0; i < size + 1; i++){

            if (i % 100 == 0) {
                System.out.println((i + 0.0) / size);
            }

            line1 = lines1.get(leftIndex);
            line2 = lines2.get(rightIndex);

            String[] splitLine1 = line1.split(":");
            String[] splitLine2 = line2.split(":");

            //index1 = line1.indexOf(':');
            //index2 = line2.indexOf(':');


            // separating name and number.
            term1 = splitLine1[0]; //.substring(0, index1);
            value1 = splitLine1[1]; //.substring(index1 + 1);

            term2 = splitLine2[0]; //.substring(0, index2);
            value2 = splitLine2[1]; //.substring(index2 + 1);

            TermPosting termPosting1 = new TermPosting(value1);
            TermPosting termPosting2 = new TermPosting(value2);

            if (leftIndex < lines1.size() && rightIndex < lines2.size()) {
                if (term1.equals(term2)){

                    TermMerge newTermMerge =  new TermMerge(term1,termPosting1,termPosting2);
                    //System.out.println(newTermMerge);
                    ans.add(newTermMerge);

                }
                else
                if (term1.compareTo(term2)<0) {
                    //TermPosting termPosting1 = new TermPosting(value1);
                    TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
                    ans.add(newTermMerge);
                    leftIndex++;
                } else {
                    //TermPosting termPosting2 = new TermPosting(value2);
                    TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
                    ans.add(newTermMerge);
                    rightIndex++;
                }
            } else if (leftIndex < lines1.size()) {
                // If all elements have been copied from rightArray, copy rest of leftArray
                //TermPosting termPosting1 = new TermPosting(value1);
                TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
                ans.add(newTermMerge);
                leftIndex++;
            } else if (rightIndex < lines2.size()) {
                // If all elements have been copied from leftArray, copy rest of rightArray
                //TermPosting termPosting2 = new TermPosting(value2);
                TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
                ans.add(newTermMerge);
                rightIndex++;
            }




        }

         */

        boolean readLeft = true;
        boolean readRight = true;

        String line1, line2;
        line1="";
        line2="";

        while (sc1.hasNextLine()  && sc2.hasNextLine()  ) {
            String term1, term2, value1, value2;
            Integer index1, index2;



            if (readLeft){
                line1 = sc1.nextLine();
            }
            if (readRight) {
                line2 = sc2.nextLine();
            }
            readLeft= false;
            readRight = false;

            index1 = line1.indexOf(':');
            index2 = line2.indexOf(':');


            // separating name and number.
            term1 = line1.substring(0, index1);
            value1 = line1.substring(index1 + 1);

            term2 = line2.substring(0, index2);
            value2 = line2.substring(index2 + 1);

            //System.out.println(term1 + " = " + term1.compareTo(term2) + " = " +  term2);

            TermPosting termPosting1 = new TermPosting(value1);
            TermPosting termPosting2 = new TermPosting(value2);


            // INTERSECT

            if (term1.equals(term2)){
                //System.out.println(term1);
                //System.out.println(termPosting1.m_postingList);
                //System.out.println(termPosting2.m_postingList);
                TermMerge newTermMerge =  new TermMerge(term1,termPosting1,termPosting2);
                //System.out.println(newTermMerge);
                ans.add(newTermMerge);
                //line1 = raf1.readLine();
                //line2 = raf2.readLine();
                readLeft = true;
                readRight = true;
            }
            else {

                if(term1.compareTo(term2)<0){
                    TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
                    ans.add(newTermMerge);
                    //line1 = raf1.readLine();
                    readLeft = true;

                }
                else{
                    TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
                    ans.add(newTermMerge);
                    //line2 = raf2.readLine();
                    readRight = true;
                }
            }
        }

        while (sc1.hasNextLine()) {
            String line1T = sc1.nextLine();
            //System.out.println("line1:" + line1T);
            Integer index1 = line1T.indexOf(':');
            String term1 = line1T.substring(0, index1);
            String value1 = line1T.substring(index1 + 1);
            TermPosting termPosting1 = new TermPosting(value1);
            TermMerge newTermMerge =  new TermMerge(term1,termPosting1);
            ans.add(newTermMerge);
            //System.out.println(termPosting1.m_postingList);
        }

        while (sc2.hasNextLine()  ) {
            String line2T = sc2.nextLine();
            //System.out.println("line2:" + line2T);
            Integer index2 = line2T.indexOf(':');
            String term2 = line2T.substring(0, index2);
            String value2 = line2T.substring(index2 + 1);
            TermPosting termPosting2 = new TermPosting(value2);
            TermMerge newTermMerge =  new TermMerge(term2,termPosting2);
            ans.add(newTermMerge);
        }

        //System.out.println(ans);

        //raf1.close();
        //raf2.close();

        sc1.close();
        sc2.close();

        // List<Pair<String,List<TermData>>>
        //List<TermData> termData = new ArrayList<>();
        Map<String, List<TermData>> stringListMap = new LinkedHashMap<>();
        System.out.println("reformat");
        for (TermMerge termMerge : ans){
            //System.out.println(termMerge.m_term);
            termMerge.concatenateLists();
            stringListMap.put(termMerge.m_term,termMerge.m_termData);
        }

        //TODO: retuen or save ans
        saveMerge(mergeFile,stringListMap);
    }


    public void saveMerge(String filePath, Map<String, List<TermData>> terms) {
        //String strPostingShardPath = getPath("Merge",0);
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
