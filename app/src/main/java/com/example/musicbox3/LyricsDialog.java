package com.example.musicbox3;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LyricsDialog extends Dialog {
    private String songTitle;
    private String artistName;
    private EditText lyricsEditText;
    private Button saveButton;

    public LyricsDialog(Context context, String songTitle, String artistName) {
        super(context);
        this.songTitle = songTitle;
        this.artistName = artistName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_lyrics);

        TextView titleView = findViewById(R.id.lyrics_title);
        saveButton = findViewById(R.id.save_button);
        Button closeButton = findViewById(R.id.close_button);

        // Set the title with song info
        titleView.setText(songTitle + " - " + artistName);

        // Sample lyrics for demonstration
        lyricsEditText.setText("This is a sample placeholder for lyrics or comments.\n\n" +
                "You can edit this text to add your own lyrics or notes about the song.\n\n" +
                "In a complete implementation, these would be stored with each song.");

        // Setup button listeners
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the lyrics (would be implemented in a full version)
                dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}