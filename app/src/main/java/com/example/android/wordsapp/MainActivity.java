package com.example.android.wordsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.wordsapp.data.WordsContract;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsDbHelper;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mDrawerView;
    private ArrayList<String> mDrawerList = new ArrayList<>();
    private ArrayAdapter<String> mDrawerAdapter;

    private FloatingActionButton mFab;
    private WordsCursorAdapter mAdapter;
    private ListView mListView;
    private String mCurrentWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set working toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find the views
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListView = findViewById(R.id.drawer_list);
        mDrawerView = findViewById(R.id.drawer_view);
        mFab = findViewById(R.id.fab);
        mListView = findViewById(R.id.main_list);

        setupDrawer();
        // TODO: setup the main list with its swipe functionality and etc.

        // Set the floating button
        // The button is not available until a table is selected
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Call a DialogFragment to handle word insertion
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(mDrawerView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveList();
    }

    private void setupDrawer() {
        // Set drawer list to display the array list
        loadList();
        mDrawerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mDrawerList);
        mDrawerListView.setAdapter(mDrawerAdapter);
        //TODO: Implement single-choice mode for mDrawerListView

        // Add-word-list button
        Button btAddWordList = findViewById(R.id.add_word_list);
        btAddWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWordList();
            }
        });

        // Setup the navigational function of the drawer
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setTitle((String)parent.getItemAtPosition(position));

                mAdapter = new WordsCursorAdapter(MainActivity.this, null);
                mListView.setAdapter(mAdapter);
                new WordsListLoaderHelper(
                        MainActivity.this,
                        WordsEntry.buildContentUri(getTitle().toString()),
                        null,
                        mAdapter
                ).initLoader();

                mDrawerLayout.closeDrawer(mDrawerView);
                mFab.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addWordList() {
        final EditText et = new EditText(MainActivity.this);
        et.setHint("名称");
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("新建单词本")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Snackbar.make(
                                    mDrawerLayout,
                                    "名称不能为空",
                                    Snackbar.LENGTH_LONG
                            ).show();
                        } else {
                            WordsDbHelper.getInstance(MainActivity.this, input).createTable();
                            mDrawerList.add(input);
                            mDrawerAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    private void saveList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(mDrawerList);
        editor.putStringSet("list", set).apply();
    }

    private void loadList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = prefs.getStringSet("list", new HashSet<String>());
        mDrawerList = new ArrayList<String>(set);
    }
}
