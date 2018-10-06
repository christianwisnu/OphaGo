package com.example.project.ophago;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import service.BaseApiService;
import utilities.AppController;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class DataPasien2 extends AppCompatActivity {

    private BaseApiService mApiService;
    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private ProgressDialog pDialog;
    private int status;
    private String kode, alamat, telp, nama, tgl, gender, hasilTgl, userId;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar dateAndTime = Calendar.getInstance();
    private Date tglLahir;
    private RadioButton radioButton;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private AlertDialog alert;
    private EditText edTgl;

    @BindView(R.id.btnDataPasienSave2)Button btnSave;
    @BindView(R.id.btnDataPasienSaveLanjut2)Button btnSaveLanjut;
    @BindView(R.id.input_layout_datapasien_kode2)TextInputLayout inputLayoutKode;
    @BindView(R.id.input_layout_datapasien_nama2)TextInputLayout inputLayoutNama;
    @BindView(R.id.input_layout_datapasien_alamat2)TextInputLayout inputLayoutAlamat;
    @BindView(R.id.input_layout_datapasien_telp2)TextInputLayout inputLayoutTelp;
    @BindView(R.id.eDataPasienKode2)EditText eKode;
    @BindView(R.id.eDataPasienNama2)EditText eNama;
    @BindView(R.id.eDataPasienAlamat2)EditText eAlamat;
    @BindView(R.id.eDataPasienTelp2)EditText eTelp;
    //@BindView(R.id.eDataPasienTgl2)EditText eTgl;
    @BindView(R.id.img_datapasien_kalendar2)ImageView imgKalendar;
    @BindView(R.id.rgDataPasienGender2)RadioGroup rgGender;
    @BindView(R.id.rbDataPasienLaki2)RadioButton rbLaki;
    @BindView(R.id.rbDataPasienWanita2)RadioButton rbWanita;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_pasien2);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                status = i.getInt("Status");
                kode = i.getString("kode");
                nama = i.getString("nama");
                alamat = i.getString("alamat");
                tgl = i.getString("tgl");
                telp = i.getString("telp");
                gender = i.getString("gender");
            } catch (Exception e) {}
        }
        mApiService         = Link.getAPIService();
        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
        }catch (Exception e){e.getMessage();}
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        edTgl = (EditText)findViewById(R.id.eDataPasienTgl2);
        try{
            if(status==2 || status==10){
                eKode.setText(kode);
                eNama.setText(nama);
                eAlamat.setText(alamat);
                hasilTgl=tgl;
                Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
                edTgl.setText(sdf1.format(date1.getTime()));
                eTelp.setText(telp);
                if(gender.equals("L")){
                    rbLaki.setChecked(true);
                    rbWanita.setChecked(false);
                }else{
                    rbLaki.setChecked(false);
                    rbWanita.setChecked(true);
                }
            }else{
                eKode.setText("XXXXXXXXXX");
            }
        }catch (Exception ex){}
        btnSaveLanjut.setText(Html.fromHtml("Simpan & Lanjutkan"));

        edTgl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edTgl.setEnabled(false);
                    hideKeyboard(v);
                    new DatePickerDialog(DataPasien2.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                    edTgl.setEnabled(true);
                }
            }
        });

        edTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edTgl.setEnabled(false);
                hideKeyboard(v);
                new DatePickerDialog(DataPasien2.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                edTgl.setEnabled(true);
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.img_datapasien_kalendar2)
    protected void kalendar(){
        new DatePickerDialog(DataPasien2.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.btnDataPasienSave2)
    protected void save(){
        if(validateNama(eNama) && validateKalender(edTgl) && validateGender(rgGender)
                && validateAlamat(eAlamat) && validateTelp(eTelp)){
            int selectedId = rgGender.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);

            AlertDialog.Builder msMaintance = new AlertDialog.Builder(DataPasien2.this);
            msMaintance.setCancelable(false);
            msMaintance.setMessage("Data akan disimpan? ");
            msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (status==1){
                        getMaxNumber(Link.BASE_URL_API+"getMaxNomor.php", false);
                    }else if (status==2 || status==10){
                        if(validateKode(eKode)){
                            savePasien(eKode.getText().toString(),eNama.getText().toString(),
                                    eAlamat.getText().toString(), eTelp.getText().toString(),
                                    radioButton.getText().toString().equals("Laki-laki")?"L":"P", hasilTgl,
                                    0, 0, 0, Link.BASE_URL_API+"savePasien.php","EDIT", false);
                        }
                    }
                }
            });

            msMaintance.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }
            });
            alert	=msMaintance.create();
            alert.show();
        }
    }

    @OnClick(R.id.btnDataPasienSaveLanjut2)
    protected void savelanjut(){
        if(validateNama(eNama) && validateKalender(edTgl) && validateGender(rgGender)
                && validateAlamat(eAlamat) && validateTelp(eTelp)){
            int selectedId = rgGender.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);

            AlertDialog.Builder msMaintance = new AlertDialog.Builder(DataPasien2.this);
            msMaintance.setCancelable(false);
            msMaintance.setMessage("Simpan dan lanjutkan ke pemeriksaan? ");
            msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (status==1){
                        getMaxNumber(Link.BASE_URL_API+"getMaxNomor.php", true);
                    }else if (status==2 || status==10){
                        if(validateKode(eKode)){
                            savePasien(eKode.getText().toString(),eNama.getText().toString(),
                                    eAlamat.getText().toString(), eTelp.getText().toString(),
                                    radioButton.getText().toString().equals("Laki-laki")?"L":"P", hasilTgl,
                                    0, 0, 0, Link.BASE_URL_API+"savePasien.php","EDIT", true);
                        }
                    }
                }
            });

            msMaintance.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }
            });
            alert	=msMaintance.create();
            alert.show();
        }
    }

    private void getMaxNumber(String Url, final boolean lanjut){//getMaxNo.php
        pDialog.setMessage("Saving ...");
        showDialog();
        Calendar cal = Calendar.getInstance();
        final Integer bulan = cal.get(Calendar.MONTH)+1;
        final Integer tahun = cal.get(Calendar.YEAR);
        StringRequest register = new StringRequest(Request.Method.POST, Url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d("Respone", response.toString());
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    int Sucsess = jsonrespon.getInt("success");

                    if (Sucsess > 0 ){
                        JSONArray JsonArray = jsonrespon.getJSONArray("uploade");
                        JSONObject object = JsonArray.getJSONObject(0);
                        Integer maxNo = object.getInt("count")+1;
                        //Integer number = Integer.valueOf(nomor)+1;
                        String noPasien = String.valueOf(tahun)+new DecimalFormat("00").format(bulan)+
                                new DecimalFormat("0000").format(maxNo);
                        savePasien(noPasien,eNama.getText().toString(), eAlamat.getText().toString(), eTelp.getText().toString(),
                                radioButton.getText().toString().equals("Laki-laki")?"L":"P", hasilTgl,
                                bulan, tahun, maxNo, Link.BASE_URL_API+"savePasien.php","SAVE", lanjut);
                    }else{
                        hideDialog();
                        Toast.makeText(DataPasien2.this,"Tidak dapat set nomor", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    hideDialog();
                    Toast.makeText(DataPasien2.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(DataPasien2.this,"Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bulan", String.valueOf(bulan));
                params.put("tahun", String.valueOf(tahun));
                params.put("code", "MSTR");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(register);
    }

    private void savePasien(final String kode, final String nama, final String alamat, final String telp, final String gender,
                            final String birthday, final int bulan, final int tahun, final int nomor,
                            String Url, final String statusku, final boolean lanjut){
        final String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        final String tanggalNow =df.format(today);
        StringRequest register = new StringRequest(Request.Method.POST, Url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d("Respone", response.toString());
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    int Sucsess = jsonrespon.getInt("success");
                    hideDialog();
                    if (Sucsess > 0 ){
                        Toast.makeText(DataPasien2.this,"Data berhasil disimpan", Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        if(lanjut==true){
                            Intent i = new Intent(DataPasien2.this, AddDataPasien2.class);
                            i.putExtra("status","EDIT");
                            i.putExtra("kode", kode);
                            i.putExtra("nama", nama);
                            i.putExtra("alamat", alamat);
                            i.putExtra("gender", gender);
                            i.putExtra("telp", telp);
                            Date date1=new SimpleDateFormat("dd-MM-yyy").parse(birthday);
                            i.putExtra("tgl", df2.format(date1));
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }else{
                            if(status==10){
                                returnIntent.putExtra("kode",kode);
                                returnIntent.putExtra("nama", nama);
                                returnIntent.putExtra("alamat", alamat);
                                returnIntent.putExtra("gender", gender);
                                returnIntent.putExtra("telp", telp);
                                returnIntent.putExtra("tgl", birthday);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }else{
                                setResult(RESULT_CANCELED, returnIntent);
                                finish();
                            }
                        }
                    }else{
                        Toast.makeText(DataPasien2.this,"Gagal Coba Lagi", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hideDialog();
                    Toast.makeText(DataPasien2.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(DataPasien2.this,"Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userId);
                params.put("kode", kode);
                params.put("nama", nama);
                params.put("alamat", alamat);
                params.put("telp", telp);
                params.put("gender", gender);
                params.put("tgl", birthday);
                params.put("userBy", device);
                params.put("tglNow", tanggalNow);
                params.put("bulan", String.valueOf(bulan));
                params.put("tahun", String.valueOf(tahun));
                params.put("nomor", String.valueOf(nomor));
                params.put("status", statusku);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(register);
    }

    private DatePickerDialog.OnDateSetListener dFrom =new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelFrom();
        }
    };

    private void updatelabelFrom(){
        edTgl.setText(sdf1.format(dateAndTime.getTime()));
        tglLahir=dateAndTime.getTime();
        hasilTgl=sdf2.format(dateAndTime.getTime());
    }

    private boolean validateKode(EditText edittext) {
        boolean value;
        if (eKode.getText().toString().isEmpty()){
            value=false;
            inputLayoutKode.setError(getString(R.string.err_msg_kode));
        } else {
            value=true;
            inputLayoutKode.setError(null);
        }
        return value;
    }

    private boolean validateNama(EditText edittext) {
        boolean value;
        if (eNama.getText().toString().isEmpty()){
            value=false;
            requestFocus(eNama);
            inputLayoutNama.setError(getString(R.string.err_msg_nama));
        } else {
            value=true;
            inputLayoutNama.setError(null);
        }
        return value;
    }

    private boolean validateAlamat(EditText edittext) {
        boolean value;
        if (eAlamat.getText().toString().isEmpty()){
            value=false;
            requestFocus(eAlamat);
            inputLayoutAlamat.setError(getString(R.string.err_msg_alamat));
        } else {
            value=true;
            inputLayoutAlamat.setError(null);
        }
        return value;
    }

    private boolean validateTelp(EditText edittext) {
        boolean value;
        if (eTelp.getText().toString().isEmpty()){
            value=false;
            requestFocus(eTelp);
            inputLayoutTelp.setError(getString(R.string.err_msg_hp));
        } else {
            value=true;
            inputLayoutTelp.setError(null);
        }
        return value;
    }

    private boolean validateGender(RadioGroup rg) {
        boolean value;
        int a = rg.getCheckedRadioButtonId();
        if (a < 0){
            value=false;
            Toast.makeText(DataPasien2.this,"Pilih Gender Pasien", Toast.LENGTH_LONG).show();
        } else{
            value=true;
        }
        return value;
    }

    private boolean validateKalender(EditText edittext) {
        boolean value;
        if (edTgl.getText().toString().isEmpty()){
            Toast.makeText(DataPasien2.this, "Tanggal Lahir harap diisi!", Toast.LENGTH_LONG).show();
            value=false;
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
