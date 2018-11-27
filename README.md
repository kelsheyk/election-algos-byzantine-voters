# Election Algorithms with Byzantine Voters - Term Project

Input File Format: Each line in the file represents a single voter's ranking of candidates. Example:  
a,b,c  
c,b,a  
a,c,b  
This represents 3 different voter's rankings of candidates a, b, & c.  
The input file format can be easily altered by modifying the VoteParser class.

## Run Pruned Kemeny:

`java PrunedKemeny <F> <inputFileName>`

* F: The number of suspected byzantine voters to prune.
* inputFileName: Input file of voter rankings as described above

Example: 
`java -cp ".;Libraries/*" prunedkemeny.PrunedKemeny 2 TestCases\ThreeCandidateSevenVoter.txt`

Assumptions:

1. There is a strict order on candidate preference -- a voter cannot be indifferent between 2 candidates.
2. Every voter ranks all candidates.

## Build Pruned Kemeny

### Command Line

Windows:

1. Have java jdk bin on your path
2. Navigate to the root directory election-algos-byzantine-voters
4. Run `javac -cp ".;Libraries/*" .\prunedkemeny\*.java`

Solaris, Linux, and OS X

1. Same as above but use : instead of ; as the classpath separator both for building and running

### IntelliJ IDEA

1. Open IntelliJ IDEA
2. File -> Open -> Navigate to git root directory -> Click OK
3. Run the 2 byzantine and ThreeCandidateSevenVoter configuration
4. Add or change configurations as needed
