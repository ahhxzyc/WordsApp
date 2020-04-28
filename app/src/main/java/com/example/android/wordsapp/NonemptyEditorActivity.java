package com.example.android.wordsapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsItemLoaderHelper;
import com.example.android.wordsapp.data.WordsLoaderHelper;

public class NonemptyEditorActivity extends EditorActivity {

    private Uri mUri;
    private long mId;
    private String mTableName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setTitle(R.string.title_update);

        // decide the file path
        mUri = getIntent().getData();
        mId = ContentUris.parseId(mUri);
        mTableName = mUri.getPath().substring(1);

        mAudioFilePath = buildFilePath(mTableName, mId);

        // Deal with the Views
        grabViewObjects();
        WordsLoaderHelper loaderHelper = new WordsItemLoaderHelper(this, mUri);
        loaderHelper.initLoader();

        // Setup the buttons
        m_btRecord.setText(R.string.button_restart_recording);
        m_btPlay.setText(R.string.button_start_playing);
        m_btRecord.setOnClickListener(m_listenerRecord);
        m_btPlay.setOnClickListener(m_listenerPlay);
        mPlayer.setOnCompletionListener(m_listenerPlayComplete);
    }

    private ContentValues buildValues() {
        ContentValues values = new ContentValues();
        values.put(WordsEntry.COLUMN_WORD, m_etWord.getText().toString());
        values.put(WordsEntry.COLUMN_DESCRIPTION, m_etDescription.getText().toString());
        values.put(WordsEntry.COLUMN_AUDIO, buildFilePath(mTableName, mId));
        return values;
    }

    // *********************** The menu and its options ************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                getContentResolver().update(
                        mUri,
                        buildValues(),
                        WordsEntry._ID + "=?",
                        new String[]{String.valueOf(mId)}
                );
                makeSnackbarMessage(R.string.snackbar_update_done);
                finish();
                break;
        }
        return true;
    }
}
