package ke.topcast.presenter;

import java.util.List;

import ke.topcast.model.Podcast;

public interface OnNetworkTaskCompleted {
     void OnNetworkTaskCompleted(List<Podcast> podcasts);
}
