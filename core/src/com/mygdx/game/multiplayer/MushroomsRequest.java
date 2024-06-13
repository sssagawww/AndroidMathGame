package com.mygdx.game.multiplayer;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class MushroomsRequest {
    private OkHttpClient client;
    private int opponentId;
    private float opponentScore;
    private boolean done = false;
    private String opponentName = "";
    private String winner = "";
    private boolean everyoneReady;
    private JsonReader json;
    private static String ip = "";
    private static String url = "http://" + ip + "/multiplayer/";
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static boolean unableToConnect = true;

    public MushroomsRequest() {
        client = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request();
                Request newRequest = request.newBuilder().addHeader("SecretHeader", "super-secret-password").build();
                return chain.proceed(newRequest);
            }
        }).build();
        json = new JsonReader();
    }

    public void postInfo(int id, String userName, float number) {
        String jsonRequest = "{\"userId\":" + id + ", \"userName\": \"" + userName + "\", \"number\":" + number + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder().url(url + "info").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! postInfo");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Thread.sleep(10);
                    JsonValue jObject = json.parse(response.body().string());
                    opponentName = jObject.getString("userName");
                    opponentScore = jObject.getFloat("number");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void playerIsReady(int id, String userName) {
        String jsonRequest = "{\"userId\":" + id + ", \"userName\": \"" + userName + "\"}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request request = new Request.Builder().url(url + "playerisready").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! playerIsReady");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setPlayerReady(int id, boolean ready) {
        String jsonRequest = "{\"userId\":" + id + ", \"ready\": \"" + ready + "\"}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request request = new Request.Builder().url(url + "setplayerready").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! setPlayerReady");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("setPlayerReady called");
            }
        });
    }

    public void join(int id, String userName, String miniGame, float number) {
        String jsonRequest = "{\"userId\":" + id + ", \"userName\": \"" + userName + "\", \"miniGame\": \"" + miniGame + "\", \"number\":" + number + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder().url(url + "join").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                unableToConnect = true;
                System.out.println("Unable to connect! join");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    System.out.println(response.body().string());
                    done = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void leave(int id) {
        String jsonRequest = "{\"userId\":" + id + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder().url(url + "leave").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! leave");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isReady() {
        Request request = new Request.Builder().url(url + "readyornot").get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! isReady");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    everyoneReady = Boolean.parseBoolean(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return everyoneReady;
    }

    public void getWinner() {
        Request request = new Request.Builder().url(url + "getwinner").get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unable to connect! getWinner");
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JsonValue jObject = json.parse(response.body().string());
                    winner = jObject.getString("joined");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ping() {
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                unableToConnect = true;
                System.out.println("Unable to connect! join");
            }

            @Override
            public void onResponse(Call call, Response response) {
                unableToConnect = false;
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getOpponentId() {
        return opponentId;
    }

    public float getOpponentScore() {
        return opponentScore;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getWinnerName() {
        return winner;
    }

    public static boolean isUnableToConnect() {
        return unableToConnect;
    }

    public static void setUnableToConnect(boolean b) {
        unableToConnect = b;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        MushroomsRequest.ip = ip;
        url = "http://" + ip + "/multiplayer/";
    }
}
