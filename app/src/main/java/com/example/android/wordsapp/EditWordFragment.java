package com.example.android.wordsapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class EditWordFragment extends DialogFragment {

    private EditText etWord;
    private Button btConfirm;
    private Button btRecord;

    private MediaManager mMediaManager;

    public interface OnEditFinishedListener {
        boolean onEditFinished(String word);
    }

    private void setMediaManager(MediaManager mediaManager) {
        mMediaManager = mediaManager;
    }

    public static EditWordFragment newInstance(MediaManager mediaManager) {
        EditWordFragment frag = new EditWordFragment();
        frag.setMediaManager(mediaManager);
        return frag;
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
        btRecord = getView().findViewById(R.id.button_record);

        // Setup ok button event
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etWord.getText().toString();
                OnEditFinishedListener listener = (OnEditFinishedListener) getActivity();
                if (listener.onEditFinished(word)) {
                    dismiss();
                }
            }
        });

        // Setup record button event
        btRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaManager.isRecording()) {
                    mMediaManager.startRecording();
                    btRecord.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                } else {
                    mMediaManager.stopRecording();
                    btRecord.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMediaManager.reset();
    }
}
