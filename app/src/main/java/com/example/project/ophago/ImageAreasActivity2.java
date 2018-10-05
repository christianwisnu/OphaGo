package com.example.project.ophago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @BindView(R.id.ckTelKn2)CheckBox ckTlKn;
    @BindView(R.id.ckTelKr2)CheckBox ckTlKr;
    @BindView(R.id.ckHidKn2)CheckBox ckHdKn;
    @BindView(R.id.ckHidKr2)CheckBox ckHdKr;
    @BindView(R.id.ckTenggorokan2)CheckBox ckTgr;
    @BindView(R.id.ckRonggaMulut2)CheckBox ckMlt;
    @BindView(R.id.img_anatomi_lanjutkan)ImageView imgNext;

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

    @OnClick(R.id.img_anatomi_lanjutkan)
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

    static class Urut implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Integer a =((TransaksiItemModel)o1).getLine();
            Integer b = ((TransaksiItemModel)o2).getLine();
            return a.compareTo(b); //desc. Kalo asc     a.compareTo(b)
        }
    }

    @OnClick(R.id.ckTelKn2)
    protected void tlkn(){
        tlKn=!tlKn;
        ckTlKn.setChecked(tlKn);
        if(ckTlKn.isChecked()){
            noUrut++;
            list.put(noUrut, Link.TEL_KN);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.TEL_KN)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckTelKr2)
    protected void tlkr(){
        tlKr=!tlKr;
        ckTlKr.setChecked(tlKr);
        if(ckTlKr.isChecked()){
            noUrut++;
            list.put(noUrut, Link.TEL_KR);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.TEL_KR)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckHidKn2)
    protected void hdkn(){
        hdKn=!hdKn;
        ckHdKn.setChecked(hdKn);
        if(ckHdKn.isChecked()){
            noUrut++;
            list.put(noUrut, Link.HDG_KN);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.HDG_KN)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckHidKr2)
    protected void hdkr(){
        hdKr=!hdKr;
        ckHdKr.setChecked(hdKr);
        if(ckHdKr.isChecked()){
            noUrut++;
            list.put(noUrut, Link.HDG_KR);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.HDG_KR)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckTenggorokan2)
    protected void tgrk(){
        tgr=!tgr;
        ckTgr.setChecked(tgr);
        if(ckTgr.isChecked()){
            noUrut++;
            list.put(noUrut, Link.TGR);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.TGR)){
                    list.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @OnClick(R.id.ckRonggaMulut2)
    protected void mulut(){
        mlt=!mlt;
        ckMlt.setChecked(mlt);
        if(ckMlt.isChecked()){
            noUrut++;
            list.put(noUrut, Link.MLT);
        }else{
            for (Map.Entry<Integer, String> entry : list.entrySet()) {
                if(entry.getValue().equals(Link.MLT)){
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
        ckHdKn.setChecked(false);
        ckHdKr.setChecked(false);
        ckTlKn.setChecked(false);
        ckTlKr.setChecked(false);
        ckTgr.setChecked(false);
        ckMlt.setChecked(false);
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
