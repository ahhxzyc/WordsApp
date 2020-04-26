package com.example.android.wordsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.android.wordsapp.data.WordsContract;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.example.android.wordsapp.data.WordsLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    // The adapter for the list view
    private CursorAdapter mAdapter;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set working toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);

//        // Setup the list view display
//        final SwipeMenuListView lvMain = findViewById(R.id.main_list);
//        lvMain.setEmptyView(findViewById(R.id.empty_view));
//        mAdapter = new WordsCursorAdapter(this, null); // Initially empty
//        lvMain.setAdapter(mAdapter);

//        // Create a swipe menu creator
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                SwipeMenuItem itemDel = new SwipeMenuItem(getApplicationContext());
//                itemDel.setWidth(dp2px(140));
//                itemDel.setBackground(R.color.red);
//                itemDel.setIcon(R.drawable.ic_add_pet);
//                menu.addMenuItem(itemDel);
//            }
//            private int dp2px(int dp) {
//                return Math.round(getResources().getDisplayMetrics().density * dp);
//            }
//        };
//        lvMain.setMenuCreator(creator);
//
//        // Set swipe menu click listener
//        lvMain.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                long id = lvMain.getItemIdAtPosition(position);
//                Uri uri = WordsEntry.buildWordsUri(id);
//                switch (index) {
//                    case 0:
//                        // delete this word
//                        getContentResolver().delete(
//                                uri,
//                                WordsEntry._ID + "=?",
//                                new String[] {String.valueOf(position)}
//                        );
//                        break;
//                }
//                return false;
//            }
//        });
//
//        // Setup the Loader on its mission
//        WordsLoaderHelper loaderHelper = new WordsListLoaderHelper(
//                this,
//                WordsEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null,
//                mAdapter
//        );
//        loaderHelper.initLoader();
//
//        // Setup the list view click event
//        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, NonemptyEditorActivity.class);
//                intent.setData(WordsEntry.buildWordsUri(id));
//                startActivity(intent);
//            }
//        });
    }

    /**
     * Creating the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
