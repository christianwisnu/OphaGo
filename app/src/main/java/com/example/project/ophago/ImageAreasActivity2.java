package com.example.project.ophago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.transaksi.TransaksiItemModel;
import model.transaksi.TransaksiModel;
import utilities.Link;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class ImageAreasActivity2 extends AppCompatActivity {

    @BindView(R.id.ckMataKanan)CheckBox ckMtKn;
    @BindView(R.id.ckMataKiri)CheckBox ckMtKr;
    @BindView(R.id.btn_pasien_anatomi_lanjut2)Button imgNext;
    @BindView(R.id.imgAnatomiBack)ImageView imgBack;

    private boolean tlKn=false, tlKr=false, hdKn=false, hdKr=false, tgr=false, mlt=false;
    private TransaksiModel model;
    private int line=0, noUrut=0;
    private Map<Integer,String> list = new HashMap<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noUrut=0;
        setContentView(R.layout.anatomi2);
        ButterKnife.bind(this);
        Intent i = getIntent();
        model = (TransaksiModel) i.getSerializableExtra("object");
        refreshAll();
    }

    @OnClick(R.id.btn_pasien_anatomi_lanjut2)
    protected void lanjut(){
        if(list.isEmpty()){
            Toast.makeText (ImageAreasActivity2.this, "Pilih salah satu dari bagian pemeriksaan", Toast.LENGTH_SHORT).show ();
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                TransaksiItemModel item = new TransaksiItemModel();
                item.setAnatomi(entry.getValue());
                item.setLine(entry.getKey());
                item.setStat("A");
                //line++;
                model.addItem(item);
            }
            Collections.sort(model.getItemList(), new Urut());
            Intent i = new Intent(getApplicationContext(), CheckoutListAnatomi2.class);
            i.putExtra("object",model);
            startActivityForResult(i,10);
        }
    }

    @OnClick(R.id.imgAnatomiBack)
    protected void back(){
        finish();
    }

    static class Urut implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Integer a =((TransaksiItemModel)o1).getLine();
            Integer b = ((TransaksiItemModel)o2).getLine();
            return a.compareTo(b); //desc. Kalo asc     a.compareTo(b)
        }
    }

    @OnClick(R.id.ckMataKanan)
    protected void tlkn(){
        tlKn=!tlKn;
        ckMtKn.setChecked(tlKn);
        if(ckMtKn.isChecked()){
            noUrut++;
            list.put(noUrut, Link.MATA_KN);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.MATA_KN)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckMataKiri)
    protected void tlkr(){
        tlKr=!tlKr;
        ckMtKr.setChecked(tlKr);
        if(ckMtKr.isChecked()){
            noUrut++;
            list.put(noUrut, Link.MATA_KR);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.MATA_KR)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10) {
            if(resultCode == RESULT_CANCELED) {
                refreshAll();
                list.clear();
                model = (TransaksiModel) data.getSerializableExtra("object");
                noUrut=0;
            }
        }
    }

    private void refreshAll(){
        ckMtKr.setChecked(false);
        ckMtKn.setChecked(false);
        tlKn=false;
        tlKr=false;
        hdKn=false;
        hdKr=false;
        tgr=false;
        mlt=false;
        line=0;
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
