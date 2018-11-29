package measure;

import java.lang.reflect.Array;
import java.util.*;

import com.sun.org.apache.xerces.internal.impl.dv.xs.IntegerDV;
import org.javatuples.Pair;

import javax.print.DocFlavor;
import javax.xml.transform.Result;

public class VoterData {
    // There will be 6 different charts output, one for 3, 4, 5, 6, 7, and 8 candidates
    // The ideal order varies with the number of candidates and is simply alphabetical
    // Each chart runs 50 elections at each of the 8 good probabilities .55 .60 .65 .70 .75 .80 .85 .9
    // each election involves 100 voters and 33 bad voters
    private final int totalVoters = 100;
    public final int badVoters = 33; // must be less than a third of total
    private final int goodVoters = totalVoters - badVoters;
    public final int aggregateBallots = 50;
    private final int badProb = 90;
    private final int goodProbMin = 55;
    private final int goodProbMax = 90;
    private final int goodProbIncrement = 5;
    private final int minCandidates = 3;
    private final int maxCandidates = 8; // they'll be named alphabetically automatically so don't go over 26 unless you tweak how they're named
    public final ArrayList<Integer> goodProbabilities = new ArrayList<>();
    private Random random = new Random();

    // Collected Ballots [Good Probabilities] [ Elections 0 to 49] [ Voter Preferences in order 0 to candidate count-1]
    // This started out just a big matrix of lists but i made objects instead because i think i'll need extra properties later
    public final ArrayList<ResultsForCandidateCount> CollectedBallots = new ArrayList<>();


    // The number of candidates for the test is the highest level we have, it informs the graphs that will be in the paper
    // Printing to string returns the ideal order of candidates
    class ResultsForCandidateCount {
        final int count;
        final ArrayList<String> idealOrder;
        final ArrayList<ResultsByGoodProbability> VoterDataCollection = new ArrayList<>();
        final private HashMap<String, ArrayList<Pair<Integer, Double>>> algorithmCoordinates = new HashMap<>();

        public void RecordAlgorithmResults (String nameOfAlgorithm, ArrayList<Pair<Integer, Double>> points) {
            algorithmCoordinates.put(nameOfAlgorithm, points);
        }

        public ArrayList<String> GetAllAlgorithmResultsForLatex () {
            ArrayList<String> returnVal = new ArrayList<>();

            algorithmCoordinates.forEach((k, v) ->
                    returnVal.add("coordinates { " + Arrays.toString(v.toArray()) + "}; \\legend{" + k + "}")
                    );

            return returnVal;
        }

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
        HashSet<Pair<String, String>> idealPairs = new HashSet<>();

        public Election(ArrayList<String> idealOrder, int goodProb, int numOfCandidates) {

            ArrayList<Pair<String, String>> idealPairs = SplitRankedListToPairs(idealOrder);

            for (int i = 0; i < totalVoters; i++) {

                ArrayList<Pair<String, String>> voterPrefPairs = new ArrayList<>();

                if (i < goodVoters) { // do good
                    for (Pair<String, String> pair : idealPairs) {
                        if (random.nextInt(totalVoters) < goodProb) {
                            // Match the ideal order for the pair
                            voterPrefPairs.add(pair);
                        } else {
                            // Invert the ideal order for the pair
                            voterPrefPairs.add(new Pair<String,String>(pair.getValue0(), pair.getValue1()));
                        }
                    }
                }
                else { // do evil
                    for (Pair<String, String> pair : idealPairs) {
                        if (random.nextInt(totalVoters) < badProb) { // set very high to 90
                            // INVERT ideal order for the pair
                            voterPrefPairs.add(new Pair<String,String>(pair.getValue0(), pair.getValue1()));
                        } else {
                            // Match ideal order for the pair
                            voterPrefPairs.add(pair);
                        }
                    }
                }

                electionData.add(MergePrefPairsToOrderedList(voterPrefPairs));
            }
            // mix up good and bad voters just in case order affects one of the algorithms
            Collections.shuffle(electionData);

        }

        private ArrayList<String> MergePrefPairsToOrderedList(ArrayList<Pair<String, String>> voterPrefPairs) {
            // for each pair count the number of each of the first values
            // order them into a list by frequency, highest first
            HashMap<String, Integer> winnerCounts = new HashMap<>();
            for (Pair<String, String> pair : voterPrefPairs) {
                if (winnerCounts.containsKey(pair.getValue0())) {
                    winnerCounts.replace(pair.getValue0(), winnerCounts.get(pair.getValue0()) + 1);
                } else {
                    // Now that i write this i understand why Kelsey was doing it in her vote tally
                    winnerCounts.put(pair.getValue0(), 0);
                }
            }
            SortedSet<String> ordered = new SortedSet<String>() {
                // TODO: figure out how to do this (hint: it won't work like i've planned)
            }
        }

        // Thanks Kelsey
        private ArrayList<Pair<String, String>> SplitRankedListToPairs(ArrayList<String> idealOrder) {
            ArrayList<Pair<String, String>> returnList = new ArrayList<>();

            for (int i = 0; i < idealOrder.size(); i++ ) {
                String c1 = idealOrder.get(i);
                for (int j = i + 1; j < idealOrder.size(); j++ ) {
                    String c2 = idealOrder.get(j);
                    returnList.add(new Pair<String, String>(c1, c2));
                }
            }
            return returnList;
        }

    }




    // Create all the election data and voter preferences in memory
    // Some separate cass or method will pipe it into the different algorithms and calculate distances from ideal
    public void VoterData () {
        for (int i = minCandidates; i < maxCandidates + 1; i++) {
            CollectedBallots.add(new ResultsForCandidateCount(i)); // we need this later to populate the results into it
        }

        for (int i = goodProbMin; i < goodProbMax + 1; i = i + goodProbIncrement) {
            goodProbabilities.add(i);
        }

        for (ResultsForCandidateCount candCount : CollectedBallots) {

            /* I tried to use the vote tally hashmap and pair stuff from Kelsey's Pruned Kemeny here,
            but Pairs considers A B != B A and so it won't work for what i need here.
             */

            for (Integer goodProb : goodProbabilities) {
                ResultsByGoodProbability results = new ResultsByGoodProbability(goodProb);

                for (int i = 0; i < aggregateBallots; i++) { // 50 runs
                    Election e = new Election(candCount.idealOrder, goodProb, candCount.count);
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
}

