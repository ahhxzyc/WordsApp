package com.example.android.wordsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        EditWordFragment.OnEditFinishedListener,
        MediaPlayer.OnCompletionListener {

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mDrawerView;
    private ArrayList<String> mDrawerList = new ArrayList<>();
    private ArrayAdapter<String> mDrawerAdapter;

    private FloatingActionButton mFab;
    private WordsCursorAdapter mAdapter;
    private SwipeMenuListView mListView;
    private String mCurrentWordList;

    private MediaManager mMediaManager;
    private int mCurrentPlayingPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set working toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instance initialization
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListView = findViewById(R.id.drawer_list);
        mDrawerView = findViewById(R.id.drawer_view);
        mFab = findViewById(R.id.fab);
        mListView = findViewById(R.id.main_list);

        mMediaManager = new MediaManager(this);

        setupDrawer();
        setupFab();
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

    private void setupFab() {
        // Setup floating button event
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Call a DialogFragment to handle word insertion
                EditWordFragment frag = EditWordFragment.newInstance(mMediaManager);
                frag.show(getSupportFragmentManager(), "EditWordFragment");
            }
        });
    }

    private void setupDrawer() {
        // Setup list display
        loadList();
        mDrawerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mDrawerList);
        mDrawerListView.setAdapter(mDrawerAdapter);
        //TODO: Implement single-choice mode for mDrawerListView

        // Setup button event
        Button btAddWordList = findViewById(R.id.add_word_list);
        btAddWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddWordList();
            }
        });

        // Setup list item click event
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentWordList = (String) parent.getItemAtPosition(position);
                mCurrentPlayingPos = -1;
                mMediaManager.reset();
                setupWordList();
            }
        });
    }

    private void setupWordList() {
        setTitle(mCurrentWordList);
        setupWordListDisplay();
        setupWordListItemClick();
        setupWordListSwipeMenu();
        mDrawerLayout.closeDrawer(mDrawerView);
        mFab.setVisibility(View.VISIBLE);
    }

    private void setupWordListDisplay() {
        mAdapter = new WordsCursorAdapter(MainActivity.this, null);
        mListView.setAdapter(mAdapter);
        new WordsListLoaderHelper(
                MainActivity.this,
                WordsEntry.CONTENT_URI,
                null,
                WordsEntry.COLUMN_WORDLIST + "=?",
                new String[] {mCurrentWordList},
                null,
                mAdapter
        ).initLoader();
        mListView.setEmptyView(findViewById(R.id.empty_view));
    }

    private void setupWordListItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentPlayingPos == position) {
                    stopPlaying();
                    view.setBackgroundColor(Color.WHITE);
                    return;
                }
                if (mCurrentPlayingPos != -1) {
                    View vCur = parent.getChildAt(mCurrentPlayingPos - parent.getFirstVisiblePosition());
                    vCur.setBackgroundColor(Color.WHITE);
                    stopPlaying();
                }
                View v = parent.getChildAt(position - parent.getFirstVisiblePosition());
                v.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                mMediaManager.reset();
                startPlaying(id, position);
            }
        });
    }

    private void setupWordListSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem itemDel = new SwipeMenuItem(MainActivity.this);
                itemDel.setIcon(R.drawable.ic_cross);
                itemDel.setBackground(R.color.red);
                itemDel.setWidth(dp2px(80));
                menu.addMenuItem(itemDel);
            }
            private int dp2px(int dp) {
                return Math.round(getResources().getDisplayMetrics().density * dp);
            }
        };
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Uri uri = WordsEntry.buildWordsUri(mListView.getItemIdAtPosition(position));
                        getContentResolver().delete(
                                uri,
                                WordsEntry._ID + "=?",
                                new String[] {String.valueOf(ContentUris.parseId(uri))}
                        );
                        break;
                }
                return false;
            }
        });
    }

    private void dialogAddWordList() {
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

    private void startPlaying(long id, int position) {
        mCurrentPlayingPos = position;
        mMediaManager.startPlaying(id);
    }

    private void stopPlaying() {
        mCurrentPlayingPos = -1;
        mMediaManager.stopPlaying();
    }

    // Called when "Confirm" in the editor-fragment is pressed
    @Override
    public boolean onEditFinished(String word) {

        if (mMediaManager.isRecording()) {
            mMediaManager.stopRecording();
        }

        if (word == null || word.equals("")) {
            Snackbar.make(mDrawerLayout, "单词不能为空", Snackbar.LENGTH_LONG).show();
            return false;
        }

        String tempFilePath = mMediaManager.getTempAudioFilePath();
        File file = new File(tempFilePath);
        if (!file.exists()) {
            Snackbar.make(mDrawerLayout, "录音不能为空", Snackbar.LENGTH_LONG).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(WordsEntry.COLUMN_WORD, word);
        values.put(WordsEntry.COLUMN_WORDLIST, mCurrentWordList);
        values.put(WordsEntry.COLUMN_AUDIO, tempFilePath);

        Uri insertedUri = getContentResolver().insert(WordsEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(insertedUri);

        String filePath = mMediaManager.getAudioFilePath(id);
        values.clear();
        values.put(WordsEntry.COLUMN_AUDIO, filePath);
        getContentResolver().update(
                insertedUri,
                values,
                WordsEntry._ID + "=?",
                new String[] {String.valueOf(id)}
        );


        file.renameTo(new File(filePath));

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        View view = mListView.getChildAt(mCurrentPlayingPos - mListView.getFirstVisiblePosition());
        view.setBackgroundColor(Color.WHITE);
        mCurrentPlayingPos = -1;
    }
}
