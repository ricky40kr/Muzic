package com.example.muzic.util;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muzic.R;
import com.example.muzic.model.MediaPlayerSingleton;
import com.example.muzic.model.SongModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity{
    TextView titleTv, currTime, totalTime, artistTv;
    SeekBar seekBar;
    ImageView playPauseBtn, nxtBtn, prevBtn, musicIcon;
    ArrayList<SongModel> songs;
    SongModel currSong;
    MediaPlayer mediaPlayer= MediaPlayerSingleton.getInstance();
    private boolean completionListenerTriggered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        titleTv=findViewById(R.id.song_title);
        currTime=findViewById(R.id.currTime);
        totalTime=findViewById(R.id.totalTime);
        seekBar=findViewById(R.id.seekbar);
        playPauseBtn=findViewById(R.id.play_pause);
        prevBtn=findViewById(R.id.previous);
        nxtBtn=findViewById(R.id.next);
        musicIcon=findViewById(R.id.music_icon_big);
        artistTv=findViewById(R.id.song_artist);

        titleTv.setSelected(true); // to marquee the title
        artistTv.setSelected(true);

        songs= getIntent().getParcelableArrayListExtra("LIST");

        setResourceWithSong();

        //Make sure you update Seekbar on UI thread
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress((mediaPlayer.getCurrentPosition()));
                    currTime.setText(timeConvert(mediaPlayer.getCurrentPosition()+""));
                }

                new Handler().postDelayed(this, 50);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        mediaPlayer.setOnCompletionListener(mediaPlayer-> playNextSong());

    }

    private void setResourceWithSong() {
        currSong=songs.get(MediaPlayerSingleton.currIdx);

        titleTv.setText(currSong.getTitle());
        if (!currSong.getArtist().equals("<unknown>")) artistTv.setText(currSong.getArtist());
        else artistTv.setText(R.string.unknown_artist);
        totalTime.setText(timeConvert(currSong.getDuration()));
        playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_outline_24);

        // for MusicArtWork
        Uri artWorkUri=currSong.getAlbumArt();
        if(artWorkUri!=null) {
            // set uri to ImageView
            musicIcon.setImageURI(artWorkUri);

            // to sure that Uri have an artWork
            if (musicIcon.getDrawable()==null){
                musicIcon.setImageResource(R.drawable.baseline_music_note_24);
            }
        }

        // onClick
        playPauseBtn.setOnClickListener(v->pausePlay());
        nxtBtn.setOnClickListener(v->playNextSong());
        prevBtn.setOnClickListener(v->playPreviousSong());

        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currSong.getPath());
            mediaPlayer.prepareAsync(); // for better performance

            mediaPlayer.setOnPreparedListener(mp -> { // onPreparedListener is required when we use prepareAsync() instead od prepare()
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            });
        }catch (IOException e){
            Toast.makeText(this, "Play Error!", Toast.LENGTH_SHORT).show();
        }
    }
    private void playNextSong(){
        if (MediaPlayerSingleton.currIdx==songs.size()-1){
            MediaPlayerSingleton.currIdx=-1;
        }
        MediaPlayerSingleton.currIdx++;
        mediaPlayer.reset();
        setResourceWithSong();
    }
    private void playPreviousSong(){
        if (MediaPlayerSingleton.currIdx==0){
            MediaPlayerSingleton.currIdx=songs.size();
        }
        MediaPlayerSingleton.currIdx--;
        mediaPlayer.reset();
        setResourceWithSong();
    }
    private void pausePlay(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseBtn.setImageResource(R.drawable.baseline_play_circle_outline_24);
        }else {
            mediaPlayer.start();
            playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        }
    }

    public static String timeConvert(String time){
        long millis=Long.parseLong(time);

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)%TimeUnit.MINUTES.toSeconds(1));
    }

}