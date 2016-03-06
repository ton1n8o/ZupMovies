package zup.com.br.zupmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import zup.com.br.zupmovies.R;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/4/16.
 */
public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private final String[] mActor;

    public ActorAdapter(String[] movieList) {
        mActor = movieList;
    }

    @Override
    public ActorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_actor, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActorViewHolder holder, int position) {
        holder.actor.setText(mActor[position]);
    }

    @Override
    public int getItemCount() {
        return mActor.length;
    }

    /*Inner Classes */

    public class ActorViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.lbl_actor) TextView actor;

        public ActorViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}