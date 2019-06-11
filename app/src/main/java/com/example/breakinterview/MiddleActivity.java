package com.example.breakinterview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MiddleActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSIONS = 1001;
    private int mScreenDensity;
    Button btn_action;
    private MediaProjectionManager mediaProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;


    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;

    private RelativeLayout rootLayout;


    private MediaRecorder mediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSION_KEY = 1;
    boolean isRecording = false;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private PaintView paintView;
    private ToggleButton toggleButton;
    private VideoView videoView;
    private String videoUri="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.middleactivity);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

//        String[] PERMISSIONS = {
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//        };
////        if (!Function.hasPermissions(this, PERMISSIONS)) {
////            ContextCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
////            ContextCompat.req
////        }
//
//
        DisplayMetrics metrics1 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics1);
        mScreenDensity = metrics1.densityDpi;
//
        mediaRecorder = new MediaRecorder();
//
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        videoView=findViewById(R.id.videoView);
        rootLayout=findViewById(R.id.rootLayout);
//
//
        toggleButton = (ToggleButton) findViewById(R.id.btn_action);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MiddleActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                        ContextCompat.checkSelfPermission(MiddleActivity.this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MiddleActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                            ActivityCompat.shouldShowRequestPermissionRationale(MiddleActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    {
                        toggleButton.setChecked(false);
//                        Toast.makeText(MiddleActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        Snackbar.make(rootLayout,"Permissions",Snackbar.LENGTH_INDEFINITE).
                                setAction("Enable", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        ActivityCompat.requestPermissions(MiddleActivity.this,
                                                new String[]{

                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.RECORD_AUDIO

                                                },REQUEST_PERMISSIONS);
                                    }
                                }).show();
                    }

                    else
                    {
                        ActivityCompat.requestPermissions(MiddleActivity.this,
                                new String[]{

                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.RECORD_AUDIO

                                },REQUEST_PERMISSIONS);

                    }
                }

                else
                {

                    toggleScreenShare(v);
                }
            }
        });





    }

    private void toggleScreenShare(View v) {

        if(((ToggleButton)v).isChecked())
        {
            initRecorder();
            recordScreen();
        }
        else
        {
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopScreenSharing();

            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(videoUri));
            videoView.start();
        }
    }

    private void initRecorder() {

        try
        {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //THREE_GPP
            videoUri=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
            new StringBuilder("/").append(new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss")
            .format(new Date())).append(".mp4").toString();

            mediaRecorder.setOutputFile(videoUri);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mediaRecorder.setVideoFrameRate(30); // 30
//            mediaRecorder.setVideoEncodingBitRate(3000000);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation+90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();
        }
        catch (Exception e){

        }
    }

    private void recordScreen() {

        if(mediaProjection==null)
        {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),REQUEST_CODE);
            return;
        }
        virtualDisplay=createVirtualDisplay();
        mediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
//            isRecording = false;
//            actionBtnReload();
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mediaProjection.registerCallback(mMediaProjectionCallback, null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
//        isRecording = true;
//        actionBtnReload();
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {

        @Override
        public void onStop() {
            if (toggleButton.isChecked()) {

                toggleButton.setChecked(false);
                 mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopScreenSharing();

        super.onStop();
        }
    }


    private void stopScreenSharing() {
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
//        isRecording = false;
       }

    private void destroyMediaProjection() {
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(mMediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS:
            {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    toggleScreenShare(toggleButton);
                }
                else {
                    toggleButton.setChecked(false);

                    Snackbar.make(rootLayout,"Permissions",Snackbar.LENGTH_INDEFINITE).
                            setAction("Enable", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(MiddleActivity.this,
                                            new String[]{

                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO

                                            },REQUEST_PERMISSIONS);
                                }
                            }).show();

                }
                return;
            }
        }
    }


//    public void actionBtnReload() {
//        if (isRecording) {
//            btn_action.setText("Stop Recording");
//        } else {
//            btn_action.setText("Start Recording");
//        }
//
//    }
//
//
//    public void onToggleScreenShare() {
//        if (!isRecording) {
//            initRecorder();
//            shareScreen();
//        } else {
//            mMediaRecorder.stop();
//            mMediaRecorder.reset();
//            stopScreenSharing();
//        }
//    }
//
//    private void shareScreen() {
//        if (mMediaProjection == null) {
//            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
//            return;
//        }
//        mVirtualDisplay = createVirtualDisplay();
//        mMediaRecorder.start();
//        isRecording = true;
//        actionBtnReload();
//    }
//
//    private VirtualDisplay createVirtualDisplay() {
//        return mMediaProjection.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
//    }
//
//    private void initRecorder() {
//        try {
//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //THREE_GPP
//            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/video.mp4");
//            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
//            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
//            mMediaRecorder.setVideoFrameRate(16); // 30
//            mMediaRecorder.setVideoEncodingBitRate(3000000);
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            int orientation = ORIENTATIONS.get(rotation + 90);
//            mMediaRecorder.setOrientationHint(orientation);
//            mMediaRecorder.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private void stopScreenSharing() {
//        if (mVirtualDisplay == null) {
//            return;
//        }
//        mVirtualDisplay.release();
//        destroyMediaProjection();
//        isRecording = false;
//        actionBtnReload();
//    }
//
//
//
//    private void destroyMediaProjection() {
//        if (mMediaProjection != null) {
//            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
//            mMediaProjection.stop();
//            mMediaProjection = null;
//        }
//        Log.i(TAG, "MediaProjection Stopped");
//    }
//
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != REQUEST_CODE) {
//            Log.e(TAG, "Unknown request code: " + requestCode);
//            return;
//        }
//        if (resultCode != RESULT_OK) {
//            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
//            isRecording = false;
//            actionBtnReload();
//            return;
//        }
//        mMediaProjectionCallback = new MediaProjectionCallback();
//        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
//        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
//        mVirtualDisplay = createVirtualDisplay();
//        mMediaRecorder.start();
//        isRecording = true;
//        actionBtnReload();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_KEY:
//            {
//                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
//                    onToggleScreenShare();
//                } else {
//                    isRecording = false;
//                    actionBtnReload();
//
//                    Snackbar.make(findViewById(android.R.id.content), "Please enable Microphone and Storage permissions.",
//                            Snackbar.SnackbarDuration.LENGTH_LONG).setAction("ENABLE",
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                                    intent.setData(Uri.parse("package:" + getPackageName()));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                    startActivity(intent);
//                                }
//                            }).show();
//                }
//                return;
//            }
//        }
//    }
//
//
//
//
//
//    private class MediaProjectionCallback extends MediaProjection.Callback {
//        @Override
//        public void onStop() {
//            if (isRecording) {
//                isRecording = false;
//                actionBtnReload();
//                mMediaRecorder.stop();
//                mMediaRecorder.reset();
//            }
//            mMediaProjection = null;
//            stopScreenSharing();
//        }
//    }
//
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        destroyMediaProjection();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (isRecording) {
//            Snackbar.make(findViewById(android.R.id.content), "Wanna Stop recording and exit?",
//                    Snackbar.SnackbarDuration.LENGTH_LONG).setAction("Stop",
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mMediaRecorder.stop();
//                            mMediaRecorder.reset();
//                            Log.v(TAG, "Stopping Recording");
//                            stopScreenSharing();
//                            finish();
//                        }
//                    }).show();
//        } else {
//            finish();
//        }
//    }



}


