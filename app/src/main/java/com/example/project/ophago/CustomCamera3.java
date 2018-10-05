package com.example.project.ophago;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import utilities.Utils;

import static com.otaliastudios.cameraview.CameraView.PERMISSION_REQUEST_CODE;

/**
 * Created by Chris on 21/05/2018.
 */

public class CustomCamera3 extends AppCompatActivity {

    private Camera myCamera;
    private SurfaceView mySurfaceView;
    private SurfaceHolder mySurfaceHolder;
    private boolean isPreview, isCameraFlashOn;
    private String bagian, videoDuration;
    private Button btnFlash, btnZin, btnZout;
    private TextView txtTimer, txtAnatomi;
    private int zoom=6;
    private CountDownTimer countDownTimer;
    private long startVideoRecordingTime=10*1000*60*10;
    private boolean isTime=false, recording=false;
    private long timeElapsed;
    private MediaRecorder mMediaRecorder;
    private File file;
    private ImageView imgStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        Intent i = getIntent();
        bagian = (String) i.getStringExtra("bagian");
        isPreview = false;
        mySurfaceView = (SurfaceView)findViewById(R.id.surface_camera);
        btnFlash = (Button) findViewById(R.id.flash_button_video);
        btnZin = (Button) findViewById(R.id.camera_zoom_in);
        imgStart = (ImageView) findViewById(R.id.ImgRecordVideo);
        btnZout = (Button) findViewById(R.id.camera_zoom_out);
        txtTimer = (TextView) findViewById(R.id.timer_textview_video);
        txtAnatomi = (TextView) findViewById(R.id.txtBagianAnatomiRecord);
        txtAnatomi.setText("PEMERIKSAAN : "+bagian);
        myCamera = Camera.open();
        btnFlash.setVisibility(View.VISIBLE);
        btnZin.setVisibility(View.VISIBLE);
        btnZout.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= 23) {
            isStoragePermissionGranted();
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
            }
        }

        mySurfaceHolder = mySurfaceView.getHolder();
        mySurfaceHolder.addCallback(mySurfaceCallback);
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (recording) {
                        btnFlash.setVisibility(View.VISIBLE);
                        btnZin.setVisibility(View.VISIBLE);
                        btnZout.setVisibility(View.VISIBLE);
                        stopRecordingTimer();
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                        recording = false;
                        imgStart.setImageResource(R.drawable.record);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("pathvideo", file.getAbsolutePath());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        btnFlash.setVisibility(View.INVISIBLE);
                        btnZin.setVisibility(View.INVISIBLE);
                        btnZout.setVisibility(View.INVISIBLE);
                        recording = true;
                        startVideoRecordingTimer();
                        startRecording();
                        imgStart.setImageResource(R.drawable.stop);
                    }
                }catch (Exception e) {
                    String message = e.getMessage();
                    Log.i(null, "Problem Start"+message);
                    mMediaRecorder.release();
                }
            }
        });

        btnZin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomCamera(true);
            }
        });

        btnZout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomCamera(false);
            }
        });

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashAvailable(CustomCamera3.this)){
                    Camera.Parameters p = myCamera.getParameters();
                    if(isCameraFlashOn){
                        isCameraFlashOn=false;
                        btnFlash.setBackgroundResource(R.mipmap.ic_flash_off_black_36dp);
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        myCamera.setParameters(p);
                        myCamera.startPreview();
                    }else{
                        isCameraFlashOn=true;
                        btnFlash.setBackgroundResource(R.mipmap.ic_flash_on_black_36dp);
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        myCamera.setParameters(p);
                        myCamera.startPreview();
                    }
                }else{
                    Log.d("flash not available", "flash not here");
                }
            }
        });
    }

    protected void startRecording() throws IOException{
        mMediaRecorder = new MediaRecorder();  // Works well
        myCamera.unlock();
        mMediaRecorder.setCamera(myCamera);

        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setPreviewDisplay(mySurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        mMediaRecorder.setPreviewDisplay(mySurfaceHolder.getSurface());
        mMediaRecorder.setOutputFile(initFile().getAbsolutePath());

        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    /*private void prepareRecorder() {
        mMediaRecorder.setPreviewDisplay(mySurfaceHolder.getSurface());
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }*/

    private File initFile() {
        File sd = Environment.getExternalStorageDirectory();
        String sdbname = (Utils.getDateTimeNameFile()+".mp4");
        Utils.writeVideoToSDFile(sdbname);
        String backupDBPath = "OphaGo/Video/"+sdbname;
        File mediaFile = new File(sd, backupDBPath);
        file = mediaFile;
        return file;
    }

    public void startVideoRecordingTimer(){
        setupCountDown();
        countDownTimer.start();
    }

    private void stopRecordingTimer(){
        countDownTimer.cancel();
    }

    private void setupCountDown() {
        countDownTimer = new CountDownTimer(startVideoRecordingTime, 1000) {
            public void onTick(long millisUntilFinished) {
                if(!isTime){
                    timeElapsed = startVideoRecordingTime - millisUntilFinished;
                    videoDuration=String.format("%d : %d ",
                            TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
                            TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                    showRecordingTime(timeElapsed);
                }else{
                    timeElapsed = startVideoRecordingTime - millisUntilFinished;
                    videoDuration=String.format("%d : %d ",
                            TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
                            TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                    showRecordingTime(millisUntilFinished);
                }
            }
            public void onFinish() {
                Log.d("finish", "yes");
                stopRecordingTimer();
            }
        };
    }

    @SuppressLint("InlinedApi")
    public void showRecordingTime(long time){
        Log.d("startVideoRecordingTime", startVideoRecordingTime+"");
        txtTimer.setText(""+String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));
    }

    public void zoomCamera(boolean zoomInOrOut) {
        if(myCamera!=null) {
            Camera.Parameters parameter = myCamera.getParameters();
            if(parameter.isZoomSupported()) {
                int MAX_ZOOM = parameter.getMaxZoom();
                int currnetZoom = parameter.getZoom();
                if(zoomInOrOut && (currnetZoom <MAX_ZOOM && currnetZoom >=0)) {
                    parameter.setZoom(++currnetZoom);
                }
                else if(!zoomInOrOut && (currnetZoom <=MAX_ZOOM && currnetZoom >0)) {
                    parameter.setZoom(--currnetZoom);
                }
            }
            else
                Toast.makeText(CustomCamera3.this, "Zoom Not Avaliable", Toast.LENGTH_LONG).show();
            myCamera.setParameters(parameter);
            myCamera.startPreview();
        }
    }

    SurfaceHolder.Callback mySurfaceCallback = new SurfaceHolder.Callback(){

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters myParameters = myCamera.getParameters();
            Camera.Size myBestSize = getBestPreviewSize(width, height, myParameters);
            if(myBestSize != null){
                myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
                myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                myParameters.setZoom(zoom);
                myCamera.setDisplayOrientation(90);
                myCamera.setParameters(myParameters);
                myCamera.startPreview();
                isPreview = true;
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                myCamera.setPreviewDisplay(mySurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            /*if (recording) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                recording = false;
            }*/
        }
    };

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters){
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        return bestSize;
    }

    public static boolean isFlashAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //myCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        /*if(isPreview){
            myCamera.stopPreview();
        }
        myCamera.release();
        myCamera = null;
        isPreview = false;*/
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
//https://stackoverflow.com/questions/1817742/how-can-i-record-a-video-in-my-android-app