package com.example.musicbox3;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Song> songList;
    private SongAdapter songAdapter;
    private ListView songListView;
    private TextView nowPlayingTextView;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        songListView = findViewById(R.id.song_list_view);
        nowPlayingTextView = findViewById(R.id.now_playing_text);
        playButton = findViewById(R.id.play_button);

        // Create song list
        createSongList();

        // Setup adapter
        songAdapter = new SongAdapter(this, songList);
        songListView.setAdapter(songAdapter);

        // ListView item click listener
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSong = songList.get(position);
                nowPlayingTextView.setText("Now Playing: " + currentSong.getTitle() +
                        " - " + currentSong.getArtist());

                // Stop previous song if playing
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                // Create new MediaPlayer
                mediaPlayer = MediaPlayer.create(MainActivity.this, currentSong.getResourceId());
            }
        });

        // Play button listener
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong != null) {
                    if (mediaPlayer != null) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            playButton.setText("Pause");
                        } else {
                            mediaPlayer.pause();
                            playButton.setText("Play");
                        }
                    }
                }
            }
        });
    }

    private void createSongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Moonlight Sonata", "Beethoven", R.raw.song1));
        songList.add(new Song("Canon in D", "Pachelbel", R.raw.song2));
        songList.add(new Song("Swan Lake", "Tchaikovsky", R.raw.song3));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}