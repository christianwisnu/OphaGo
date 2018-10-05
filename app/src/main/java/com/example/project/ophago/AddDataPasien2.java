package com.example.project.ophago;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import list.ListPasienView2;
import model.transaksi.TransaksiHeaderModel;
import model.transaksi.TransaksiModel;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class AddDataPasien2 extends AppCompatActivity {

    private RadioButton radioButton;
    private TransaksiModel model;
    private TransaksiHeaderModel headerModel;
    private int RESULT_PASIEN = 2;
    private int RESULT_ADD_PASIEN = 3;
    private String kode, nama, alamat, tgl, gender, telp, status;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    private ProgressDialog pDialog;
    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private String userId, username;

    @BindView(R.id.btn_pasien_lanjut2)Button btnLanjut;
    @BindView(R.id.img_newpasien_pilih2)ImageView imgPilih;
    @BindView(R.id.img_newpasien_add2)ImageView imgCreatePasien;
    @BindView(R.id.input_layout_pasien_birthday2)TextInputLayout til_birthday;
    @BindView(R.id.input_layout_pasien_nama2)TextInputLayout til_nama_pasien;
    @BindView(R.id.input_layout_pasien_alamat2)TextInputLayout til_alamat;
    @BindView(R.id.input_layout_pasien_telp2)TextInputLayout til_telp;
    @BindView(R.id.input_layout_pasien_umur2)TextInputLayout til_umur;
    @BindView(R.id.input_layout_pasien_nomor2)TextInputLayout til_kode;

    @BindView(R.id.edPasienBirthday2)EditText edPasienBirthday;
    @BindView(R.id.edPasienNama2)EditText edNamaPasien;
    @BindView(R.id.edPasienNomor2)EditText edNoPasien;
    @BindView(R.id.edPasienAlamat2)EditText edAlamat;
    @BindView(R.id.edPasienTelp2)EditText edTelp;
    @BindView(R.id.edPasienUmur2)EditText edUmur;

    @BindView(R.id.radiogroup2)RadioGroup radioGrup;
    @BindView(R.id.rbLaki2)RadioButton rbLaki;
    @BindView(R.id.rbWanita2)RadioButton rbWanita;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pasien_activity2);
        model = new TransaksiModel();
        ButterKnife.bind(this);
        Intent i = getIntent();
        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
            username = shared.getString(PrefUtil.NAME, null);
        }catch (Exception e){e.getMessage();}
        status =  i.getStringExtra("status");
        if(status.equals("MASTER")){
            kode = i.getStringExtra("kode");
            nama = i.getStringExtra("nama");
            alamat = i.getStringExtra("alamat");
            gender = i.getStringExtra("gender");
            telp = i.getStringExtra("telp");
            tgl = i.getStringExtra("tgl");
            try{
                edNoPasien.setText(kode);
                edNamaPasien.setText(nama);
                edAlamat.setText(alamat);
                if(gender.equals("L")){
                    rbLaki.setChecked(true);
                    rbWanita.setChecked(false);
                }else{
                    rbLaki.setChecked(false);
                    rbWanita.setChecked(true);
                }
                edTelp.setText(telp);
                Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
                edPasienBirthday.setText(sdf1.format(date1.getTime()));
                int age = calculateAge(date1);
                edUmur.setText(String.valueOf(age));
            }catch (Exception ex){
            }
        }
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }

    @OnClick(R.id.img_newpasien_pilih2)
    protected void pilihpasien(){
        Intent i = new Intent(AddDataPasien2.this, ListPasienView2.class);
        i.putExtra("userid", userId);
        startActivityForResult(i,RESULT_PASIEN);
    }

    @OnClick(R.id.img_newpasien_add2)
    protected void createpasien(){
        Intent i = new Intent(AddDataPasien2.this, DataPasien2.class);
        i.putExtra("Status",1);
        startActivityForResult(i,RESULT_ADD_PASIEN);
    }

    @OnClick(R.id.btn_pasien_lanjut2)
    protected void lanjut(){
        if(validateKode(edNoPasien) && validateNama(edNamaPasien) && validateKalender(edPasienBirthday) && validateGender(radioGrup)
                && validateAlamat(edAlamat) && validateTelp(edTelp)){
            int selectedId = radioGrup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            try{
                headerModel = new TransaksiHeaderModel();
                headerModel.setNoPasien(edNoPasien.getText().toString());
                headerModel.setNamaPasien(edNamaPasien.getText().toString());
                headerModel.setGender(radioButton.getText().toString());
                headerModel.setTglLahir(format.parse(edPasienBirthday.getText().toString()));
                headerModel.setUmur(Integer.valueOf(edUmur.getText().toString()));
                headerModel.setAlamat(edAlamat.getText().toString());
                headerModel.setTelp(edTelp.getText().toString());
                model.setHeader(headerModel);
                Intent i = new Intent(getApplicationContext(), AddDataKeluhan2.class);
                i.putExtra("object",model);
                startActivity(i);
            }catch (Exception ex){}

        }
    }

    private boolean validateKode(EditText edittext) {
        boolean value;
        if (edNoPasien.getText().toString().isEmpty()){
            value=false;
            til_kode.setError(getString(R.string.err_msg_kode));
        } else {
            value=true;
            til_kode.setError(null);
        }
        return value;
    }

    private boolean validateNama(EditText edittext) {
        boolean value;
        if (edNamaPasien.getText().toString().isEmpty()){
            value=false;
            til_nama_pasien.setError(getString(R.string.err_msg_nama));
        } else {
            value=true;
            til_nama_pasien.setError(null);
        }
        return value;
    }

    private boolean validateAlamat(EditText edittext) {
        boolean value;
        if (edAlamat.getText().toString().isEmpty()){
            value=false;
            til_alamat.setError(getString(R.string.err_msg_alamat));
        } else {
            value=true;
            til_alamat.setError(null);
        }
        return value;
    }

    private boolean validateTelp(EditText edittext) {
        boolean value;
        if (edTelp.getText().toString().isEmpty()){
            value=false;
            til_telp.setError(getString(R.string.err_msg_hp));
        } else {
            value=true;
            til_telp.setError(null);
        }
        return value;
    }

    private boolean validateGender(RadioGroup rg) {
        boolean value;
        int a = rg.getCheckedRadioButtonId();
        if (a < 0){
            value=false;
            Toast.makeText(AddDataPasien2.this,"Pilih Gender Pasien", Toast.LENGTH_LONG).show();
        } else{
            value=true;
        }
        return value;
    }

    private boolean validateKalender(EditText edittext) {
        boolean value;
        if (edPasienBirthday.getText().toString().isEmpty()){
            value=false;
            til_birthday.setError(getString(R.string.err_msg_birthday));
        } else {
            value=true;
            til_birthday.setError(null);
        }
        return value;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_PASIEN) {
            if(resultCode == RESULT_OK) {
                pDialog.setMessage("Loading ...");
                showDialog();
                kode = data.getStringExtra("kode");
                nama = data.getStringExtra("nama");
                alamat = data.getStringExtra("alamat");
                gender = data.getStringExtra("gender");
                telp = data.getStringExtra("telp");
                tgl = data.getStringExtra("tgl");
                try{
                    edNoPasien.setText(kode);
                    edNamaPasien.setText(nama);
                    edAlamat.setText(alamat);
                    if(gender.equals("L")){
                        rbLaki.setChecked(true);
                        rbWanita.setChecked(false);
                    }else{
                        rbLaki.setChecked(false);
                        rbWanita.setChecked(true);
                    }
                    edTelp.setText(telp);
                    Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
                    edPasienBirthday.setText(sdf1.format(date1.getTime()));
                    int age = calculateAge(date1);
                    edUmur.setText(String.valueOf(age));
                    hideDialog();
                }catch (Exception ex){
                    hideDialog();
                }
            }
        }
        if(requestCode == RESULT_ADD_PASIEN) {
            if(resultCode == RESULT_OK) {
                pDialog.setMessage("Loading ...");
                showDialog();
                kode = data.getStringExtra("kode");
                nama = data.getStringExtra("nama");
                alamat = data.getStringExtra("alamat");
                gender = data.getStringExtra("gender");
                telp = data.getStringExtra("telp");
                tgl = data.getStringExtra("tgl");
                try{
                    edNoPasien.setText(kode);
                    edNamaPasien.setText(nama);
                    edAlamat.setText(alamat);
                    if(gender.equals("L")){
                        rbLaki.setChecked(true);
                        rbWanita.setChecked(false);
                    }else{
                        rbLaki.setChecked(false);
                        rbWanita.setChecked(true);
                    }
                    edTelp.setText(telp);
                    Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
                    edPasienBirthday.setText(sdf1.format(date1.getTime()));
                    int age = calculateAge(date1);
                    edUmur.setText(String.valueOf(age));
                    hideDialog();
                }catch (Exception ex){
                    hideDialog();
                }
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
            new AlertDialog.Builder(this)
                    .setMessage("Batalkan Pemeriksaan?")
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
}
