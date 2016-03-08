package zup.com.br.zupmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class NetworkUtil {

    private static NetworkUtil networkUtil;
    private static Context context;

    private NetworkUtil(Context c) {
        context = c;
    }

    public static NetworkUtil getInstance(Context c) {
        if (networkUtil == null) {
            networkUtil = new NetworkUtil(c);
        }
        return networkUtil;
    }

    public static NetworkUtil getInstance() {
        if (networkUtil == null) {
            throw new IllegalStateException(NetworkUtil.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        }
        return networkUtil;
    }

    public static boolean isConected() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

}
