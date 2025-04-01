package com.example.musicbox3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    public SongAdapter(@NonNull Context context, @NonNull List<Song> songs) {
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.song_title);
        TextView artistTextView = convertView.findViewById(R.id.song_artist);

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());

        return convertView;
    }
}