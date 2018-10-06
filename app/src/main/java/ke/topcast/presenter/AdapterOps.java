package ke.topcast.presenter;

public interface AdapterOps {
    void notifyItemInsert(int position);
    void notifyItemRemove(int position);
    void notifyDataSetChange();
}
