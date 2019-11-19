package edu.IR.Engine.nlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;

public class POSExample {

     public static void main(String args[]){
         StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
         String text = "Hello. My name is simon. I'm a house cat. 25 percent. 3+3.5\n4+4/3=9.";
         CoreDocument coreDocument = new CoreDocument(text);
         stanfordCoreNLP.annotate(coreDocument);
         List<CoreLabel> coreLabelList = coreDocument.tokens();
         for (CoreLabel coreLabel : coreLabelList){
             String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
             System.out.println(coreLabel.originalText() + " = " + pos);
         }
     }

}
