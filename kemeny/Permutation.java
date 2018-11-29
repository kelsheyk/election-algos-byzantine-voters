package Kemeny;

import java.util.ArrayList;

public class Permutation {
    ArrayList<ArrayList<String>> result;
    
    public ArrayList<ArrayList<String>> performPermute(ArrayList<String> data) {
        this.result = new ArrayList<>();
        permute(data, 0, data.size()-1);
        return result;
    }
    
    /** 
     * permute String[] arr starting at l, end at r 
     */
    private void permute(ArrayList<String> arr, int l, int r) { 
        if (l == r) {
            this.result.add(arr);
        } else { 
            for (int i = l; i <= r; i++) { 
                arr = swap(arr, l, i); 
                permute(arr, l+1, r); 
                arr = swap(arr, l, i); 
            } 
        } 
    } 
  
    /** 
     * Swap array arr elems at pos i & j 
     */
    public ArrayList<String> swap(ArrayList<String> arr, int i, int j) { 
        String temp; 
        temp = arr.get(i) ; 
        arr.set(i, arr.get(j)); 
        arr.set(j, temp); 
        return arr; 
    } 
    
}
