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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private String normalizeUrl(String host) {
        if (host == null) return null;

        if (host.startsWith("http://") || host.startsWith("https://")) {
            return host;
        }
        return "http://" + host + API_URL;
    }


    public void fetchStations (StationsCallback callback) {
        Random r= new Random();
        callback.onLoading();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(normalizeUrl(address.get(r.nextInt(address.size()))))
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
                                //Log.e("RadioAPI", "Unexpected response: " + jsonResponse);
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
}
