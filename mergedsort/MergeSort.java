package mergedsort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Tuple;
import prunedkemeny.VoteParser;

public class MergeSort {
    public ArrayList<ArrayList<String>> voterData;
    private Integer numCandidates;
    private ArrayList<String> candidateList;
    private HashMap<Tuple, Integer> ballot;
    private Integer numVoters;
    public MergeSort(ArrayList<ArrayList<String>> voterData) {
        this.voterData = voterData;
    }

    public void run() {
        this.candidateList = this.voterData.get(0);
        this.numCandidates = this.candidateList.size();
        this.numVoters = this.voterData.size();
        this.ballot = new HashMap<>();
        this.tallyVotes();
        ArrayList<String> sortedCandidates = sortCandidates();
        System.out.println(sortedCandidates.get(0));

    }

    private ArrayList<String> sortCandidates() {
        ArrayList<String> sortedCandidates;
        sortedCandidates = mergeSort(this.candidateList);
        return sortedCandidates;
    }

    /**

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
     */

    private ArrayList<String> mergeSort(ArrayList<String> list) {
        if (list.size() == 1) {
            return list;
        }

        ArrayList<String> left = new ArrayList<>();
        ArrayList<String> right = new ArrayList<>();

        for (int i=0; i < list.size(); i++) {
            if (i < list.size() / 2) {
                left.add(list.get(i));
            } else {
                right.add(list.get(i));
            }
        }

        left = this.mergeSort(left);
        right = this.mergeSort(right);

        return this.merge(left, right);

    }

    private ArrayList<String> merge(ArrayList<String> left, ArrayList<String> right) {
        ArrayList<String> result = new ArrayList<>();

        while (left.size() != 0 && right.size() != 0) {
            String firstLeft = left.remove(0);
            String firstRight = right.remove(0);
            if (this.ballot.get(new Pair<>(firstLeft, firstRight)) >= this.ballot.get(new Pair<>(firstRight, firstLeft))) {
                result.add(firstLeft);
            } else {
                result.add(firstRight);
            }
        }

        while (left.size() != 0) {
            String firstLeft = result.remove(0);
            result.add(firstLeft);
        }
        while (right.size() != 0) {
            String firstRight = result.remove(0);
            result.add(firstRight);
        }
        return result;
        }

    private void tallyVotes() {
        // Starting point: all pairs 0
        String c1, c2;
        for (int i=0; i<this.numCandidates; i++) {
            c1 = this.candidateList.get(i);
            for (int j=0; j<this.numCandidates; j++) {
                if (i != j) {
                    assert true;  // todo delete me
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
                    assert true; // todo delete me
                    pairwiseCount = this.ballot.get(pair);
                    this.ballot.put(pair, pairwiseCount + 1);
                }
            }
        }
    }


    public static void main(String[] args) {
        VoteParser vp = new VoteParser(args[1]);
        ArrayList<ArrayList<String>> parsedVotes;
        try {
            parsedVotes = vp.parseVotes();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        MergeSort ms = new MergeSort(parsedVotes);
        ms.run();
    }

}
