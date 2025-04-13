package com.example.musicbox3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Song> songList;
    private SongAdapter songAdapter;
    private ListView songListView;
    private TextView nowPlayingTextView;
    private Button playButton;
    private Button nowPlayingButton;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private int currentSongIndex = 0;

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
                currentSongIndex = position;
                currentSong = songList.get(position);
                nowPlayingTextView.setText("Now Playing: " + currentSong.getTitle() +
                        " - " + currentSong.getArtist());

                launchNowPlayingActivity();
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
                    } else {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, currentSong.getResourceId());
                        mediaPlayer.start();
                        playButton.setText("Pause");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please select a song first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Optional: Hook up Now Playing button if it exists
        try {
            nowPlayingButton = findViewById(R.id.play_button); // Probably should be R.id.now_playing_button?
            if (nowPlayingButton != null) {
                nowPlayingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentSong != null) {
                            launchNowPlayingActivity();
                        } else {
                            Toast.makeText(MainActivity.this, "Please select a song first", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Silently ignore if not found
        }
    }

    private void launchNowPlayingActivity() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            ArrayList<Song> songArrayList = new ArrayList<>(songList);
            Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
            intent.putParcelableArrayListExtra("songList", songArrayList);
            intent.putExtra("songIndex", currentSongIndex);

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void createSongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Papercut", "Linkin Park", R.raw.song1));
        songList.add(new Song("Breaking The Habit", "Linkin Park", R.raw.song2));
        songList.add(new Song("Numb", "Linkin Park", R.raw.song3));
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
