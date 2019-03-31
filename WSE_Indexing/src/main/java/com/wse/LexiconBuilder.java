package com.wse;

import java.io.*;
import java.util.*;

/**
 * Created by chaoqunhuang on 10/12/17.
 */
public class LexiconBuilder {
    private static Map<String, Integer> wordIdTable = new HashMap<>();

    /**
     * build the lexicon and inverted index
     */
/*
    public static void build() {
        try {
            FileInputStream fileInputStream = new FileInputStream(FilePath.INTERMEDIATE_POSTING_SORTED);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            File invertedIndexFile = new File(FilePath.INVERTED_INDEX);
            invertedIndexFile.createNewFile();
            DataOutputStream invertedIndexOut = new DataOutputStream(new FileOutputStream(invertedIndexFile));

            File lexicon = new File(FilePath.LEXICON);
            lexicon.createNewFile();
            PrintWriter lexiconOut = new PrintWriter(new FileWriter(lexicon, true));
            String buffer;
            int pointer = 0;
            String word = "";
            int length = 0;
            int count = 0;

            while ((buffer = bufferedReader.readLine()) != null) {
                String[] params = buffer.split(" ");
                if (!word.equals(params[0])) {
                    if (pointer == 0) {
                        System.out.println("Generating word:" + word + " lexicon, No." + IndexerConstant.WORD_ID);
                        lexiconOut.print(IndexerConstant.WORD_ID + " " + pointer + " ");
                    } else {
                        System.out.println("Generating wordId:" + word + " lexicon, No." + IndexerConstant.WORD_ID);
                        lexiconOut.print(length + " " + count + "\n" + IndexerConstant.WORD_ID + " " + ++pointer + " ");
                        count = 0;
                        length = 0;
                    }
                    byte[] compressedInt = VbyteCompress.encode(Integer.valueOf(params[1]));
                    length += compressedInt.length;
                    invertedIndexOut.write(compressedInt);

                    byte[] compressedInt2 = VbyteCompress.encode(Integer.valueOf(params[2]));
                    length += compressedInt2.length;
                    invertedIndexOut.write(compressedInt2);

                    pointer += compressedInt.length + compressedInt2.length;
                    word = params[0];
                    wordIdTable.put(word, IndexerConstant.WORD_ID++);
                    count++;
                } else {
                    byte[] compressedInt = VbyteCompress.encode(Integer.valueOf(params[1]));
                    length += compressedInt.length;
                    invertedIndexOut.write(compressedInt);

                    byte[] compressedInt2 = VbyteCompress.encode(Integer.valueOf(params[2]));
                    length += compressedInt2.length;
                    invertedIndexOut.write(compressedInt2);

                    pointer += compressedInt.length + compressedInt2.length;
                    count++;
                }
            }
            lexiconOut.print(length + " " + count + "\n");
            lexiconOut.close();
            invertedIndexOut.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    */

