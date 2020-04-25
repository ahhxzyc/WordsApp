package com.example.android.wordsapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.wordsapp.R;

import java.util.zip.Inflater;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;

public class WordsCursorAdapter extends CursorAdapter {

    public WordsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String word = cursor.getString(cursor.getColumnIndex(WordsEntry.COLUMN_WORD));
        String description = cursor.getString(cursor.getColumnIndex(WordsEntry.COLUMN_DESCRIPTION));
        TextView tvWord = view.findViewById(R.id.item_word);
        TextView tvDescription = view.findViewById(R.id.item_description);
        tvWord.setText(word);
        tvDescription.setText(description);
    }
}
