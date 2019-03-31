package com.wse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Parsing Intermediate Posting
        ExecutorService es = Executors.newFixedThreadPool(5);
        CommonCrawlReader commonCrawlReader = new CommonCrawlReader();

        // Add file path
        CountDownLatch countDownLatch = new CountDownLatch(FilePath.WETS.length);
        for (String wet : FilePath.WETS) {
            final String filename = wet;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    commonCrawlReader.startParser(filename);
                    countDownLatch.countDown();
                }
            };
            es.execute(runnable);
        }
        es.shutdown();
        countDownLatch.await();

        // Calling Unix Sort
        log("Intermediate posting is done");
        System.out.println("Intermediate posting is done");
        System.out.println("=====================================================================");
        System.out.println("Calling unix sort");
        log("Calling Unix Sort");
        SortUtil.sortUsingUnixSort(FilePath.INTERMEDIATE_POSTING, FilePath.INTERMEDIATE_POSTING_SORTED);
        log("Sort is done");
        commonCrawlReader.outputUrlTable();

        // Building index
        log("Start building index");
        LexiconBuilder.buildV2();
        LexiconBuilder.outputWordList();

        TestResult.testInvertedIndex();
    }
    private static void log(String logMessage) {
        try {
            File file = new File(FilePath.LOG_PATH);
            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            printWriter.print(logMessage);
            printWriter.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
