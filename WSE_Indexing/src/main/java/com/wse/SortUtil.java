package com.wse;

import java.io.IOException;

/**
 * Created by chaoqunhuang on 10/12/17.
 */
public class SortUtil {
    /**
     * This method is used to calling I/O efficient unix sort to sort posting file
     *
     * @param input  The file path of the posting file to sort
     * @param output The file path of the sorted posting file
     */
    public static void sortUsingUnixSort(String input, String output) {
        try {
            String cmd[] = {
                    "/bin/sh",
                    "-c",
                    "sort -k 1,1 " + input + " > " + output
            };
            Process p = Runtime.getRuntime().exec(cmd);
            int exit = p.waitFor();
            System.out.println(exit);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sortUsingUnixSortAsNum(String input, String output) {
        try {
            String cmd[] = {
                    "/bin/sh",
                    "-c",
                    "sort -k 1n,1 " + input + " > " + output
            };
            Process p = Runtime.getRuntime().exec(cmd);
            int exit = p.waitFor();
            System.out.println(exit);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sortUsingUnixSortWith(String input, String output) {
        try {
            String cmd[] = {
                    "/bin/sh",
                    "-c",
                    "sort -k 1,1 -k 2n,2 " + input + " > " + output
            };
            Process p = Runtime.getRuntime().exec(cmd);
            int exit = p.waitFor();
            System.out.println(exit);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
