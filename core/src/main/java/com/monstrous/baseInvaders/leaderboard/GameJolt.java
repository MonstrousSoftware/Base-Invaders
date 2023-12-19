package com.monstrous.baseInvaders.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class GameJolt {

    private static final String GJ_GATEWAY = "https://api.gamejolt.com/api/game/v1_2/";
    private static final String GAME_ID = "862678";
    private static final String TABLE_ID = "873573";
    private static final String LIMIT = "10";        // top # scores to show
    private String privateKey;
    private Array<LeaderBoardEntry> leaderBoard;


    public void init( Array<LeaderBoardEntry> leaderBoard ) {
        this.leaderBoard = leaderBoard;

//        testMD5("Message Digest");
//        testMD5("abcdefghijklmnopqrstuvwxyz");
//        testMD5("Hello!");

        try {


            FileHandle handle = Gdx.files.internal("private.txt");

            String text = handle.readString();
            String words[] = text.split("\\r?\\n");
            privateKey = words[0];

            getScores();

            //addScore("Guest3", "00:09:52", 9*60+52);
        } catch (GdxRuntimeException e) {
            Gdx.app.error("Cannot read key file", "private.txt");
        }

    }


    public void testMD5(String message) {

        byte[] digest = MD5.computeMD5(message.getBytes());
        String hex = "0x" + MD5.toHexString(digest);
        Gdx.app.log("MD5 on ["+message+"]", hex);

    }

    public void addScore(String username, String score, int timeInSeconds ) {
        Map<String, String> params = new HashMap<String, String>();

        Integer numScore = timeInSeconds;

        params.put("game_id", GAME_ID);
        params.put("guest", username);
        params.put("score", score);
        params.put("sort", numScore.toString());
        params.put("table_id", TABLE_ID);

        Net.HttpRequest http = buildRequest("scores/add/?", params);

        Gdx.net.sendHttpRequest(http, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();

                Gdx.app.log("GameJolt reply", response);
                getScores();        // now retrieve new score table
            }
            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameJolt server", "Gamestate load failed", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.error("GameJolt server", "Gamestate load cancelled");
            }
        });
    }


    public void getScores() {
        Map<String, String> params = new HashMap<String, String>();

        params.put("game_id", GAME_ID);
        params.put("limit", LIMIT);

        Gdx.app.log("GameJolt", "fetch scores");

        Net.HttpRequest http = buildJsonRequest("scores/fetch/", params);

        Gdx.net.sendHttpRequest(http, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();

                Gdx.app.log("GameJolt replied", "");

                JsonValue response = null;
                try {
                    response = new JsonReader().parse(json).get("response");

                    if (response == null || !response.getBoolean("success")) {
                        Gdx.app.error("GameJolt", "Could not parse answer from GameJolt: " + json);
                        //callback.onLeaderBoardResponse(null);
                    } else {
                        JsonValue scores = response.get("scores");
                        int rank = 0;
                        leaderBoard.clear();

                        for (JsonValue score = scores.child; score != null; score = score.next) {
                            rank++;
                            LeaderBoardEntry entry = scoreJsonToObject(rank, score);
                            if (entry != null)
                                leaderBoard.add(entry);
                        }

                        for(LeaderBoardEntry entry : leaderBoard ){
                            entry.print();
                        }
                    }
                } catch (Throwable t) {
                    Gdx.app.error("GameJolt", "Could not parse answer from GameJolt: " + json, t);
                }
            }


            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameJolt server", "Gamestate load failed", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.error("GameJolt server", "Gamestate load cancelled");
            }
        });
    }

    protected LeaderBoardEntry scoreJsonToObject(int rank, JsonValue score) {
        return LeaderBoardEntry.fromJson(score, rank);
    }



    protected Net.HttpRequest buildJsonRequest(String component, Map<String, String> params) {
        component = component + "?format=json&";
        return buildRequest(component, params);
    }


    protected Net.HttpRequest buildRequest(String component, Map<String,String> params) {
        String request = GJ_GATEWAY + component;

        request += HttpParametersUtils.convertHttpParameters(params);

        String concat = request + privateKey;

        /* Generate signature */
        final String signature = md5(request + privateKey);

        /* Append signature */
        String complete = request;
        complete += "&";
        complete += "signature";
        complete += "=";
        complete += signature;

        Gdx.app.log("URL", complete);

        final Net.HttpRequest http = new Net.HttpRequest();
        http.setMethod(Net.HttpMethods.GET);
        http.setUrl(complete);

        return http;
    }


    private String md5(String s) {
        return MD5.toHexString(MD5.computeMD5(s.getBytes()));
    }


    protected String md5ori(String s)  {

        final StringBuffer sb = new StringBuffer();
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            try {
                final byte[] bytes = s.getBytes("UTF-8");

                final byte[] digest = md.digest(bytes);

                for (int i = 0; i < digest.length; ++i) {
                    sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
                }

            } catch (Exception e) {
                Gdx.app.log("Encoding not supported", "UTF-8");
            }
        }  catch (Exception e) {
            Gdx.app.log("Message Digest not supported", "MD5");
        }
        return sb.toString();
    }
}
