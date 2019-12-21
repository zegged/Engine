package edu.IR.Engine.nlp;

import java.util.ArrayList;
import java.util.List;

public class TermMerge {

    List<TermData> m_termData;
    String m_term;
    TermPosting termPosting1;
    TermPosting termPosting2 = null;

    public TermMerge(String term, TermPosting termPosting1, TermPosting termPosting2) {
        //m_termData = concatenateLists(termPosting1,termPosting2);
        m_term = term;
        this.termPosting1 = termPosting1;
        this.termPosting2 = termPosting2;

    }

    public TermMerge(String term1, TermPosting termPosting1) {
        this.termPosting1 = termPosting1;
        m_term = term1;
    }


    public void concatenateLists() {
        m_termData = new ArrayList<>();
        m_termData.addAll(termPosting1.m_postingList);
        if (termPosting2 != null) {
            m_termData.addAll(termPosting2.m_postingList);
        }
        //return newTermData;

    }

    public String toString(){
        return m_term + m_termData.toString() + System.lineSeparator();
    }


}
