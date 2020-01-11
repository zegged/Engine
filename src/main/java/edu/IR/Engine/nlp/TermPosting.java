package edu.IR.Engine.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermPosting {

    List<TermData> m_postingList;

    public TermPosting(String unParsedPostingList){
        m_postingList=parse(unParsedPostingList);

    }


    private List<TermData> parse(String unParsedPostingList) {
        List<TermData> posting = new ArrayList<TermData>();

        //System.out.println(unParsedPostingList);


        String regexpStr = "([0-9]+)\\-\\>([0-9]+)";
        String inputData = unParsedPostingList.trim();

        Pattern regexp = Pattern.compile(regexpStr);
        Matcher matcher = regexp.matcher(inputData);
        while (matcher.find()) {
            MatchResult result = matcher.toMatchResult();

            //  String res = result.group(0);
            Integer frequency =  Integer.valueOf(result.group(1));
           Integer document = Integer.valueOf(result.group(2));


            TermData termData = new TermData(document,frequency);
            posting.add(termData);
        }
        return posting;
    }

    @Override
    public String toString() {
        return m_postingList.toString();
    }
}
