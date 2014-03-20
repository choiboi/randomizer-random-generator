package com.alottapps.randomizer.util;

import java.util.ArrayList;

public class RandomGenerator {

    public static int singleRandomNumber(int bound) {
        return (int)(Math.random() * (bound + 1));
    }
    
    public static int singleRangeRandomNumber(int start, int end) {
        return start + (int)(Math.random() * ((end - start) + 1));
    }
    
    public static ArrayList<Integer> listUniqueRandomNumber(int listSize, int bound) {
        ArrayList<Integer> numL = new ArrayList<Integer>();
        
        int i = 0;
        int num = -1;
        while (i < listSize) {
            num = singleRandomNumber(bound);
            if (!numL.contains(num)) {
                numL.add(num);
                i++;
            }
        }
        
        return numL;
    }
}
