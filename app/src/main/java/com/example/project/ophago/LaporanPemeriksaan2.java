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

    private ProgressDialog pDialog;
    private AdpTransaksi2 adapter;
    private ListView lsvupload;
    private LaporanModel model2;
    private ArrayList<LaporanDetail> columnlist= new ArrayList<LaporanDetail>();
    private TextView txtId, txtNama, txtAlamat, txtTelp, txtUmur, txtStatus;
    private Button btnNewTrans, btnEditProfile, btnCetakAll;
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
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
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
        btnNewTrans = (Button) findViewById(R.id.btnPemeriksaanBaru);
        btnEditProfile = (Button) findViewById(R.id.btnEditDataPasien);
        btnCetakAll = (Button) findViewById(R.id.btnCetakSemuaPdf);

        txtId.setText(item.getKodePasien());
        txtNama.setText(item.getNamaPasien());
        txtAlamat.setText(item.getAlamat());
        txtTelp.setText(item.getTelp());
        txtUmur.setText(String.valueOf(item.getUmur())+" Tahun");

        adapter		= new AdpTransaksi2(LaporanPemeriksaan2.this, R.layout.col_transaksi2, model2.getHeaderList(), userId);
        lsvupload.setAdapter(adapter);

        btnNewTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent i = new Intent(LaporanPemeriksaan2.this, AddDataPasien2.class);
                    i.putExtra("kode",item.getKodePasien());
                    i.putExtra("nama", item.getNamaPasien());
                    i.putExtra("alamat", item.getAlamat());
                    i.putExtra("gender", item.getGender());
                    i.putExtra("telp", item.getTelp());
                    Date date1=new SimpleDateFormat("dd-MM-yyy").parse(item.getBirthday());
                    i.putExtra("tgl", df2.format(date1));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }catch (Exception e){}

            }
        });

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

        btnCetakAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("Create Report\nPlease Wait ...");
                showDialog();
                createandDisplayPdf(item.getKodePasien());
            }
        });
    }

    public void createandDisplayPdf(String kodePasien) {
        Document doc = new Document();
        try {
            File sd = Environment.getExternalStorageDirectory();
            Utils.writeToDocSDFile( kodePasien+".pdf");
            String backupDBPath = "OphaGo/Document/"+kodePasien+".pdf";
            final File file = new File(sd, backupDBPath);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new PdfPageEventHelper(){
                public void onEndPage(PdfWriter writer, Document document){
                    ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Created by "+username), 80, 30, 0);
                    ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("=" + document.getPageNumber()+"="), 550, 30, 0);
                }
            });
            Font paraFont1= new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD|Font.UNDERLINE);
            Font paraFont2= new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD|Font.UNDERLINE);
            Font paraFont3= new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

            doc.open();

            for(LaporanHeaderModel header:model2.getHeaderList()){
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
                p6.add(new Chunk(": "+header.getNoTrans(), paraFont3));

                p4.setAlignment(Paragraph.ALIGN_LEFT);
                p4.add(new Chunk("Tanggal Periksa", paraFont3));
                p4.setTabSettings(new TabSettings(62f));
                p4.add(Chunk.TABBING);
                p4.add(new Chunk(": "+header.getTglTrans(), paraFont3));

                p7.setAlignment(Paragraph.ALIGN_LEFT);
                p7.add(new Chunk("Nama", paraFont3));
                p7.setTabSettings(new TabSettings(62f));
                p7.add(Chunk.TABBING);
                p7.setTabSettings(new TabSettings(62f));
                p7.add(Chunk.TABBING);
                p7.add(new Chunk(": "+header.getNamaPasien(), paraFont3));

                p10.setAlignment(Paragraph.ALIGN_LEFT);
                p10.add(new Chunk("Jenis Kelamin", paraFont3));
                p10.setTabSettings(new TabSettings(62f));
                p10.add(Chunk.TABBING);
                p10.add(new Chunk(": "+(header.getGender().equals("L")?"Laki-laki":"Perempuan"), paraFont3));

                p12.setAlignment(Paragraph.ALIGN_LEFT);
                p12.add(new Chunk("Usia", paraFont3));
                p12.setTabSettings(new TabSettings(62f));
                p12.add(Chunk.TABBING);
                p12.setTabSettings(new TabSettings(62f));
                p12.add(Chunk.TABBING);
                p12.add(new Chunk(": "+String.valueOf(header.getUmur())+" Tahun", paraFont3));

                p8.setAlignment(Paragraph.ALIGN_LEFT);
                p8.add(new Chunk("Alamat", paraFont3));
                p8.setTabSettings(new TabSettings(62f));
                p8.add(Chunk.TABBING);
                p8.setTabSettings(new TabSettings(62f));
                p8.add(Chunk.TABBING);
                p8.add(new Chunk(": "+header.getAlamat(), paraFont3));

                p11.setAlignment(Paragraph.ALIGN_LEFT);
                p11.add(new Chunk("No Telp/HP", paraFont3));
                p11.setTabSettings(new TabSettings(62f));
                p11.add(Chunk.TABBING);
                p11.add(new Chunk(": "+header.getTelp(), paraFont3));

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
                if(header.getPathGbr1()!=null ){
                    if( !header.getPathGbr1().trim().equals("")){
                        String[] a = header.getPathGbr1().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()){
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
                }
                if(header.getPathGbr2()!=null){
                    if(!header.getPathGbr2().trim().equals("")){
                        pFoto1.setTabSettings(new TabSettings(20f));
                        pFoto1.add(Chunk.TABBING);
                        String[] a = header.getPathGbr2().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()) {
                            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                    ".com.example.project.ophago.provider", pdfFile);
                            //Uri fileUri=Uri.parse(header.getPathGbr2());
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
                }
                if(header.getPathGbr3()!=null){
                    if(!header.getPathGbr3().trim().equals("")){
                        pFoto1.setTabSettings(new TabSettings(20f));
                        pFoto1.add(Chunk.TABBING);
                        String[] a = header.getPathGbr3().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()) {
                            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                    ".com.example.project.ophago.provider", pdfFile);
                            //Uri fileUri=Uri.parse(header.getPathGbr3());
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
                }
                doc.add(pFoto1);
                doc.add(pSpace1);

                pFoto2=new Paragraph();
                pFoto2.setAlignment(Element.ALIGN_CENTER);
                if(header.getPathGbr4()!=null ){
                    if(!header.getPathGbr4().trim().equals("")){
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        String[] a = header.getPathGbr4().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()) {
                            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                    ".com.example.project.ophago.provider", pdfFile);
                            //Uri fileUri=Uri.parse(header.getPathGbr4());
                            InputStream ims = getContentResolver().openInputStream(path);
                            Bitmap bmp = BitmapFactory.decodeStream(ims);
                            Bitmap circularBitmap = Utils.getRoundedCornerBitmap(bmp, 100);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            circularBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image image = Image.getInstance(stream.toByteArray());
                            image.scaleToFit(120, 120);
                            pFoto2.add(new Phrase(new Chunk(image, 0, 0, true)));
                        }
                    }
                }
                if(header.getPathGbr5()!=null){
                    if(!header.getPathGbr5().trim().equals("")){
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        String[] a = header.getPathGbr5().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()) {
                            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                    ".com.example.project.ophago.provider", pdfFile);
                            //Uri fileUri=Uri.parse(header.getPathGbr5());
                            InputStream ims = getContentResolver().openInputStream(path);
                            Bitmap bmp = BitmapFactory.decodeStream(ims);
                            Bitmap circularBitmap = Utils.getRoundedCornerBitmap(bmp, 100);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            circularBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image image = Image.getInstance(stream.toByteArray());
                            image.scaleToFit(120, 120);
                            pFoto2.add(new Phrase(new Chunk(image, 0, 0, true)));
                        }
                    }
                }
                if(header.getPathGbr6()!=null ){
                    if(!header.getPathGbr6().trim().equals("")){
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        String[] a = header.getPathGbr6().split("/");
                        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                                "/" + "OphaGo/Image" + "/" + a[a.length-1]);
                        if(pdfFile.exists()) {
                            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() +
                                    ".com.example.project.ophago.provider", pdfFile);
                            //Uri fileUri=Uri.parse(header.getPathGbr6());
                            InputStream ims = getContentResolver().openInputStream(path);
                            Bitmap bmp = BitmapFactory.decodeStream(ims);
                            Bitmap circularBitmap = Utils.getRoundedCornerBitmap(bmp, 100);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            circularBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image image = Image.getInstance(stream.toByteArray());
                            image.scaleToFit(120, 120);
                            pFoto2.add(new Phrase(new Chunk(image, 0, 0, true)));
                        }
                    }
                }
                doc.add(pSpace1);
                doc.add(pFoto2);

                doc.add(p5);
                p15.setAlignment(Paragraph.ALIGN_LEFT);
                p15.add(new Chunk("Kesimpulan:", paraFont2));
                doc.add(p15);
                p9.setAlignment(Paragraph.ALIGN_LEFT);
                p9.add(new Chunk(header.getKesimpulan(), paraFont3));
                doc.add(p9);
                doc.add(pSpace1);

                p16.setAlignment(Paragraph.ALIGN_LEFT);
                p16.add(new Chunk("Diagnosa:", paraFont2));
                doc.add(p16);
                p14.setAlignment(Paragraph.ALIGN_LEFT);
                p14.add(new Chunk(header.getDiagnosa(), paraFont3));
                doc.add(p14);
                doc.add(pSpace1);

                p17.setAlignment(Paragraph.ALIGN_LEFT);
                p17.add(new Chunk("Saran:", paraFont2));
                doc.add(p17);
                p18.setAlignment(Paragraph.ALIGN_LEFT);
                p18.add(new Chunk(header.getSaran(), paraFont3));
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
            }
        } catch (DocumentException de) {
            hideDialog();
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            hideDialog();
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            hideDialog();
            doc.close();
        }
        hideDialog();
        viewPdf(kodePasien+".pdf", "OphaGo/Document");
    }

    private void viewPdf(String file, String directory) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                "/" + directory + "/" + file);
        Uri path = FileProvider.getUriForFile(LaporanPemeriksaan2.this, getApplicationContext().getPackageName() +
                ".com.example.project.ophago.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(LaporanPemeriksaan2.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
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