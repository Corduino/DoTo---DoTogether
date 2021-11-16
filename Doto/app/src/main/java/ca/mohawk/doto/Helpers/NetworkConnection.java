package ca.mohawk.doto.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//Semi depricated, but still works (Will connect and check if user has data/ wifi
public class NetworkConnection {
    public boolean isNetworkAvailable(Context context)
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }
}
