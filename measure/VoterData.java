package measure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.javatuples.Pair;

import javax.xml.transform.Result;

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
    private final ArrayList<Integer> goodProbabilities = new ArrayList<>();

    // Collected Ballots [Good Probabilities] [ Elections 0 to 49] [ Voter Preferences in order 0 to candidate count-1]
    // This started out just a big matrix of lists but i made objects instead because i think i'll need extra properties later
    public final ArrayList<ResultsForCandidateCount> CollectedBallots = new ArrayList<>();


    // The number of candidates for the test is the highest level we have, it informs the graphs that will be in the paper
    // Printing to string returns the ideal order of candidates
    class ResultsForCandidateCount {
        final int count;
        final ArrayList<String> idealOrder;
        final ArrayList<ResultsByGoodProbability> VoterDataCollection = new ArrayList<>();

        public ResultsForCandidateCount (int count) {
           this.count = count;
            idealOrder = new ArrayList<String>();
            // 97 is 'a' on the ascii table
            for (int i = 97; i < 97+count; i++) {
                idealOrder.add(String.valueOf((char)i));
            }

           // VoterDataCollection will be added to by the class who instantiates this
           // then this will be tossed into CollectedBallots for later use by code running each algorithm
        }

        @Override
        public String toString() {
            final String s = Arrays.toString(idealOrder.toArray());
            return s;
        }

    }

    class ResultsByGoodProbability {
        final int probability;
        final ArrayList<ArrayList<ArrayList<String>>> elections = new ArrayList<>();

        public ResultsByGoodProbability (int probability) {
            this.probability = probability;
            // elections gets populated later by the instantiating class
        }
    }

    

    // Single election of X candidates, 100 voters, and Y good probability.
    // Input is the list of candidates in ideal order and good probabability.
    // Also uses these final global variables goodVoters, badVoters, totalVoters, badProb.
    // Output is a list of voters represented by their ordered list of candidates
    class Election {
        final ArrayList<ArrayList<String>> electionData = new ArrayList<>();

        public Election(HashMap idealPairs, int goodProb, int numOfCandidates) {

            for (int i = 0; i < totalVoters; i++) {
                ArrayList<String> voterPreferences = new ArrayList<>(numOfCandidates);
                for (int j = 0; j < numOfCandidates; j++) {

                    if (i < goodVoters) { // do good
                        // for each candidate pair, match the ideal order a percent of the time that matches the good probability, otherwise the opposite order
                    }
                    else { // do evil
                        // for each candidate pair, invert the ideal order 90 percent of the time, otherwise the ideal order

                    }
                    // need a step where i take the pairs and turn them back into an ordered list
                    // i'm pretty sure all our algos are going to turn the list back into pairs then back into a list, but too late now
                    /* maybe this from kelsey's pruned kemeny will help:
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


    // Create all the election data and voter preferences in memory
    // Some separate cass or method will pipe it into the different algorgitms and calculate distances from ideal
    // this should probably just be tbe public constructor
    public void run () {
        for (int i = minCandidates; i < maxCandidates + 1; i++) {
            CollectedBallots.add(new ResultsForCandidateCount(i)); // we need this later to populate the results into it
        }

        for (int i = goodProbMin; i < goodProbMax + 1; i = i + goodProbIncrement) {
            goodProbabilities.add(i);
        }

        for (ResultsForCandidateCount candCount : CollectedBallots) {

            // Borrowing this structure from Kelsey's PrunedKemeny
            // we're converting the list of ideal canditates, "A", "B", "C" into ordered pairs like:
            // "A/B" "A/C" "B/C".
            // Since pairs say "A/B" is equal to "B/A", the HashMap will prevent dupes....**??????!!!???
            // However the order is important, the first one is the voter preference over the second
            HashMap idealPairs = new HashMap<>();
            String c1, c2;
            for (int i = 0; i < candCount.count; i++) {
                c1 = candCount.idealOrder.get(i);
                for (int j = 0; j < candCount.count; j++) {
                    if (i != j) {
                        c2 = candCount.idealOrder.get(j);
                        Pair<String, String> pair = new Pair<>(c1, c2);
                        idealPairs.put(pair, 0);
                    }
                }
            }

            for (Integer goodProb : goodProbabilities) {
                ResultsByGoodProbability results = new ResultsByGoodProbability(goodProb);

                for (int i = 0; i < aggregateBallots; i++) { // 50 runs
                    Election e = new Election(idealPairs, goodProb, candCount.count);
                    results.elections.add(e.electionData);

                }
                candCount.VoterDataCollection.add(results);

            }
            /*
            *   At this point, CollectedBallots has 6 items, one for each count of candidates from 3 to 8.
            *
            *   Each candidate count VoterDataCollection list which has 7 items, one for each good probability from 55 to
            * 90 in increments of 5.
            *
            *   Each good probability item on it has an elections list which has 50 items, one for each set of voter
            * preferences we generated.
            *
            *   Each voter preferences item is a matrix of 100 lists of ordered candidate names which represent the
            * randomly generated preferences for each of 100 voters.
            *
            *   That completes the variety of input data we feed into the algorithms to get outputs to compare to ideal
            * and graph it all out. In theory we could have run this once and saved to files or generated it in some
            * spreadsheet, but neither of those would have been a lot easier for me. It's still a fairly complicated
            * script that would need to be run.
            *
             */

        }





    }

    public static void main(String[] args) throws Exception {
        VoterData d = new VoterData();
        d.run();
        System.out.println(Arrays.toString(d.CollectedBallots.toArray())); // prints each ResultsByCandidateCount which prints the ideal order
        System.out.println(Arrays.toString(d.goodProbabilities.toArray()));
    }

}

