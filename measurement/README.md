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
* Distance from ideal: number of pairwise orders different from ideal in a ballot
* **** need to verify i'm right this is how to calculate distance from ideal
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
* Also compare run time since our algorithm is reportedly faster


## Constructing the ballots
* for 1-77 times and for each candidate pair, match the ideal order a percent of the time that matches the good probability, otherwise the opposite order
* for 1-33 times and for each candidate pair, invert the ideal order 90 percent of the time, otherwise the ideal order
* repeat 50 times so we have 50 aggregate ballots per candidate count and good probability

## Calculating Distance from Ideal

* Need to double check this against the paper, their highest average distance from ideal is
* Compare an output rank to the ideal rank pairwise, any pairs out of order count for 1 distance from ideal
* Input: ideal rank (might be global variable), actual rank
* Output: distance from ideal // integer, maybe a float, 0 to at most 30ish

## Algo Runner

* Input: vote matrix, algo name, goodness probability??
* Output: ranking

## Algo Combiner

* Input: vote matrix, list of algorithms to run, goodness probability???
* Output: list of algorithms with name, distance from ideal
* Example: 5 candidates, .9 goodness probability, all algorithms, expected output is prunedkemeny:0, kemeny:0, polynominalpruned: 0

## Expected Results

* Kemeny and Pruned are similar to results from paper
* Polynominal Pruned Kemeny similar to pruned Kemeny
* Polynominal pruned kemeny is faster than pruned kemeny for some number of candidates
