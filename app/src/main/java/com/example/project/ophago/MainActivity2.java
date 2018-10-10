package com.example.project.ophago;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utilities.ExceptionHandler;
import utilities.PrefUtil;
import utilities.RequestPermissionHandler;
import utilities.Utils;

/**
 * Created by Chris on 01/05/2018.
 */

public class MainActivity2 extends AppCompatActivity {

    @BindView(R.id.txtMainNama)TextView txtNama;
    @BindView(R.id.imgLogout)ImageView imgLogout;
    @BindView(R.id.imgMainPasienBaru)ImageView imgNewTrans;
    @BindView(R.id.imgMainHistoryPasien)ImageView imgHistory;

    private PrefUtil pref;
    private SharedPreferences shared;
    private String userName;
    private RequestPermissionHandler mRequestPermissionHandler;
    private List<String> listPermissionsNeeded;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRequestPermissionHandler = new RequestPermissionHandler();
            checkAndRequestPermissions();
            openPermission();
        }
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        pref = new PrefUtil(this);
        try{
            shared  = pref.getUserInfo();
            userName= shared.getString(PrefUtil.NAME, null);
        }catch (Exception e){e.getMessage();}
        txtNama.setText(userName);
    }

    private void openPermission(){
        if (!listPermissionsNeeded.isEmpty()) {
            mRequestPermissionHandler.requestPermission(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    123, new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity2.this, "Request permission success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(MainActivity2.this, "Request permission failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkAndRequestPermissions() {
        int writeExtStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExtStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int micPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (micPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
    }

    @OnClick(R.id.imgLogout)
    protected void logout(){
        pref.clear();
        startActivity(new Intent(MainActivity2.this, Login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.imgMainPasienBaru)
    protected void baru(){
        Intent a = new Intent(MainActivity2.this, DataPasien2.class);
        a.putExtra("Status",1);
        startActivity(a);
    }

    @OnClick(R.id.imgMainHistoryPasien)
    protected void history(){
        Intent i = new Intent(MainActivity2.this, LaporanPemeriksaan.class);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Apakah anda yakin keluar dari aplikasi ini?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }
}