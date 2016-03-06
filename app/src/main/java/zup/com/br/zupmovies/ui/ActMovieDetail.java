package zup.com.br.zupmovies.ui;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.activeandroid.Model;

import butterknife.Bind;
import butterknife.ButterKnife;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.adapters.ActorAdapter;
import zup.com.br.zupmovies.domains.Movie;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class ActMovieDetail extends AppCompatActivity {

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
    @Bind(R.id.actors_card_list)
    RecyclerView recyclerViewList;

    @Bind(R.id.ll_card_view_plot)
    LinearLayout llPlot;

    /*Variables*/
    private Movie mMovie;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_movie_detail);
        ButterKnife.bind(this);

        this.setupRecyclerView();

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            this.mMovie = Model.load(Movie.class, b.getLong(ActMain.MOVIE_ID));
            this.showMovieData(this.mMovie);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
        }

        return super.onOptionsItemSelected(item);
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
        setResult(RESULT_OK);
        finish();
    }

    private void setupRecyclerView() {
        recyclerViewList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewList.setLayoutManager(llm);
    }

    private void showMovieData(Movie movie) {

        tvTitle.setText(movie.getTitle());
        tvYear.setText(movie.getYear());
        tvGenre.setText(movie.getGenre());
        tvDirector.setText(movie.getDirector());
        tvScore.setText(String.format("%s %s", getResources().getString(R.string.lbl_score), movie.getImdbRating()));

        if (TextUtils.isEmpty(movie.getPlot())) {
            llPlot.setVisibility(View.GONE);
        } else {
            tvPlot.setText(movie.getPlot());
        }

        if (movie.getPosterData() != null) {
            imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(movie.getPosterData(), 0, movie.getPosterData().length)
            );
        } else {
            imageView.setImageResource(R.drawable.ic_zup_movies);
        }

        if (!TextUtils.isEmpty(movie.getActors())) {
            recyclerViewList.setAdapter(new ActorAdapter(movie.getActors().split(",")));
        }

    }

}
