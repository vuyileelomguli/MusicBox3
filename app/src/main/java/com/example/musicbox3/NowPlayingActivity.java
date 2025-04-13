package com.example.musicbox3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NowPlayingActivity extends AppCompatActivity {
    private ImageView albumArtView;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView elapsedTimeTextView;
    private TextView remainingTimeTextView;
    private SeekBar progressBar;
    private ImageView rewindButton;
    private ImageView playPauseButton;
    private ImageView forwardButton;
    private ImageView shuffleButton;
    private ImageView playlistButton;
    private ImageView lyricsButton;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private List<Song> songList;
    private int currentSongIndex = 0;
    private boolean isShuffleOn = false;
    private boolean isPlaying = false;

    // Activity result launcher for image selection
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        setAlbumArtFromUri(uri);
                        songList.get(currentSongIndex).setAlbumArtUri(uri.toString());
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // Initialize views
        initializeViews();

        // Get song list and current song index from intent
        if (getIntent().hasExtra("songList") && getIntent().hasExtra("songIndex")) {
            songList = getIntent().getParcelableArrayListExtra("songList");
            currentSongIndex = getIntent().getIntExtra("songIndex", 0);
        } else {
            // If no song data, create sample data
            createSampleSongList();
        }

        // Set up initial song
        loadCurrentSong();

        // Set up click listeners
        setupClickListeners();

        // Set up seek bar
        setupSeekBar();
    }

    private void initializeViews() {
        albumArtView = findViewById(R.id.album_art);
        titleTextView = findViewById(R.id.song_title);
        artistTextView = findViewById(R.id.song_artist);
        elapsedTimeTextView = findViewById(R.id.elapsed_time);
        remainingTimeTextView = findViewById(R.id.remaining_time);
        progressBar = findViewById(R.id.progress_bar);
        rewindButton = findViewById(R.id.rewind_button);
        playPauseButton = findViewById(R.id.play_pause_button);
        forwardButton = findViewById(R.id.forward_button);
        shuffleButton = findViewById(R.id.shuffle_button);
        playlistButton = findViewById(R.id.playlist_button);
        lyricsButton = findViewById(R.id.lyrics_button);
    }

    private void createSampleSongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Papercut", "Linkin Park", R.raw.song1));
        songList.add(new Song("Breaking The Habit", "Linkin Park", R.raw.song2));
        songList.add(new Song("Numb", "Linkin Park", R.raw.song3));
    }

    private void loadCurrentSong() {
        // Get current song
        Song currentSong = songList.get(currentSongIndex);


        // Reset media player
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Create new media player
        mediaPlayer = MediaPlayer.create(this, currentSong.getResourceId());
        mediaPlayer.setOnCompletionListener(mp -> playNextSong());

        // Set song info
        titleTextView.setText(currentSong.getTitle());
        artistTextView.setText(currentSong.getArtist());

        // Load album art
        if (currentSong.getAlbumArtUri() != null) {
            // Load custom album art if available
            albumArtView.setImageResource(R.drawable.default_album_art);
        } else {
            // Load default album art
            albumArtView.setImageResource(R.drawable.default_album_art);
        }

        // Start playing
        playPauseButton.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        isPlaying = true;

        // Update seek bar
        updateSeekBar();
    }

    private void setupClickListeners() {
        // Album art click listener (to select custom album art)
        albumArtView.setOnLongClickListener(v -> {
            pickImage.launch("image/*");
            return true;
        });

        // Play/Pause button
        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            }
            isPlaying = !isPlaying;
        });

        // Forward button
        forwardButton.setOnClickListener(v -> playNextSong());

        // Rewind button
        rewindButton.setOnClickListener(v -> playPreviousSong());

        // Shuffle button
        shuffleButton.setOnClickListener(v -> {
            isShuffleOn = !isShuffleOn;
            if (isShuffleOn) {
                shuffleButton.setAlpha(1.0f);
                Toast.makeText(this, "Shuffle On", Toast.LENGTH_SHORT).show();
            } else {
                shuffleButton.setAlpha(0.5f);
                Toast.makeText(this, "Shuffle Off", Toast.LENGTH_SHORT).show();
            }
        });

        // Playlist button
        playlistButton.setOnClickListener(v -> {
            // Return to song list
            finish();
        });

        // Lyrics button
        lyricsButton.setOnClickListener(v -> {
            Song currentSong = songList.get(currentSongIndex);
            showLyricsDialog(currentSong.getTitle(), currentSong.getArtist());
        });
    }

    private void setupSeekBar() {
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    updateTimeLabels();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to do
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing to do
            }
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            // Set max value
            progressBar.setMax(mediaPlayer.getDuration());

            // Update progress
            Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isPlaying) {
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        updateTimeLabels();
                        handler.postDelayed(this, 1000); // Update every second
                    }
                }
            };
            handler.post(updateRunnable);
        }
    }

    private void updateTimeLabels() {
        if (mediaPlayer != null) {
            // Calculate elapsed time
            int elapsedMillis = mediaPlayer.getCurrentPosition();
            String elapsed = formatTime(elapsedMillis);
            elapsedTimeTextView.setText(elapsed);

            // Calculate remaining time
            int remainingMillis = mediaPlayer.getDuration() - elapsedMillis;
            String remaining = "-" + formatTime(remainingMillis);
            remainingTimeTextView.setText(remaining);
        }
    }

    private String formatTime(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void playNextSong() {
        if (isShuffleOn) {
            // Random song (but not the current one)
            int nextIndex;
            do {
                nextIndex = (int) (Math.random() * songList.size());
            } while (nextIndex == currentSongIndex && songList.size() > 1);
            currentSongIndex = nextIndex;
        } else {
            // Next song
            currentSongIndex = (currentSongIndex + 1) % songList.size();
        }
        loadCurrentSong();
    }

    private void playPreviousSong() {
        if (isShuffleOn) {
            // Random song (but not the current one)
            int nextIndex;
            do {
                nextIndex = (int) (Math.random() * songList.size());
            } while (nextIndex == currentSongIndex && songList.size() > 1);
            currentSongIndex = nextIndex;
        } else {
            // Previous song
            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        }
        loadCurrentSong();
    }

    private void setAlbumArtFromUri(Uri uri) {
        if (uri != null) {
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    // Set the bitmap to the ImageView
                    albumArtView.setImageBitmap(bitmap);
                } else {
                    // If bitmap is null, load the default image
                    albumArtView.setImageResource(R.drawable.default_album_art);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                // Load the default image if the URI is not found
                albumArtView.setImageResource(R.drawable.default_album_art);
            } finally {
                // Close the InputStream if it was opened
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // If the URI is null, load the default image
            albumArtView.setImageResource(R.drawable.default_album_art);
        }
    }

    private void showLyricsDialog(String title, String artist) {
        // Create a simple dialog to show dummy lyrics
        LyricsDialog dialog = new LyricsDialog(this, title, artist);
        dialog.show();
    }
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String uriString = params[0];
            Bitmap bitmap = null;
            try {
                Uri uri = Uri.parse(uriString);
                bitmap = BitmapFactory.decodeStream(imageView.getContext().getContentResolver().openInputStream(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacksAndMessages(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}