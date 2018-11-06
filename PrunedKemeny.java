package prunedkemeny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import org.javatuples.Pair;


public class PrunedKemeny {
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
    //HashMap<String, HashMap<String, Integer>> ballot;
    HashMap<Pair, Integer> ballott;
    ArrayList<String> B; // agreed upon ballot of n votes
    int maxScore = 0;
    ArrayList<String> maxRank = null;
    
    public PrunedKemeny(String fname, int numByzantine) throws Exception {
        VoteParser vp = new VoteParser(fname);
        this.voterData = vp.parseVotes();
        this.numByzantine = numByzantine;
        this.numVoters = this.voterData.size();
        this.candidateList = this.voterData.get(0); // Order does not matter, just grab first
        this.numCandidates = this.candidateList.size();
        // compute P.
        Permutation permutation = new Permutation(); 
        this.P = permutation.performPermute(this.candidateList);
        tallyVotes();
    }
    
    private void tallyVotes() {
        // Starting point: all pairs 0
        this.ballott = new HashMap<>();
        String c1, c2;
        for (int i=0; i<this.numCandidates; i++) {
            c1 = this.candidateList.get(i);
            for (int j=0; j<this.numCandidates; j++) {
                if (i != j) {
                    c2 = this.candidateList.get(j);
                    Pair<String, String> pair = new Pair<>(c1, c2);
                    this.ballott.put(pair, 0);
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
                    pairwiseCount = this.ballott.get(pair);
                    this.ballott.put(pair, pairwiseCount + 1);
                }
            }
        }
    }
    
    // return Kendall tau distance between two permutations
    public static int distance(ArrayList<String> a, ArrayList<String> b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Arrays not same size");
        }
        int distance = 0;
        List<String> blist = b;
        // for every pair i,j in a
        for (int i=0; i<a.size(); i++) {
            for (int j=i+1; j< a.size(); j++) {
                // if i after j, distance++
                if (blist.indexOf(a.get(i)) > blist.indexOf(a.get(j))) {
                    distance++;
                }
            }
        }
        return distance;
    }
    
    private HashMap<Pair, Integer> pruneBallot(ArrayList<String> r, int F) { 
        HashMap<Pair, Integer> prunedBallot = new HashMap<>();
        // clone ballot to prunedBallot.
        prunedBallot = (HashMap<Pair, Integer>) this.ballott.clone();

        // Clone voterData
        ArrayList<ArrayList<String>> voterDataCopy = new ArrayList<>();
        for (int i=0; i<this.voterData.size(); i++) {
            voterDataCopy.add((ArrayList<String>) this.voterData.get(i).clone());
        }
        
        // Find the F most distant ranks from r & prune
        int maxDist;
        int maxIndex;
        ArrayList<String> maxRank;
        //copy of voterData
        for (int f=0; f<F; f++) {
            maxDist = 0;
            maxIndex = 0;
            maxRank = voterDataCopy.get(0);
            for (int i=0; i<voterDataCopy.size(); i++) {
                if (distance(r, voterDataCopy.get(i)) > maxDist) {
                    maxDist = distance(r, voterDataCopy.get(i));
                    maxIndex = i;
                }
            }
            // splice voterDataCopy & remove ith rank
            voterDataCopy.remove(maxIndex);
            // subtract 1 from prunedBallot for all pairs in removed rank
            HashMap<String, Integer> innerMap;
            String c1, c2;
            for (int i=0; i<maxRank.size(); i++) {
                c1 = maxRank.get(i);
                for (int j=i+1; j<maxRank.size(); j++) {
                    c2 = maxRank.get(j);
                    Pair<String, String> pair = new Pair<>(c1, c2);
                    prunedBallot.put(pair, prunedBallot.get(pair)-1);
                }
            }
        }       
        return prunedBallot;
    }
    
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
                score = score + this.ballott.get(pair);
            }
        }
        return score;
    }
    
    public ArrayList<String> run() {
        ArrayList<String> r;
        int score;
        HashMap<Pair, Integer> prunedBallot;
        for(int i=0; i<this.P.size(); i++) {
            r = P.get(i);
            
            // Non-Pruned Kemeny
            //score = KemenyYoungScore(r, this.ballott);
            
            // Pruned Kemeny
            prunedBallot = pruneBallot(r, this.numByzantine);
            score = KemenyYoungScore(r, prunedBallot);
            if (score > this.maxScore) {
                this.maxScore = score;
                this.maxRank = r;
            }
        }
        return maxRank;
    }
    
    /*
    * args[0] is number of suspected Byzantine voters to filter out
    * args[1] is path to input file
    */
    public static void main(String[] args) throws Exception {
        int numByzantine = Integer.parseInt(args[0]);
        PrunedKemeny pk = new PrunedKemeny(args[1], numByzantine);
        ArrayList<String> result = pk.run();
        System.out.println(Arrays.toString(result.toArray()));
    }
    
}
