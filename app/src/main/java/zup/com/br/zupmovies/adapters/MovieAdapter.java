package zup.com.br.zupmovies.adapters;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private final OnCardClickListener mCardClickListener;

    public MovieAdapter(List<Movie> movieList,
                        OnCardClickListener clickListener) {
        mMovies = movieList;
        mCardClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        Movie m = mMovies.get(position);
        holder.title.setText(m.getTitle());
        holder.year.setText(m.getYear());
        holder.actors.setText(m.getActors());
        if (m.getPosterData() != null) {
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

    public Movie getItem(int position) {
        return this.mMovies.get(position);
    }

    /*Inner Classes */

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_view_list_item) View rowItem;
        @Bind(R.id.img_poster) ImageView poster;
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