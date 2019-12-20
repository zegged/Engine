package edu.IR.Engine.nlp;

import edu.stanford.nlp.util.Triple;

import java.util.ArrayList;
import java.util.List;

public class TermPointers {
    String m_termName;
    //  filename ptr-start ptr-end
    List<Triple<String,Long,Long>> m_pointers;

    public TermPointers(String term){
        m_termName = term;
        m_pointers = new ArrayList<>();
    }

    public void add(String fileNameToSave, Long plStart, Long plEnd) {
        m_pointers.add(new Triple<>(fileNameToSave,plStart,plEnd));
    }

    public String toString(){
        return m_termName + " : " + m_pointers;
    }
}
