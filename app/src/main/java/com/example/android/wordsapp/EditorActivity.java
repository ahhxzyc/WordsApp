package com.example.android.wordsapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public abstract class EditorActivity extends AppCompatActivity {

    private final String LOG_TAG = "EditorActivity";
    protected final long TEMP_AUDIO_ID = -1;

    // This path can either be a temp file or an existing file
    protected String mAudioFilePath;

    // Media recorder and player
    protected MediaRecorder mRecorder = new MediaRecorder();
    protected MediaPlayer mPlayer = new MediaPlayer();
    protected boolean isRecording = false;

    // The views
    protected EditText m_etWord;
    protected EditText m_etDescription;
    protected Button m_btRecord;
    protected Button m_btPlay;

    // Listeners for buttons
    protected View.OnClickListener m_listenerRecord = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        }
    };
    protected View.OnClickListener m_listenerPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer.isPlaying()) {
                stopPlaying();
            } else {
                startPlaying();
            }
        }
    };
    protected MediaPlayer.OnCompletionListener m_listenerPlayComplete =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            };

    protected void grabViewObjects() {
        m_etWord = findViewById(R.id.et_word);
        m_etDescription = findViewById(R.id.et_description);
        m_btRecord = findViewById(R.id.button_record);
        m_btPlay = findViewById(R.id.button_play);
    }

    protected String buildFilePath(long id) {
        return getFilesDir() + "/audio_" + id + ".3gp";
    }

    protected void makeSnackbarMessage(int msgId) {
        Snackbar.make(findViewById(R.id.editor_parent_view), msgId, Snackbar.LENGTH_LONG).show();
    }

    // ******************************** Recorder part ***************************************
    private void configureRecorder() throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mAudioFilePath);
        mRecorder.prepare();
    }

    private void startRecording() {
        try {
            configureRecorder();
        } catch (IOException e) {
            Log.e(LOG_TAG, "failed to setup the recorder");
        }
        mRecorder.start();
        isRecording = true;
        m_btRecord.setText(R.string.button_stop_recording);
        makeSnackbarMessage(R.string.snackbar_recording_start);
    }

    private void stopRecording() {
        mRecorder.stop();
        isRecording = false;
        m_btRecord.setText(R.string.button_restart_recording);
        makeSnackbarMessage(R.string.snackbar_recording_done);
    }

    // ******************************** Player part ***************************************
    private void configurePlayer() throws Exception {
        mPlayer.setDataSource(mAudioFilePath);
        mPlayer.prepare();
    }

    private void startPlaying() {
        try {
            configurePlayer();
        } catch (Exception e) {
            Log.e(LOG_TAG, "failed to setup the player");
        }
        mPlayer.start();
        m_btPlay.setText(R.string.button_stop_playing);
        makeSnackbarMessage(R.string.snackbar_playing_start);
    }

    private void stopPlaying() {
        mPlayer.reset();
        m_btPlay.setText(R.string.button_start_playing);
        makeSnackbarMessage(R.string.snackbar_playing_done);
    }
}