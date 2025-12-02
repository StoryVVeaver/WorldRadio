package by.roman.worldradio0.business_logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude, String countryName, String countryCode);
        void onError(String error);
    }

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    public static void requestLocation(Activity activity, LocationCallback callback) {
        if (isInternetAvailable(activity)) {
            new Thread(() -> {
                try {
                    JSONObject json = getJsonObject();

                    double lat = json.getDouble("lat");
                    double lon = json.getDouble("lon");
                    String countryName = json.getString("country");
                    String countryCode = json.getString("countryCode");

                    activity.runOnUiThread(() ->
                            callback.onLocationReceived(lat, lon, countryName, countryCode));

                } catch (Exception e) {
                    Log.e("HybridLocation", "Internet geolocation failed: " + e.getMessage());
                    requestLocalLocation(activity, callback);
                }
            }).start();
        } else {
            requestLocalLocation(activity, callback);
        }
    }

    @NonNull
    private static JSONObject getJsonObject() throws IOException, JSONException {
        URL url = new URL("http://ip-api.com/json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return new JSONObject(sb.toString());
    }

    private static void requestLocalLocation(Activity activity, LocationCallback callback) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            callback.onError("Location permission not granted");
            return;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            promptEnableLocation(activity);
            callback.onError("No location provider enabled");
            return;
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            getCountryFromLocation(activity, lastLocation, callback);
        }

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                getCountryFromLocation(activity, location, callback);
                locationManager.removeUpdates(this);
            }

            @Override public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onProviderDisabled(@NonNull String provider) {}
        };

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }
    }

    private static void getCountryFromLocation(Context context, Location location, LocationCallback callback) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                callback.onLocationReceived(
                        location.getLatitude(),
                        location.getLongitude(),
                        address.getCountryName(),
                        address.getCountryCode()
                );
            } else {
                callback.onError("Unable to get address from coordinates");
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onError("Geocoder failed: " + e.getMessage());
        }
    }

    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private static void promptEnableLocation(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Enable Location")
                .setMessage("Location is required to determine your coordinates and country. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) ->
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .show();
    }

    public static List<String> getCountryNamesFromIso(List<String> isoCodes) {
        Locale userLocale = Locale.getDefault();
        List<String> result = new ArrayList<>();

        for (String iso : isoCodes) {
            if (iso == null || iso.trim().isEmpty()) continue;

            Locale locale = new Locale("", iso);
            String name = locale.getDisplayCountry(userLocale);

            if (!name.isEmpty()) {
                result.add(name);
            } else {
                result.add(iso);
            }
        }

        return result;
    }


}
