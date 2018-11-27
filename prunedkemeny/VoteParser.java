package prunedkemeny;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class VoteParser {
    String fileName;
    
    public VoteParser(String fileName) {
        this.fileName = fileName;
    }
    
    // Parses input file where row i is
    // the comma seperated voter ranking of voter i
    // Returns ArrayList<ArrayList<String>> voterData;
    //         where voterData[i] is voter i's ranking    
    public ArrayList<ArrayList<String>> parseVotes() throws Exception {
        ArrayList<ArrayList<String>> voterData;
        BufferedReader buffer = new BufferedReader(new FileReader(this.fileName));
        String line;
        voterData = new ArrayList<ArrayList<String>>();
        while ((line = buffer.readLine()) != null) {
            voterData.add(new ArrayList(Arrays.asList(line.trim().split(","))));
        }
        return voterData;
    }
}
