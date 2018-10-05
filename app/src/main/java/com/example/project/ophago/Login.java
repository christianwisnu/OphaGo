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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

public class Login extends AppCompatActivity {

    private ProgressDialog pDialog;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BaseApiService mApiService;
    private PrefUtil prefUtil;

    @BindView(R.id.bLoginLogin)Button btnLogin;
    @BindView(R.id.btnLoginClearUser)Button btnClearUser;
    @BindView(R.id.input_layout_login_userid)TextInputLayout inputLayoutUser;
    @BindView(R.id.input_layout_login_sandi)TextInputLayout inputLayoutPasw;
    @BindView(R.id.eLoginUserID)EditText eUserID;
    @BindView(R.id.eLoginSandi)EditText ePassword;
    @BindView(R.id.txtLoginSignUp)TextView txtSignUp;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mApiService         = Link.getAPIService();
        prefUtil = new PrefUtil(this);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }

    @OnTextChanged(value = R.id.eLoginUserID, callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void txtChangePass(){
        btnClearUser.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(value = R.id.eLoginSandi, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangePass(Editable editable){
        validatePasw(editable.length());
    }

    @OnTextChanged(value = R.id.eLoginUserID, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterTxtChangeUser(Editable editable){
        validateUser(eUserID);
    }

    @OnClick(R.id.txtLoginSignUp)
    protected void signUp(){
        startActivityForResult(new Intent(Login.this, Register.class),3);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.btnLoginClearUser)
    protected void clearUser(){
        eUserID.setText("");
        btnClearUser.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.bLoginLogin)
    protected void loginOther(){
        if(validateUser(eUserID) && validatePasw(ePassword.length())){
            requestLogin(eUserID.getText().toString(), ePassword.getText().toString());
        }
    }

    private void requestLogin(String user, String pasw){;
        pDialog.setMessage("Login ...");
        showDialog();
        mApiService.loginRequest(user, pasw)
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
                                    Toast.makeText(Login.this, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity2.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else {
                                    hideDialog();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(Login.this, error_message, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(Login.this, "GAGAL LOGIN", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(Login.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateUser(EditText edittext) {
        boolean value;
        if (eUserID.getText().toString().isEmpty()){
            value=false;
            requestFocus(eUserID);
            inputLayoutUser.setError(getString(R.string.err_msg_user));
        } else{
            value=true;
            inputLayoutUser.setError(null);
        }
        return value;
    }

    private boolean validatePasw(int length) {
        boolean value = true;
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
