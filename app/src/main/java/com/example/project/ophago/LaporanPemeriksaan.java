package com.example.project.ophago;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.AdpTransaksi;
import list.ListPasienView2;
import model.list.ListTransaksi;
import model.transaksi.LaporanHeaderModel;
import model.transaksi.LaporanItemModel;
import model.transaksi.LaporanModel;
import utilities.AppController;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 05/05/18.
 */

public class LaporanPemeriksaan extends AppCompatActivity {

    private ProgressDialog pDialog;
    private AdpTransaksi adapter;
    private ListView lsvupload;
    private ArrayList<ListTransaksi> columnlist= new ArrayList<ListTransaksi>();
    private ListTransaksi item;
    private CheckBox ckAllPasien, ckAllPeriode;
    private TextView txtStatus;
    private EditText edTglFrom, edTglTo, edNamaPasien;
    private Button btnProses, btnPemeriksaan;
    private ImageView imgTglFrom, imgTglTo;
    private String getData2	="laporanDistinct.php";
    private boolean allPasien = false, allPeriode = false;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar dateAndTime = Calendar.getInstance();
    private String hasilTglTo, hasilTglFrom, userId, username, kodePasien;
    private Date tglFrom, tglTo;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int RESULT_PASIEN = 2;
    private LaporanModel model2;
    private LaporanModel modelNext;
    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private ImageButton btnddPasien;
    private int RESULT_EDIT_KESIMPULAN = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat1);
        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
            username = shared.getString(PrefUtil.NAME, null);
        }catch (Exception e){e.getMessage();}
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        lsvupload	= (ListView)findViewById(R.id.LsvTransaksi2);
        ckAllPasien = (CheckBox)findViewById(R.id.ckAllPasienTrans2);
        ckAllPeriode = (CheckBox)findViewById(R.id.ckAllPeriode2);
        edNamaPasien = (EditText) findViewById(R.id.eRiwayat1PasienNama2);
        edTglTo = (EditText) findViewById(R.id.edTglToTrans2);
        edTglFrom = (EditText) findViewById(R.id.edTglFromTrans2);
        btnProses = (Button) findViewById(R.id.btnProsesListTrans2);
        imgTglFrom = (ImageView) findViewById(R.id.img_listtrans_tglfrom2);
        imgTglTo = (ImageView) findViewById(R.id.img_listtrans_tglto2);
        txtStatus = (TextView) findViewById(R.id.TvStatusDataListTransaksi2);
        btnddPasien = (ImageButton) findViewById(R.id.btnRiwayatAddPasien);
        btnPemeriksaan = (Button)findViewById(R.id.btnProsesTransaksiBaru);

        model2 = new LaporanModel();
        adapter		= new AdpTransaksi(LaporanPemeriksaan.this, R.layout.col_transaksi, columnlist);
        lsvupload.setAdapter(adapter);

        edTglFrom.requestFocus();

        lsvupload.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> Parent, View view, int position,
                                    long id) {
                item = columnlist.get(position);
                String idPasien = item.getKodePasien();
                modelNext = new LaporanModel();
                for(LaporanHeaderModel header : model2.getHeaderList()){
                    if(header.getNoPasien().equals(idPasien)){
                        modelNext.addItem(header);
                    }
                }
                Collections.sort(modelNext.getHeaderList(), new Urut());
                Intent i = new Intent(getApplicationContext(), LaporanPemeriksaan2.class);
                i.putExtra("object",item);
                i.putExtra("model",modelNext);
                i.putExtra("tglFrom",ckAllPeriode.isChecked()?"-":hasilTglFrom);
                i.putExtra("tglTo",ckAllPeriode.isChecked()?"-":hasilTglTo);
                i.putExtra("allPeriode",ckAllPeriode.isChecked()?"Y":"N");
                startActivityForResult(i, RESULT_EDIT_KESIMPULAN);
            }
        });

        btnddPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(LaporanPemeriksaan.this, DataPasien2.class);
                a.putExtra("Status",1);
                startActivity(a);
            }
        });

        btnPemeriksaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LaporanPemeriksaan.this, AddDataPasien2.class);
                i.putExtra("status","NEW");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        ckAllPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allPasien){
                    allPasien=false;
                    //imgPilihPasien.setVisibility(View.VISIBLE);
                }else{
                    allPasien=true;
                    edNamaPasien.setText(null);
                    //imgPilihPasien.setVisibility(View.INVISIBLE);
                }
            }
        });

        ckAllPeriode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allPeriode){
                    allPeriode=false;
                    //imgTglFrom.setVisibility(View.VISIBLE);
                    //imgTglTo.setVisibility(View.VISIBLE);
                }else{
                    allPeriode=true;
                    edTglFrom.setText(null);
                    hasilTglFrom=null;
                    tglFrom=null;
                    edTglTo.setText(null);
                    hasilTglTo=null;
                    tglTo=null;
                    //imgTglFrom.setVisibility(View.INVISIBLE);
                    //imgTglTo.setVisibility(View.INVISIBLE);
                }
            }
        });

        edNamaPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allPasien){
                    edNamaPasien.setEnabled(false);
                    hideKeyboard(v);
                    pilihPasien();
                    edNamaPasien.setEnabled(true);
                }
            }
        });

        edNamaPasien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(!allPasien){
                        edNamaPasien.setEnabled(false);
                        hideKeyboard(v);
                        pilihPasien();
                        edNamaPasien.setEnabled(true);
                    }
                }
            }
        });

        imgTglFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allPeriode){
                    new DatePickerDialog(LaporanPemeriksaan.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        imgTglTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allPeriode){
                    new DatePickerDialog(LaporanPemeriksaan.this, dTo, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePasien() && validateTanggal()){
                    getDataTransaksiDistinct(Link.BASE_URL_API+getData2, ckAllPasien.isChecked()?"%":kodePasien,
                            ckAllPeriode.isChecked()?"Y":"N", hasilTglFrom, hasilTglTo);
                }
            }
        });
    }

    private void pilihPasien(){
        Intent i = new Intent(LaporanPemeriksaan.this, ListPasienView2.class);
        i.putExtra("userid", userId);
        startActivityForResult(i,RESULT_PASIEN);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static class Urut implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            String a =((LaporanHeaderModel)o1).getTglTrans();
            String b = ((LaporanHeaderModel)o2).getTglTrans();
            Date date1 = null, date2 = null;
            try{
                date1 = new SimpleDateFormat("dd-MM-yyyy").parse(a);
                date2 = new SimpleDateFormat("dd-MM-yyyy").parse(b);
            }catch (Exception ex){}
            return date2.compareTo(date1); //desc. Kalo asc     a.compareTo(b)
        }
    }

    private boolean validatePasien() {
        boolean value;
        if (edNamaPasien.getText().toString().isEmpty() && !ckAllPasien.isChecked()){
            value=false;
            Toast.makeText(LaporanPemeriksaan.this,"Pasien harap dipilih!", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private boolean validateTanggal() {
        boolean value;
        if(!ckAllPeriode.isChecked()){
            if (edTglFrom.getText().toString().isEmpty() || edTglTo.getText().toString().isEmpty()){
                value=false;
                Toast.makeText(LaporanPemeriksaan.this,"Tanggal tidak boleh kosong!", Toast.LENGTH_LONG).show();
            } else if (tglFrom.compareTo(tglTo)>0){
                value=false;
                Toast.makeText(LaporanPemeriksaan.this,"Format tanggal salah!", Toast.LENGTH_LONG).show();
            }else {
                value=true;
            }
        }else {
            value=true;
        }
        return value;
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
        edTglFrom.setText(sdf1.format(dateAndTime.getTime()));
        tglFrom=dateAndTime.getTime();
        hasilTglFrom=sdf2.format(dateAndTime.getTime());
    }

    DatePickerDialog.OnDateSetListener dTo =new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelTo();
        }
    };

    private void updatelabelTo(){
        edTglTo.setText(sdf1.format(dateAndTime.getTime()));
        tglTo=dateAndTime.getTime();
        hasilTglTo=sdf2.format(dateAndTime.getTime());
    }

    private void getModel(String Url, final String pasien, final String pilihTgl,
                          final String stglFrom, final String stglTo, final String kodeUser){
        pDialog.setMessage("Loading....");
        showDialog();
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        StringRequest register = new StringRequest(Request.Method.POST, Url, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    String message = (String) jsonrespon.get("message");
                    if(message.trim().equals("1")){
                        model2 = new LaporanModel();
                        JSONArray JsonHeader = jsonrespon.getJSONArray("header");
                        for (int i = 0; i <JsonHeader.getJSONArray(0).length(); i++) {
                            Object obj = JsonHeader.getJSONArray(0).get(i);
                            LaporanHeaderModel header = new LaporanHeaderModel();
                            header.setNoTrans((String)((JSONObject) obj).get("noTrans"));
                            header.setNoPasien((String)((JSONObject) obj).get("pasienId"));
                            Date tglTrans=new SimpleDateFormat("yyyy-MM-dd").parse((String)((JSONObject) obj).get("tglTrans"));
                            header.setTglTrans(sdf1.format(tglTrans));
                            header.setKeluhan((String)((JSONObject) obj).get("keluhan"));
                            header.setKesimpulan((String)((JSONObject) obj).get("kesimpulan"));
                            header.setSaran((String)((JSONObject) obj).get("saran"));
                            header.setDiagnosa((String)((JSONObject) obj).get("diagnosa"));
                            header.setNamaPasien((String)((JSONObject) obj).get("namaPasien"));
                            header.setAlamat((String)((JSONObject) obj).get("alamat"));
                            header.setTelp((String)((JSONObject) obj).get("nohp"));
                            header.setGender((String)((JSONObject) obj).get("gender"));
                            Date date1=new SimpleDateFormat("yyyy-MM-dd").parse((String)((JSONObject) obj).get("tglLahir"));
                            header.setTglLahir(date1);
                            header.setPathGbr1(((JSONObject) obj).get("gbr1path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr1path"));
                            header.setPathGbr2(((JSONObject) obj).get("gbr2path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr2path"));
                            header.setPathGbr3(((JSONObject) obj).get("gbr3path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr3path"));
                            header.setPathGbr4(((JSONObject) obj).get("gbr4path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr4path"));
                            header.setPathGbr5(((JSONObject) obj).get("gbr5path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr5path"));
                            header.setPathGbr6(((JSONObject) obj).get("gbr6path")==JSONObject.NULL?"":(String)((JSONObject) obj).get("gbr6path"));
                            cal.setTime(header.getTglLahir());
                            int yearBirthday = cal.get(Calendar.YEAR);
                            header.setUmur(year-yearBirthday);

                            JSONArray JsonItem = (JSONArray) ((JSONObject) obj).get("item");
                            for (int a = 0; a <JsonItem.length(); a++) {
                                Object objItem = JsonItem.getJSONObject(a);
                                LaporanItemModel item = new LaporanItemModel();
                                item.setNoTrans((String)((JSONObject) objItem).get("noTrans"));
                                item.setLine((Integer)((JSONObject) objItem).get("line"));
                                item.setAnatomi((String)((JSONObject) objItem).get("anatomi"));
                                item.setPathVideo(((JSONObject) objItem).get("videopath")==JSONObject.NULL?"":(String) ((JSONObject) objItem).get("videopath"));
                                header.addItem(item);
                            }
                            model2.addItem(header);
                        }
                    }else{//GAGAL
                        Toast.makeText(LaporanPemeriksaan.this,"Tidak Ada Data", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                    hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LaporanPemeriksaan.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(LaporanPemeriksaan.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"AuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", kodeUser);
                params.put("pasienId", pasien);
                params.put("pilihTgl", pilihTgl);// Y/N
                params.put("tglFrom", stglFrom);
                params.put("tglTo", stglTo);
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

    private void getDataTransaksiDistinct(String Url, final String pasien, final String pilihTgl,
                                          final String stglFrom, final String stglTo){
        pDialog.setMessage("Loading....");
        showDialog();
        StringRequest register = new StringRequest(Request.Method.POST, Url, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    JSONArray JsonArray = jsonrespon.getJSONArray("transaksi");
                    adapter.clear();
                    if(JsonArray.get(0).toString().trim().equals("null")==true){
                        txtStatus.setVisibility(View.VISIBLE);
                        txtStatus.setText("Data Tidak Ada");
                        hideDialog();
                    }else if(JsonArray.length()>0){
                        txtStatus.setVisibility(View.INVISIBLE);
                        for (int i = 0; i <JsonArray.getJSONArray(0).length(); i++) {
                            Object object = JsonArray.getJSONArray(0).get(i);
                            ListTransaksi colums 	= new ListTransaksi();
                            colums.setNamaPasien((String)((JSONObject) object).get("namaPasien"));
                            colums.setKodePasien((String)((JSONObject) object).get("kodePasien"));
                            colums.setAlamat((String)((JSONObject) object).get("alamat"));
                            colums.setTelp((String)((JSONObject) object).get("telp"));
                            Date date1=new SimpleDateFormat("yyyy-MM-dd").parse((String)((JSONObject) object).get("tglLahir"));
                            colums.setBirthday(sdf1.format(date1.getTime()));
                            colums.setGender((String)((JSONObject) object).get("gender"));
                            int age = calculateAge(date1);
                            colums.setUmur(age);
                            colums.setNomorUrut(i+1);
                            columnlist.add(colums);
                        }
                        adapter.notifyDataSetChanged();
                        getModel(Link.BASE_URL_API+"getModel2.php", pasien,
                                pilihTgl, stglFrom, stglTo, userId);
                    }else{
                        Toast.makeText(LaporanPemeriksaan.this,"Tidak Ada Data", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LaporanPemeriksaan.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(LaporanPemeriksaan.this,ex.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"AuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,"Check Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    hideDialog();
                    Toast.makeText(LaporanPemeriksaan.this,error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pasienId", pasien);
                params.put("pilihTgl", pilihTgl);// Y/N
                params.put("tglFrom", stglFrom);
                params.put("tglTo", stglTo);
                params.put("userId", userId);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_PASIEN) {
            if(resultCode == RESULT_OK) {
                kodePasien = data.getStringExtra("kode");
                edNamaPasien.setText(data.getStringExtra("nama"));
            }
        }
        if(requestCode == RESULT_EDIT_KESIMPULAN) {
            if(resultCode == RESULT_OK) {
                model2 = new LaporanModel();
                columnlist = new ArrayList<ListTransaksi>();
                adapter		= new AdpTransaksi(LaporanPemeriksaan.this, R.layout.col_transaksi, columnlist);
                adapter.clear();
                adapter.notifyDataSetChanged();
                lsvupload.setAdapter(adapter);

                edNamaPasien.setText(null);
                ckAllPasien.setChecked(false);
                edTglFrom.setText(null);
                edTglTo.setText(null);
                ckAllPeriode.setChecked(false);
                imgTglFrom.setVisibility(View.VISIBLE);
                imgTglTo.setVisibility(View.VISIBLE);
                allPasien = false;
                allPeriode = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}