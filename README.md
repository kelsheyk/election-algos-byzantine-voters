# election-algos-byzantine-voters


Input File Format:
Each line in the file represents a single voter's ranking of candidates. Example:
a,b,c
c,b,a
a,c,b
This represents 3 different voter's rankings of candidates a, b, & c.
The input file format can be easily altered by modifying the VoteParser class.


To run Pruned Kemeny:
>> java PrunedKemeny <F> <inputFileName>
F: The number of suspected byzantine voters to prune.
inputFileName: Input file of voter rankings as described above


Assumptions:
There is a strict order on candidate preference -- a voter cannot be indifferent between 2 candidates.
Every voter ranks all candidates.

