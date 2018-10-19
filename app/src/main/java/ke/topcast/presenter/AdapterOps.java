package ke.topcast.presenter;

import ke.topcast.view.adapters.MyAdapter;

public interface AdapterOps {
    void notifyItemInsert(MyAdapter adapter, int position);
    void notifyItemRemove(MyAdapter adapter, int position);
    void notifyDataSetChange(MyAdapter adapter);
}
