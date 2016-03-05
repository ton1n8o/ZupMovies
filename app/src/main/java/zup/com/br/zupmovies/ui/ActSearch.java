package zup.com.br.zupmovies.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.services.Services;

public class ActSearch extends AppCompatActivity implements Services.OnServiceResponse {

    /*Constants*/

    private static final String REQUEST_TAG = "SEARCH";

    // View Elements
    @Bind(R.id.edt_search) EditText edtSearch;
    @Bind(R.id.fab_save) FloatingActionButton fabSave;

    // cardView
    @Bind(R.id.card_view) View cardView;
    @Bind(R.id.tv_title) TextView title;
    @Bind(R.id.tv_year) TextView year;
    @Bind(R.id.tv_actors) TextView actors;
    @Bind(R.id.img_poster) NetworkImageView poster;

    /*Variables*/
    private Context appCtx;
    private Movie movieFound;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        ButterKnife.bind(this);
        //TODO: Home Button enabled:
        appCtx = this.getApplicationContext();

        // hide controls
        setControlVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Services.getInstance(appCtx, this).getRequestQueue().cancelAll(REQUEST_TAG);
    }

    /* Activity Controls */

    @OnEditorAction(R.id.edt_search)
    public boolean actionSearchMovie(int actionId) {
        if (EditorInfo.IME_ACTION_SEARCH == actionId) {
            doSearch();
            return true;
        }
        return false;
    }

    @OnClick(R.id.fab_save)
    public void actoinSave() {
        if (movieFound != null) {

            if (Movie.findByImdbId(movieFound.getImdbID()) != null) {
                Toast.makeText(this, R.string.msg_movie_duplicated, Toast.LENGTH_LONG).show();
                return;
            }

            if (movieFound.getPoster() != null) {
                movieFound.customSave((BitmapDrawable) poster.getDrawable());
            } else {
                movieFound.save();
            }

            // finish and notify the main activity to reload its content.
            setResult(RESULT_OK);
            finish();

        }
    }

    /* Private Methods */

    private void doSearch() {
        String searchTerm = this.edtSearch.getText().toString();
        if (!TextUtils.isEmpty(searchTerm)) {
            Services.getInstance(appCtx, this).searchMovie(searchTerm, REQUEST_TAG);
        } else {
            Toast.makeText(this, R.string.msg_fill_in_search_term, Toast.LENGTH_LONG).show();
        }
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.edtSearch.getWindowToken(), 0);
    }

    private void showMovie(Movie movie) {
        if (movie != null) {

            this.movieFound = movie;
            this.setControlVisibility(View.VISIBLE);
            System.out.println("movieFound = \n" + movie.getTitle() + "img:" + movie.getPoster());

            title.setText(movie.getTitle());
            year.setText(movie.getYear());
            actors.setText(movie.getActors());

            if (!TextUtils.isEmpty(movie.getPoster())) {
                poster.setImageUrl(movie.getPoster(), Services.getInstance(appCtx, this).getImageLoader());
            } else {
                //TODO: show default image
            }
        }
    }

    private void setControlVisibility(int visibility) {
        this.fabSave.setVisibility(visibility);
        this.cardView.setVisibility(visibility);
    }

    // Services.OnServiceResponse

    @Override
    public void onResponse(Movie movie) {
        this.showMovie(movie);
    }

    @Override
    public void onError(String msg) {

    }
}
