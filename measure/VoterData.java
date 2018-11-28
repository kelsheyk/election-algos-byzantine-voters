package measure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.javatuples.Pair;

public class VoterData {
    // There will be 6 different charts output, one for 3, 4, 5, 6, 7, and 8 candidates
    // The ideal order varies with the number of candidates and is simply alphabetical
    // Each chart runs 50 elections at each of the 8 good probabilities .55 .60 .65 .70 .75 .80 .85 .9
    // each election involves 100 voters and 33 bad voters
    private final int totalVoters = 100;
    private final int badVoters = 33; // must be less than a third of total
    private final int goodVoters = totalVoters - badVoters;
    private final int aggregateBallots = 50;
    private final int badProb = 90;
    private final int goodProbMin = 55;
    private final int goodProbMax = 90;
    private final int goodProbIncrement = 5;
    private final int minCandidates = 3;
    private final int maxCandidates = 8; // they'll be named alphabetically automatically so don't go over 26 unless you tweak how they're named
    private final ArrayList<CandidateCount> candidateCounts = new ArrayList<CandidateCount>();
    private final ArrayList<Integer> goodProbabilities = new ArrayList<Integer>();
    private final ArrayList<ArrayList<ArrayList<String>>> VoterDataCollection = new ArrayList<ArrayList<ArrayList<String>>>(); // i heard you like lists

    class CandidateCount {
        int count;
        final ArrayList<String> idealOrder;

        public CandidateCount (int count) {
            this.count = count;
            idealOrder = new ArrayList<String>();
            // 97 is 'a' on the ascii table
            for (int i = 97; i < 97+count; i++) {
                idealOrder.add(String.valueOf((char)i));
            }

        }

        @Override
        public String toString() {
            final String s = Arrays.toString(idealOrder.toArray());
            return s;
        }

    }

    // Single election of X candidates, 100 voters, and Y good probability.
    // Input is the list of candidates in ideal order and good probabability.
    // Also uses these final global variables goodVoters, badVoters, totalVoters, badProb.
    // Output is a list of voters represented by their ordered list of candidates
    class Election {
        final ArrayList<ArrayList<String>> electionData = new ArrayList<ArrayList<String>>();

        public Election(HashMap idealPairs, int goodProb, int numOfCandidates) {

            for (int i = 0; i < totalVoters; i++) {
                ArrayList<String> voterPreferences = new ArrayList<String>(numOfCandidates);
                for (int j = 0; j < numOfCandidates; j++) {

                    if (i < goodVoters) { // do good
                        // for each candidate pair, match the ideal order a percent of the time that matches the good probability, otherwise the opposite order
                    }
                    else { // do evil
                        // for each candidate pair, invert the ideal order 90 percent of the time, otherwise the ideal order

                    }
                    /* need to figure out if i need to do something like this or if i can build the voter pref list directly

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
                     */

                }
                electionData.add(voterPreferences);
            }
            // mix up good and bad voters just in case order affects one of the algorithms
            Collections.shuffle(electionData);


        }

    }


    public void run () {
        for (int i = minCandidates; i < maxCandidates + 1; i++) {
            candidateCounts.add(new CandidateCount(i));
        }

        for (int i = goodProbMin; i < goodProbMax + 1; i = i + goodProbIncrement) {
            goodProbabilities.add(i);
        }

        for (CandidateCount batch : candidateCounts) {

            // Borrowing this structure from Kelsey's PrunedKemeny
            HashMap idealPairs = new HashMap<>();
            String c1, c2;
            for (int i = 0; i < batch.idealOrder.size(); i++) {
                c1 = batch.idealOrder.get(i);
                for (int j = 0; j < batch.idealOrder.size(); j++) {
                    if (i != j) {
                        c2 = batch.idealOrder.get(j);
                        Pair<String, String> pair = new Pair<>(c1, c2);
                        idealPairs.put(pair, 0);
                    }
                }
            }

            for (Integer goodProb : goodProbabilities) {
                Election e;
                for (int i = 0; i < aggregateBallots; i++) {
                    e = new Election(idealPairs, goodProb, batch.idealOrder.size());
                    VoterDataCollection.add(e.electionData);
                    // what do i do with this data? we probably want to get out of all these loops.
                    // stash the data in memory because we'll need to loop through it to send one election at a time to a given algorithm
                    // they come back with a rank, we compare that to ideal
                    // which kind of silly we're all doing pairs but we're passing them as lists that seems redundant, oh well

                }

            }



        }





    }

    public static void main(String[] args) throws Exception {
        VoterData d = new VoterData();
        d.run();
        System.out.println(Arrays.toString(d.candidateCounts.toArray()));
        System.out.println(Arrays.toString(d.goodProbabilities.toArray()));
    }

}

