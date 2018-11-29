package measure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.javatuples.Pair;

/*
Main method creates a VoterData which will generate 50 voter preferences for each candidate number and good probability
Then it loops through all that to calculate the average distance of the 50 voter prefs from the ideal
Finally it prints the coordinates of good probability and average distance for each algorithm for each candidate count
 */
public class TestResults {
    final static VoterData data = new VoterData();

    public static void main(String[] args) throws Exception {
        System.out.println("Ideal Order: " + Arrays.toString(data.CollectedBallots.toArray())); // prints each ResultsByCandidateCount which prints the ideal order
        System.out.println("Good Probabilities: " + Arrays.toString(data.goodProbabilities.toArray()));

        for (VoterData.ResultsForCandidateCount candidateCount : data.CollectedBallots) {

            ArrayList<Pair<Integer, Double>> coordinates = new ArrayList<>(); // plot points for good probability and average distance

            for (VoterData.ResultsByGoodProbability goodProb : candidateCount.VoterDataCollection) {
                int[] distances = new int[data.aggregateBallots];
                for (ArrayList<ArrayList<String>> election : goodProb.elections) {
                    prunedkemeny.PrunedKemeny pk = new prunedkemeny.PrunedKemeny(election, data.badVoters);
                    ArrayList<String> result = pk.run();
                    distances[election.indexOf(election)] = findDistance(candidateCount.idealOrder, result);
                }
                /// shamelessly copied from https://www.baeldung.com/java-array-sum-average
                double avgDistance =  Arrays.stream(distances).average().orElse(Double.NaN);
                coordinates.add(new Pair<Integer, Double>(goodProb.probability, avgDistance));
            }
            candidateCount.RecordAlgorithmResults("Pruned Kemeny", coordinates);

            System.out.println("For " + candidateCount.count + "candidates:");
            System.out.println(Arrays.toString(candidateCount.GetAllAlgorithmResultsForLatex().toArray()));
            // this should have printed something easily portable to https://www.overleaf.com/learn/latex/Pgfplots_package
            System.out.println("-----");

        }
    }

    private static int findDistance(ArrayList<String> ideal, ArrayList<String> actual) {
        // Thank you Kelsey
        return prunedkemeny.PrunedKemeny.distance(ideal, actual);
    }
}
