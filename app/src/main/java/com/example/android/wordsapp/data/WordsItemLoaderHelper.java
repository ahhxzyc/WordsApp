package com.example.android.wordsapp.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.wordsapp.R;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;

public class WordsItemLoaderHelper extends WordsLoaderHelper {

    // The view objects
    private EditText etWord;
    private EditText etDescription;

    public WordsItemLoaderHelper(Context context, Uri uri) {
        super(
                context,
                uri,
                null,
                WordsContract.WordsEntry._ID + "=?",
                new String[] {String.valueOf(ContentUris.parseId(uri))},
                null
        );
        // Grab the view objects
        etWord = ((AppCompatActivity)getContext()).findViewById(R.id.et_word);
        etDescription = ((AppCompatActivity)getContext()).findViewById(R.id.et_description);
    }

    /**
     * Update the EditTexts
     */
    @Override
    public void onLoadFinished(Cursor cursor) {
        cursor.moveToFirst();
        String word = cursor.getString(cursor.getColumnIndex(WordsEntry.COLUMN_WORD));
        String description = cursor.getString(cursor.getColumnIndex(WordsEntry.COLUMN_DESCRIPTION));
        etWord.setText(word);
        etDescription.setText(description);
    }

    @Override
    public void onLoaderReset() {
        // do nothing
    }
}
