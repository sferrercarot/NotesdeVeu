package com.example.notesdeveu;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;

import com.example.notesdeveu.databinding.FragmentFirstBinding;

import java.io.File;
import java.io.IOException;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private MediaRecorder recorder;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private ImageView logo;
    private FloatingActionButton record;
    private FloatingActionButton stop;
    private FloatingActionButton play;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);


        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory().getPath());

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logo = view.findViewById(R.id.logo);
        record = view.findViewById(R.id.record);
        stop = view.findViewById(R.id.stop);
        play = view.findViewById(R.id.play);

        record.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.recording)
    );
        });

        stop.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.stop)
    );
        });

        play.setOnClickListener(v -> {
            logo.setImageDrawable
                    (getResources().getDrawable(R.drawable.playing)
            );
        });


        String ruta = getContext().getExternalFilesDir(null).getAbsolutePath();
        var fileName = ruta + "/audiorecord.3gp";

        record.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.recording));
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(fileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e("RECORDING",
                            "No es pot iniciar la gravació");
                }

                mRecorder.start();
            }
        });

        play.setOnClickListener(v -> {
            logo.setImageDrawable
                    (getResources().getDrawable(R.drawable.playing));
            if (mRecorder == null && mPlayer == null) {
                mPlayer = new MediaPlayer();

                try {
                    mPlayer.setDataSource(fileName);
                    mPlayer.prepare();
                    mPlayer.start();

                    mPlayer.setOnCompletionListener(mediaPlayer -> {
                        stop.callOnClick();
                    });
                } catch (IOException e) {
                    Log.e("RECORDING", "No es pot iniciar la reproducció");
                }
            }
        });


        stop.setOnClickListener(v -> {
            logo.setImageDrawable(
                    getResources().getDrawable(R.drawable.stop));

            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            } else if (mPlayer != null) {
                mPlayer.stop();
                mPlayer = null;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(
                    getContext(),
                    "Permission needed",
                    Toast.LENGTH_LONG
            ).show();
            getActivity().finish();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        binding = null;
    }
}
