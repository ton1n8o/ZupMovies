package zup.com.br.zupmovies.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.adapters.MovieAdapter;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.services.Services;
import zup.com.br.zupmovies.util.Constants;
import zup.com.br.zupmovies.util.NetworkUtil;
import zup.com.br.zupmovies.util.Util;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class ActSearch extends AppCompatActivity implements
        Services.OnServiceResponse, MovieAdapter.OnCardClickListener {

    /*Constants*/

    private static final String REQUEST_TAG = "SEARCH";
    public static final String MOVIE = "MOVIE";
    private static final String MOVIES_LIST = "MOVIES_LIST";

    // View Elements
    @Bind(R.id.edt_search)
    EditText edtSearch;
    @Bind(R.id.movies_card_list)
    RecyclerView recyclerViewList;
    @Bind(R.id.no_data)
    TextView tvNodata;

    /*Variables*/
    private Context appCtx;
    private ArrayList<Movie> moviesList;
    private ProgressDialog mProgress;
    private MovieAdapter mMovieAdapter;
    private boolean updateHomeOnback;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_search);
        ButterKnife.bind(this);

        this.setupRecyclerView();

        appCtx = this.getApplicationContext();
        if (savedInstanceState != null) {
            moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST);
            showMovies(moviesList);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST, moviesList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.MOVIE_UPDATE && data != null && data.getBooleanExtra("UPDATE", false)) {
            if (resultCode == RESULT_OK && !updateHomeOnback) { // movie deleted/created.
                updateHomeOnback = true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("UPDATE", updateHomeOnback);
        setResult(RESULT_OK, i);
        finish();
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

    /* Private Methods */

    private void setupRecyclerView() {
        recyclerViewList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewList.setLayoutManager(llm);
    }

    private void doSearch() {

        if (!NetworkUtil.isConected(appCtx)) {
            Util.createDialog(this, null, getString(R.string.msg_no_connection)).show();
            return;
        }

        String searchTerm = this.edtSearch.getText().toString();
        if (!TextUtils.isEmpty(searchTerm)) {

            mProgress = Util.createProgressDialog(this, getString(R.string.msg_title_please_wait),
                    getString(R.string.msg_searching));
            mProgress.show();

            Services.getInstance(appCtx, this).searchMovies(searchTerm, REQUEST_TAG);
        } else {
            Toast.makeText(this, R.string.msg_fill_in_search_term, Toast.LENGTH_LONG).show();
        }
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.edtSearch.getWindowToken(), 0);
    }

    private void showMovies(List<Movie> listMovies) {

        if (listMovies == null || listMovies.isEmpty()) {
            Util.createDialog(this, null, getResources().getString(R.string.msg_movie_not_found)).show();
            return;
        }

        this.moviesList = (ArrayList<Movie>) listMovies;

        this.mMovieAdapter = new MovieAdapter(
                listMovies,
                Services.getInstance(appCtx, this).getImageLoader(),
                true,
                this
        );
        recyclerViewList.setAdapter(this.mMovieAdapter);
        tvNodata.setVisibility(listMovies.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showMovieDetails(Movie movie) {
        Intent i = new Intent(this, ActMovieDetail.class);
        i.putExtra(MOVIE, movie);
        this.startActivityForResult(i, Constants.MOVIE_UPDATE);
    }

    private void hideProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.cancel();
        }
    }

    // Services.OnServiceResponse

    @Override
    public void onResponse(Movie movie) {

    }

    @Override
    public void onResponse(List<Movie> movies) {
        hideProgress();
        showMovies(movies);
    }

    @Override
    public void onError(String msg) {
        hideProgress();
        Util.createDialog(this, "Error", msg).show();
    }

    // MovieAdapter.OnCardClickListener

    @Override
    public void onCardClick(int position, ImageView imageView) {
        Movie m = this.mMovieAdapter.getItem(position);

        if (!NetworkUtil.isConected(appCtx)) {
            Util.createDialog(this, null, getString(R.string.msg_no_connection)).show();
            return;
        }

        this.showMovieDetails(m);
    }
}
