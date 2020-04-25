package com.example.android.wordsapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;

import java.io.File;

public class EmptyEditorActivity extends EditorActivity {
    /**
     * Grab view objects and setup listeners on creation of this activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setTitle(R.string.title_insert);
        grabViewObjects();

        // decide the file path
        mAudioFilePath = buildFilePath(TEMP_AUDIO_ID);

        // Initialize the button texts
        m_btRecord.setText(R.string.button_start_recording);
        m_btPlay.setText(R.string.button_start_playing);

        // Setup the buttons
        m_btRecord.setOnClickListener(m_listenerRecord);
        m_btPlay.setOnClickListener(m_listenerPlay);
        mPlayer.setOnCompletionListener(m_listenerPlayComplete);
    }

    private ContentValues buildValues() {
        ContentValues values = new ContentValues();
        values.put(WordsEntry.COLUMN_WORD, m_etWord.getText().toString());
        values.put(WordsEntry.COLUMN_DESCRIPTION, m_etDescription.getText().toString());
        values.put(WordsEntry.COLUMN_AUDIO, "foo");
        return values;
    }

    // ************************ The menu and its options ****************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertWord(WordsEntry.CONTENT_URI);
                makeSnackbarMessage(R.string.snackbar_insert_done);
                finish();
                break;
        }
        return true;
    }

    private void insertWord(Uri uri) {
        // Insert with a dummy column
        Uri retUri = getContentResolver().insert(WordsEntry.CONTENT_URI, buildValues());
        long id = ContentUris.parseId(retUri);
        // Then update it with the correct file path
        ContentValues updateValues = new ContentValues();
        updateValues.put(WordsEntry.COLUMN_AUDIO, buildFilePath(id));
        getContentResolver().update(
                retUri,
                updateValues,
                WordsEntry._ID + "=?",
                new String[]{String.valueOf(id)}
        );
        // Correct the actual file name
        File file = new File(buildFilePath(TEMP_AUDIO_ID));
        file.renameTo(new File(buildFilePath(id)));
    }
}
