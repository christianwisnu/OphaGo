package com.example.project.ophago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.transaksi.TransaksiHeaderModel;
import model.transaksi.TransaksiModel;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class AddDataKeluhan2 extends AppCompatActivity {

    private TransaksiHeaderModel header;
    private TransaksiModel model;

    @BindView(R.id.edKeluhanPasien2)EditText edKeluhan;
    @BindView(R.id.btn_pasien_keluhan_lanjut2)Button btnLanjut;
    @BindView(R.id.imgKeluhanBack)ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pasien_keluhan2);
        ButterKnife.bind(this);
        Intent i = getIntent();
        model = (TransaksiModel) i.getSerializableExtra("object");
        header=model.getHeader();
    }

    @OnClick(R.id.btn_pasien_keluhan_lanjut2)
    protected void lanjut(){
        if(validateKeluhan(edKeluhan)){
            header.setKeluhan(edKeluhan.getText().toString());
            Intent i = new Intent(getApplicationContext(), ImageAreasActivity2.class);
            i.putExtra("object",model);
            startActivity(i);
        }
    }

    @OnClick(R.id.imgKeluhanBack)
    protected void back(){
        finish();
    }

    private boolean validateKeluhan(EditText edittext) {
        boolean value;
        if (edKeluhan.getText().toString().isEmpty()){
            value=false;
            requestFocus(edKeluhan);
            Toast.makeText(AddDataKeluhan2.this,"Keluhan Pasien harap diisi!", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }else if(requestCode == RESULT_CANCEL) {
            Intent intent = new Intent();
            setResult(RESULT_CANCEL, intent);
            finish();
        }*/
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
