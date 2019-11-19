package edu.IR.Engine.nlp;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Indexer {
    public Indexer(String pathToPosting){
        System.out.println("Indexer init");
        //create folders

        //init dictionary
        //add method w/ limit
        //limit trigger or end of corpus for dump

        //dump dic to new file, merge at the end.


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


}
