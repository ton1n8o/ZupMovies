package zup.com.br.zupmovies.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.android.volley.toolbox.ImageLoader;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.adapters.ActorAdapter;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.services.Services;
import zup.com.br.zupmovies.util.Util;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class ActMovieDetail extends AppCompatActivity implements Services.OnServiceResponse {

    /*Constants*/

    private static final String REQUEST_TAG = "SEARCH_IMDB_ID";

    // View Elements
    @Bind(R.id.img_poster)
    ImageView imageView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_year)
    TextView tvYear;
    @Bind(R.id.tv_genre)
    TextView tvGenre;
    @Bind(R.id.tv_director)
    TextView tvDirector;
    @Bind(R.id.tv_imdb_score)
    TextView tvScore;
    @Bind(R.id.tv_plot)
    TextView tvPlot;
    @Bind(R.id.lbl_actors)
    TextView tvLabelActors;
    @Bind(R.id.actors_card_list)
    RecyclerView recyclerViewList;
    @Bind(R.id.fab_save)
    FloatingActionButton fabSave;
    @Bind(R.id.ll_card_view_movie)
    View cardViewMovie;
    @Bind(R.id.ll_card_view_plot)
    LinearLayout llPlot;

    /*Variables*/
    private boolean newMovie;
    private Movie mMovie;
    private ProgressDialog mProgress;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_movie_detail);
        ButterKnife.bind(this);

        mProgress = Util.createProgressDialog(this, getString(R.string.msg_title_please_wait),
                getString(R.string.msg_loading));
        mProgress.show();

        this.initView(View.INVISIBLE);
        this.setupRecyclerView();

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            mMovie = Model.load(Movie.class, b.getLong(ActMain.MOVIE_ID));
            if (mMovie != null) {
                newMovie = false;
                showMovie(mMovie);
                setControlSaveButtonVisilibity(View.GONE);
            } else {
                newMovie = true;
                mMovie = b.getParcelable(ActSearch.MOVIE);
                this.loadDetails(mMovie);
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    public void onBackPressed() {
        Services.getInstance().getRequestQueue().cancelAll(REQUEST_TAG);
        super.onBackPressed();
    }

    /* Activity Controls */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!newMovie) {
            getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete: {
                this.confirmDelete();
                break;
            }
            case android.R.id.home:{
                onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_save)
    public void actoinSave() {
        if (mMovie != null) {

            if (Movie.findByImdbId(mMovie.getImdbID()) != null) {
                Util.createDialog(this, null, getString(R.string.msg_movie_duplicated)).show();
                return;
            }

            mMovie.setCreated(new Date());

            if (mMovie.getPoster() != null) {
                mMovie.setPosterData(Util.extractImageByteArray(
                                (BitmapDrawable) imageView.getDrawable())
                );
            }
            mMovie.save();

            Toast.makeText(this, R.string.msb_movie_saved, Toast.LENGTH_LONG).show();

            // finish and notify the main activity to reload its content.
            Intent i = new Intent();
            i.putExtra("UPDATE", true);

            setResult(RESULT_OK, i);
            finish();

        }
    }

    /* Private Methods */

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_confirm_movie_deletion))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteMovie();
                    }
                })
                .setNegativeButton(getString(R.string.msg_no), null)
                .show();
    }

    private void deleteMovie() {
        // finish and notify the main activity to reload its content.
        Movie.delete(Movie.class, this.mMovie.getId());

        Toast.makeText(this, R.string.msg_movie_deleted, Toast.LENGTH_LONG).show();

        Intent i = new Intent();
        i.putExtra("UPDATE", true);
        setResult(RESULT_OK, i);
        this.finish();
    }

    private void setupRecyclerView() {
        recyclerViewList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewList.setLayoutManager(llm);
    }

    private void showMovie(Movie movie) {

        hideProgress();
        this.mMovie = movie;

        if (movie == null || "False".equalsIgnoreCase(movie.getResponse())) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_error_on_search)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            return;
        }

        this.initView(View.VISIBLE);


        Util.setText(tvTitle, movie.getTitle());
        Util.setText(tvGenre, movie.getGenre());
        Util.setText(tvDirector, movie.getDirector());
        Util.setText(tvYear, movie.getYear());

        if (TextUtils.isEmpty(movie.getImdbRating())) {
            tvScore.setText("");
        } else {
            tvScore.setText(String.format("%s %s", getResources().getString(R.string.lbl_score), movie.getImdbRating()));
        }

        if (TextUtils.isEmpty(movie.getPlot())) {
            llPlot.setVisibility(View.GONE);
        } else {
            tvPlot.setText(movie.getPlot());
        }

        // load from data base
        if (movie.getPosterData() != null) {
            imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(movie.getPosterData(), 0, movie.getPosterData().length)
            );
        } else if (!TextUtils.isEmpty(movie.getPoster())) {
            Services.getInstance().getImageLoader(this).get(movie.getPoster(), ImageLoader.getImageListener(
                    imageView, R.drawable.ic_zup_movies, R.drawable.ic_zup_movies
            ));
        } else {
            imageView.setImageResource(R.drawable.ic_zup_movies);
        }

        if (!TextUtils.isEmpty(movie.getActors())) {
            recyclerViewList.setAdapter(new ActorAdapter(movie.getActors().split(",")));
        } else {
            tvLabelActors.setVisibility(View.GONE);
        }

    }

    private void loadDetails(Movie movie) {
        if (!TextUtils.isEmpty(movie.getImdbID())) {
            Services.getInstance().searchByImdbId(this, movie.getImdbID(), REQUEST_TAG);
        } else {
            Toast.makeText(this, R.string.msg_error_on_search, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void setControlSaveButtonVisilibity(int visibility) {
        this.fabSave.setVisibility(visibility);
    }

    private void initView(int visilibity) {
        recyclerViewList.setVisibility(visilibity);
        fabSave.setVisibility(visilibity);
        llPlot.setVisibility(visilibity);
        cardViewMovie.setVisibility(visilibity);
        tvLabelActors.setVisibility(visilibity);
    }

    private void hideProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.cancel();
        }
    }

    // Services.OnServiceResponse

    @Override
    public void onResponse(Movie movie) {
        this.showMovie(movie);
    }

    @Override
    public void onResponse(List<Movie> movies) {
        hideProgress();
    }

    @Override
    public void onError(String msg) {
        hideProgress();
        Util.createDialog(this, getResources().getString(R.string.msg_error), msg).show();
    }
}
