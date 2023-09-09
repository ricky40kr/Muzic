package com.example.muzic.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongModel implements Parcelable {
    private String path;
    private String title;
    private String duration;
    private Uri albumArt;
    private String artist;

    public SongModel(String path, String title, String duration, String artist, Uri albumArt) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.artist=artist;
        this.albumArt=albumArt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Uri albumArt) {
        this.albumArt = albumArt;
    }

    // Parcelable implementation
    protected SongModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        duration = in.readString();
        artist=in.readString();
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeString(artist);
    }
}
