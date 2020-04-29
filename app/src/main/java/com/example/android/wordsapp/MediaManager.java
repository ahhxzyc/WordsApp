package com.example.android.wordsapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import androidx.appcompat.app.AppCompatActivity;

public class MediaManager {

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private boolean isRecording;

    private AppCompatActivity mActivity;

    public MediaManager(AppCompatActivity activity) {
        mActivity = activity;
        mPlayer = new MediaPlayer();
        mRecorder = new MediaRecorder();
    }

    public String getTempAudioFilePath() {
        return mActivity.getFilesDir() + "/temp_audio.3gp";
    }

    public String getAudioFilePath(long id) {
       return  mActivity.getFilesDir() + "/audio_" + id + ".3gp";
    }

    public void startPlaying(long id) {
        // TODO: play the audio file specified by *id*
        try {
            mPlayer.setDataSource(getAudioFilePath(id));
            mPlayer.prepare();
        } catch(Exception ignored) {

        }
        mPlayer.start();
        mPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) mActivity);
    }

    public void startRecording() {
        // TODO: record an audio and store it in a temp file
        isRecording = true;
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(getTempAudioFilePath());
        try {
            mRecorder.prepare();
        } catch (Exception ignored) {

        }
        mRecorder.start();
    }

    public void stopPlaying() {
        mPlayer.reset();
    }

    public void stopRecording() {
        isRecording = false;
        mRecorder.reset();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void reset() {
        stopPlaying();
        stopRecording();
    }
}
