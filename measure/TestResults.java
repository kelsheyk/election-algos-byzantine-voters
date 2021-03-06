package measure;

import java.util.ArrayList;
import java.util.Arrays;

import Kemeny.Kemeny;
import PrunedRankedPairs.PrunedRankedPairs;
import RankedPairs.RankedPairs;
import mergedsort.MergeSort;
import mergedsort.MergeSortUnpruned;
import org.javatuples.Pair;
import prunedkemeny.PrunedKemeny;
import measure.VoterData;

/*
Main method creates a VoterData which will generate 50 voter preferences for each candidate number and good probability
Then it loops through all that to calculate the average distance of the 50 voter prefs from the ideal
Finally it prints the coordinates of good probability and average distance for each algorithm for each candidate count
 */
public class TestResults {
    final static VoterData data = new VoterData();

    public static void main(String[] args) throws Exception {
        ArrayList<prunedkemeny.AbstractDemocracyClass> algorithms = new ArrayList<>();
        algorithms.add(new MergeSort());
        algorithms.add(new MergeSortUnpruned());
        algorithms.add(new PrunedRankedPairs());
        algorithms.add(new RankedPairs());
        algorithms.add(new Kemeny());
        algorithms.add(new PrunedKemeny());

        System.out.println("Ideal Order: " + Arrays.toString(data.CollectedBallots.toArray())); // prints each ResultsByCandidateCount which prints the ideal order
        System.out.println("Good Probabilities: " + Arrays.toString(data.goodProbabilities.toArray()));

        for (VoterData.ResultsForCandidateCount candidateCount : data.CollectedBallots) {
            System.out.println(" ## For " + candidateCount.count + " candidates: ## ");
            for (int a = 0; a < algorithms.size(); a++) { // using for loop instead of foreach so they run in order so kemeny is always last
                prunedkemeny.AbstractDemocracyClass algo = algorithms.get(a);
                ArrayList<Pair<Integer, Double>> coordinates = new ArrayList<>(); // plot points for good probability and average distance
                for (VoterData.ResultsByGoodProbability goodProb : candidateCount.VoterDataCollection) {
                    int[] distances = new int[data.aggregateBallots];
                    for (int i = 0; i < data.aggregateBallots; i++) {
                        ArrayList<String> result = algo.run(goodProb.elections.get(i), data.badVoters);
                        distances[i] = findDistance(candidateCount.idealOrder, result);
                    }
                    /// Thanks https://www.baeldung.com/java-array-sum-average
                    double avgDistance =  Arrays.stream(distances).average().orElse(Double.NaN);
                    coordinates.add(new Pair<Integer, Double>(goodProb.probability, avgDistance));
                }
                /* print out the coordinates and algorithm name such that it can be dropped into latex like so:
                    https://www.overleaf.com/learn/latex/Pgfplots_package#Plotting_from_data
                    \addplot[color=red,mark=square,] coordinates { (55, 1.34) (60, 1.42) ... (90, 0.78) }; \addlegendentry{K}
                    I'm using both string builder and string format so the code is readable and i can split out the coords
                    Performance isn't an issue since we'll be waiting on PrunedKemeny for hours anyway
                */
                StringBuilder sb = new StringBuilder(165);
                sb.append(String.format("\\addplot[color=%s,mark=%s,] coordinates { ", algo.getColor(), algo.getMark()));
                for (int c = 0; c < coordinates.size(); c++) {
                    sb.append(coordinates.get(c)
                            .toString()
                            .replace("[", "(")
                            .replace("]",")")
                    );
                }
                sb.append(String.format(" }; \\addlegendentry{%s}", algo.getName()));
                System.out.println(sb);
            }
        }
    }

    private static int findDistance(ArrayList<String> ideal, ArrayList<String> actual) {
        // Thank you Kelsey
        return prunedkemeny.PrunedKemeny.distance(ideal, actual);
    }
}