    public static void buildV2() {
        try {
            FileInputStream fileInputStream = new FileInputStream(FilePath.INTERMEDIATE_POSTING_SORTED);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            File invertedIndexFile = new File(FilePath.INVERTED_INDEX);
            invertedIndexFile.createNewFile();
            DataOutputStream invertedIndexOut = new DataOutputStream(new FileOutputStream(invertedIndexFile));

            File lexicon = new File(FilePath.LEXICON);
            lexicon.createNewFile();
            PrintWriter lexiconOut = new PrintWriter(new FileWriter(lexicon, true));
            String buffer;
            int pointer = 0;
            String word = "";
            int count = 0;
            Map<Integer, Integer> docFre = new TreeMap<>();

            while ((buffer = bufferedReader.readLine()) != null) {
                String[] params = buffer.split(" ");
                if ("".equals(word)) {
                    // Starting
                    System.out.println("Starting Writing inverted index for word: " + params[0]);
                    docFre.put(Integer.valueOf(params[1]), Integer.valueOf(params[2]));
                    word = params[0];
                    count++;
                } else if (!word.equals(params[0])) {
                    // Change word
                    pointer = writeInvertedListBlock(docFre, invertedIndexOut, lexiconOut, count, pointer);
                    wordIdTable.put(word, IndexerConstant.WORD_ID++);
                    System.out.println("Recorded" + word + " " + (IndexerConstant.WORD_ID - 1));
                    count = 1;
                    System.out.println("Writing inverted index for word: " + params[0]);
                    docFre = new TreeMap<>();
                    docFre.put(Integer.valueOf(params[1]), Integer.valueOf(params[2]));
                    word = params[0];
                } else {
                    // Same word
                    docFre.put(Integer.valueOf(params[1]), Integer.valueOf(params[2]));
                    count++;
                }
            }
            writeInvertedListBlock(docFre, invertedIndexOut, lexiconOut, count, pointer);
            lexiconOut.close();
            invertedIndexOut.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int writeInvertedListBlock(Map<Integer, Integer> docFre,
                                             DataOutputStream invertedIndexOut,
                                             PrintWriter lexiconOut,
                                             int count, int pointer) throws IOException {
        List<Integer> docList = new ArrayList<>();
        List<Integer> freList = new ArrayList<>();
        docFre.forEach((k, v) -> {
            docList.add(k);
            freList.add(v);
        });

        if (docList.size() != freList.size()) {
            throw new AssertionError("docId does not match frequency");
        }
        int orgin = pointer;
        int remaining = docList.size();
        int start = 0;

        while (remaining > 128) {
            int docBytesLength = 0;
            int freBytesLength = 0;
            List<Integer> docSub = docList.subList(start, start + 128);
            List<Integer> freSub = freList.subList(start, start + 128);

            // Number of docs in this block
            invertedIndexOut.writeInt(128);
            pointer += 4;

            // Start docId
            invertedIndexOut.writeInt(Collections.min(docSub));
            pointer += 4;

            // End docId
            invertedIndexOut.writeInt(Collections.max(docSub));
            pointer += 4;

            // Transform to gaps
            for (int i = docSub.size() - 1; i > 0; i--) {
                docSub.set(i, docSub.get(i) - docSub.get(i - 1));
            }

            for (Integer i : docSub) {
                docBytesLength += VbyteCompress.encode(i).length;
            }

            for (Integer i : freSub) {
                freBytesLength += VbyteCompress.encode(i).length;
            }

            // DocLength
            invertedIndexOut.writeInt(docBytesLength);
            pointer += 4;

            // FrequencyLength
            invertedIndexOut.writeInt(freBytesLength);
            pointer += 4;

            for (Integer i : docSub) {
                byte[] compressedInt = VbyteCompress.encode(i);
                invertedIndexOut.write(compressedInt);
                pointer += compressedInt.length;
            }

            for (Integer i : freSub) {
                byte[] compressedInt = VbyteCompress.encode(i);
                invertedIndexOut.write(compressedInt);
                pointer += compressedInt.length;
            }
            remaining -= 128;
            start += 128;
        }
        if (remaining > 0) {
            int docBytesLength = 0;
            int freBytesLength = 0;
            List<Integer> docSub = docList.subList(start, docList.size());
            List<Integer> freSub = freList.subList(start, docList.size());

            // Number of docs in this block
            invertedIndexOut.writeInt(remaining);
            pointer += 4;

            // Start docId
            invertedIndexOut.writeInt(Collections.min(docSub));
            pointer += 4;

            // End docId
            invertedIndexOut.writeInt(Collections.max(docSub));
            pointer += 4;

            // transform to gaps
            for (int i = docSub.size() - 1; i > 0; i--) {
                docSub.set(i, docSub.get(i) - docSub.get(i - 1));
            }

            for (Integer i : docSub) {
                docBytesLength += VbyteCompress.encode(i).length;
            }
            for (Integer i : freSub) {
                freBytesLength += VbyteCompress.encode(i).length;
            }


            // DocLength
            invertedIndexOut.writeInt(docBytesLength);
            pointer += 4;

            // FrequencyLength
            invertedIndexOut.writeInt(freBytesLength);
            pointer += 4;

            for (Integer i : docSub) {
                byte[] compressedInt = VbyteCompress.encode(i);
                invertedIndexOut.write(compressedInt);
                pointer += compressedInt.length;
            }

            for (Integer i : freSub) {
                byte[] compressedInt = VbyteCompress.encode(i);
                invertedIndexOut.write(compressedInt);
                pointer += compressedInt.length;
            }
        }

        lexiconOut.print(IndexerConstant.WORD_ID + " " + orgin + " " + (pointer - orgin) + " " + count + "\n");
        return pointer;
    }

    /**
     * Write the word table to file
     */
    public static void outputWordList() {
        try {
            System.out.println("Outputing wordlist");
            File file = new File(FilePath.WORD_LIST);
            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            wordIdTable.forEach((k, v) -> {
                if (!"".equals(k)) {
                    printWriter.println(k + " " + v);
                }
            });
            printWriter.close();
            SortUtil.sortUsingUnixSort(FilePath.WORD_LIST, FilePath.WORD_LIST_SORTED);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
