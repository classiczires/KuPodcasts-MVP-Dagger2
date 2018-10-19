package ke.topcast.view.fragments.CategoriesFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ke.topcast.R;
import ke.topcast.model.Podcast;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MViewHolder> {

    private List<Podcast> podcasts;

    public class MViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, description;

        public MViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.item_list_title);
            description = (TextView) view.findViewById(R.id.item_list_description);
        }
    }

    public CategoriesAdapter(List<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_layout, parent, false);

        return new MViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {

        Podcast podcast = podcasts.get(position);

        holder.title.setText(podcast.getTitle());
        holder.description.setText(podcast.getDescription());
    }

    @Override
    public int getItemCount() {
        return podcasts.size();
    }

}