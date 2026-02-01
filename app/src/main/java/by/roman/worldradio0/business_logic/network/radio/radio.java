package by.roman.worldradio0.business_logic.network.radio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;
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
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
public class radio {
    private List<String> address = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new GsonBuilder()
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
                    listResult.add("de2.api.radio-browser.info");
                    listResult.add("fi1.api.radio-browser.info");
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

    public void fetchStations(RadioCallback<List<RadioStation>> callback, Filter filter, int offset, int limit) {
        if (address == null || address.isEmpty()) {
            Log.e("RadioAPI", "All addresses failed or list is empty");
            callback.onFailure(new Exception("No working servers found"));
            return;
        }

        callback.onLoading();
        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = normalizeUrl(address.get(index),"");

        HttpUrl.Builder urlBuilder = okhttp3.HttpUrl.parse(currentHost + "/json/stations/search").newBuilder();

        urlBuilder.addQueryParameter("offset", String.valueOf(offset));
        urlBuilder.addQueryParameter("limit", String.valueOf(limit));

        if (filter != null) {
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                urlBuilder.addQueryParameter("name", filter.getName());
            }
            if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
                urlBuilder.addQueryParameter("countrycode", filter.getCountry());
            }
            if (filter.getTag() != null && !filter.getTag().isEmpty()) {
                urlBuilder.addQueryParameter("tag", filter.getTag());
            }
            if (filter.getLang() != null && !filter.getLang().isEmpty()) {
                urlBuilder.addQueryParameter("language", filter.getLang());
            }
            if (filter.getCodec() != null && !filter.getCodec().isEmpty()) {
                urlBuilder.addQueryParameter("codec", filter.getCodec());
            }
            if (filter.getSort() == 1) urlBuilder.addQueryParameter("order", "name");
            if (filter.getSort() == 2) {urlBuilder.addQueryParameter("order", "votes"); urlBuilder.addQueryParameter("reverse", "true");}
            if (filter.getSort() == 3) {urlBuilder.addQueryParameter("order", "bitrate"); urlBuilder.addQueryParameter("reverse", "true");}
        }

        String fullUrl = urlBuilder.build().toString();
        Log.v("radio", fullUrl);

        Request request = new Request.Builder()
                .url(fullUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();

                        if (jsonResponse.startsWith("[")) {
                            Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                            List<RadioStation> list = new ArrayList<>();
                            for (Model i : stations) {
                                if (i.getUrl() != null) {
                                    list.add(new RadioStationDTO().fromModel(i).toModel());
                                }
                            }
                            callback.onSuccess(list);
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
                fetchStations(callback, filter, offset, limit);
            }
        });
    }

    public void fetchStationsByUUID(RadioCallback<List<RadioStation>> callback, List<String> list) {
        if (address == null || address.isEmpty()) {
            Log.e("RadioAPI", "All addresses failed or list is empty");
            callback.onFailure(new Exception("No working servers found"));
            return;
        }

        if (list == null || list.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        callback.onLoading();

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = normalizeUrl(address.get(index),"");

        String uuidsJoined = TextUtils.join(",", list);

        HttpUrl.Builder urlBuilder = okhttp3.HttpUrl.parse(currentHost + "/json/stations/byuuid").newBuilder();
        urlBuilder.addQueryParameter("uuids", uuidsJoined);

        String fullUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(fullUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String jsonResponse = body.string();

                        if (jsonResponse.startsWith("[")) {

                            Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                            List<RadioStation> list = new ArrayList<>();

                            for (Model i : stations) {
                                if (i.getUrl() != null) {
                                    list.add(new RadioStationDTO().fromModel(i).toModel());
                                }
                            }
                            callback.onSuccess(list);
                        } else {
                            handleFailure(new Exception("Not a JSON array: " + jsonResponse));
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
                fetchStationsByUUID(callback, list);
            }
        });
    }

    public void click(String uuid, RadioCallback<ClickModel> callback) {
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

    public void getCountries(RadioCallback<List<CountryModel>> callback) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/countrycodes"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String json = body.string();
                        CountryModel[] model = gson.fromJson(json, CountryModel[].class);
                        List<CountryModel> list = new ArrayList<>(Arrays.asList(model));
                        callback.onSuccess(list);
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
                getCountries(callback);
            }
        });
    }

    public void getLang(RadioCallback<List<LangModel>> callback) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/languages"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String json = body.string();
                        LangModel[] model = gson.fromJson(json, LangModel[].class);
                        List<LangModel> list = new ArrayList<>(Arrays.asList(model));
                        callback.onSuccess(list);
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
                getLang(callback);
            }
        });
    }

    public void getCodec(RadioCallback<List<CodecModel>> callback) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/codecs"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String json = body.string();
                        CodecModel[] model = gson.fromJson(json, CodecModel[].class);
                        List<CodecModel> list = new ArrayList<>(Arrays.asList(model));
                        callback.onSuccess(list);
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
                getCodec(callback);
            }
        });
    }

    public void getTags(RadioCallback<List<TagModel>> callback, String filter) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = address.get(index);

        Request request = new Request.Builder()
                .url(normalizeUrl(currentHost, "/json/tags/" + filter+ "?limit=50"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String json = body.string();
                        TagModel[] model = gson.fromJson(json, TagModel[].class);
                        List<TagModel> list = new ArrayList<>(Arrays.asList(model));
                        callback.onSuccess(list);
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
                getTags(callback, filter);
            }
        });
    }

    public void vote(String uuid, RadioCallback<VoteModel> callback) {
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

    public void getNames(RadioCallback<List<RadioStation>> callback, String filter) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        int index = r.nextInt(address.size());
        String currentHost = normalizeUrl(address.get(index),"");

        okhttp3.HttpUrl.Builder urlBuilder = okhttp3.HttpUrl.parse(currentHost + "/json/stations/search").newBuilder();

        urlBuilder.addQueryParameter("name", String.valueOf(filter));
        urlBuilder.addQueryParameter("limit", String.valueOf(50));
        urlBuilder.addQueryParameter("order", "votes");
        urlBuilder.addQueryParameter("reverse", "true");

        String fullUrl = urlBuilder.build().toString();
        Log.v("radio", fullUrl);

        Request request = new Request.Builder()
                .url(fullUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String jsonResponse = response.body().string();

                        if (jsonResponse.startsWith("[")) {
                            Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                            List<RadioStation> list = new ArrayList<>();
                            for (Model i : stations) {
                                if (i.getUrl() != null) {
                                    list.add(new RadioStationDTO().fromModel(i).toModel());
                                }
                            }
                            callback.onSuccess(list);
                        } else {
                            callback.onFailure(new Exception("Not a JSON array"));
                        }
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
                getNames(callback, filter);
            }
        });
    }

    public void getStationsByLocation(RadioCallback<List<RadioStation>> callback,
                                      double lat, double lon, double distance, int limit) {
        if (address.isEmpty()) {
            callback.onFailure(new Exception("No addresses available"));
            return;
        }

        Random r = new Random();
        String currentHost = normalizeUrl(address.get(r.nextInt(address.size())), "");

        okhttp3.HttpUrl.Builder urlBuilder = okhttp3.HttpUrl.parse(currentHost + "/json/stations/search").newBuilder();

        urlBuilder.addQueryParameter("geo_lat", String.valueOf(lat));
        urlBuilder.addQueryParameter("geo_long", String.valueOf(lon));
        urlBuilder.addQueryParameter("geo_distance", String.valueOf(distance));
        urlBuilder.addQueryParameter("has_geo_info", "true");
        urlBuilder.addQueryParameter("hidebroken", "true");
        urlBuilder.addQueryParameter("order", "clickcount");
        //urlBuilder.addQueryParameter("reverse", "true");
        urlBuilder.addQueryParameter("limit", String.valueOf(limit));

        String fullUrl = urlBuilder.build().toString();
        Log.v("radio_geo", fullUrl);

        Request request = new Request.Builder().url(fullUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful() && body != null) {
                        String jsonResponse = body.string();
                        if (jsonResponse.startsWith("[")) {
                            Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                            List<RadioStation> list = new ArrayList<>();
                            for (Model i : stations) {
                                if (i.getUrl() != null && i.getGeoLat() != 0) {
                                    list.add(new RadioStationDTO().fromModel(i).toModel());
                                }
                            }
                            callback.onSuccess(list);
                        } else {
                            callback.onFailure(new Exception("Not a JSON array"));
                        }
                    } else {
                        retry();
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                retry();
            }

            private void retry() {
                if (address.size() > 1) {
                    address.remove(currentHost);
                    getStationsByLocation(callback, lat, lon, distance, limit);
                } else {
                    callback.onFailure(new Exception("All mirrors failed"));
                }
            }
        });
    }
}