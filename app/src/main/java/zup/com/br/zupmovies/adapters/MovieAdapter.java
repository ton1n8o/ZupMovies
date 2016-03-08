package zup.com.br.zupmovies.adapters;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.util.NetworkUtil;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/4/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovies;
    private final ImageLoader mImageLoader;
    private static OnCardClickListener mCardClickListener;
    private static boolean mShowMovieType;

    public MovieAdapter(List<Movie> movieList, ImageLoader imageLoader,
                        boolean showMovieType,
                        OnCardClickListener clickListener) {
        mMovies = movieList;
        mImageLoader = imageLoader;
        mShowMovieType = showMovieType;
        mCardClickListener = clickListener;
    }

    public MovieAdapter(List<Movie> movieList, OnCardClickListener clickListener) {
        this(movieList, null, false, clickListener);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_view_search, parent, false
        );
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Movie m = mMovies.get(position);
        holder.title.setText(m.getTitle());
        holder.year.setText(m.getYear());
        holder.actors.setText(mShowMovieType ? m.getType() : m.getActors()); // only to populate this fild
        holder.director.setText(m.getDirector());

        if (NetworkUtil.isConected() && !TextUtils.isEmpty(m.getPoster())) {
            this.mImageLoader.get(m.getPoster(), ImageLoader.getImageListener(
                    holder.poster, R.drawable.ic_zup_movies, R.drawable.ic_zup_movies
            ));
        } else if (m.getPosterData() != null) {
            // load image from byte array
            holder.poster.setImageBitmap(
                    BitmapFactory.decodeByteArray(m.getPosterData(), 0, m.getPosterData().length)
            );
        } else {
            holder.poster.setImageResource(R.drawable.ic_zup_movies);
        }

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setMovies(List<Movie> list) {
        this.mMovies = list;
        this.notifyItemMoved(0, this.mMovies.size());
    }

    public Movie getItem(int position) {
        return this.mMovies.get(position);
    }

    /*Inner Classes */

    static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_view_list_item)
        View rowItem;
        @Bind(R.id.img_poster)
        ImageView poster;
        @Bind(R.id.tv_title)
        TextView title;
        @Bind(R.id.tv_year)
        TextView year;
        @Bind(R.id.tv_actors)
        TextView actors;
        @Bind(R.id.tv_director)
        TextView director;

        boolean ok;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            rowItem.setOnClickListener(this);
            ok = false;
        }

        @Override
        public void onClick(View v) {
            mCardClickListener.onCardClick(getAdapterPosition());
        }
    }

    public interface OnCardClickListener {
        void onCardClick(int position);
    }
}