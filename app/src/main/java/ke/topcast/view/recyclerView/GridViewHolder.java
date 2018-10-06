package ke.topcast.view.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ke.topcast.R;

public class GridViewHolder extends RecyclerView.ViewHolder {
    public ImageView art, purchaseArt;
    public TextView title, description, duration;

    public GridViewHolder(View view) {
        super(view);
        art = (ImageView) view.findViewById(R.id.backImage);
        purchaseArt = (ImageView) view.findViewById(R.id.card_purchase);
        title = (TextView) view.findViewById(R.id.card_title);
        description = (TextView) view.findViewById(R.id.card_description);
        duration = (TextView) view.findViewById(R.id.card_duration);
    }
}
