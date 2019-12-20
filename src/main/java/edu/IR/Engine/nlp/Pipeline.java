package edu.IR.Engine.nlp;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Pipeline {

    private static Properties properties;
    //private static String propertiesNames = "tokenize,  ssplit, pos, lemma, ner";
    private static String propertiesNames = "tokenize,  ssplit";
    private static StanfordCoreNLP stanfordCoreNLP;

    private Pipeline(){

    }

    static{
        properties = new Properties();
        properties.setProperty("annotators", propertiesNames);
    }

    public static StanfordCoreNLP getPipeline(){

        if(stanfordCoreNLP==null){
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }

        return stanfordCoreNLP;
    }

}








