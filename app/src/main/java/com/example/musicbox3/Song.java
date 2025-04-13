package com.example.musicbox3;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String artist;
    private int resourceId;
    private String albumArtUri;
    private String lyrics;

    public Song(String title, String artist, int resourceId) {
        this.title = title;
        this.artist = artist;
        this.resourceId = resourceId;
        this.albumArtUri = null; // Default, no custom album art
        this.lyrics = ""; // Default, no lyrics
    }

    public Song(String title, String artist, int resourceId, String albumArtUri) {
        this.title = title;
        this.artist = artist;
        this.resourceId = resourceId;
        this.albumArtUri = albumArtUri;
        this.lyrics = ""; // Default, no lyrics
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        resourceId = in.readInt();
        albumArtUri = in.readString();
        lyrics = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(resourceId);
        dest.writeString(albumArtUri);
        dest.writeString(lyrics);
    }
}