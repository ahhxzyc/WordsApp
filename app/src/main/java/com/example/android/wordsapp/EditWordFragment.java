package com.example.android.wordsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;
import com.google.android.material.snackbar.Snackbar;

public class EditWordFragment extends DialogFragment {

    private EditText etWord;
    private Button btConfirm;

    public interface OnEditFinishedListener {
        void onEditFinished(String word);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_word, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etWord = getView().findViewById(R.id.et_word);
        btConfirm = getView().findViewById(R.id.button_confirm);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etWord.getText().toString();
                if (word == null || word.equals("")) {
                    Snackbar.make(getView(), "单词不能为空", Snackbar.LENGTH_LONG);
                    return;
                }
                OnEditFinishedListener listener = (OnEditFinishedListener) getActivity();
                listener.onEditFinished(word);
                dismiss();
            }
        });
    }
}
