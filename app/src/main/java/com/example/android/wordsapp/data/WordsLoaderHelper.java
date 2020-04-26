package com.example.android.wordsapp.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.wordsapp.MainActivity;

/**
 * Wraps up the details involved in loader using, and provides API for responding to the loader callbacks.
 */
public abstract class WordsLoaderHelper {

    // Loader id, need to be unique, increments every time a new loader is created
    private static int sLoaderId = 0;

    // The information needed for query
    private Context mContext;
    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;

    public WordsLoaderHelper(Context context, Uri uri, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {
        mContext = context;
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    /**
     * Do something based on the query result
     * @param cursor the query result passed in by the loader
     */
    public abstract void onLoadFinished(Cursor cursor);

    public abstract void onLoaderReset();

    /**
     * Setup a loader using what's already known in this class
     */
    public void initLoader() {
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks =
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @NonNull
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                        return new CursorLoader(
                                mContext,
                                mUri,
                                mProjection,
                                mSelection,
                                mSelectionArgs,
                                mSortOrder
                        );
                    }

                    @Override
                    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                        WordsLoaderHelper.this.onLoadFinished(data);
                    }

                    @Override
                    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                        WordsLoaderHelper.this.onLoaderReset();
                    }
                };
        LoaderManager.getInstance((AppCompatActivity)mContext).initLoader( ++ sLoaderId, new Bundle(), loaderCallbacks);
    }

    public Context getContext() {
        return mContext;
    }
}
