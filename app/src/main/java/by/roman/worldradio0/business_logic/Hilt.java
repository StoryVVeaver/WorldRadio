package by.roman.worldradio0.business_logic;

import android.app.Application;

import java.io.File;

import dagger.hilt.android.HiltAndroidApp;
import org.osmdroid.config.Configuration;

@HiltAndroidApp
public class Hilt extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File basePath = new File(getFilesDir(), "osmdroid");
        File tileCache = new File(basePath, "tiles");

        Configuration.getInstance().setOsmdroidBasePath(basePath);
        Configuration.getInstance().setOsmdroidTileCache(tileCache);
        Configuration.getInstance().setUserAgentValue("@string/app_name");
    }
}

