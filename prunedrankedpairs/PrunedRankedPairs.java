package PrunedRankedPairs;

import java.util.*;
import org.javatuples.Pair;
import PrunedRankedPairs.VoteParser;


public class PrunedRankedPairs extends prunedkemeny.AbstractDemocracyClass {
    int numVoters;
    int numCandidates;
    // Number of voters to prune out
    int numByzantine = 2;
    // List of all Candidates -- order does not matter
    ArrayList<String> candidateList;
    // voterData[i] is voter i's ranking
    ArrayList<ArrayList<String>> voterData;
    //ballot[(a,b)] is # of voters that prefer candidate a to candidate b
    HashMap<Pair, Integer> ballot;
    // agreed upon ballot of n votes
    ArrayList<String> B;
    int maxScore = 0;
    
    /*
    *  1. Parses the input file to populate voterData
    *  2. Tallys the votes into this.ballot for pairwise comparison
    */
     public PrunedRankedPairs(String fname, int numByzantine) throws Exception {
        VoteParser vp = new VoteParser(fname);
        setVoterData(vp.parseVotes(), numByzantine);
    }

    public PrunedRankedPairs() throws Exception {
    }

    public String getName() {return "Pruned Ranked Pairs";}

    private void setVoterData(ArrayList<ArrayList<String>> voterData, int numByzantine) {
        this.voterData = voterData;
        this.numByzantine = numByzantine;
        this.numVoters = this.voterData.size();
        this.candidateList = this.voterData.get(0); // Order does not matter, just grab first
        this.numCandidates = this.candidateList.size();
        this.ballot = tallyVotes(new ArrayList<Integer>());
    }
    
    /*
    *  Create ballot of pairwise comparisons.
    *  ballot[(a,b)] is the number of candidates that prefer a to b
    */
    private HashMap<Pair, Integer> tallyVotes(ArrayList<Integer> badVoterIds) {
        // Starting point: all pairs 0
        HashMap<Pair, Integer> b = new HashMap<>();
        String c1, c2;
        for (int i=0; i<this.numCandidates; i++) {
            c1 = this.candidateList.get(i);
            for (int j=0; j<this.numCandidates; j++) {
                if (i != j) {
                    c2 = this.candidateList.get(j);
                    Pair<String, String> pair = new Pair<>(c1, c2);
                    b.put(pair, 0);
                }
            }
        }
        
        // iterate over voter data & populate pairwise prefs
        ArrayList<String> voterRank;
        int pairwiseCount;
        for (int voterIndex=0; voterIndex<this.numVoters; voterIndex++) {
            if (badVoterIds.indexOf(voterIndex) < 0) {
                voterRank = this.voterData.get(voterIndex);
                for (int i=0; i<this.numCandidates; i++) {
                    c1 = voterRank.get(i);
                    for (int j=i+1; j<this.numCandidates; j++) {
                        c2 = voterRank.get(j);
                        Pair<String, String> pair = new Pair<>(c1, c2);
                        pairwiseCount = b.get(pair);
                        b.put(pair, pairwiseCount + 1);
                    }
                }
            }
        }
        return b;
    }
    
    private ArrayList<Integer> getOpposingVoters(String left, String right) {
        ArrayList<Integer> opposingVoters = new ArrayList();
        ArrayList<String> voterRank;
        for (int voterIndex=0; voterIndex<this.numVoters; voterIndex++) {
            voterRank = this.voterData.get(voterIndex);
            if (voterRank.indexOf(left) > voterRank.indexOf(right)) {
                opposingVoters.add(voterIndex);
            }
        }
        return opposingVoters;
    }

    
    // function to sort hashmap by values 
    private static List<Map.Entry<Pair, Integer>> sortByValue(HashMap<Pair, Integer> hm) { 
        // Create a list from elements of HashMap 
        List<Map.Entry<Pair, Integer>> list = new LinkedList<Map.Entry<Pair, Integer> >(hm.entrySet()); 

        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Pair, Integer> >() { 
            public int compare(Map.Entry<Pair, Integer> o1, Map.Entry<Pair, Integer> o2) { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        });
        
        return list;
    }
    
