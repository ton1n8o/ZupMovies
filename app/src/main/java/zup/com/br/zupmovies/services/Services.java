package zup.com.br.zupmovies.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.domains.SearchResponse;
import zup.com.br.zupmovies.util.Constants;

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
    private static final String NOT_FOUND = "False";
    private static final int PARSER_LIST = 0;
    private static final int PARSER_DETAIL = 1;

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

    /**
     * @param searhTerm  Movie name.
     * @param requestTag Tag used to cancel all requests.
     */
    public void searchMovies(OnServiceResponse onServiceResponse, @NonNull String searhTerm, @NonNull String requestTag) {

        mResponseHandler = onServiceResponse;

        if (TextUtils.isEmpty(searhTerm)) {
            Log.i(TAG, "informar termo de busca");
            return;
        }

        JsonObjectRequest jsonObjReq = buildJsonOjbectRequest(
                searhTerm,
                requestTag,
                PARSER_LIST
        );

        if (jsonObjReq != null) {
            addToRequestQueue(jsonObjReq);
        }

    }

    /**
     * @param imdbID     movie imdbId.
     * @param requestTag Tag used to cancel all requests.
     */
    public void searchByImdbId(OnServiceResponse onServiceResponse, @NonNull String imdbID, @NonNull String requestTag) {

        mResponseHandler = onServiceResponse;

        if (TextUtils.isEmpty(imdbID)) {
            Log.i(TAG, "informar imdbID");
            return;
        }

        JsonObjectRequest jsonObjReq = buildJsonOjbectRequest(
                imdbID,
                requestTag,
                PARSER_DETAIL
        );

        if (jsonObjReq != null) {
            addToRequestQueue(jsonObjReq);
        }

    }

    /**
     * Set a tag used to cancel all requests with it.
     *
     * @return JsonObjectRequest.
     */
    private JsonObjectRequest buildJsonOjbectRequest(String searhTerm, String requestTag, int parserType) {

        try {

            searhTerm = URLEncoder.encode(searhTerm.trim(), "UTF-8");
            String url = buildSearchRequestUrl(searhTerm, parserType);

            JsonObjectRequest jsonObjectRequest = buildJsonObjectRequest(url,
                    mCtx.getString(R.string.msg_error_on_search), parserType);

            jsonObjectRequest.setTag(requestTag);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return jsonObjectRequest;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            mResponseHandler.onError(mCtx.getString(R.string.msg_url_encoding_error) + e.getMessage());
        }

        return null;
    }

    public static synchronized Services getInstance(@NonNull Context appContext) {
        if (mInstance == null) {
            mInstance = new Services(appContext);
        }
        return mInstance;
    }

    public static synchronized Services getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(Services.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
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

    public ImageLoader getImageLoader(OnServiceResponse onServiceResponse) {
        mResponseHandler = onServiceResponse;
        return mImageLoader;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /*Private Methods*/

    private JsonObjectRequest buildJsonObjectRequest(String url, final String errorMessage, final int parserType) {
        return new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // parser do Objeto JSON
                try {

                    if (parserType == PARSER_LIST) {
                        SearchResponse listResponse = gson.fromJson(response.toString(), SearchResponse.class);

                        if (listResponse != null && listResponse.getSearch() != null) {
                            for (Movie movie : listResponse.getSearch()) {
                                validadeProperties(movie);
                            }
                            mResponseHandler.onResponse(listResponse.getSearch());
                        } else {
                            mResponseHandler.onResponse(new ArrayList<Movie>());
                        }

                    } else {

                        Movie m = gson.fromJson(response.toString(), Movie.class);
                        validadeProperties(m);
                        mResponseHandler.onResponse(m);
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Erro ao fazer parser do objeto JSON: " + e.getMessage());
                    mResponseHandler.onError(errorMessage);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mResponseHandler.onError(mCtx.getString(R.string.msg_connection_error) + error.getLocalizedMessage());
            }
        });
    }

    private static Movie validadeProperties(Movie movie) {
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getPoster())) {
            movie.setPoster(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getPlot())) {
            movie.setPlot(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getGenre())) {
            movie.setGenre(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getDirector())) {
            movie.setDirector(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getYear())) {
            movie.setYear(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getActors())) {
            movie.setActors(null);
        }
        if (NOT_APPLICABLE.equalsIgnoreCase(movie.getImdbRating())) {
            movie.setImdbRating(null);
        }
        if (NOT_FOUND.equalsIgnoreCase(movie.getResponse())) {
            movie = null;
        }
        return movie;
    }

    private static String buildSearchRequestUrl(String searchTerm, int parseType) {
        if (parseType == PARSER_DETAIL) { // MOVIE
            return String.format("%s?i=%s&plot=full&r=json", Constants.SERVER, searchTerm);
        }
        return String.format("%s?s=%s", Constants.SERVER, searchTerm);
    }

    /* Inner Classes */

    /**
     * Handle the server response.
     */
    public interface OnServiceResponse {
        void onResponse(Movie movie);

        void onResponse(List<Movie> movies);

        void onError(String msg);
    }

}
