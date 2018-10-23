package com.example.project.ophago;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.gson.Gson;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import model.transaksi.LaporanHeaderModel;
import model.transaksi.LaporanItemModel;
import model.transaksi.TransaksiItemModel;
import model.transaksi.TransaksiModel;
import utilities.Anim;
import utilities.AppController;
import utilities.JSONfunctions;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class KesimpulanActivity2 extends AppCompatActivity {

    private TransaksiModel model;
    private LaporanHeaderModel laporanModel;
    private SharedPreferences shared;
    private ProgressDialog pDialog;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private PrefUtil prefUtil;
    private Uri selectedVideo;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String userId, sdbname;
    private int stat;
    private static final int VIEW_PDF = 77;

    @BindView(R.id.linHeader)LinearLayout lineHeader;
    @BindView(R.id.imgKesimpulanBack)ImageView imgBack;
    @BindView(R.id.txtJudulKesimpulan)TextView txtJudul;
    @BindView(R.id.imgExpandPasien2)
    ImageView imgColExpPasien;
    @BindView(R.id.imgExpandDiagnosa2)
    ImageView imgColExpDiagnosa;
    @BindView(R.id.linDataPasien2)
    LinearLayout linDataPasien;
    @BindView(R.id.linDataDiagnosa2)
    LinearLayout linDataDiagnosa;
    @BindView(R.id.inputlayout_kesimpulankode2)
    TextInputLayout ilKesKode;
    @BindView(R.id.eKesimpulanKode2)
    EditText edKodePasein;
    @BindView(R.id.inputlayout_kesimpulan_nama2)
    TextInputLayout ilKesNama;
    @BindView(R.id.eKesimpulanNama2)
    EditText edNamaPasien;
    @BindView(R.id.inputlayout_kesimpulan_alamat2)
    TextInputLayout ilKesAlamat;
    @BindView(R.id.eKesimpulanAlamat2)
    EditText eAlamat;
    @BindView(R.id.inputlayout_kesimpulan_telp2)
    TextInputLayout ilKesTelp;
    @BindView(R.id.eKesimpulanTelp2)
    EditText eTelp;
    @BindView(R.id.inputlayout_kesimpulan_birthday2)
    TextInputLayout ilKesBirthday;
    @BindView(R.id.eKesimpulanBirthday2)
    EditText eBirthday;
    @BindView(R.id.inputlayout_kesimpulan_kelamin2)
    TextInputLayout ilKesKelamin;
    @BindView(R.id.eKesimpulanKelamin2)
    EditText eGender;
    @BindView(R.id.inputlayout_kesimpulan_keluhan2)
    TextInputLayout ilKeluhan;
    @BindView(R.id.eKesimpulanKeluhan2)
    EditText eKeluhan;
    @BindView(R.id.inputlayout_diagnosa_text2)
    TextInputLayout ilDiagnosa;
    @BindView(R.id.eDiagnosaText2)
    EditText eDiagnosa;
    @BindView(R.id.inputlayout_kesimpulan_text2)
    TextInputLayout ilKesimpulan;
    @BindView(R.id.eKesimpulanText2)
    EditText eKesimpulan;
    @BindView(R.id.inputlayout_saran_text2)
    TextInputLayout ilSaran;
    @BindView(R.id.eSaranText2)
    EditText eSaran;
    @BindView(R.id.btnKesimpulanSave2)
    Button btnSave;
    @BindView(R.id.ckKesimMtKn)
    CheckBox ckMtKn;
    @BindView(R.id.ckKesimMtKr)
    CheckBox ckMtKr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kesimpulan_activity2);
        ButterKnife.bind(this);
        Intent i = getIntent();
        stat = i.getExtras().getInt("status");
        if(stat==1){
            model = (TransaksiModel) i.getSerializableExtra("object");
            edKodePasein.setText(model.getHeader().getNoPasien());
            edNamaPasien.setText(model.getHeader().getNamaPasien());
            eAlamat.setText(model.getHeader().getAlamat());
            eTelp.setText(model.getHeader().getTelp());
            eBirthday.setText(sdf1.format(model.getHeader().getTglLahir()));
            eGender.setText(model.getHeader().getGender());
            eKeluhan.setText(model.getHeader().getKeluhan());
            for(TransaksiItemModel item:model.getItemList()){
                if(item.getAnatomi().equals(Link.MATA_KN)){
                    ckMtKn.setChecked(true);
                }else if(item.getAnatomi().equals(Link.MATA_KR)){
                    ckMtKr.setChecked(true);
                }
            }
            txtJudul.setText("Kesimpulan");
            lineHeader.setVisibility(View.VISIBLE);
        }else{
            laporanModel = (LaporanHeaderModel) i.getSerializableExtra("object");
            edKodePasein.setText(laporanModel.getNoPasien());
            edNamaPasien.setText(laporanModel.getNamaPasien());
            eAlamat.setText(laporanModel.getAlamat());
            eTelp.setText(laporanModel.getTelp());
            eBirthday.setText(sdf1.format(laporanModel.getTglLahir()));
            eGender.setText(laporanModel.getGender().equals("L")?"Laki-laki":"Perempuan");
            eKeluhan.setText(laporanModel.getKeluhan());
            eDiagnosa.setText(laporanModel.getDiagnosa());
            eSaran.setText(laporanModel.getSaran());
            eKesimpulan.setText(laporanModel.getKesimpulan());
            for(LaporanItemModel item:laporanModel.getItemList()){
                if(item.getAnatomi().equals(Link.MATA_KN)){
                    ckMtKn.setChecked(true);
                }else if(item.getAnatomi().equals(Link.MATA_KR)){
                    ckMtKr.setChecked(true);
                }
            }
            txtJudul.setText("Edit Kesimpulan");
            lineHeader.setVisibility(View.GONE);
        }
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
        }catch (Exception e){e.getMessage();}
    }

    @OnClick(R.id.imgKesimpulanBack)
    protected void back(){
        finish();
    }

    @OnClick(R.id.btnKesimpulanSave2)
    protected void save(){
        if(validateDiagnosa() && validateKesimpulan() && validateSaran()){
            new AlertDialog.Builder(KesimpulanActivity2.this)
                    .setMessage("Data akan disimpan?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(stat==1){
                                Calendar cal = Calendar.getInstance();
                                String device = Utils.getDeviceName();
                                model.getHeader().setKesimpulan(eKesimpulan.getText().toString());
                                model.getHeader().setSaran(eSaran.getText().toString());
                                model.getHeader().setDiagnosa(eDiagnosa.getText().toString());
                                model.getHeader().setTglTrans(df.format(dateNow()));
                                model.getHeader().setTglNow(df.format(cal.getTime()));
                                model.getHeader().setUserBy(device);
                                model.getHeader().setUserId(userId);
                                getMaxNumber();
                            }else{//edit header saja(kesimpulan, saran, diagnosa)
                                updateKesimpulan(Link.BASE_URL_API+"editKesimpulan.php",
                                        eKesimpulan.getText().toString(), eSaran.getText().toString(),
                                        eDiagnosa.getText().toString(), laporanModel.getNoTrans().trim());
                            }
                        }
                    }).create().show();
        }
    }

    public void expcol_pasien(View v) {
        if (linDataPasien.isShown()) {
            Anim.slide_up(this, linDataPasien);
            linDataPasien.setVisibility(View.GONE);
            imgColExpPasien.setBackgroundResource(R.drawable.expand);
        } else {
            Anim.slide_down(this, linDataPasien);
            linDataPasien.setVisibility(View.VISIBLE);
            imgColExpPasien.setBackgroundResource(R.drawable.collapse);
        }
    }

    public void expcol_diagnosa(View v) {
        if (linDataDiagnosa.isShown()) {
            Anim.slide_up(this, linDataDiagnosa);
            linDataDiagnosa.setVisibility(View.GONE);
            imgColExpDiagnosa.setBackgroundResource(R.drawable.expand);
        } else {
            Anim.slide_down(this, linDataDiagnosa);
            linDataDiagnosa.setVisibility(View.VISIBLE);
            imgColExpDiagnosa.setBackgroundResource(R.drawable.collapse);
        }
    }

    private boolean validateKesimpulan() {
        boolean value;
        if (eKesimpulan.getText().toString().isEmpty()) {
            value = false;
            requestFocus(eKesimpulan);
            ilKesimpulan.setError(getString(R.string.err_msg_kesimpulan));
        } else {
            value = true;
            ilKesimpulan.setError(null);
        }
        return value;
    }

    private boolean validateSaran() {
        boolean value;
        if (eSaran.getText().toString().isEmpty()) {
            value = false;
            requestFocus(eSaran);
            ilSaran.setError(getString(R.string.err_msg_saran));
        } else {
            value = true;
            ilSaran.setError(null);
        }
        return value;
    }

    private boolean validateDiagnosa() {
        boolean value;
        if (eDiagnosa.getText().toString().isEmpty()) {
            value = false;
            requestFocus(eDiagnosa);
            ilDiagnosa.setError(getString(R.string.err_msg_diagnosa));
        } else {
            value = true;
            ilDiagnosa.setError(null);
        }
        return value;
    }

    private void getMaxNumber(){
        pDialog.setMessage("Saving ...");
        showDialog();
        Calendar cal = Calendar.getInstance();
        String Url = Link.BASE_URL_API+"getMaxNomor.php";
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
                        String noTrans = "TRANS"+String.valueOf(tahun)+new DecimalFormat("00").format(bulan)+
                                new DecimalFormat("0000").format(maxNo);
                        model.getHeader().setNoTrans(noTrans);
                        model.getHeader().setTahun(tahun);
                        model.getHeader().setBulan(bulan);
                        model.getHeader().setNomor(maxNo);
                        model.getHeader().setKodeTrans("TRANS");
                        sendToServer();
                    }else{
                        hideDialog();
                        Toast.makeText(KesimpulanActivity2.this,"Tidak dapat set nomor", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    hideDialog();
                    Toast.makeText(KesimpulanActivity2.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(KesimpulanActivity2.this,"Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bulan", String.valueOf(bulan));
                params.put("tahun", String.valueOf(tahun));
                params.put("code", "TRANS");
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

    private void sendToServer(){
        RequestTask task = new RequestTask();
        task.applicationContext = KesimpulanActivity2.this;
        task.execute(new String[] { Link.BASE_URL_API+"saveTransaksi.php" });
    }

    class RequestTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            //this.dialog = ProgressDialog.show(applicationContext,"Proses Menyimpan Data", "Loading...", true);
        }

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            responseString = saveData();
            Log.d("TAGS","response:"+responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideDialog();
            if(result.trim().equals("1")==true){
                Toast.makeText(KesimpulanActivity2.this,"Data berhasil disimpan", Toast.LENGTH_LONG).show();
                createandDisplayPdf2(model);
            }else{
                Toast.makeText(KesimpulanActivity2.this,"Gagal simpan data", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String saveData(){
        String sret="";
        Gson gson = new Gson();
        String json = gson.toJson(model);
        //System.out.println(json);
        try {
            JSONObject data = new JSONObject(json);
            sret= JSONfunctions.doPost(Link.BASE_URL_API+"saveTransaksi.php", data);
            Log.d("TAG","response:"+sret);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return sret;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void updateKesimpulan(final String url, final String saran, final String diagnosa, final String kesimpulan,
                                  final String noTrans){
        final String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        final String tanggalNow =df.format(today);
        StringRequest register = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d("Respone", response.toString());
                try {
                    JSONObject jsonrespon = new JSONObject(response);
                    int Sucsess = jsonrespon.getInt("success");
                    hideDialog();
                    if (Sucsess > 0 ){
                        Toast.makeText(KesimpulanActivity2.this,"Data berhasil diupdate", Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("saran",saran);
                        returnIntent.putExtra("diagnosa", diagnosa);
                        returnIntent.putExtra("kesimpulan", kesimpulan);
                        returnIntent.putExtra("noTrans", noTrans);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }else{
                        Toast.makeText(KesimpulanActivity2.this,"Gagal Coba Lagi", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hideDialog();
                    Toast.makeText(KesimpulanActivity2.this,"Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(KesimpulanActivity2.this,"Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("saran", saran);
                params.put("diagnosa", diagnosa);
                params.put("kesimpulan", kesimpulan);
                params.put("userBy", device);
                params.put("tglNow", tanggalNow);
                params.put("noTrans", noTrans);
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

    private static Date dateNow(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Set time fields to zero
        //cal.set(Calendar.HOUR_OF_DAY, 0);
        //cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();
        return date;
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }

    public void createandDisplayPdf2(TransaksiModel header) {
        Document doc = new Document();
        try {
            File sd = Environment.getExternalStorageDirectory();
            Utils.writeToDocSDFile( header.getHeader().getNoTrans()+".pdf");
            String backupDBPath = "OphaGo/Document/"+header.getHeader().getNoTrans()+".pdf";
            final File file = new File(sd, backupDBPath);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new PdfPageEventHelper(){
                public void onEndPage(PdfWriter writer, Document document){
                    try{
                        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Created by "+ userId), 80, 30, 0);
                        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("=" + document.getPageNumber()+"="), 550, 30, 0);
                    }catch (Exception ex){}
                }
            });
            Font paraFont1= new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD|Font.UNDERLINE);
            Font paraFont2= new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD|Font.UNDERLINE);
            Font paraFont3= new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

            doc.open();
            Paragraph p1 = new Paragraph("DIAGNOSA PASIEN" , paraFont1);
            Paragraph pSpace1 = new Paragraph("\n");
            Paragraph pSpace2 = new Paragraph("\n\n");
            Paragraph p4 = new Paragraph();
            Paragraph p5 = new Paragraph("--------------------------------------------------------------------------------------------------", paraFont3);
            Paragraph p6 = new Paragraph();
            Paragraph p7 = new Paragraph();
            Paragraph p8 = new Paragraph();
            Paragraph p9 = new Paragraph();
            Paragraph p10= new Paragraph();
            Paragraph p11= new Paragraph();
            Paragraph p12= new Paragraph();
            Paragraph p14= new Paragraph();
            Paragraph p15= new Paragraph();
            Paragraph p16= new Paragraph();
            Paragraph p17= new Paragraph();
            Paragraph p18= new Paragraph();
            Paragraph pFoto1;
            Paragraph pFoto2;

            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont1);

            p6.setAlignment(Paragraph.ALIGN_LEFT);
            p6.add(new Chunk("No. Rekam Medis", paraFont3));
            p6.setTabSettings(new TabSettings(62f));
            p6.add(Chunk.TABBING);
            p6.add(new Chunk(": "+header.getHeader().getNoTrans(), paraFont3));

            p4.setAlignment(Paragraph.ALIGN_LEFT);
            p4.add(new Chunk("Tanggal Periksa", paraFont3));
            p4.setTabSettings(new TabSettings(62f));
            p4.add(Chunk.TABBING);
            p4.add(new Chunk(": "+header.getHeader().getTglTrans(), paraFont3));

            p7.setAlignment(Paragraph.ALIGN_LEFT);
            p7.add(new Chunk("Nama", paraFont3));
            p7.setTabSettings(new TabSettings(62f));
            p7.add(Chunk.TABBING);
            p7.setTabSettings(new TabSettings(62f));
            p7.add(Chunk.TABBING);
            p7.add(new Chunk(": "+header.getHeader().getNamaPasien(), paraFont3));

            p10.setAlignment(Paragraph.ALIGN_LEFT);
            p10.add(new Chunk("Jenis Kelamin", paraFont3));
            p10.setTabSettings(new TabSettings(62f));
            p10.add(Chunk.TABBING);
            p10.add(new Chunk(": "+(header.getHeader().getGender()), paraFont3));

            p12.setAlignment(Paragraph.ALIGN_LEFT);
            p12.add(new Chunk("Usia", paraFont3));
            p12.setTabSettings(new TabSettings(62f));
            p12.add(Chunk.TABBING);
            p12.setTabSettings(new TabSettings(62f));
            p12.add(Chunk.TABBING);
            p12.add(new Chunk(": "+String.valueOf(header.getHeader().getUmur())+" Tahun", paraFont3));

            p8.setAlignment(Paragraph.ALIGN_LEFT);
            p8.add(new Chunk("Alamat", paraFont3));
            p8.setTabSettings(new TabSettings(62f));
            p8.add(Chunk.TABBING);
            p8.setTabSettings(new TabSettings(62f));
            p8.add(Chunk.TABBING);
            p8.add(new Chunk(": "+header.getHeader().getAlamat(), paraFont3));

            p11.setAlignment(Paragraph.ALIGN_LEFT);
            p11.add(new Chunk("No Telp/HP", paraFont3));
            p11.setTabSettings(new TabSettings(62f));
            p11.add(Chunk.TABBING);
            p11.add(new Chunk(": "+header.getHeader().getTelp(), paraFont3));

            doc.add(p1);
            doc.add(pSpace2);
            doc.add(p6);
            doc.add(p4);
            doc.add(p7);
            doc.add(p10);
            doc.add(p12);
            doc.add(p8);
            doc.add(p11);
            doc.add(p5);
            doc.add(pSpace1);

            pFoto1=new Paragraph();
            pFoto1.setAlignment(Element.ALIGN_CENTER);
            Map<String, Map<String, Integer>> data = new HashMap<String, Map<String, Integer>>();
            for(int i=1;i<=6;i++){
                if(header.getHeader().getPathGbr(i)!=null){
                    if(!header.getHeader().getPathGbr(i).trim().equals("")){
                        String[] a = header.getHeader().getPathGbr(i).split("-");
                        String[] b = a[a.length-1].split("\\.");
                        String namaAnatomi = b[0];
                        if (data.get(namaAnatomi) == null) {
                            Map<String, Integer> detail = new HashMap<String, Integer>();
                            detail.put(String.valueOf(i), i);
                            data.put(namaAnatomi.trim(), detail);
                        }else{
                            Map<String, Integer> detail = data.get(namaAnatomi.trim());
                            detail.put(String.valueOf(i), i);
                        }
                    }
                }
            }

            for (String key : data.keySet()) {
                Map<String, Integer> detail = data.get(key);
                pFoto1.add(new Chunk(key, paraFont3));
                for(String keyDetail : detail.keySet()){
                    Integer nomor = detail.get(keyDetail);
                    String[] a = header.getHeader().getPathGbr(nomor).split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()){
                        pFoto1.setTabSettings(new TabSettings(20f));
                        pFoto1.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                ".com.example.project.ophago.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr1());
                        InputStream ims = getContentResolver().openInputStream(path);
                        Bitmap bmp = BitmapFactory.decodeStream(ims);
                        Bitmap circularBitmap = Utils.getRoundedCornerBitmap(bmp, 100);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        circularBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        image.scaleToFit(120, 120);
                        pFoto1.add(new Phrase(new Chunk(image, 0, 0, true)));
                    }
                }
                doc.add(pFoto1);
                doc.add(pSpace1);
                pFoto1=new Paragraph();
                pFoto1.setAlignment(Element.ALIGN_CENTER);
            }

            doc.add(p5);
            p15.setAlignment(Paragraph.ALIGN_LEFT);
            p15.add(new Chunk("Kesimpulan:", paraFont2));
            doc.add(p15);
            p9.setAlignment(Paragraph.ALIGN_LEFT);
            p9.add(new Chunk(header.getHeader().getKesimpulan(), paraFont3));
            doc.add(p9);
            doc.add(pSpace1);

            p16.setAlignment(Paragraph.ALIGN_LEFT);
            p16.add(new Chunk("Diagnosa:", paraFont2));
            doc.add(p16);
            p14.setAlignment(Paragraph.ALIGN_LEFT);
            p14.add(new Chunk(header.getHeader().getDiagnosa(), paraFont3));
            doc.add(p14);
            doc.add(pSpace1);

            p17.setAlignment(Paragraph.ALIGN_LEFT);
            p17.add(new Chunk("Saran:", paraFont2));
            doc.add(p17);
            p18.setAlignment(Paragraph.ALIGN_LEFT);
            p18.add(new Chunk(header.getHeader().getSaran(), paraFont3));
            doc.add(p18);
            doc.add(pSpace1);

            Drawable d = getResources().getDrawable(R.drawable.icon_pdf);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image waterMarkImage = Image.getInstance(stream.toByteArray());
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(waterMarkImage, 320, 0, 10, 350, 150, 240);

            doc.newPage();

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (Exception e) {
            Log.e("PDFCreator", "Exception:" + e);
        }
        finally {
            doc.close();
        }
        viewPdf(header.getHeader().getNoTrans()+".pdf", "OphaGo/Document");
    }

    private void viewPdf(String file, String directory) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                "/" + directory + "/" + file);
        Uri path = FileProvider.getUriForFile(KesimpulanActivity2.this, getApplicationContext().getPackageName() +
                ".com.example.project.ophago.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(pdfIntent, VIEW_PDF);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(KesimpulanActivity2.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_PDF) {
            Intent i = new Intent(KesimpulanActivity2.this, MainActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}