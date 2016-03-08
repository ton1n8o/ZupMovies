package zup.com.br.zupmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.adapters.MovieAdapter;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.services.Services;
import zup.com.br.zupmovies.util.Constants;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
public class ActMain extends AppCompatActivity implements MovieAdapter.OnCardClickListener,
        PopupMenu.OnMenuItemClickListener {

    /*Constants*/

    public static final String MOVIE_ID = "MOVIE_ID";

    // View Elements
    @Bind(R.id.movies_card_list)
    RecyclerView recyclerViewList;
    @Bind(R.id.no_data)
    TextView tvNodata;

    /*Variables*/
    private MovieAdapter mMovieAdapter;
    private int lastSort;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);

        this.setupRecyclerView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.showMovies(Movie.findAll(Movie.SORT_BY_NAME_DESC));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getBooleanExtra("UPDATE", false)) {
            if (resultCode == RESULT_OK) { // movie created/deleted.
                showMovies(Movie.findAll(Movie.SORT_BY_DATE_DESC));
            }
        }
    }

    /* Activity Controls */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_by) {
            View menuItemView = findViewById(R.id.action_sort_by);
            showPopupMenu(menuItemView);
        }
        return true;
    }

    @OnClick(R.id.fabAddMovie)
    void showSearch() {
        this.startActivityForResult(new Intent(this, ActSearch.class), Constants.MOVIE_UPDATE);
    }

    /* Private Methods */

    private void setupRecyclerView() {
        recyclerViewList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewList.setLayoutManager(llm);
    }

    private void showMovies(List<Movie> listMovies) {
        this.mMovieAdapter = new MovieAdapter(
                listMovies,
                Services.getInstance().getImageLoader(),
                false,
                this
        );
        recyclerViewList.setAdapter(this.mMovieAdapter);
        tvNodata.setVisibility(listMovies.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showMovieDetails(Movie movie) {
        Intent i = new Intent(this, ActMovieDetail.class);
        i.putExtra(MOVIE_ID, movie.getId());
        this.startActivityForResult(i, Constants.MOVIE_UPDATE);
    }

    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main_options, popup.getMenu());
        popup.show();
    }


    // MovieAdapter.OnCardClickListener

    @Override
    public void onCardClick(int position) {
        Movie m = this.mMovieAdapter.getItem(position);
        this.showMovieDetails(m);
    }

    // PopupMenu.OnMenuItemClickListener

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order_by_date: {
                lastSort = (lastSort == Movie.SORT_BY_DATE_DESC) ?
                        Movie.SORT_BY_DATE_ASC : Movie.SORT_BY_DATE_DESC;
                this.showMovies(Movie.findAll(lastSort));
                break;
            }
            case R.id.action_order_by_name: {
                lastSort = (lastSort == Movie.SORT_BY_NAME_DESC) ?
                        Movie.SORT_BY_NAME_ASC : Movie.SORT_BY_NAME_DESC;
                this.showMovies(Movie.findAll(lastSort));
                break;
            }
            case R.id.action_order_by_year: {
                lastSort = (lastSort == Movie.SORT_BY_YEAR_DESC) ?
                        Movie.SORT_BY_YEAR_ASC : Movie.SORT_BY_YEAR_DESC;
                this.showMovies(Movie.findAll(lastSort));
                break;
            }
        }
        return false;
    }

}
