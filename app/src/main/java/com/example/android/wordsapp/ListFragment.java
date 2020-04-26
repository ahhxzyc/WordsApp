package com.example.android.wordsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment {

    private String mTableName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wordlist, container, false);
        TextView tv = view.findViewById(R.id.list_fragment_empty_text);
        tv.setText(mTableName);
        return view;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }
}
