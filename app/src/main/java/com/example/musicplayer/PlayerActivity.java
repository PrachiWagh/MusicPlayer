package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.musicplayer.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    TextView song_name,artist_name,duration_played,duration_total;
    SeekBar scrubBar;
    FloatingActionButton play_pause_button;
    ImageView skip_prev,skip_next,shuffle,repeat,cover_art,back_button;
    static int position1=-1;
    static ArrayList<MusicFiles> listOFSongs=new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
   private Handler handler=new Handler();
   private Thread playThread,prevThread,nextThread;
   static boolean shuffleBoolean=false,repeatBoolean=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        song_name.setText(listOFSongs.get(position1).getTitle());
        artist_name.setText(listOFSongs.get(position1).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        scrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if(mediaPlayer!=null && fromUser) { mediaPlayer.seekTo(progress);}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(play_pause_button.getTag()=="playing")mediaPlayer.pause();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               if(play_pause_button.getTag()=="playing") mediaPlayer.start();

            }
        });
       PlayerActivity.this.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if(mediaPlayer!=null){
                   int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                   scrubBar.setProgress(mCurrentPos*1000);
                   duration_played.setText(formattedTime(mCurrentPos));
               }
               handler.postDelayed(this,1000);
           }
       });
       shuffle.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(shuffleBoolean){
                   shuffleBoolean=false;
                   shuffle.setImageResource(R.drawable.ic_shuffle_off);
                   shuffle.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
               }
               else{
                   shuffleBoolean=true;
                   shuffle.setImageResource(R.drawable.ic_shuffle_on);
                   shuffle.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.teal_200));
               }
           }
       });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    repeatBoolean=false;
                    repeat.setImageResource(R.drawable.ic_repeat_off);
                    repeat.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
                }
                else{
                    repeatBoolean=true;
                    repeat.setImageResource(R.drawable.ic_repeat_on);
                    repeat.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.teal_200));
                }
            }
        });

    }



    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                play_pause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }


        };
        playThread.start();
    }
    private void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying()){
            play_pause_button.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            play_pause_button.setTag("paused");
            scrubBar.setMax(mediaPlayer.getDuration());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
        });

    }
        else{
            play_pause_button.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            play_pause_button.setTag("playing");
            scrubBar.setMax(mediaPlayer.getDuration());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }
    private void prevThreadBtn(){
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                skip_prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }


        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position1=getRandom(listOFSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean ){
                position1=((position1-1)<0 ? (listOFSongs.size()-1):(position1-1));;}

            uri=Uri.parse(listOFSongs.get(position1).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            song_name.setText(listOFSongs.get(position1).getTitle());
            artist_name.setText(listOFSongs.get(position1).getArtist());
            getMetaData(uri);
            mediaPlayer.setOnCompletionListener(this);
            scrubBar.setMax(mediaPlayer.getDuration());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_button.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position1=getRandom(listOFSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean ){
                position1=((position1-1)<0 ? (listOFSongs.size()-1):(position1-1));;}
            uri=Uri.parse(listOFSongs.get(position1).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            song_name.setText(listOFSongs.get(position1).getTitle());
            artist_name.setText(listOFSongs.get(position1).getArtist());
            getMetaData(uri);
            scrubBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(this);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_button.setImageResource(R.drawable.ic_play);
        }
    }

    private  void nextThreadBtn(){
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                skip_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }


        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position1=getRandom(listOFSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean ){
            position1=((position1+1)%listOFSongs.size());}
            //else repeat hence position remains same
            uri=Uri.parse(listOFSongs.get(position1).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            song_name.setText(listOFSongs.get(position1).getTitle());
            artist_name.setText(listOFSongs.get(position1).getArtist());
            getMetaData(uri);
            mediaPlayer.setOnCompletionListener(this);
            scrubBar.setMax(mediaPlayer.getDuration());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_button.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position1=getRandom(listOFSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean ){
                position1=((position1+1)%listOFSongs.size());}
            uri=Uri.parse(listOFSongs.get(position1).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            song_name.setText(listOFSongs.get(position1).getTitle());
            artist_name.setText(listOFSongs.get(position1).getArtist());
            getMetaData(uri);
            mediaPlayer.setOnCompletionListener(this);
            scrubBar.setMax(mediaPlayer.getDuration());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPos=mediaPlayer.getCurrentPosition()/1000;
                        scrubBar.setProgress(mCurrentPos*1000);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            play_pause_button.setImageResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private String formattedTime(int mCurrentPos) {
        String totalOut="";
        String totalNew="";
        String seconds=String.valueOf(mCurrentPos%60);
        String minutes=String.valueOf(mCurrentPos/60);
        totalOut=minutes+":"+seconds;
        totalNew=minutes+":0"+seconds;
        if(seconds.length()==1){
            return totalNew;
        }
        return  totalOut;
    }


    private void getIntentMethod() {
        position1=getIntent().getIntExtra("position",-1);
        listOFSongs=musicFiles;
        if(listOFSongs!=null){
            play_pause_button.setImageResource(R.drawable.ic_pause);
            uri=Uri.parse(listOFSongs.get(position1).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else{
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        scrubBar.setMax(mediaPlayer.getDuration());
        getMetaData(uri);
    }

    private void initViews() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.artist_name);
        duration_played=findViewById(R.id.duration_played);
        duration_total=findViewById(R.id.durationTotal);
        scrubBar=findViewById(R.id.seekBar);
        play_pause_button=findViewById(R.id.play_pause);
        skip_next=findViewById(R.id.id_next);
        skip_prev=findViewById(R.id.id_prev);
        shuffle=findViewById(R.id.id_shuffle);
        repeat=findViewById(R.id.id_repeat);
        cover_art=findViewById(R.id.cover_art);

    }
    private void getMetaData(Uri uri){
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listOFSongs.get(position1).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=mediaMetadataRetriever.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this).asBitmap().load(art).into(cover_art);
        }
        else{
            Glide.with(this).asBitmap().load(R.drawable.no_album_art).into(cover_art);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            play_pause_button.setImageResource(R.drawable.ic_pause);
            play_pause_button.setTag("playing");
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}