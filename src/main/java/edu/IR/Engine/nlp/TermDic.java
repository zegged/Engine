package edu.IR.Engine.nlp;


import java.io.Serializable;

/**
 * This class creates an object for a Term to save in the Dictionary from the corpus' documents
 */
public class TermDic implements Comparable,Serializable {
    String name;
    int apperances;
    int pointer;//row number in posting file
    int numOfDocs;

    /**
     * the constructor
     * @param name the term string
     * @param appearances number of appearances in corpus
     * @param linePointer pointer to the line in posting file or cache
     * @param num number of docs that the term appear in
     */
    public TermDic(String name, int appearances,int linePointer,int num) {
        this.name = name;
        this.apperances = appearances;
        this.pointer=linePointer;
        this.numOfDocs=num;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public int getNumOfDocs() {
        return numOfDocs;
    }

    public void setNumOfDocs(int numOfDocs) {
        this.numOfDocs = this.numOfDocs+numOfDocs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApperances() {
        return apperances;
    }

    public void setApperances(int apperances) {
        this.apperances = this.apperances+apperances;
    }

    /**
     * this method is a compare for two terms according to the alphabet order
     * @param o - anooter TermDic
     * @return the order between the terms
     */
    @Override
    public int compareTo(Object o) {
        if(this.numOfDocs==((TermDic)o).getNumOfDocs())
            return 0;
        else if(this.numOfDocs>((TermDic)o).getNumOfDocs())
            return 1;
        else
            return -1;
    }
}