package zup.com.br.zupmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/4/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> mMovies;
    private final ImageLoader mImageLoader;
    private final OnCardClickListener mCardClickListener;

    public MovieAdapter(List<Movie> movieList, ImageLoader imageLoader,
                        OnCardClickListener clickListener) {
        mMovies = movieList;
        mImageLoader = imageLoader;
        mCardClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        Movie m = mMovies.get(position);
        holder.title.setText(m.getTitle());
        holder.year.setText(m.getYear());
        holder.actors.setText(m.getActors());
        if (!TextUtils.isEmpty(m.getPoster())) {
            holder.poster.setImageUrl(m.getPoster(), this.mImageLoader);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public Movie getItem(int position) {
        return this.mMovies.get(position);
    }

    /*Inner Classes */

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_view) View rowItem;
        @Bind(R.id.img_poster)
        NetworkImageView poster;
        @Bind(R.id.tv_title) TextView title;
        @Bind(R.id.tv_year) TextView year;
        @Bind(R.id.tv_actors) TextView actors;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            rowItem.setOnClickListener(this);
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