package com.example.project.ophago;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import model.transaksi.TransaksiHeaderModel;
import model.transaksi.TransaksiModel;
import utilities.Utils;

/**
 * Created by christian on 16/04/18.
 */

public class ViewVideoActivity extends AppCompatActivity {//implements GestureDetection.SimpleGestureListener {

    private VideoView mVideoView;
    private Button btnSs;
    private int line;
    private String sdbname, video, namaFileVideo, bagian;
    private Uri fileUri;
    private Bitmap mbitmap;
    private int count=0, position=0;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private TransaksiModel model;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_video);

        Intent i = getIntent();
        video = i.getStringExtra("fileuri");
        line = i.getIntExtra("line",0);
        model = (TransaksiModel) i.getSerializableExtra("object");
        namaFileVideo = i.getStringExtra("namaFile");
        bagian = i.getStringExtra("bagian");

        mVideoView = (VideoView)findViewById(R.id.surface_view);
        //audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //detector = new GestureDetection(this, this);
        btnSs = (Button)findViewById(R.id.btnScreenshoot);
        count=0;
        File fileVideo = new File( Environment.getExternalStorageDirectory() +
                "/" + "OphaGo/Video"+ "/" +namaFileVideo);
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                ".com.example.project.ophago.provider", fileVideo);
        mVideoView.setVideoURI(fileUri);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this, fileUri);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);

        mVideoView.setMediaController(mediaController);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaController.show(5000);
                mVideoView.start();
                /*mVideoView.seekTo(position);
                if (position == 0){
                    mVideoView.start();
                } else{
                    // if we come from a resumed activity, video playback will
                    // be paused
                    mVideoView.pause();
                }*/
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(ViewVideoActivity.this,"Terjadi kesalahan dalam memutar video", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    //https://stackoverflow.com/questions/36521242/how-can-cast-local-data-to-chromecast-device
    //https://stackoverflow.com/questions/32229127/creating-playlist-from-sd-card-and-sending-it-to-chromecast
    //https://stackoverflow.com/questions/27600316/android-chromecast-video-cast-from-sd-card-or-internal-storage
    //https://stackoverflow.com/questions/14309256/using-nanohttpd-in-android

    public void proses(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("object", model);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void screenShot(View view) {
        TransaksiHeaderModel header = model.getHeader();
        count=0;
        if(header.getPathGbr1()!=null ){
            count++;
        }
        if(header.getPathGbr2()!=null ){
            count++;
        }
        if(header.getPathGbr3()!=null ){
            count++;
        }
        if(header.getPathGbr4()!=null ){
            count++;
        }
        if(header.getPathGbr5()!=null ){
            count++;
        }
        if(header.getPathGbr6()!=null ){
            count++;
        }
        if(count<6){
            int currentPosition = mVideoView.getCurrentPosition();
            mbitmap = getBitmapOFRootView(btnSs, currentPosition);
            createImage(mbitmap);
        }else{
            Toast.makeText (ViewVideoActivity.this, "Max screenshoot 6X", Toast.LENGTH_LONG).show ();
        }
    }

    public Bitmap getBitmapOFRootView(View v, int currentPosition) {
        View rootview = v.getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bmFrame = mediaMetadataRetriever
                .getFrameAtTime(currentPosition * 1000);
        return bmFrame;
    }

    public void createImage(Bitmap bmp) {
        File sd = Environment.getExternalStorageDirectory();
        sdbname = (Utils.getDateTimeNameFile()+"-"+bagian+".jpg");
        Utils.writeToImgSDFile( sdbname);
        String backupDBPath = "OphaGo/Image/"+sdbname;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        final File file = new File(sd,  backupDBPath);
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes.toByteArray());
            outputStream.close();

            AlertDialog.Builder myCaptureDialog =
                    new AlertDialog.Builder(ViewVideoActivity.this);
            ImageView capturedImageView = new ImageView(ViewVideoActivity.this);
            capturedImageView.setImageBitmap(bmp);
            LayoutParams capturedImageViewLayoutParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
            capturedImageView.setLayoutParams(capturedImageViewLayoutParams);

            myCaptureDialog.setTitle("Screenshoot");
            myCaptureDialog.setIcon(R.drawable.ic_menu_gallery);
            myCaptureDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TransaksiHeaderModel header = model.getHeader();
                    if(header.getPathGbr1()==null ){
                        header.setPathGbr1("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }else if(header.getPathGbr2()==null ){
                        header.setPathGbr2("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }else if(header.getPathGbr3()==null ){
                        header.setPathGbr3("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }else if(header.getPathGbr4()==null ){
                        header.setPathGbr4("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }else if(header.getPathGbr5()==null ){
                        header.setPathGbr5("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }else if(header.getPathGbr6()==null ){
                        header.setPathGbr6("file://"+file.getParentFile().getPath()+"/"+sdbname);
                    }
                    /*for (TransaksiItemModel item : model.getItemList()){
                        if(item.getLine()==line){
                            if(count==1){
                                item.setPathGbr1("file://"+file.getParentFile().getPath()+"/"+sdbname);
                            }else if(count==2){
                                item.setPathGbr2("file://"+file.getParentFile().getPath()+"/"+sdbname);
                            }else if(count==3){
                                item.setPathGbr3("file://"+file.getParentFile().getPath()+"/"+sdbname);
                            }else if(count==4){
                                item.setPathGbr4("file://"+file.getParentFile().getPath()+"/"+sdbname);
                            }else if(count==5){
                                item.setPathGbr5("file://"+file.getParentFile().getPath()+"/"+sdbname);
                            }
                        }
                    }*/
                }
            });
            myCaptureDialog.setView(capturedImageView);
            myCaptureDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case GestureDetection.SWIPE_LEFT:
                mVideoView.pause();
                //currentPosition = mVideoView.getCurrentPosition();
                currentPosition = mVideoView.getCurrentPosition() - 10000;
                mVideoView.seekTo(currentPosition);
                mVideoView.start();
                str = "Swipe Left";
                break;
            case GestureDetection.SWIPE_RIGHT:
                mVideoView.pause();
                //currentPosition = mVideoView.getCurrentPosition();
                currentPosition = mVideoView.getCurrentPosition() + 10000;
                mVideoView.seekTo(currentPosition);
                mVideoView.start();
                str = "Swipe Right";
                break;
            case GestureDetection.SWIPE_DOWN:
                currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume - 1, 0);
                str = "Swipe Down";
                break;
            case GestureDetection.SWIPE_UP:
                currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume + 1, 0);
                str = "Swipe Up";
                break;
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }*/
}