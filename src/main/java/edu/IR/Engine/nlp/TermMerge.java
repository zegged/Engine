package edu.IR.Engine.nlp;

import java.util.ArrayList;
import java.util.List;

public class TermMerge {

    List<TermData> m_termData;
    String m_term;

    public TermMerge(String term, TermPosting termPosting1, TermPosting termPosting2) {
        m_termData = concatenateLists(termPosting1,termPosting2);
        m_term = term;
    }

    public TermMerge(String term1, TermPosting termPosting1) {
        m_termData = termPosting1.m_postingList;
        m_term = term1;
    }


    private List<TermData> concatenateLists(TermPosting termPosting1, TermPosting termPosting2) {
        List<TermData> newTermData = new ArrayList<>();
        newTermData.addAll(termPosting1.m_postingList);
        newTermData.addAll(termPosting2.m_postingList);
        return newTermData;

    }

    public String toString(){
        return m_term + m_termData.toString() + System.lineSeparator();
    }


}
