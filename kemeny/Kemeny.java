package Kemeny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.javatuples.Pair;
import Kemeny.VoteParser;
import Kemeny.Permutation;



public class Kemeny extends prunedkemeny.AbstractDemocracyClass {
    public String getName() {return "Kemeny";}
    int numVoters;
    int numCandidates;
    // Number of voters to prune out
    int numByzantine = 2;
    // List of all Candidates -- order does not matter
    ArrayList<String> candidateList;
    // voterData[i] is voter i's ranking
    ArrayList<ArrayList<String>> voterData;
    // set of all permutations of K candidates
    ArrayList<ArrayList<String>> P;
    //ballot[(a,b)] is # of voters that prefer candidate a to candidate b
    HashMap<Pair, Integer> ballot;
    // agreed upon ballot of n votes
    ArrayList<String> B;
    int maxScore = 0;
    ArrayList<String> maxRank = null;

    
    /*
    *  1. Parses the input file to populate voterData
    *  2. Computes all permutations (P) of the candidateList
    *  3. Tallys the votes inot this.ballot for pairwise comparison
    */
    public Kemeny(String fname, int numByzantine) throws Exception {
        VoteParser vp = new VoteParser(fname);
        setVoterDataAndPermutation(vp.parseVotes(), numByzantine);
    }

    public Kemeny() {

    }

    public ArrayList<String> run (ArrayList<ArrayList<String>> voterData, int numByzantine) {
        setVoterDataAndPermutation(voterData, numByzantine);
        return this.run();
    }

    private void setVoterDataAndPermutation(ArrayList<ArrayList<String>> voterData, int numByzantine) {
        this.voterData = voterData;
        this.numByzantine = numByzantine;
        this.numVoters = this.voterData.size();
        this.candidateList = this.voterData.get(0); // Order does not matter, just grab first
        this.numCandidates = this.candidateList.size();
        // compute P.
        Permutation permutation = new Permutation();
        this.P = permutation.performPermute(this.candidateList);
        this.maxScore = 0;
        this. maxRank = null;
        tallyVotes();
    }

    /*
    *  Create ballot of pairwise comparisons.
    *  ballot[(a,b)] is the number of candidates that prefer a to b
    */
    private void tallyVotes() {
        // Starting point: all pairs 0
        this.ballot = new HashMap<>();
        String c1, c2;
        for (int i=0; i<this.numCandidates; i++) {
            c1 = this.candidateList.get(i);
            for (int j=0; j<this.numCandidates; j++) {
                if (i != j) {
                    c2 = this.candidateList.get(j);
                    Pair<String, String> pair = new Pair<>(c1, c2);
                    this.ballot.put(pair, 0);
                }
            }
        }
        
        // iterate over voter data & populate pairwise prefs
        ArrayList<String> voterRank;
        int pairwiseCount;
        for (int voterIndex=0; voterIndex<this.numVoters; voterIndex++) {
            voterRank = this.voterData.get(voterIndex);
            for (int i=0; i<this.numCandidates; i++) {
                c1 = voterRank.get(i);
                for (int j=i+1; j<this.numCandidates; j++) {
                    c2 = voterRank.get(j);
                    Pair<String, String> pair = new Pair<>(c1, c2);
                    pairwiseCount = this.ballot.get(pair);
                    this.ballot.put(pair, pairwiseCount + 1);
                }
            }
        }
    }
    
    /*
    *  returns the Kemeny-Young score of a particular ranking an a ballot.
    */
    private int KemenyYoungScore(ArrayList<String> ranking, HashMap<Pair, Integer> ballot) {
        int score = 0;
        //for each pair (a≻b) in ranking:
        //    score += # of occurrences of a≻b in ballot;
        String c1, c2;
        for (int i=0; i<ranking.size(); i++) {
            c1 = ranking.get(i);
            for (int j=i+1; j<ranking.size(); j++) {
                c2 = ranking.get(j);
                Pair<String, String> pair = new Pair<>(c1, c2);
                score = score + this.ballot.get(pair);
            }
        }
        if (ranking.size() != this.numCandidates) {
            throw new IllegalArgumentException("Kemeny: KemenyYoungScore : ranking : Arrays not same size");
        }
        return score;
    }
    
    /*
    *  Run Pruned Kemeny:
    *    Find the ranking with the maximum Kemeny Young Score & return it.
    */
    public ArrayList<String> run() {
        ArrayList<String> r;
        int score;
        for(int i=0; i<this.P.size(); i++) {
            r = P.get(i);
            if (r.size() != this.numCandidates) {
                throw new IllegalArgumentException("Kemeny: run : r : Arrays not same size");
            }

            // Non-Pruned Kemeny
            score = KemenyYoungScore(r, this.ballot);
            
            if (score > this.maxScore) {
                this.maxScore = score;
                this.maxRank = r;
            }
        }
        if (this.maxRank.size() != this.numCandidates) {
            throw new IllegalArgumentException("Kemeny: run : maxRank : Arrays not same size");
        }
        return maxRank;
    }
    
    /*
    * args[0] is number of suspected Byzantine voters to filter out
    * args[1] is path to input file
    */
    public static void main(String[] args) throws Exception {
        int numByzantine = Integer.parseInt(args[0]);
        Kemeny pk = new Kemeny(args[1], numByzantine);
        ArrayList<String> result = pk.run();
        System.out.println(Arrays.toString(result.toArray()));
    }
    
}
