package by.roman.worldradio0.business_logic.network.radio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.network.radio.callbacks.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
public class radio {
    private List<String> address;
    private static final String API_URL = "/json/stations/topclick/1000";
    //private static final String API_URL = "http://162.55.180.156/json/stations";


    public radio() {
        updateDnsList();
    }
    private void updateDnsList(){
        Log.v("radio", "start scanning");
        @SuppressLint("StaticFieldLeak")
        final AsyncTask<Void, Void, String[]> xxx = new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... params) {
                Vector<String> listResult = new Vector<>();
                try {
                    InetAddress[] list = InetAddress.getAllByName("all.api.radio-browser.info");
                    for (InetAddress item : list) {
                        listResult.add(item.getCanonicalHostName());
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return listResult.toArray(new String[0]);
            }

            @Override
            protected void onPostExecute(String[] result) {
                address = Arrays.asList(result);
                super.onPostExecute(result);
            }
        }.execute();
    }

    private String normalizeUrl(String host, String end) {
        if (host == null) return null;

        if (host.startsWith("http://") || host.startsWith("https://")) {
            return host;
        }
        return "http://" + host + end;
    }


    public void fetchStations (StationsCallback callback) {
        Random r= new Random();
        callback.onLoading();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(normalizeUrl(address.get(r.nextInt(address.size())), API_URL))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try (response) {
                        if(response.body() == null) {
                            Log.e("RadioAPI", "Response body is empty");
                            return;
                        }
                        String jsonResponse = response.body().string();

                        if (response.code() != 200) {
                            Log.e("RadioAPI", "Response code: " + response.code());
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("RadioAPI", "Empty response body.");
                            callback.onFailure(new Exception("Empty response body"));
                            return;
                        }

                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(new TypeToken<List<String>>() {
                                }.getType(), new TagsAdapter())
                                .create();

                        try {
                            if (jsonResponse.startsWith("[")) {
                                Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                                List<RadioStationDTO> dto = new ArrayList<>();
                                for (Model i : stations) {
                                    if(i.getUrl() != null){
                                        dto.add(new RadioStationDTO().fromModel(i));
                                    }
                                }
                                callback.onSuccess(dto);
                            } else {
                                Log.e("RadioAPI", "Unexpected request url: " + request.url());
                                callback.onFailure(new Exception("Unexpected response: " + jsonResponse));
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("RadioAPI", "JSON parsing error", e);
                            callback.onFailure(e);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("RadioAPI", "Request failed with code: " + response.code());
                    callback.onFailure(new Exception("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }

    public void click(String uuid, ClickCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Random r = new Random();

        Request request = new Request.Builder()
                .url(normalizeUrl(address.get(r.nextInt(address.size())), "/json/url/" + uuid))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) {
                        Log.e("RadioAPI", "Click failed: " + response.code());
                        callback.onFailure(new Exception("Click failed: " + response.code()));
                        return;
                    }

                    if (body == null) {
                        callback.onFailure(new Exception("Response body is null"));
                        return;
                    }

                    String jsonResponse = body.string();
                    Log.d("RadioAPI", "Click response: " + jsonResponse);

                    if (jsonResponse.isEmpty()) {
                        callback.onFailure(new Exception("Empty JSON response"));
                        return;
                    }

                    Gson gson = new Gson();
                    try {
                        ClickModel model = gson.fromJson(jsonResponse, ClickModel.class);

                        if (model != null) {
                            callback.onSuccess(model);
                        } else {
                            callback.onFailure(new Exception("API error: " + "Unknown"));
                        }

                    } catch (JsonSyntaxException e) {
                        Log.e("RadioAPI", "JSON parsing error", e);
                        callback.onFailure(e);
                    }

                } catch (Exception e) {
                    Log.e("RadioAPI", "Execution error", e);
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RadioAPI", "Network error", e);
                callback.onFailure(e);
            }
        });
    }

    public void vote(String uuid, VoteCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Random r = new Random();

        Request request = new Request.Builder()
                .url(normalizeUrl(address.get(r.nextInt(address.size())), "/json/vote/" + uuid))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new Exception("Vote error: " + response.code()));
                        return;
                    }

                    if (body != null) {
                        String json = body.string();
                        Log.d("RadioAPI", "Vote response: " + json);
                        VoteModel model = new Gson().fromJson(json, VoteModel.class);

                        if (model != null) {
                            callback.onSuccess(model);
                        } else {
                            String errorMsg = "empty";
                            callback.onFailure(new Exception("API message: " + errorMsg));
                        }
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
        });
    }
}
