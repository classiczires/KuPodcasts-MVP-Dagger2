package ke.topcast.view.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ke.topcast.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView art, purchaseArt;
    public TextView title, description, duration;

    public MyViewHolder(View view, boolean isGrid) {
        super(view);
        if (isGrid){
            art = (ImageView) view.findViewById(R.id.backImage);
            purchaseArt = (ImageView) view.findViewById(R.id.card_purchase);
            title = (TextView) view.findViewById(R.id.card_title);
            description = (TextView) view.findViewById(R.id.card_description);
            duration = (TextView) view.findViewById(R.id.card_duration);
        }else {
            art = (ImageView) view.findViewById(R.id.img);
            purchaseArt = (ImageView) view.findViewById(R.id.item_list_purchase);
            title = (TextView) view.findViewById(R.id.item_list_title);
            description = (TextView) view.findViewById(R.id.item_list_description);
            duration = (TextView) view.findViewById(R.id.item_list_duration);
        }
    }
}

