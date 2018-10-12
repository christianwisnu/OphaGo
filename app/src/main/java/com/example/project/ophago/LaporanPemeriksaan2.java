package com.example.project.ophago;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adapter.AdpTransaksi2;
import model.list.ListTransaksi;
import model.transaksi.LaporanDetail;
import model.transaksi.LaporanHeaderModel;
import model.transaksi.LaporanModel;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 05/05/18.
 */

public class LaporanPemeriksaan2 extends AppCompatActivity {

    private AdpTransaksi2 adapter;
    private ListView lsvupload;
    private LaporanModel model2;
    private ArrayList<LaporanDetail> columnlist= new ArrayList<LaporanDetail>();
    private TextView txtId, txtNama, txtAlamat, txtTelp, txtUmur, txtStatus;
    private Button btnEditProfile;
    private String getData	="laporanDetail.php";
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar dateAndTime = Calendar.getInstance();
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private ListTransaksi item;
    private int RESULT_EDIT_PASIEN = 10;
    private int RESULT_EDIT_KESIMPULAN = 11;
    private String kode, nama, alamat, tgl, umur, gender, telp, hasilTglFrom, hasilTglTo, hasilCheckPeriode, userId, username;
    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private boolean editTrans=false;
    private static final ImageLoader imgloader = ImageLoader.getInstance();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat2);
        Intent i = getIntent();
        item = (ListTransaksi) i.getSerializableExtra("object");
        model2 = (LaporanModel) i.getSerializableExtra("model");
        hasilTglFrom = (String) i.getStringExtra("tglFrom");
        hasilTglTo = (String) i.getStringExtra("tglTo");
        hasilCheckPeriode = (String) i.getStringExtra("allPeriode");

        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
            username = shared.getString(PrefUtil.NAME, null);
        }catch (Exception e){e.getMessage();}

        lsvupload	= (ListView)findViewById(R.id.LsvTransaksiNext);
        txtId = (TextView)findViewById(R.id.txtIdpasienLaporan);
        txtNama = (TextView)findViewById(R.id.txtNamapasienLaporan);
        txtAlamat = (TextView)findViewById(R.id.txtAlamatpasienLaporan);
        txtTelp = (TextView)findViewById(R.id.txtTelppasienLaporan);
        txtUmur = (TextView)findViewById(R.id.txtUmurpasienLaporan);
        txtStatus = (TextView)findViewById(R.id.TvStatusDataListTransaksi);
        //btnNewTrans = (Button) findViewById(R.id.btnPemeriksaanBaru);
        btnEditProfile = (Button) findViewById(R.id.btnEditDataPasien);
        //btnCetakAll = (Button) findViewById(R.id.btnCetakSemuaPdf);

        txtId.setText(item.getKodePasien());
        txtNama.setText(item.getNamaPasien());
        txtAlamat.setText(item.getAlamat());
        txtTelp.setText(item.getTelp());
        txtUmur.setText(String.valueOf(item.getUmur())+" Tahun");

        adapter		= new AdpTransaksi2(LaporanPemeriksaan2.this, R.layout.col_transaksi2, model2.getHeaderList(), userId);
        lsvupload.setAdapter(adapter);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent i = new Intent(LaporanPemeriksaan2.this, DataPasien2.class);
                    i.putExtra("Status",10);
                    i.putExtra("kode",item.getKodePasien());
                    i.putExtra("nama", item.getNamaPasien());
                    i.putExtra("alamat", item.getAlamat());
                    i.putExtra("gender", item.getGender());
                    i.putExtra("telp", item.getTelp());
                    Date date1=new SimpleDateFormat("dd-MM-yyy").parse(item.getBirthday());
                    i.putExtra("tgl", df2.format(date1));
                    startActivityForResult(i, RESULT_EDIT_PASIEN);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }catch (Exception e){}
            }
        });
    }

    public static int calculateAge(Date birthday) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        cal.setTime(birthday);
        int yearBirthday = cal.get(Calendar.YEAR);
        int age = yearNow-yearBirthday;
        return age;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(editTrans==true){
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }else{
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_EDIT_PASIEN) {
            if(resultCode == RESULT_OK) {
                kode = data.getStringExtra("kode");
                nama = data.getStringExtra("nama");
                alamat = data.getStringExtra("alamat");
                gender = data.getStringExtra("gender");
                telp = data.getStringExtra("telp");
                tgl = data.getStringExtra("tgl");
                try{
                    item.setKodePasien(kode);
                    txtId.setText(kode);
                    item.setNamaPasien(nama);
                    txtNama.setText(nama);
                    item.setAlamat(alamat);
                    txtAlamat.setText(alamat);
                    item.setTelp(telp);
                    txtTelp.setText(telp);
                    Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
                    item.setBirthday(sdf1.format(date1.getTime()));
                    int age = calculateAge(date1);
                    item.setUmur(age);
                    txtUmur.setText(String.valueOf(age));
                }catch (Exception ex){
                }
            }
        }
        if(requestCode == RESULT_EDIT_KESIMPULAN) {
            if(resultCode == RESULT_OK) {
                String saran = data.getStringExtra("saran");
                String diagnosa = data.getStringExtra("diagnosa");
                String kesimpulan = data.getStringExtra("kesimpulan");
                String noTrans = data.getStringExtra("noTrans");
                for(LaporanHeaderModel header:model2.getHeaderList()){
                    if(header.getNoTrans().equals(noTrans)){
                        header.setSaran(saran);
                        header.setDiagnosa(diagnosa);
                        header.setKesimpulan(kesimpulan);
                    }
                }
                editTrans=true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}