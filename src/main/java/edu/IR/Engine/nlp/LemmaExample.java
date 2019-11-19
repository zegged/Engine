package edu.IR.Engine.nlp;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;

public class LemmaExample {

    public static void main(String args[]){
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();

        String text = "these were few yet strangely upsetting sentence. Has it been gone some other way I'd be glad. doing being funny babies";

        CoreDocument coreDocument = new CoreDocument(text);

        stanfordCoreNLP.annotate(coreDocument);

        List<CoreLabel> coreLabelList = coreDocument.tokens();

        for (CoreLabel coreLabel : coreLabelList){
            String lemma = coreLabel.lemma();
            System.out.println(coreLabel.originalText() + " = " + lemma);
        }
    }
}
