package com.mygdx.game.multiplayer;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class MushroomsRequest {
    private OkHttpClient client;
    private int opponentId;
    private int opponentScore;
    private JsonReader json;
    private final String url = "";

    public MushroomsRequest() {
        client = new OkHttpClient();
        json = new JsonReader();
    }

    public void postInfo(int id, int count, boolean done) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonRequest = "{\"userId\":" + id + ", \"playerCount\":" + count + ", \"done\":" + done + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //new Thread(() -> { //???
                try {
                    JsonValue jObject = json.parse(response.body().string());
                    opponentId = jObject.getInt("userId");
                    opponentScore = jObject.getInt("playerScore");
                    System.out.println("userId: " + opponentId + " playerScore: " + opponentScore);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //}).start();
            }
        });
    }

    public int getOpponentId() {
        return opponentId;
    }

    public int getOpponentScore() {
        return opponentScore;
    }
}
