package com.example.project.ophago;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

/**
 * Created by Chris on 08/04/2018.
 */

public class Register extends AppCompatActivity {

    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private PrefUtil prefUtil;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BindView(R.id.bRegisterDaftar)Button btnRegister;
    @BindView(R.id.btnRegisterClearUserID)Button btnClearUserId;
    @BindView(R.id.input_layout_register_userid)TextInputLayout inputLayoutUserId;
    @BindView(R.id.input_layout_register_sandi)TextInputLayout inputLayoutPasw;
    @BindView(R.id.input_layout_register_sandi2)TextInputLayout inputLayoutPasw2;
    @BindView(R.id.input_layout_register_nama)TextInputLayout inputLayoutNama;
    @BindView(R.id.eRegisterUserId)EditText eUserId;
    @BindView(R.id.eRegisterSandi)EditText ePassword;
    @BindView(R.id.eRegisterSandi2)EditText ePassword2;
    @BindView(R.id.eRegisterNama)EditText eNama;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ButterKnife.bind(this);
        mApiService         = Link.getAPIService();
        prefUtil            = new PrefUtil(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }

    @OnClick(R.id.btnRegisterClearUserID)
    protected void regClearEmail(){
        eUserId.setText("");
        btnClearUserId.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.bRegisterDaftar)
    protected void register(){
        if(validateUserId(eUserId) && validateNama(eNama.length()) &&
                validatePasw(ePassword.length()) && validatePasw2(ePassword2.length())){
            requestRegister(eNama.getText().toString(), ePassword.getText().toString(), eUserId.getText().toString());
        }
    }

    @OnTextChanged(value = R.id.eRegisterUserId, callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void txtChangePass(){
        btnClearUserId.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(value = R.id.eRegisterUserId, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeEmail(Editable editable){
        validateUserId(eUserId);
    }

    @OnTextChanged(value = R.id.eRegisterSandi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeSandi(Editable editable){
        validatePasw(editable.length());
    }

    @OnTextChanged(value = R.id.eRegisterSandi2, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeSandi2(Editable editable){
        validatePasw2(editable.length());
    }

    @OnTextChanged(value = R.id.eRegisterNama, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeNama(Editable editable){
        validateNama(editable.length());
    }

    private void requestRegister(String nama, String pasw, String id){
        pDialog.setMessage("Registering ...");
        showDialog();
        String device = Utils.getDeviceName();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.registerRequest(id, pasw, nama, tanggalNow, device)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    String nama = jsonRESULTS.getJSONObject("user").getString("vc_user")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("vc_user");
                                    String uId = jsonRESULTS.getJSONObject("user").getString("c_userid")==null?"":
                                            jsonRESULTS.getJSONObject("user").getString("c_userid");

                                    hideDialog();
                                    prefUtil.saveUserInfo(nama, uId);
                                    Toast.makeText(Register.this, "BERHASIL REGISTRASI", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Register.this, MainActivity2.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(Register.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            } catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            hideDialog();
                            Toast.makeText(Register.this, "REGISTRASI GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(Register.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateUserId(EditText edittext) {
        boolean value;
        if (eUserId.getText().toString().isEmpty()){
            value=false;
            requestFocus(eUserId);
            inputLayoutUserId.setError(getString(R.string.err_msg_user));
        } else if (edittext.length() > inputLayoutUserId.getCounterMaxLength()) {
            value=false;
            inputLayoutUserId.setError("Max character User ID length is " + inputLayoutUserId.getCounterMaxLength());
        }else {
            value=true;
            inputLayoutUserId.setError(null);
        }
        return value;
    }

    private boolean validatePasw(int length) {
        boolean value=true;
        int minValue = 6;
        if (ePassword.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(ePassword);
            inputLayoutPasw.setError(getString(R.string.err_msg_sandi));
        } else if (length > inputLayoutPasw.getCounterMaxLength()) {
            value=false;
            inputLayoutPasw.setError("Max character password length is " + inputLayoutPasw.getCounterMaxLength());
        } else if (length < minValue) {
            value=false;
            inputLayoutPasw.setError("Min character password length is 6" );
        } else{
            value=true;
            inputLayoutPasw.setError(null);}
        return value;
    }

    private boolean validatePasw2(int length) {
        boolean value=true;
        int minValue = 6;
        if (ePassword2.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(ePassword2);
            inputLayoutPasw2.setError(getString(R.string.err_msg_sandi));
        } else if (length > inputLayoutPasw2.getCounterMaxLength()) {
            value=false;
            inputLayoutPasw2.setError("Max character password length is " + inputLayoutPasw2.getCounterMaxLength());
        } else if (length < minValue) {
            value=false;
            inputLayoutPasw2.setError("Min character password length is 6" );
        } else if(!ePassword2.getText().toString().equals(ePassword.getText().toString())){
            value=false;
            inputLayoutPasw2.setError("Confirm Password is wrong");
        }else{
            value=true;
            inputLayoutPasw2.setError(null);
        }
        return value;
    }

    private boolean validateNama(int length) {
        boolean value=true;
        if (eNama.getText().toString().trim().isEmpty()) {
            value=false;
            requestFocus(eNama);
            inputLayoutNama.setError(getString(R.string.err_msg_nama));
        } else if (length > inputLayoutNama.getCounterMaxLength()) {
            value=false;
            inputLayoutNama.setError("Max character name length is " + inputLayoutNama.getCounterMaxLength());
        }else {
            value=true;
            inputLayoutNama.setError(null);
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