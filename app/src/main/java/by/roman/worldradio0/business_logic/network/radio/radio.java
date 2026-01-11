package by.roman.worldradio0.business_logic.network.radio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import by.roman.worldradio0.business_logic.network.radio.callbacks.ClickCallback;
import by.roman.worldradio0.business_logic.network.radio.callbacks.StationsCallback;
import by.roman.worldradio0.business_logic.network.radio.callbacks.VoteCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
public class radio {
    private List<String> address = new ArrayList<>();
    private static final String API_URL = "/json/stations/topclick/1000";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<List<String>>() {}.getType(), new TagsAdapter())
            .create();

    public radio() {
        updateDnsList();
    }

    private void updateDnsList() {
        Log.v("radio", "start scanning");
        @SuppressLint("StaticFieldLeak")
        final AsyncTask<Void, Void, String[]> xxx = new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... params) {
                Vector<String> listResult = new Vector<>();
                try {
                    InetAddress[] list = InetAddress.getAllByName("all.api.radio-browser.info");
                    for (InetAddress item : list) {
                        Log.v("radio", item.getCanonicalHostName());
                        listResult.add(item.getCanonicalHostName());
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return listResult.toArray(new String[0]);
            }

            @Override
            protected void onPostExecute(String[] result) {
                address = new ArrayList<>(Arrays.asList(result));
            }
        }.execute();
    }

    private String normalizeUrl(String host, String end) {
        if (host == null) return null;
        if (host.startsWith("http://") || host.startsWith("https://")) {
            return host + end;
        }
        return "http://" + host + end;
    }

    public void fetchStations(StationsCallback callback) {
        if (address == null || address.isEmpty()) {
            Log.e("RadioAPI", "All addresses failed or list is empty");
            callback.onFailure(new Exception("No working servers found"));
            return;
        }

        callback.onLoading();
        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);
        String fullUrl = normalizeUrl(currentHost, API_URL);

        Request request = new Request.Builder().url(fullUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();

                        if (jsonResponse.startsWith("[")) {
                            Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                            List<RadioStationDTO> dto = new ArrayList<>();
                            for (Model i : stations) {
                                if (i.getUrl() != null) {
                                    dto.add(new RadioStationDTO().fromModel(i));
                                }
                            }
                            callback.onSuccess(dto);
                        } else {
                            handleFailure(new Exception("Not a JSON array"));
                        }
                    } else {
                        handleFailure(new Exception("Server error: " + response.code()));
                    }
                } catch (Exception e) {
                    handleFailure(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handleFailure(e);
            }
            private void handleFailure(Exception e) {
                Log.w("RadioAPI", "Host " + currentHost + " failed. Retrying...");
                address.remove(currentHost);
                fetchStations(callback);
            }
        });
    }

    public void click(String uuid, ClickCallback callback) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/url/" + uuid))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String json = body.string();
                        ClickModel model = gson.fromJson(json, ClickModel.class);
                        callback.onSuccess(model);
                    } else {
                        retry();
                    }
                } catch (Exception e) {
                    retry();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                retry();
            }

            private void retry() {
                address.remove(currentHost);
                click(uuid, callback);
            }
        });
    }

    public void vote(String uuid, VoteCallback callback) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/vote/" + uuid))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        VoteModel model = gson.fromJson(body.string(), VoteModel.class);
                        callback.onSuccess(model);
                    } else {
                        retry();
                    }
                } catch (Exception e) {
                    retry();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                retry();
            }

            private void retry() {
                address.remove(currentHost);
                vote(uuid, callback);
            }
        });
    }
}