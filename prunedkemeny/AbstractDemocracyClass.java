package prunedkemeny;

import java.util.ArrayList;

/* this didn't work as expected because it doesn't require these things be added to the underlying objects
 * also we have to be able to instantiate the algorithms without passing them voter data since we do that hundreds
  * of times. */
abstract public class AbstractDemocracyClass {
    abstract public ArrayList<String> run(ArrayList<ArrayList<String>> voterData, int numByzantine);
    abstract public String getName();

}
