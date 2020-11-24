package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.PlayerActivity.mediaPlayer;
import static com.example.musicplayer.PlayerActivity.position1;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    RelativeLayout minimized;
    TextView song_name_playing, artist_name_playing;
    FloatingActionButton playPause;
    Thread playThread1;
    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
      //  recyclerView.setHasFixedSize(true);
        Log.e("MusicFiles", String.valueOf(musicFiles.size()));
        if (!(musicFiles.size() < 1)) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        minimized = (RelativeLayout) view.findViewById(R.id.minimized);
        minimized.setVisibility(View.GONE);
        song_name_playing = view.findViewById(R.id.songName);
        artist_name_playing = view.findViewById(R.id.artistName);
        playPause=view.findViewById(R.id.playPause);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (position1!=-1 && mediaPlayer.isPlaying()) {
            song_name_playing.setText(musicFiles.get(position1).getTitle());
            artist_name_playing.setText(musicFiles.get(position1).getArtist());
            playThreadButton();
            minimized.setVisibility(View.VISIBLE);

        }
        else{

            minimized.setVisibility(View.GONE);
        }
    }

    private void playThreadButton() {
        playThread1=new Thread(){
            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }


        };
        playThread1.start();
    }

    private void playPauseButtonClicked() {
        if(mediaPlayer.isPlaying()){
            playPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
        }
        else{
            playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
           playPause.setTag("playing");

        }
    }
}
