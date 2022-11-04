package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.dantsu.thermalprinter.async.AsyncBluetoothEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;
import com.dantsu.thermalprinter.async.AsyncTcpEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncUsbEscPosPrint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String SHARED_PREF="sharedprefer";
    String TEXT="text";
    String url ;

    StringBuffer buffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView  = findViewById(R.id.web);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        url = GetURL();
        buffer =  new StringBuffer(url);
     SharedPreferences mSharedPreference=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        String value=mSharedPreference.getString(TEXT,"No Value");
        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Username", TEXT);
        editor.putString("Password", TEXT);
        editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2880));
        editor.apply();
        if (sharedpreferences.getLong("ExpiredDate", -1) > System.currentTimeMillis()) {
            // read email and password
        } else {
            editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
        }

        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
//        webView.addJavascriptInterface(new WebAppInterface(this), "jsinterface");

        if(value =="No Value"){
            webView.loadUrl(url);
        }
        else{
            String name = null,username = null,token = null;
            int usertype = 0,id = 0,adminid = 0;
            try {
                JSONObject jj = new JSONObject(String.valueOf(value));
                id=jj.getInt("id");
                name=jj.getString("name");
                username=jj.getString("username");
                token=jj.getString("token");
                usertype= jj.getInt("userType");
                adminid= jj.getInt("adminId");
            }
           catch (JSONException e) {
                e.printStackTrace();
            }

            StringBuffer buffer=new StringBuffer(url);
            buffer.append("?id="+URLEncoder.encode(String.valueOf(id)));
            buffer.append("&name="+URLEncoder.encode(String.valueOf(name)));
            buffer.append("&username="+URLEncoder.encode(String.valueOf(username)));
            buffer.append("&token="+URLEncoder.encode(String.valueOf(token)));
            buffer.append("&usertype="+URLEncoder.encode(String.valueOf(usertype)));
            buffer.append("&adminid="+URLEncoder.encode(String.valueOf(adminid)));
            webView.loadUrl(buffer.toString());
        }
    System.out.println(buffer.toString());

        webView.setWebViewClient(new MyWebViewClient());


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

    }

    private String GetURL(){
    try {
         InputStream stream = getAssets().open("appSetting.json");
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        String tContents = new String(buffer);
        System.out.println(tContents);
        JSONObject obj = new JSONObject(tContents);
        String url = obj.getString("URL");
        return url;
    }
    catch(Exception e){
        System.out.println(e);
        return  "";
    }
    }


}
