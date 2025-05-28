package by.roman.worldradio0.business_logic.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

@Singleton
public class NetworkUtil {
    public NetworkUtil(){

    }
    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    @NonNull
    public static String getConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return "DISCONNECTED";

        Network network = cm.getActiveNetwork();
        if (network == null) return "DISCONNECTED";

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (capabilities == null) return "UNKNOWN";

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return "WIFI";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return "MOBILE";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return "ETHERNET";
        } else {
            return "OTHER";
        }
    }
}