    private boolean pathExists(String l, String r, ArrayList<Pair> pairs) {
        Pair<String, String> p = new Pair<>(l, r);
        if (pairs.indexOf(p) >=0) {
            return true;
        }
        pairs.remove(p);
        for (Pair pair: pairs) {
            String c1 = (String) pair.getValue0();
            String c2 = (String) pair.getValue1();
            if (c1.equals(l)) {
                if (pathExists(c2, r, pairs)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean noCycle(Pair p, ArrayList<Pair> pairs) {
        String c1 = (String) p.getValue0();
        String c2 = (String) p.getValue1();
        return !(pathExists(c2, c1, pairs));
    }
    
    // build the ranking from the graph (list of pairs)
    private ArrayList<String> buildRank(ArrayList<Pair> pairs) {
        ArrayList<String> r = new ArrayList();
        for (Pair p : pairs) {
            String c1, c2;
            c1 = (String) p.getValue0();
            c2 = (String) p.getValue1();
            if (r.indexOf(c1) < 0 && r.indexOf(c2) < 0) {
                // neither in rank
                r.add(c1);
                r.add(c2);
            } else if (r.indexOf(c1) >= 0 && r.indexOf(c2) < 0) {
                // c1 in rank, c2 not. Add c2 after c1
                r.add(r.indexOf(c1) + 1, c2);
            } else if (r.indexOf(c1) < 0 && r.indexOf(c2) >= 0) {
                // c2 in rank, c1 not. Add c1 before c2
                r.add(r.indexOf(c2), c1);
            } else {
                // both in rank. if out of order, swap.
                if (r.indexOf(c1) > r.indexOf(c2)) {
                    r.set(r.indexOf(c1), c2);
                    r.set(r.indexOf(c2), c1);
                }
            }
        }
        return r;
    }
    
    /*
    *  Find top F Bad Voters by running the rankedPairs algo and
    *   tracking voters who oppose highly ranked pairs
    */
    public ArrayList<Integer> findBadVoters() {
        ArrayList<String> r = new ArrayList();
        ArrayList<Pair> visited = new ArrayList();
        ArrayList<Pair> winningPairs = new ArrayList();
        // setup badVoters
        int[] badVoters = new int[this.numVoters];
        
        // sort pairwise prefs
        List<Map.Entry<Pair, Integer>> sortedBallot = sortByValue(this.ballot); 

        for (Map.Entry<Pair, Integer> en : sortedBallot) {
            Pair pair = en.getKey();
            visited.add(pair);
            String c1, c2;
            c1 = (String) pair.getValue0();
            c2 = (String) pair.getValue1();
            
            if (noCycle(pair, winningPairs)) {
                winningPairs.add(pair);
            }
            
            // We assume that voters that opposed most prefered pairs are 
            //     more likely to be bad than voters that oppose more 
            //     contested pairs
            ArrayList<Integer> opposingVoters = getOpposingVoters(c1, c2);
            // Increment every opposing voter by the weight of the pair
            for (Integer voterIndex : opposingVoters) {
                badVoters[voterIndex]+= en.getValue();
            }
            
            Pair p = new Pair(c2, c1);
            if (visited.indexOf(p) > -1) {
                break;
            }
        }
        
        // Now we have the array of badVoters populated.
        // Get the indexes of the top F
        ArrayList<Integer> badVoterIndexes = new ArrayList();
        int max = 0;
        int maxIndex = 0;
        for (int i=0; i<this.numByzantine; i++) {
            max = 0;
            for (int j=0; j<badVoters.length; j++) {
                if (badVoters[j] > max) {
                    maxIndex = j;
                }
            }
            badVoters[maxIndex] = 0;
            badVoterIndexes.add(maxIndex);
        }
        return badVoterIndexes;
    }
    
    /*
    *  Run Ranked Pairs on data:
    */
    public ArrayList<String> runRankedPairs(HashMap<Pair, Integer> data) {
        ArrayList<String> r = new ArrayList();
        ArrayList<Pair> visited = new ArrayList();
        ArrayList<Pair> winningPairs = new ArrayList();
        
        // sort pairwise prefs
        List<Map.Entry<Pair, Integer>> sortedBallot = sortByValue(data); 
        
        for (Map.Entry<Pair, Integer> en : sortedBallot) {
            Pair pair = en.getKey();
            visited.add(pair);
            String c1, c2;
            c1 = (String) pair.getValue0();
            c2 = (String) pair.getValue1();
            
            // if the pair does not create a cycle, add it to winningPairs
            if (noCycle(pair, winningPairs)) {
                winningPairs.add(pair);
            }
                
            // stop when we encounter the reverse of an already seen pair
            Pair p = new Pair(c2, c1);
            if (visited.indexOf(p) > -1) {
                break;
            }
        }
        
        r = buildRank(winningPairs);
        
        return r;
    }
    
    /*
    *  Run Pruned Ranked Pairs:
    */
    public ArrayList<String> run(ArrayList<ArrayList<String>> voterData, int numByzantine) {
        setVoterData(voterData, numByzantine);
        return this.run();
    }

    public ArrayList<String> run() {
        // Non-Pruned
        //ArrayList<String> r = runRankedPairs(this.ballot);
        //return r;
        
        // Pruned
        ArrayList<Integer> badVoterIndexes = findBadVoters();
        HashMap<Pair, Integer> prunedData = tallyVotes(badVoterIndexes);    
        ArrayList<String> r = runRankedPairs(prunedData);
        return r;
    }
    
    /*
    * args[0] is number of suspected Byzantine voters to filter out
    * args[1] is path to input file
    */
    public static void main(String[] args) throws Exception {
        int numByzantine = Integer.parseInt(args[0]);
        PrunedRankedPairs pk = new PrunedRankedPairs(args[1], numByzantine);
        ArrayList<String> result = pk.run();
        System.out.println(Arrays.toString(result.toArray()));
    }
    
}
