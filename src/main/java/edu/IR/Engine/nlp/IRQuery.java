package edu.IR.Engine.nlp;

public class IRQuery {
    String title, desc, narr;
    Integer id;
    public IRQuery(String title, String id, String desc, String narr) {
        this.title = title;
        this.id = Integer.valueOf(id.split(":")[1].trim());

        this. desc = desc;
        this.narr = narr;
    }
}
