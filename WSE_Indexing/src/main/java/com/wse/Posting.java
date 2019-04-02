package com.wse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * Created by chaoqunhuang on 10/10/17.
 */
public class Posting {

    /**
     * Get the words in a String
     * @param content a String
     * @return a word list
     */
    public Map<String, Integer> getWordsList(String content) {
        String[] words = content.split("\\W+");
        Map<String, Integer> wordList = new HashMap<>();
        for (String s : words) {
            if (wordList.containsKey(s)) {
                int count = wordList.get(s);
                wordList.put(s, ++count);
            }
            else {
                wordList.put(s, 1);
            }
        }
        return wordList;
    }

    /**
     * Parser postings and write to fil
     * @param content a String
     */
    public void postToIntermediateFile(String url, String content) {
        Map<String, Integer> wordList = getWordsList(content);
        // System.out.println(wordList.toString());
        synchronized (Posting.class) {
            try {
                CommonCrawlReader.docIdTable.put(url + "###" + content.length(), IndexerConstant.DOC_ID);
                //Inserting to MongoDb
                MongoDb.writeToMongoDb(IndexerConstant.DOC_ID, url, content);

                File file = new File(FilePath.INTERMEDIATE_POSTING);
                file.createNewFile();
                PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));

                wordList.forEach((k, v) -> {
                    if (!"".equals(k)) {
                        printWriter.println(k + " " + IndexerConstant.DOC_ID + " " + v);
                    }
                });
                IndexerConstant.DOC_ID++;
                printWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage() + e.getCause());
            }
        }
    }

}
