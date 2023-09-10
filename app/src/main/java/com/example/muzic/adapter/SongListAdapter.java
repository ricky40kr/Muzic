package com.example.muzic.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.muzic.R;
import com.example.muzic.model.MediaPlayerSingleton;
import com.example.muzic.model.SongModel;
import com.example.muzic.util.PlayerActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{
    ArrayList<SongModel> songs;
    Context context;
    int lastPos=-1;

    public SongListAdapter(ArrayList<SongModel> songs, Context context){
        this.songs=songs;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.song_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.ViewHolder holder, int position) {
        SongModel songData=songs.get(position);
        holder.title.setText(songData.getTitle());
        holder.duration.setText(timeConvert(songData.getDuration()));
        if(!songData.getArtist().equals("<unknown>")) holder.artist.setText(songData.getArtist());

        // for MusicArtWork
        Uri artWorkUri=songData.getAlbumArt();
        if(artWorkUri!=null) {
            // set uri to ImageView
            holder.albumArtWork.setImageURI(artWorkUri);

            // to sure that Uri have an artWork
            if (holder.albumArtWork.getDrawable()==null){
                holder.albumArtWork.setImageResource(R.drawable.baseline_music_note_24);
            }
        }

        setAnimation(holder.itemView, position);

        // highlighting current playing song
        if(MediaPlayerSingleton.currIdx==position){
            holder.title.setTextColor(Color.parseColor("#009bff"));
            holder.artist.setTextColor(Color.parseColor("#009bff"));
            holder.duration.setTextColor(Color.parseColor("#009bff"));
            holder.playingMuzic.setVisibility(View.VISIBLE);
            if(MediaPlayerSingleton.getInstance().isPlaying()) holder.playingMuzic.playAnimation();
        }else{
            holder.title.setTextColor(Color.parseColor("#000000"));
            holder.artist.setTextColor(Color.parseColor("#000000"));
            holder.duration.setTextColor(Color.parseColor("#000000"));
            holder.playingMuzic.setVisibility(View.GONE);
            holder.playingMuzic.pauseAnimation();
        }

        // on Click songs in the list
        holder.itemView.setOnClickListener(v -> {
            MediaPlayerSingleton.getInstance().reset();
            MediaPlayerSingleton.currIdx= holder.getAdapterPosition();
            Intent intent= new Intent(context, PlayerActivity.class);
            intent.putParcelableArrayListExtra("LIST", songs);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView albumArtWork;
        TextView duration;
        TextView artist;
        LottieAnimationView playingMuzic;

        public ViewHolder(View itemView){
            super(itemView);
            title=itemView.findViewById(R.id.song_name);
            albumArtWork=itemView.findViewById(R.id.song_logo);
            duration=itemView.findViewById(R.id.song_duration);
            artist=itemView.findViewById(R.id.song_artist);
            playingMuzic=itemView.findViewById(R.id.playing_muzic_anime);
        }
    }

    private void setAnimation(View v, int position){
        if(position>lastPos) {
            Animation slideIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

            v.startAnimation(slideIn);

            lastPos=position;
        }
    }

    public static String timeConvert(String time){
        long millis=Long.parseLong(time);

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}