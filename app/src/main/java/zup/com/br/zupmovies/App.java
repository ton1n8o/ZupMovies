package zup.com.br.zupmovies;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import zup.com.br.zupmovies.services.Services;
import zup.com.br.zupmovies.util.NetworkUtil;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/7/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        Services.getInstance(getApplicationContext());
        NetworkUtil.getInstance(getApplicationContext());
    }
}
