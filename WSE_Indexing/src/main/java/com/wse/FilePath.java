package com.wse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by chaoqunhuang on 10/12/17.
 */
public class FilePath {
    public static String INTERMEDIATE_POSTING;
    public static String INTERMEDIATE_POSTING_SORTED;
    public static String LEXICON;
    public static String INVERTED_INDEX;
    public static String WORD_LIST;
    public static String WORD_LIST_SORTED;
    public static String URL_TABLE;
    public static String URL_TABLE_SORTED;
    public static String LOG_PATH;
    public static String[] WETS;
    public FilePath() {
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);
            String workspace = prop.getProperty("workspace");
            LOG_PATH = workspace + "/log.txt";
            INTERMEDIATE_POSTING = workspace + "/IntermediatePosting/posting.txt";
            INTERMEDIATE_POSTING_SORTED = workspace + "/IntermediatePosting/posting_sorted.txt";
            LEXICON = workspace + "/index/lexicon";
            INVERTED_INDEX = workspace + "/index/inverted_index";
            WORD_LIST = workspace + "/index/word_list";
            WORD_LIST_SORTED = workspace + "/index/word_list_sorted";
            URL_TABLE = workspace + "/index/url_table";
            URL_TABLE_SORTED = workspace + "/index/url_table_sorted";
            String wetPath = prop.getProperty("wetpath");
            int num  = Integer.valueOf(prop.getProperty("wetnum"));
            WETS = new String[num + 1];
            String wetPrefix = prop.getProperty("wetprefix");
            for (int i = 0; i < num + 1; i++) {
                String n = String.format("%05d", i);
                WETS[i] = wetPath + wetPrefix + n + ".warc.wet";
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
