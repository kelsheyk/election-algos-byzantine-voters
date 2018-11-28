# Simulate, measure, and compare the various election algorithms

## Goals
1. Reproduce the experimental results from the Pruned-Kemeny paper for Pruned-Kemeny and Kemeny
2. Determine if/when there is a quality-tradeoff with our algorithm compared to Pruned-Kemeny


## Definitions and Assumptions

Since we need to reproduce the results, our values here are drawn from the Pruned-Kemeny paper so we can reproduce their results and directly compare our new algorithm.

* Ideal ranking: a predefined ranking declared as "ideal" such as A > B > C
* n: number of voters, 100
* f: number of byzantine voters, 33 which is as high as possible
* Ballot: collection of the ranking from all 100 voters, really an aggregate ballot
* Good probability: chance a voter's order for 2 candidates matches ideal, varies between .55 to .90 in increments of .05
* Bad probability: chance a byzantine voter's order for 2 candidates is opposite of ideal, set to .9
* Distance from ideal: number of pairs in different order than ideal, e.g. abc and bac has a difference of 1, a and b. b>c in both and a>c in both.
* Average distance from ideal: average of 50 ballots at a given good probability and number of candidates
* Number of candidates: 3 to 8 (potentially more)
* Other assumptions maintained from Pruned Kemeny experiment:
    * one round of voting
    * voter rankings are transmitted securely, good voters have true rankings
* Graphing the results
    * Y: average distance from ideal
    * X: good probability
    * Points: each algorithm
    * New chart for each number of candidates
    * Might be able to merge the separate charts since we only have 3 algorithms


## Constructing the ballots, aka Voter Data
* for 1-77 times and for each candidate pair, match the ideal order a percent of the time that matches the good probability, otherwise the opposite order
* for 1-33 times and for each candidate pair, invert the ideal order 90 percent of the time, otherwise the ideal order
* shuffle the order of the ballots
* repeat 50 times so we have 50 aggregate ballots per candidate count and good probability

## Calculating Distance from Ideal
* Compare an output rank to the ideal rank pairwise, any pairs that don't match the ideal order count for 1 distance
* Re-use function from PrunedKemeny: public static int distance(ArrayList<String> a, ArrayList<String> b) ????
* No that's kendall tau distance, need to use pairwise

## Algo Runner

* Input: Voter Data, one algorithm name
* Output: ranking

## Algo Combiner

* Input: List of Algorithms to test
* private properties:
    * generated voter data for each increment in the range of good probability and each candidate count
* Output: for each candidate count for each good probability, for each algorithim, average distance from ideal

## Hypothesis

* Kemeny and Pruned are similar to results from paper
* The pruned versions of our less sophisticated (but faster) algorithms outperform the normal versions and underperform Pruned Kemeny
