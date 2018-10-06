package ke.topcast.view.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ke.topcast.R;

public class ListViewHolder extends RecyclerView.ViewHolder {
    public ImageView art, purchaseArt;
    public TextView title, description, duration;

    public ListViewHolder(View view) {
        super(view);
        art = (ImageView) view.findViewById(R.id.img);
        purchaseArt = (ImageView) view.findViewById(R.id.item_list_purchase);
        title = (TextView) view.findViewById(R.id.item_list_title);
        description = (TextView) view.findViewById(R.id.item_list_description);
        duration = (TextView) view.findViewById(R.id.item_list_duration);
    }
}

