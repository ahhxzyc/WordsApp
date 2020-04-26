package com.example.android.wordsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.example.android.wordsapp.data.WordsLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListFragment extends Fragment {

    private String mTableName;
    private WordsCursorAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wordlist, container, false);
        TextView tv = view.findViewById(R.id.list_fragment_empty_text);
        tv.setText(mTableName);
        setupListView(view);
        setupFab(view);
        return view;
    }

    private void setupFab(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EmptyEditorActivity.class);
                intent.setData(WordsEntry.buildContentUri(mTableName));
                startActivity(intent);
            }
        });
    }

    private void setupListView(View view) {
        // Setup the list view display
        final SwipeMenuListView lvMain = view.findViewById(R.id.main_list);
        lvMain.setEmptyView(view.findViewById(R.id.empty_view));
        mAdapter = new WordsCursorAdapter(getContext(), null); // Initially empty
        lvMain.setAdapter(mAdapter);

        // Create a swipe menu creator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem itemDel = new SwipeMenuItem(getActivity().getApplicationContext());
                itemDel.setWidth(dp2px(140));
                itemDel.setBackground(R.color.red);
                itemDel.setIcon(R.drawable.ic_add_pet);
                menu.addMenuItem(itemDel);
            }
            private int dp2px(int dp) {
                return Math.round(getResources().getDisplayMetrics().density * dp);
            }
        };
        lvMain.setMenuCreator(creator);

        // Set swipe menu click listener
        lvMain.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                long id = lvMain.getItemIdAtPosition(position);
                Uri uri = WordsEntry.buildWordsUri(mTableName, id);
                switch (index) {
                    case 0:
                        // delete this word
                        getActivity().getContentResolver().delete(
                                uri,
                                WordsEntry._ID + "=?",
                                new String[] {String.valueOf(position)}
                        );
                        break;
                }
                return false;
            }
        });

        // Setup the Loader on its mission
        WordsLoaderHelper loaderHelper = new WordsListLoaderHelper(
                getContext(),
                WordsEntry.buildContentUri(mTableName),
                null,
                null,
                null,
                null,
                mAdapter
        );
        loaderHelper.initLoader();

        // Setup the list view click event
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), NonemptyEditorActivity.class);
                intent.setData(WordsEntry.buildWordsUri(mTableName, id));
                startActivity(intent);
            }
        });
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }
}
