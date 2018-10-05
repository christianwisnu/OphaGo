package com.example.project.ophago;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by christian on 19/04/18.
 */

public class SendEmailActivity extends AppCompatActivity {

    private PrefUtil prefUtil;
    private SharedPreferences shared;
    private Uri fileUri;
    private String noTrans, userId, userName;
    private File pdfFile;

    @BindView(R.id.et_to)
    EditText et_email;
    @BindView(R.id.et_subject)
    EditText et_subject;
    @BindView(R.id.et_message)
    EditText et_message;
    @BindView(R.id.bt_send)
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_email);
        ButterKnife.bind(this);
        Intent i = getIntent();
        noTrans =  i.getStringExtra("noTrans");

        /*String uriDoc = "file:///storage/emulated/0/OphaGo/Document/"+noTrans+".pdf";
        fileUri = Uri.parse(uriDoc);
        File f = new File(fileUri.toString());*/

        pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "OphaGo/Document" + "/" + noTrans+".pdf");
        fileUri = FileProvider.getUriForFile(SendEmailActivity.this, getApplicationContext().getPackageName() +
                ".com.example.project.ophago.provider", pdfFile);

        prefUtil = new PrefUtil(this);
        try{
            shared  = prefUtil.getUserInfo();
            userId  = shared.getString(PrefUtil.ID, null);
            userName = shared.getString(PrefUtil.NAME, null);
        }catch (Exception e){e.getMessage();}
        //if(pdfFile.exists() && !pdfFile.isDirectory()) {
        if(!pdfFile.exists()){
            Toast.makeText(SendEmailActivity.this,"Attachment PDF tidak ada!", Toast.LENGTH_LONG).show();
            et_email.setEnabled(false);
            et_email.setText(null);
            et_message.setEnabled(false);
            et_message.setText(null);
            et_subject.setEnabled(false);
            et_subject.setText(null);
            btnSend.setVisibility(View.INVISIBLE);
        }else{
            et_subject.setText(noTrans);
            et_message.setText("Data Riwayat Pasien \n\n\n\nRegards "+userName);
            et_email.setEnabled(true);
            et_message.setEnabled(true);
            et_subject.setEnabled(true);
            btnSend.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.bt_send)
    protected void send(){
        if(validateTujuanEmail() && validateFileUri()){
            try{
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { et_email.getText().toString() });
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, et_subject.getText().toString());
                if (fileUri != null) {
                    emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                }
                emailIntent.putExtra(Intent.EXTRA_TEXT, et_message.getText().toString());
                this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
            }
            catch (Throwable t){
                Toast.makeText(this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validateTujuanEmail() {
        boolean value;
        if (et_email.getText().toString().isEmpty()) {
            value = false;
            requestFocus(et_email);
            Toast.makeText(SendEmailActivity.this,"Email tujuan harus diisi!", Toast.LENGTH_LONG).show();
        } else {
            value = true;
        }
        return value;
    }

    private boolean validateFileUri() {
        boolean value;
        if (!pdfFile.exists()) {
            value = false;
            Toast.makeText(SendEmailActivity.this,"File PDF belum dilampirkan!", Toast.LENGTH_LONG).show();
        } else {
            value = true;
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
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
/*[14:29, 8/19/2018] Christian: Hapus list anatomi jika record ada 1, array error (FIX)
[20:00, 8/19/2018] Christian: Icon dokter gnti segitiga  (FIX)
[20:00, 8/19/2018] Christian: Pemeriksaan baru - pemeriksaan (FIX)
[20:00, 8/19/2018] Christian: Riwayat pasien - user control (FIX)
[20:01, 8/19/2018] Christian: Tulisan endoskoptri di icon, hapus (FIX)
[20:11, 8/19/2018] Christian: Lap detail desc tgl (FIX)
[20:11, 8/19/2018] Christian: Gnti gmbar anatomi (PENDING)
[20:12, 8/19/2018] Christian: Pilih bagian pemeriksaan - pilih pemeriksaan (FIX)
[20:20, 8/19/2018] Christian: List laporan margin dperbesar (FIX)
[20:20, 8/19/2018] Christian: List lap 1 kasih button pencil (GAK BISA DITERAPKAN)
[20:20, 8/19/2018] Christian: Icon record camera ganti (PENDING)
[20:21, 8/19/2018] Christian: Icon di recording rncanax dganti (PENDING)
[20:22, 8/19/2018] Christian: Kesimpulan save lgsg cetak (FIX)
[20:29, 8/19/2018] Christian: Master pasien, tombol simpan dhilangkan (FIX)
[20:29, 8/19/2018] Christian: Pemeriksaan baru, muncul dialog box pasien baru/lama (FIX)
[20:32, 8/19/2018] Christian: Pdf dkasih stamp logo ensdoskopi (FIX)
[20:33, 8/19/2018] Christian: Tulisan kesimpulan dll di semua pdf, dbold dan dberi margin (FIX)
centang pilih anatomi disesuaikan dgn urutan centangnya (FIX)
edit profile di laporan. apakah dmunculkan tombol save, lalu simpan dan lanjutkan dhilangkan?

coba AHHH
*/