package com.wse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;


public class Server extends AbstractVerticle {
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Query query = new Query();
        server.requestHandler(request -> {
            long startTime = System.currentTimeMillis();
            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            final String word = request.getParam("word");
            System.out.println("Searching word:" + word);
            StringBuilder sb = new StringBuilder();
            try {
                if (word != null && !"".equals(word)) {
                    if (!word.contains("@") && !word.contains("$")) {
                        // One word query
                        try {
                            String[] docs = query.query(word);
                            for (String s : docs) {
                                sb.append(s);
                                sb.append("###");
                            }
                        } catch (IOException ioe) {
                            System.out.println(ioe.getMessage());
                        }
                    } else  {
                        // And word query
                        String[] words;
                        if (word.contains("$"))
                            words = word.split("\\$");
                        else
                            words = word.split("@");
                        System.out.println(words[0] + " " + words[1]);
                        String[] docs = query.andQuery(words);
                        System.out.println(docs[0] + docs[1]);
                        for (String s : docs) {
                            sb.append(s);
                            sb.append("###");
                        }
                    }
                } else {
                    System.out.println("The word is null, returning null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.putHeader("content-type", "text/plain");
            response.putHeader("Access-Control-Allow-Origin", "*");

            // Write to the response and end it
            response.end(sb.toString() + "###" + (System.currentTimeMillis() - startTime));
        });
        server.listen(8080);
    }
}
