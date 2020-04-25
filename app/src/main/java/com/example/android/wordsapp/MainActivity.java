package com.example.android.wordsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.android.wordsapp.data.WordsContract;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.example.android.wordsapp.data.WordsLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // The adapter for the list view
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind FAB to the editor activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmptyEditorActivity.class));
            }
        });

        // Setup the list view display
        ListView lvMain = findViewById(R.id.main_list);
        lvMain.setEmptyView(findViewById(R.id.empty_view));
        mAdapter = new WordsCursorAdapter(this, null); // Initially empty
        lvMain.setAdapter(mAdapter);

        // Setup the Loader on its mission
        WordsLoaderHelper loaderHelper = new WordsListLoaderHelper(
                this,
                WordsEntry.CONTENT_URI,
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
                Intent intent = new Intent(MainActivity.this, NonemptyEditorActivity.class);
                intent.setData(WordsEntry.buildWordsUri(id));
                startActivity(intent);
            }
        });
    }

    /**
     * Creating the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
