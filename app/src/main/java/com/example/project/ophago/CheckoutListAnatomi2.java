package com.example.project.ophago;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.AppVisibilityListener;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import java.util.ArrayList;
import java.util.List;

import adapter.AdpCheckoutListAnatomi2;
import model.transaksi.TransaksiItemModel;
import model.transaksi.TransaksiModel;
import utilities.Utils;

/**
 * Created by christian on 03/05/18.
 */

public class CheckoutListAnatomi2 extends AppCompatActivity implements SessionManagerListener,
        AppVisibilityListener{
        //,CastStateListener {

    private TransaksiModel model;
    private AdpCheckoutListAnatomi2 adapter;
    private ListView listku;
    private Button btnProses;
    private ImageView imgBack;
    private List<TransaksiItemModel> columnlist= new ArrayList<TransaksiItemModel>();
    private IntroductoryOverlay mIntroductoryOverlay;
    private MenuItem mMediaRouterButton;
    private String formatedIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_list_pasien2);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_checkout));

        Intent i = getIntent();
        model = (TransaksiModel) i.getSerializableExtra("object");
        btnProses = (Button)findViewById(R.id.btn_pasien_checkoutlistpasien_lanjut2);
        imgBack = (ImageView) findViewById(R.id.imgCheckoutBack);
        listku = (ListView)findViewById(R.id.LsvCheckoutListAnatomiku2);
        columnlist=model.getItemList();
        adapter		= new AdpCheckoutListAnatomi2(CheckoutListAnatomi2.this, model, R.layout.col_checkout_list_anatomi2, columnlist);
        listku.setAdapter(adapter);

        /*listku.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(columnlist.get(position).getPathVideo() != null){
                    if( CastContext.getSharedInstance(CheckoutListAnatomi2.this).getSessionManager().getCurrentCastSession() != null
                            && CastContext.getSharedInstance(CheckoutListAnatomi2.this).getSessionManager().getCurrentCastSession().getRemoteMediaClient() != null ) {
                        RemoteMediaClient remoteMediaClient = CastContext.getSharedInstance(CheckoutListAnatomi2.this).getSessionManager().getCurrentCastSession().getRemoteMediaClient();
                        if( remoteMediaClient.getMediaInfo() != null &&
                                remoteMediaClient.getMediaInfo().getMetadata() != null
                            && columnlist.get(position).getAnatomi().equalsIgnoreCase(
                            remoteMediaClient.getMediaInfo().getMetadata().getString(MediaMetadata.KEY_TITLE))) {
                            //startActivity(new Intent(CheckoutListAnatomi2.this, ExpandedControlsActivity.class));
                            startLocalServer();
                            String path = columnlist.get(position).getPathVideo().substring(1,
                                    columnlist.get(position).getPathVideo().length());
                            String[] pathHasil = path.split("/");
                            String localUrl = formatedIpAddress+ pathHasil[3]+"/"+pathHasil[4]+"/"+pathHasil[5];
                            MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                            metadata.putString(MediaMetadata.KEY_TITLE, columnlist.get(position).getAnatomi());
                            metadata.putString(MediaMetadata.KEY_SUBTITLE, "EndoskopiTRI");
                            if(columnlist.get(position).getAnatomi().equals("TELINGA KANAN") ||
                                    columnlist.get(position).getAnatomi().equals("TELINGA KIRI")) {
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.telinga_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.telinga_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("HIDUNG KANAN") ||
                                    columnlist.get(position).getAnatomi().equals("HIDUNG KIRI")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.hidung_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.hidung_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("OROFARING")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.tenggorokan_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.tenggorokan_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("LAROFARING")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.mulut_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.mulut_poster))));
                            }
                            MediaInfo mediaInfo = new MediaInfo.Builder(localUrl)
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setContentType("videos/mp4")
                                    .setMetadata(metadata)
                                    .build();
                            remoteMediaClient.load(mediaInfo, true, 0);
                        } else {
                            startLocalServer();
                            String path = columnlist.get(position).getPathVideo().substring(1,
                                    columnlist.get(position).getPathVideo().length());
                            String[] pathHasil = path.split("/");
                            String localUrl = formatedIpAddress+ pathHasil[3]+"/"+pathHasil[4]+"/"+pathHasil[5];
                            MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                            metadata.putString(MediaMetadata.KEY_TITLE, columnlist.get(position).getAnatomi());
                            metadata.putString(MediaMetadata.KEY_SUBTITLE, "EndoskopiTRI");
                            if(columnlist.get(position).getAnatomi().equals("TELINGA KANAN") ||
                                columnlist.get(position).getAnatomi().equals("TELINGA KIRI")) {
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.telinga_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.telinga_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("HIDUNG KANAN") ||
                                    columnlist.get(position).getAnatomi().equals("HIDUNG KIRI")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.hidung_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.hidung_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("OROFARING")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.tenggorokan_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.tenggorokan_poster))));
                            }else if(columnlist.get(position).getAnatomi().equals("LAROFARING")){
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.mulut_poster))));
                                metadata.addImage(new WebImage(Uri.parse(getString(R.string.mulut_poster))));
                            }
                            MediaInfo mediaInfo = new MediaInfo.Builder(localUrl)
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setContentType("videos/mp4")
                                    .setMetadata(metadata)
                                    .build();
                            remoteMediaClient.load(mediaInfo, true, 0);
                        }
                    } else {
                        //startActivity(new Intent(CheckoutListAnatomi2.this, VideoDetailActivity.class));
                        Toast.makeText(CheckoutListAnatomi2.this, "Transmisikan ke perangkat chromecast terlebih dahulu!", Toast.LENGTH_LONG).show();
                        showIntroductoryOverlay();
                    }
                }else{
                    Toast.makeText(CheckoutListAnatomi2.this, "Video masih kosong!", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CheckoutListAnatomi2.this)
                        .setMessage("Data akan hilang\nBatalkan Pemeriksaan?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                adapter.clear();
                                listku.setAdapter(null);
                                columnlist.clear();
                                adapter.notifyDataSetChanged();
                                model.removeAllItem();
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("object", model);
                                setResult(RESULT_CANCELED, returnIntent);
                                finish();
                            }
                        }).create().show();
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CheckoutListAnatomi2.this)
                        .setMessage("Yakin akan di proses?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(getApplicationContext(), KesimpulanActivity2.class);
                                i.putExtra("status",1);
                                i.putExtra("object",model);
                                startActivity(i);
                            }
                        }).create().show();
            }
        });

        /*CastContext.getSharedInstance(this).addCastStateListener(this);
        CastContext.getSharedInstance(this).addAppVisibilityListener(this);
        CastContext.getSharedInstance(this).getSessionManager().addSessionManagerListener(this);
        startLocalServer();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                model = (TransaksiModel) data.getSerializableExtra("object");
                adapter.clear();
                listku.setAdapter(null);
                columnlist=model.getItemList();
                adapter		= new AdpCheckoutListAnatomi2(CheckoutListAnatomi2.this, model, R.layout.col_checkout_list_anatomi2, columnlist);
                listku.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        new AlertDialog.Builder(this)
                .setMessage("Data akan hilang\nBatalkan Pemeriksaan?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        adapter.clear();
                        listku.setAdapter(null);
                        columnlist.clear();
                        adapter.notifyDataSetChanged();
                        model.removeAllItem();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("object", model);
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                    }
                }).create().show();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        /*CastContext.getSharedInstance(this).removeAppVisibilityListener(this);
        CastContext.getSharedInstance(this).removeCastStateListener(this);
        CastContext.getSharedInstance(this).getSessionManager().removeSessionManagerListener(this);*/
        Utils.trimCache(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        /*getMenuInflater().inflate(R.menu.expanded_controller, menu);
        mMediaRouterButton = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        showIntroductoryOverlay();*/
        return true;
    }

    /*private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mMediaRouterButton != null) && mMediaRouterButton.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            CheckoutListAnatomi2.this, mMediaRouterButton)
                            .setTitleText("Ketuk Untuk Menyambungkan ke Perangkat Chromecast Anda")
                            .setOverlayColor(R.color.colorPrimary)
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }*/

    private void startLocalServer() {
        /*server = new LocalServer();
        try {
            server.start();
        } catch (IOException ioe) {
            Log.d("zz Httpd", "The server could not start.");
        }
        Log.d("zz Httpd", "Web server initialized.");*/
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        Log.d("zz Httpd IP U: ", ipAddress + "");
        String formatedIpAddress1 = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        formatedIpAddress = "http://" + formatedIpAddress1 + ":12345/";
        Log.d("zz Httpd IP: ", "Please access! " + formatedIpAddress);
    }

    @Override
    public void onAppEnteredForeground() {
        Log.e("Tuts+", "onAppEnteredForeground");
    }

    @Override
    public void onAppEnteredBackground() {
        Log.e("Tuts+", "onAppEnteredBackground");
    }

    /*@Override
    public void onCastStateChanged(int newState) {
        Log.e("Tuts+", "onCastStateChanged");
        switch( newState ) {
            case CastState.CONNECTED: {

                break;
            } case CastState.CONNECTING: {

                break;
            } case CastState.NOT_CONNECTED: {

                break;
            } case CastState.NO_DEVICES_AVAILABLE: {

            }
        }

        if (newState != CastState.NO_DEVICES_AVAILABLE) {
            showIntroductoryOverlay();
        }
    }*/

    @Override
    public void onSessionStarting(Session session) {
        Log.e("Tuts+", "onSessionsStarting");
    }

    @Override
    public void onSessionStarted(Session session, String s) {
        Log.e("Tuts+", "onSessionStarted");
        invalidateOptionsMenu();
    }

    @Override
    public void onSessionStartFailed(Session session, int i) {
        Log.e("Tuts+", "onSessionStartFailed");
    }

    @Override
    public void onSessionEnding(Session session) {
        Log.e("Tuts+", "onSessionEnding");
    }

    @Override
    public void onSessionEnded(Session session, int i) {
        Log.e("Tuts+", "onSessionEnded");
    }

    @Override
    public void onSessionResuming(Session session, String s) {
        Log.e("Tuts+", "onSessionResuming");
    }

    @Override
    public void onSessionResumed(Session session, boolean b) {
        Log.e("Tuts+", "onSessionResumed");
        invalidateOptionsMenu();
    }

    @Override
    public void onSessionResumeFailed(Session session, int i) {
        Log.e("Tuts+", "onSessionResumeFailed");
    }

    @Override
    public void onSessionSuspended(Session session, int i) {
        Log.e("Tuts+", "onSessionSuspended");
    }


}
