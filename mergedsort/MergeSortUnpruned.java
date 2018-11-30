package mergedsort;
import java.util.ArrayList;

public class MergeSortUnpruned extends mergedsort.MergeSort {

    public String getName() {return "mSort";}
    public String getColor() {return "green";}
    public String getMark() {return "square";}

    public MergeSortUnpruned() {

    }

    public ArrayList<String> run(ArrayList<ArrayList<String>> voterData, int numByzantine) {
        this.voterData = voterData;
        this.numByzantine = 0; // mergesort runs unpruned when this is 0
        return this.run();
    }

}
