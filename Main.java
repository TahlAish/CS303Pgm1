import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // The three data files provided for the assignment
        String[] files = {"data1.txt", "data2.txt", "data3.txt"};

        SortStats[] results = new SortStats[3];

        // Read and sort each file
        for (int i = 0; i < files.length; i++) {

            try {
                int[] numbers = readFile(files[i]);

                results[i] = radixSort(numbers, files[i]);

                System.out.println(files[i] + " has been sorted.");

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + files[i]);
            }
        }

        // Create the report after all files are sorted
        writeReport(results);
    }


    
     // PRE: The file exists and contains integers separated by commas.
     // POST: The integers from the file are returned in an array.
     
    public static int[] readFile(String fileName)
            throws FileNotFoundException {

        Scanner input = new Scanner(new File(fileName));

        // The numbers are separated by commas or whitespace
        input.useDelimiter("[,\\s]+");

        ArrayList<Integer> list = new ArrayList<>();

        while (input.hasNextInt()) {
            list.add(input.nextInt());
        }

        input.close();

        int[] numbers = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            numbers[i] = list.get(i);
        }

        return numbers;
    }


    
     // PRE: The array contains non-negative integers.
     // POST: The array is sorted from smallest to largest
     // using Radix Sort.
     
    public static SortStats radixSort(int[] numbers, String fileName) {

        SortStats stats = new SortStats();

        stats.sortName = "RadixSort";
        stats.fileName = fileName;

        long start = System.nanoTime();

        // If the file is empty, return without sorting
        if (numbers.length == 0) {
            stats.timeNano = System.nanoTime() - start;
            return stats;
        }

        // Find the largest number
        int max = numbers[0];

        for (int i = 1; i < numbers.length; i++) {

            stats.loops++;
            stats.comparisons++;

            if (numbers[i] > max) {
                max = numbers[i];
            }
        }

        // Sort one digit at a time:
        // ones, tens, hundreds, thousands, etc.
        for (int place = 1; max / place > 0; place *= 10) {

            stats.loops++;

            countingSort(numbers, place, stats);
        }

        long end = System.nanoTime();

        stats.timeNano = end - start;

        return stats;
    }


    
     // PRE: The array contains non-negative integers and
     // place represents the digit being sorted.
     // POST: The array is sorted based on the current digit.
     
    public static void countingSort(int[] numbers, int place,
                                    SortStats stats) {

        int[] output = new int[numbers.length];
        int[] count = new int[10];

        // Count how many times each digit appears
        for (int i = 0; i < numbers.length; i++) {

            stats.loops++;

            int digit = (numbers[i] / place) % 10;

            count[digit]++;
        }

        // Find the correct position for each digit
        for (int i = 1; i < 10; i++) {

            stats.loops++;

            count[i] = count[i] + count[i - 1];
        }

        // Put the numbers into the output array
        for (int i = numbers.length - 1; i >= 0; i--) {

            stats.loops++;

            int digit = (numbers[i] / place) % 10;

            output[count[digit] - 1] = numbers[i];

            count[digit]--;

            stats.swaps++;
        }

        // Copy the sorted numbers back into the original array
        for (int i = 0; i < numbers.length; i++) {

            stats.loops++;

            numbers[i] = output[i];

            stats.swaps++;
        }
    }


    
     // PRE: The results array contains statistics
     // from each Radix Sort.
     // POST: The results are written to report.txt.
     
    public static void writeReport(SortStats[] results) {

        try {

            PrintWriter output = new PrintWriter("report.txt");

            // Print column headings with even spacing
            output.printf(
                    "%-15s %-15s %-12s %-15s %-12s %-15s%n",
                    "Sort Name",
                    "File",
                    "Swaps",
                    "Comparisons",
                    "Loops",
                    "Time(ns)"
            );

            // Print the results for each file
            for (int i = 0; i < results.length; i++) {

                if (results[i] != null) {

                    output.printf(
                            "%-15s %-15s %-12d %-15d %-12d %-15d%n",
                            results[i].sortName,
                            results[i].fileName,
                            results[i].swaps,
                            results[i].comparisons,
                            results[i].loops,
                            results[i].timeNano
                    );
                }
            }

            output.close();

            System.out.println("Report created.");

        } catch (FileNotFoundException e) {

            System.out.println("Could not create report.");
        }
    }
}