package edu.IR.Engine.nlp;

public class Ranker {
    double avgdl; //average document length
    int N; // total number of documents in the collection
    double b = 0.75; // free parameter
    double K1 = 1.2; // free parameter
    int K2;
    public Ranker(double avgdl, int N)
    {
        this.avgdl = avgdl;
        this.N = N;
        //Random rand = new Random();
        //this.K2 = rand.Next(0, 1001);

        //BM25();

    }


    // this needs to be calculate for every! term i in the query. (sigma)
    // number of documents containing qi

    public double BM25(int docLength, int ri, int ni, int R, int fi, int qfi)
    {
        double logNumerator = (ri+0.5)/(R-ri+0.5);

        double logDenominator = (ni - ri + 0.5) / (N - ni - R + ri + 0.5);
        if (logDenominator < 0)
            logDenominator = 0.0000001;
        double K = K1 * ((1 - b) + b * (docLength / avgdl));

        //double log = Math.Log10(logNumerator / logDenominator);
        double first = ((K1 + 1) * fi) / (K + fi);
        double second = ((K2 + 1) * qfi) / (K2 + qfi);

        //double result = log * first * second;


        return 0.0;
    }




}
