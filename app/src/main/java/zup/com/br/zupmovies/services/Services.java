package zup.com.br.zupmovies.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import zup.com.br.zupmovies.Constants;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;

/**
 * Classe de serviços responsável por comunicar com o server backend
 * carregando ou enviando dados.
 *
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class Services {

    /*Constants*/

    private static final String TAG = "Services";
    private static final String NOT_APPLICABLE = "N/A";

    /*Variables*/

    private static Services mInstance;
    private static Context mCtx;
    private static OnServiceResponse mResponseHandler;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create();

    /*Constructors*/

    private Services(Context context) {

        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {

                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }

                });

    }

    /*Public Methods*/

    public void searchMovie(String searhTerm, String requestTag) {

        if (TextUtils.isEmpty(searhTerm)) {
            Log.i(TAG, "informar termo de busca");
            return;
        }

        JsonObjectRequest jsonObjectRequest =
                buildJsonObjectRequest(buildRequestUrl(searhTerm),
                        "Erro ao processar dados do filme pesquisado.");
        jsonObjectRequest.setTag(requestTag);

        addToRequestQueue(jsonObjectRequest);

    }

    public static synchronized Services getInstance(@NonNull Context context, OnServiceResponse onServiceResponse) {

        try {
            mResponseHandler = onServiceResponse;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Services.OnServiceResponse");
        }

        if (mInstance == null) {
            mInstance = new Services(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /*Private Methods*/

    private JsonObjectRequest buildJsonObjectRequest(String url, final String errorMessage) {
        return new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // parser do Objeto JSON
                try {

                    Movie m = gson.fromJson(response.toString(), Movie.class);
                    if (NOT_APPLICABLE.equalsIgnoreCase(m.getPoster())) {
                        m.setPoster(null);
                    }
                    mResponseHandler.onResponse(m);

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao fazer parser do objeto JSON: " + e.getMessage());
                    mResponseHandler.onError(errorMessage);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                System.out.println("Services.onErrorResponse");
            }
        });
    }

    private static String buildRequestUrl(String searchTerm) {
        return String.format("%s?t=%s&plot=full&r=json", Constants.SERVER, searchTerm);
    }

    /*Inner Classes */

    /**
     *
     */
    public interface OnServiceResponse {
        void onResponse(Movie movie);

        void onError(String msg);
    }
}
