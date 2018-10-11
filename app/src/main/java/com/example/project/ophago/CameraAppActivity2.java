package com.example.project.ophago;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.transaksi.TransaksiItemModel;
import model.transaksi.TransaksiModel;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class CameraAppActivity2 extends ActionBarActivity {

    private static final int VIDEO_CAPTURE = 101;
    private static final int SCREENSHOOT = 105;
    private static final int MANUAL_RECORDING = 110;
    private String sdbname;
    private Uri hasilUri, fileUri;
    private TransaksiModel model;
    private String video, bagian;
    private int sukses=0;
    private int line;
    private String status;

    @BindView(R.id.imgRecordCam)ImageView imgRecord;
    @BindView(R.id.imgLanjutCam)ImageView imgProses;
    @BindView(R.id.imgplayvideo2)ImageView imgView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity2);
        ButterKnife.bind(this);
        Intent i = getIntent();
        model = (TransaksiModel) i.getSerializableExtra("object");
        line = i.getIntExtra("line",0);
        video = i.getStringExtra("video");
        status = i.getStringExtra("status");
        for(TransaksiItemModel item:model.getItemList() ){
            if(item.getLine()==line){
                bagian = item.getAnatomi();
                if(item.getPathVideo()==null){
                    hasilUri=null;
                }else{
                    hasilUri=Uri.parse(video);
                }
            }
        }
        if(status.equals("Y")){
            startRecording();
        }
    }

    @OnClick(R.id.imgRecordCam)
    protected void record(){
        startRecording();
    }

    @OnClick(R.id.imgLanjutCam)
    protected void proses(){
        lanjutkan();
    }

    @OnClick(R.id.imgplayvideo2)
    protected void playvideo(){
        screenshoot();
    }

    private void lanjutkan(){
        if(sukses>0 || hasilUri!=null){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("object", model);
            setResult(RESULT_OK, returnIntent);
            finish();
        }else{
            Toast.makeText (CameraAppActivity2.this, "Video tidak ada", Toast.LENGTH_LONG).show ();
        }
    }
    public void startRecording(){
        sdbname = (Utils.getDateTimeNameFile()+"-"+bagian+".mp4");
        Utils.writeVideoToSDFile(sdbname);
        File mediaFile = new File( Environment.getExternalStorageDirectory() +
                "/" + "OphaGo/Video"+ "/" +sdbname);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()+
                ".com.example.project.ophago.provider", mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);

        /*Intent i = new Intent(getApplicationContext(), CustomCamera3.class);
        i.putExtra("bagian", bagian);
        startActivityForResult(i, MANUAL_RECORDING);*/
    }

    private void screenshoot(){
        if(sukses>0 || hasilUri!=null){
            Intent i = new Intent(getApplicationContext(), ViewVideoActivity.class);
            i.putExtra("line", line);
            i.putExtra("fileuri", hasilUri.toString());
            i.putExtra("object", model);
            i.putExtra("namaFile", sdbname);
            startActivityForResult(i, SCREENSHOOT);
        }else{
            Toast.makeText (CameraAppActivity2.this, "Video tidak ada", Toast.LENGTH_LONG).show ();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                hasilUri = uri;
                Toast.makeText(this, "Video has been saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                sukses=1;
                for(TransaksiItemModel itemModel:model.getItemList()){
                    if(itemModel.getLine().intValue()==line){
                        itemModel.setPathVideo(uri.toString());
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                sukses=0;
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                sukses=0;
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
            //TODO ke screenshoot
            screenshoot();
        }else if(requestCode == SCREENSHOOT){
            if(resultCode == RESULT_OK){
                model = (TransaksiModel) data.getSerializableExtra("object");
            }
            //TODO stlh dari screenshoot, keluar ke checkoutList tadi
            lanjutkan();
        }else if(requestCode == MANUAL_RECORDING){
            if(resultCode == RESULT_OK){
                String path = (String) data.getStringExtra("pathvideo");
                for(TransaksiItemModel itemModel:model.getItemList()){
                    if(itemModel.getLine().intValue()==line){
                        itemModel.setPathVideo(path);
                    }
                }
                hasilUri=Uri.parse(path);
                Toast.makeText(this, "Video has been saved to:\n" +
                        path, Toast.LENGTH_LONG).show();
                sukses=1;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Batalkan Rekam Video?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent returnIntent = new Intent();
                            setResult(RESULT_CANCELED, returnIntent);
                            finish();
                        }
                    }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(sukses>0){
            Intent i = new Intent(CameraAppActivity.this, KesimpulanActivity.class);
            i.putExtra("object",model);
            startActivity(i);
        }*/
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
