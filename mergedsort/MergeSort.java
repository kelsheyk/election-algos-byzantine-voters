package mergedsort;

import java.util.*;

import org.javatuples.Pair;
import prunedkemeny.VoteParser;
import prunedkemeny.AbstractDemocracyClass;

public class MergeSort extends prunedkemeny.AbstractDemocracyClass {
    public String getName() {return "PrunedmSort";}
    public String getColor() {return "green";}
    public String getMark() {return "o";}
    public ArrayList<ArrayList<String>> voterData;
    private Integer numCandidates;
    private ArrayList<String> candidateList;
    private HashMap<Pair, Integer> ballot;
    private Integer numVoters;
    protected Integer numByzantine;


    public MergeSort() {

    }

    public MergeSort(ArrayList<ArrayList<String>> voterData, Integer numByzantine) {
        this.voterData = voterData;
        this.numByzantine = numByzantine;
    }

    public ArrayList<String> run(ArrayList<ArrayList<String>> voterData, int numByzantine) {
        this.voterData = voterData;
        this.numByzantine = numByzantine;
        return this.run();

    }

    public ArrayList<String> run() {
        this.candidateList = this.voterData.get(0);
        this.numCandidates = this.candidateList.size();
        this.numVoters = this.voterData.size();
        this.ballot = new HashMap<>();
        this.tallyVotes();
        ArrayList<String> sortedCandidates;
        if (this.numByzantine == 0) {
            sortedCandidates = sortCandidates();
        } else {
            ArrayList<Integer> badVoterIndexes = findBadVoters();
            this.tallyVotes(badVoterIndexes);
            sortedCandidates = sortCandidates();

        }
        // output(sortedCandidates);
        return sortedCandidates;

    }

    private static void output(ArrayList<String> sortedCandidates) {
        System.out.println(Arrays.toString(sortedCandidates.toArray()));
    }

    private ArrayList<String> sortCandidates() {
        ArrayList<String> sortedCandidates;
        sortedCandidates = mergeSort(this.candidateList);
        return sortedCandidates;
    }
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
    private boolean pathExists(String l, String r, ArrayList<Pair> pairs) {
        Pair<String, String> p = new Pair<>(l, r);
        if (pairs.indexOf(p) >=0) {
            assert true; //todo delete me
            return true;
        }
        pairs.remove(p);
        for (Pair pair: pairs) {
            String c1 = (String) pair.getValue0();
            String c2 = (String) pair.getValue1();
            if (c1.equals(l)) {
                if (pathExists(c2, r, pairs)) {
                    assert true; //todo delete me
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
                badVoters[voterIndex] += en.getValue();
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
                    max = badVoters[j];
                    maxIndex = j;
                }
            }
            badVoters[maxIndex] = 0;
            badVoterIndexes.add(maxIndex);
        }
        return badVoterIndexes;
    }

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
            String firstLeft = left.get(0);
            String firstRight = right.get(0);
            // generate some random int
            // if the random int > .9
            if (this.ballot.get(new Pair<>(firstLeft, firstRight)) >= this.ballot.get(new Pair<>(firstRight, firstLeft))) {
                result.add(firstLeft);
                left.remove(0);
                // if random int < .9
            } else {
                result.add(firstRight);
                right.remove(0);
            }
        }

        while (left.size() != 0) {
            String firstLeft = left.remove(0);
            result.add(firstLeft);
        }
        while (right.size() != 0) {
            String firstRight = right.remove(0);
            result.add(firstRight);
        }
        return result;
    }

    private void tallyVotes(ArrayList<Integer> badVoterIds) {
        // Starting point: all pairs 0
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
            if (badVoterIds.indexOf(voterIndex) < 0) {
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
        MergeSort ms = new MergeSort(parsedVotes, Integer.parseInt(args[0]));
        ms.run();
    }

}
