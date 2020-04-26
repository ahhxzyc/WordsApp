package com.example.android.wordsapp.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.CursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class WordsListLoaderHelper extends WordsLoaderHelper {

    // Need to know to adapter in order to update its content
    private CursorAdapter mAdapter;

    public WordsListLoaderHelper(Context context, Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder, CursorAdapter adapter) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        mAdapter = adapter;
    }

    /**
     * Update the adapter by substituting its content with the latest cursor.
     * @param cursor the query result passed in by the loader
     */
    @Override
    public void onLoadFinished(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset() {
        mAdapter.swapCursor(null);
    }
}
