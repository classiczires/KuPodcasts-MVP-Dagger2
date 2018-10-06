package ke.topcast.view.fragments.PlayerFragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import ke.topcast.R;
import ke.topcast.view.activities.MainActivity;
import ke.topcast.interfaces.OnPodcastListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    PlayerFragmentCallbackListener mCallback;
    OnPodcastListener mCallback2;

    public RelativeLayout smallPlayer;
    ImageView smallCover;
    private MainActivity mainActivity;

    public ImageView player_controller;
    public RelativeLayout spToolbar;
    ImageView hidePlayer;

    PlayerView playerView;
    SimpleExoPlayer player;
    long playbackPosition;
    boolean playWhenReady = true;
    int currentWindow;

    public PlayerFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        playerView = view.findViewById(R.id.video_view);
        player_controller = (ImageView) view.findViewById(R.id.player_control_sp);
        player_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        spToolbar = (RelativeLayout) view.findViewById(R.id.smallPlayer_AB);
        smallCover = (ImageView) view.findViewById(R.id.selected_Podcast_image_sp);

        smallPlayer = (RelativeLayout) view.findViewById(R.id.smallPlayer);
        smallPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSmallPlayerTouched();
            }
        });

        hidePlayer = (ImageView) view.findViewById(R.id.hidePlayer);
        hidePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smallPlayer != null) {
                    if (playerView.getDefaultArtwork() != null) {
                        Bitmap bitmap = Bitmap.createBitmap(playerView.getDefaultArtwork());
                        smallCover.setImageBitmap(bitmap);
                    }
                    smallCover.setImageDrawable(playerView.getBackground());
                    mainActivity.hidePlayer();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        try {
            mCallback = (PlayerFragmentCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface PlayerFragmentCallbackListener {
        void onSmallPlayerTouched();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        /*
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);
        playerView.setControllerAutoShow(true);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);


        Uri uri = Uri.parse("http://dl.musickordi.com/Saiwan-Gagli/Seyvan-Gagli_Lorke[128].mp3");
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-topcast")).
                createMediaSource(uri);
    }
}
