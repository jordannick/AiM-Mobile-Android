package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

/**
 * Created by SellersK on 7/1/2015.
 */
public class Euler {


    public static void listAllPalindromes(int max){
        for (int i = max; i > 0; i--) {
            if(isPalindrome(i)){
                Log.d("Ohh yea", ":: " + i);
                //break;
            }
        }
    }

    public static boolean isPalindrome(int num){
        Integer integer = Integer.valueOf(num);
        String number = integer.toString();

        int i = 0;
        int j = number.length()-1;

        while(i < j){
            if(number.charAt(i) == number.charAt(j)){
                i++;
                j--;
            }else{
                return false;
            }
        }
        return true;

    }


    public static void findSumAmicableNumbers(int max){
        int totalSum = 0;
        for (int i = 0; i < max; i++) {
            if(isAmicableHelper(i)){
                totalSum += i;
                Log.d("Ohh 2yea", "adding: " + i);
            }
        }

        Log.d("Ohh yea", "THE SUM: " + totalSum);

    }

    public static boolean isAmicableHelper(int num){
        int firstSum = findDivisorSum(num);
        return findDivisorSum(firstSum) == firstSum;
    }

    public static int findDivisorSum(int num){
        int divisorSum = 0;
        Log.d("Ohh yea", "DOING NUMBER :: " + num);
        for (int j = 1; j <= num/2; j++) {
            if(num%j==0) { //Divides evenly
                Log.d("Ohh yea", "--- " + j);
                divisorSum += j;
            }
        }
        return divisorSum;
    }

}
