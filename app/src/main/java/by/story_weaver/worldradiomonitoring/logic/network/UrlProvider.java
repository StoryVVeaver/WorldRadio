package by.story_weaver.worldradiomonitoring.logic.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UrlProvider {

    private final List<String> urls = new ArrayList<>();
    private boolean loaded = false;
    private final Random random = new Random();

    public UrlProvider() {
        loadUrlsAsync();
    }

    private void loadUrlsAsync() {
        new Thread(() -> {
            try {
                InetAddress[] list = InetAddress.getAllByName("all.api.radio-browser.info");
                synchronized (urls) {
                    urls.clear();
                    for (InetAddress addr : list) {
                        urls.add("https://" + addr.getHostName());
                    }
                    loaded = true;
                }
            } catch (Exception e) {
                synchronized (urls) {
                    urls.clear();
                    urls.add("https://de1.api.radio-browser.info/");
                    loaded = true;
                }
            }
        }).start();
    }

    public synchronized String getRandom() {
        if (!loaded || urls.isEmpty()) {
            return "https://de1.api.radio-browser.info/";
        }
        return urls.get(random.nextInt(urls.size()));
    }

    public synchronized void switchOnError() {
        if (!loaded || urls.size() < 2) return;
        String old = getRandom();
        String next;
        do {
            next = getRandom();
        } while (next.equals(old));
        urls.set(0, next);
    }
}

