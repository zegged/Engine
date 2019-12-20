package edu.IR.Engine.nlp;

public class ParseResult {
    DocumentTerms documentTerms;
    DocumentData documentData;

    public ParseResult(DocumentData documentData, DocumentTerms documentTerms) {
        this.documentTerms=documentTerms;
        this.documentData=documentData;
    }
}
