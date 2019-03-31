package com.wse;

import java.io.*;

/**
 * Created by chaoqunhuang on 10/13/17.
 */
public class TestResult {
    public static void testInvertedIndex() {
        try {
            FileInputStream fileInputStream = new FileInputStream(FilePath.INVERTED_INDEX);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            FileInputStream fileInputStream2 = new FileInputStream(FilePath.LEXICON);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream2));
            //String[] params = bufferedReader.readLine().split(" ");
            System.out.println(2);
            int skip = dataInputStream.skipBytes(88823 + 20 + 128 + 130);
            System.out.println(skip);
            int numsBlk = dataInputStream.readInt();
            int startBlk = dataInputStream.readInt();
            int endBlk = dataInputStream.readInt();
            int docLength = dataInputStream.readInt();
            int freLength = dataInputStream.readInt();

            System.out.println("MetaData:" + numsBlk + "start:" + startBlk + " end:" + endBlk + "docL:" + docLength +
                    "freL:" + freLength);
            byte[] res = new byte[12662 - 20];
            dataInputStream.read(res, 0, 12662 - 20);

            int[] result = VbyteCompress.decode(res, numsBlk);
            System.out.println(result.length);
            System.out.print(result[0] + " ");
            for (int i = 1; i < result.length; i++) {
                System.out.print(result[i] + result[i - 1] + " ");
                result[i] += result[i - 1];
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
