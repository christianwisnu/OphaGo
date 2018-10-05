package adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ophago.KesimpulanActivity2;
import com.example.project.ophago.R;
import com.example.project.ophago.SendEmailActivity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import model.transaksi.LaporanHeaderModel;
import utilities.Utils;

/**
 * Created by christian on 05/05/18.
 */

public class AdpTransaksi2 extends ArrayAdapter<LaporanHeaderModel> {

    private List<LaporanHeaderModel> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private int lastPosition = -1;
    private ViewHolder holder;
    private Activity parent;
    private Context context;
    private ListView lsvchoose;
    private static final int SEND_UPLOAD = 201;
    private AlertDialog alert;
    private ProgressDialog pDialog;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private String userid;
    private int RESULT_EDIT_KESIMPULAN = 11;

    public AdpTransaksi2(Context context, int resource, List<LaporanHeaderModel> objects, String user) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
        userid = user;
        pDialog = new ProgressDialog(this.context);
        pDialog.setCancelable(false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.TvNotrans    = 	 (TextView)v.findViewById(R.id.TvColListTransNomor2);
            holder.TvTgl	    =	 (TextView)v.findViewById(R.id.TvColListTransTanggal2);
            holder.ImgPdf 	    =	 (ImageView) v.findViewById(R.id.imgColPrintPdf);
            holder.ImgEmail	    =	 (ImageView) v.findViewById(R.id.imgColEmailPdf);
            holder.ImgEdit	    =	 (ImageView) v.findViewById(R.id.imgColEdiTrans);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }
        holder.TvTgl.setText(columnslist.get(position).getTglTrans());
        holder.TvNotrans.setText(String.valueOf(columnslist.get(position).getNoTrans()));

        holder.ImgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SendEmailActivity.class);
                i.putExtra("noTrans",columnslist.get(position).getNoTrans());
                getContext().startActivity(i);
            }
        });

        holder.ImgPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaporanHeaderModel modelku = columnslist.get(position);
                createandDisplayPdf2(modelku);
            }
        });

        holder.ImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), KesimpulanActivity2.class);
                i.putExtra("status",2);
                i.putExtra("object",columnslist.get(position));
                ((Activity) getContext()).startActivityForResult(i, RESULT_EDIT_KESIMPULAN);
            }
        });

        return v;
    }

    static class ViewHolder{
        private TextView TvNotrans;
        private TextView TvTgl;
        private ImageView ImgPdf;
        private ImageView ImgEmail;
        private ImageView ImgEdit;
    }

    private void createandDisplayPdf2(LaporanHeaderModel header) {
        Document doc = new Document();
        try {
            File sd = Environment.getExternalStorageDirectory();
            Utils.writeToDocSDFile( header.getNoTrans()+".pdf");
            String backupDBPath = "Diagnosa/Document/"+header.getNoTrans()+".pdf";
            final File file = new File(sd, backupDBPath);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            writer.setPageEvent(new PdfPageEventHelper(){
                public void onEndPage(PdfWriter writer, Document document){
                    try{
                        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Created by "+ userid), 80, 30, 0);
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
                if(!header.getPathGbr1().trim().equals("")){
                    String[] a = header.getPathGbr1().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()){
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr1());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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
            if(header.getPathGbr2()!=null ){
                if(!header.getPathGbr2().trim().equals("")){
                    String[] a = header.getPathGbr2().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()) {
                        pFoto1.setTabSettings(new TabSettings(20f));
                        pFoto1.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr2());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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
            if(header.getPathGbr3()!=null ){
                if(!header.getPathGbr3().trim().equals("")){
                    String[] a = header.getPathGbr3().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()) {
                        pFoto1.setTabSettings(new TabSettings(20f));
                        pFoto1.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr3());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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
                    String[] a = header.getPathGbr4().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()) {
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr4());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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
            if(header.getPathGbr5()!=null ){
                if(!header.getPathGbr5().trim().equals("")){
                    String[] a = header.getPathGbr5().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()) {
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr5());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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
            if(header.getPathGbr6()!=null){
                if(!header.getPathGbr6().trim().equals("")){
                    String[] a = header.getPathGbr6().split("/");
                    File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/" + "Diagnosa/Image" + "/" + a[a.length-1]);
                    if(pdfFile.exists()) {
                        pFoto2.setTabSettings(new TabSettings(20f));
                        pFoto2.add(Chunk.TABBING);
                        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                                ".com.example.project.diagnosa.provider", pdfFile);
                        //Uri fileUri=Uri.parse(header.getPathGbr6());
                        InputStream ims = getContext().getContentResolver().openInputStream(path);
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

            Drawable d = getContext().getResources().getDrawable(R.drawable.icon_pdf);
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
        }
        finally {
            doc.close();
        }
        viewPdf(header.getNoTrans()+".pdf", "Diagnosa/Document");
    }

    private void viewPdf(String file, String directory) {
        /*File file = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            mParam1.getEmlak_id()+".pdf");
        Uri pdfUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.onur.emlakdosyasi.provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);*/

        File pdfFile = new File(Environment.getExternalStorageDirectory() +
                "/" + directory + "/" + file);
        Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() +
                ".com.example.project.diagnosa.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            //PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            getContext().startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }
}
