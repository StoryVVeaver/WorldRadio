package by.story_weaver.worldradiomonitoring.logic.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UrlProvider {

    private final List<String> urls = new ArrayList<>();
    private boolean loaded = false;

    public UrlProvider() {
        loadUrlsAsync();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadUrlsAsync() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> result = new ArrayList<>();
                try {
                    InetAddress[] list = InetAddress.getAllByName("all.api.radio-browser.info");
                    for (InetAddress item : list) {
                        result.add(item.getHostName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<String> result) {
                urls.clear();
                urls.addAll(result);
                loaded = true;
            }
        }.execute();
    }

    public synchronized String getRandom() {
        if (!loaded || urls.isEmpty()) {
            return "https://de1.api.radio-browser.info/";
        }
        return urls.get((int) (Math.random() * urls.size()));
    }

    public synchronized void switchOnError() {
        if (!loaded || urls.size() < 2) return;
        String old = getRandom();
        String next;
        do {
            next = getRandom();
        } while (next.equals(old));
    }
}
