package edu.IR.Engine.nlp;

public class TermData {

    Integer document;
    Integer frequency;

    public TermData(Integer document, Integer frequency){
        this.document=document;
        this.frequency=frequency;
    }

    public String toString()
    {
        return frequency+"->"+document;
    }


}
