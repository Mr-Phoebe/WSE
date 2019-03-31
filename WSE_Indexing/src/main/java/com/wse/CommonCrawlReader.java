package com.wse;

import com.google.common.base.CharMatcher;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaoqunhuang on 10/10/17.
 */
public class CommonCrawlReader {
    public static Map<String, Integer> docIdTable = new HashMap<String, Integer>();
    private Posting posting = new Posting();

    /**
     * Start parsing file and write to file
     *
     * @param fileName the file path of the Wet file
     */
    public void startParser(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            // Skip the metadata for the file
            skipLines(bufferedReader, 18);

            // Start reading page
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                StringBuilder sb = new StringBuilder();
                String url = "";
                while (!"".equals(buffer)) {
                    buffer = bufferedReader.readLine();
                    if (buffer.startsWith("WARC-Target-URI: ")) {
                        url = buffer.split("WARC-Target-URI: ")[1];
                    }
                }
                buffer = bufferedReader.readLine();
                while (!"WARC/1.0".equals(buffer) && buffer != null) {
                    buffer = bufferedReader.readLine();
                    if (buffer != null) {
                        if (CharMatcher.ASCII.matchesAllOf(buffer)) {
                            sb.append(" " + buffer);
                        }
                    }
                }
                if (sb.length() != 0) {
                    System.out.println("Parsing:" + IndexerConstant.PAGE_NO++);
                    //System.out.println(sb);
                    System.out.println(url);
                    posting.postToIntermediateFile(url, sb.toString());
                } else {
                    System.out.println("Not English");
                }
            }
            bufferedReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Skip certain lines in wet file
     *
     * @param bufferedReader bufferedReader of the wet file
     * @param lines          lines to skip
     */
    private void skipLines(BufferedReader bufferedReader, int lines) {
        for (int i = 0; i < lines; i++) {
            try {
                bufferedReader.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Write Url table to file
     */
    public void outputUrlTable() {
        try {
            System.out.println("Generating Url Table");
            File file = new File(FilePath.URL_TABLE);
            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            docIdTable.forEach((k, v) -> {
                if (!"".equals(k)) {
                    String[] splits = k.split("###");
                    printWriter.println(v + " " + splits[0] + " " + splits[1]);
                }
            });
            printWriter.close();
            SortUtil.sortUsingUnixSortAsNum(FilePath.URL_TABLE, FilePath.URL_TABLE_SORTED);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
