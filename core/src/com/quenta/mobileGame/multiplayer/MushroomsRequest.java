package com.quenta.mobileGame.multiplayer;

import static com.quenta.mobileGame.MyGdxGame.MUSHROOMS_GAME;
import static com.quenta.mobileGame.MyGdxGame.PAINT_GAME;
import static com.quenta.mobileGame.handlers.GameStateManager.MUSHROOMS;
import static com.quenta.mobileGame.handlers.GameStateManager.PAINT;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.quenta.mobileGame.states.PaintState;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
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
    private final OkHttpClient client;
    private final JsonReader json;
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String ip = "";
    private static String url = "http://" + ip + "/multiplayer/";
    private static String name = "name";
    private int roomId;
    private boolean joined;
    private boolean created;
    private boolean failed;
    private String miniGame;
    private static boolean unableToConnect = true;
    private String winnerMessage = "";
    private HashMap<String, Float> opponents = new HashMap<>();
    private boolean done = false;
    private boolean everyoneReady;

    public MushroomsRequest() {
        //создаём клиент, где все запросы будут с нужным заголовком
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

    public void createRoom(int id, String miniGame) {
        //создаём тело запроса
        String jsonRequest = "{\"userId\":" + id + ", \"userName\": \"" + name + "\", \"miniGame\": \"" + miniGame + "\"}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "createroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! createRoom()");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    //получаем id созданной комнаты
                    JsonValue jObject = json.parse(response.body().string());
                    response.body().close();
                    roomId = Integer.parseInt(jObject.getString("string"));
                    created = true;
                    done = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void joinRoom(int userId, int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"roomId\":" + roomId + ", \"userName\": \"" + name + "\", \"userId\": " + userId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "joinroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! joinRoom()");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    //проверяем, получилось ли зайти
                    JsonValue jObject = json.parse(response.body().string());
                    response.body().close();
                    if (jObject.getString("string").equals("can't join")) {
                        joined = false;
                    } else {
                        //если да, то смотрим, какая мини-игра в комнате
                        joined = true;
                        miniGame = jObject.getString("string");
                    }
                    done = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void leaveRoom(int userId, int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"roomId\":" + roomId + ", \"userId\": " + userId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "leaveroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! leaveRoom()");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    System.out.println(response.body().string());
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //устанавливаем, что игрок готов
    public void playerIsReady(int userId, int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"userId\":" + userId + ", \"userName\": \"" + name + "\", \"roomId\":" + roomId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "playerisreadyroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! playerIsReady()");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    System.out.println(response.body().string());
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setPlayerReady(int id, boolean ready, int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"userId\":" + id + ", \"ready\": \"" + ready + "\", \"roomId\":" + roomId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "setplayerreadyroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! setPlayerReady");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                System.out.println("setPlayerReady called");
            }
        });
    }

    public boolean isEveryoneReady(int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"roomId\":" + roomId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "readyornotroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! isReady");
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    //проверяем, все ли игроки готовы
                    everyoneReady = Boolean.parseBoolean(response.body().string());
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return everyoneReady;
    }

    public void getWinner(int roomId) {
        //создаём тело запроса
        String jsonRequest = "{\"roomId\":" + roomId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        //создаём сам запрос
        Request request = new Request.Builder().url(url + "getwinnerroom").post(body).build();

        //отправляем запрос
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! getWinner");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    JsonValue jObject = json.parse(response.body().string());
                    response.body().close();
                    winnerMessage = jObject.getString("string");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postInfo(int id, float number, int roomId) {
        String jsonRequest = "{\"userId\":" + id + ", \"userName\": \"" + name + "\", \"number\":" + number + ", \"roomId\":" + roomId + "}";
        RequestBody body = RequestBody.create(jsonRequest, JSON);

        Request request = new Request.Builder().url(url + "info").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Unable to connect! postInfo");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String s = response.body().string();
                    //System.out.println(s); //почему-то иногда не получается распарсить json
                    JsonValue jObject = json.parse(s);
                    response.body().close();

                    if(jObject != null){
                        String[] names = jObject.getString("userNames").replaceAll("\\[|]|\"", "").split(",");
                        String[] nums = jObject.getString("numbers").replaceAll("\\[|]|\"", "").split(",");
                        for (int i = 0; i < names.length; i++) {
                            if (!names[i].equals("") && !names[i].equals(" ")) {
                                opponents.put(names[i], Float.valueOf(nums[i]));
                            }
                        }
                    } else {
                        failed = true;
                        opponents.put("Ошибка!", 0f);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ping() {
        //проверяем, доступен ли сервер
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                unableToConnect = true;
                System.out.println("Unable to connect! ping");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                unableToConnect = false;
                System.out.println("server is active");
            }
        });
    }

    public HashMap<String, Float> getOpponents() {
        return opponents;
    }

    public ArrayList<String> getOpponentNames() {
        return new ArrayList<>(opponents.keySet());
    }

    public ArrayList<Float> getOpponentScores() {
        return new ArrayList<>(opponents.values());
    }

    public boolean isDone() {
        return done;
    }

    public String getWinnerName() {
        return winnerMessage;
    }

    public static boolean isUnableToConnect() {
        return unableToConnect;
    }

    public static void setUnableToConnect(boolean b) {
        unableToConnect = b;
    }

    public static void setIp(String ip) {
        MushroomsRequest.ip = ip;
        url = "http://" + ip + "/multiplayer/";
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MushroomsRequest.name = name;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public int getMiniGame() {
        if (miniGame.equals(MUSHROOMS_GAME)) {
            return MUSHROOMS;
        } else if (miniGame.equals(PAINT_GAME)) {
            PaintState.setOnline(true);
            return PAINT;
        }
        return 0;
    }
}