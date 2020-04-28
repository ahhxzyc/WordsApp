package com.example.android.wordsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.android.wordsapp.data.WordsContract;
import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.example.android.wordsapp.data.WordsCursorAdapter;
import com.example.android.wordsapp.data.WordsDbHelper;
import com.example.android.wordsapp.data.WordsListLoaderHelper;
import com.example.android.wordsapp.data.WordsLoaderHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mDrawerView;
    private ArrayList<String> mList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set working toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set drawer list to display the array list
        mDrawerLayout = findViewById(R.id.drawer_layout);
        loadList();
        mDrawerList = findViewById(R.id.drawer_list);
        mDrawerView = findViewById(R.id.drawer_view);
        mAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mList);
        mDrawerList.setAdapter(mAdapter);

        Button btAddWordList = findViewById(R.id.add_word_list);
        btAddWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWordList();
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Set the ListView to display a specific table
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
                            mList.add(input);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    private void saveList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(mList);
        editor.putStringSet("list", set).apply();
    }

    private void loadList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = prefs.getStringSet("list", new HashSet<String>());
        mList = new ArrayList<String>(set);
    }
}
