package com.dantsu.thermalprinter;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isLocationPermissonGranted = false;
    private boolean isCameraPermissonGranted = false;
    private boolean isContactPermissonGranted = false;
    private boolean isMediaPermisson = false;


    private void requestPermisson() {
        isLocationPermissonGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        isCameraPermissonGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
        isContactPermissonGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED;
        isMediaPermisson = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
        List<String> permissionrequest = new ArrayList<String>();
        if (!isLocationPermissonGranted) {
            permissionrequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!isCameraPermissonGranted) {
            permissionrequest.add(Manifest.permission.CAMERA);
        }
        if (!isContactPermissonGranted) {
            permissionrequest.add(Manifest.permission.READ_CONTACTS);
        }
        if (!isMediaPermisson) {
            permissionrequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionrequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionrequest.toArray(new String[0]));
        }
    }
    String SHARED_PREF="sharedprefer";
    String SETTING="setting";
    String TEXT="text";
    String url ;
    WebView webView;
    StringBuffer buffer;
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            webView  = findViewById(R.id.web);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        context=getApplicationContext();
        url = "https://hmsdev.fy5restaurantsoftware.com/#/login";
//        getDetail();
        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.CAMERA) != null) {
                    isLocationPermissonGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isCameraPermissonGranted  = result.get(Manifest.permission.CAMERA);
                }
                if (result.get(Manifest.permission.READ_CONTACTS) != null) {
                    isContactPermissonGranted= result.get(Manifest.permission.READ_CONTACTS);
                }
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isMediaPermisson  = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }

        });
        requestPermisson();
        buffer =  new StringBuffer(url);
     SharedPreferences mSharedPreference=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
//        SharedPreferences mSharedPreferencee=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
//        int invy=mSharedPreferencee.getInt(SETTING, Integer.parseInt("0"));
        String value=mSharedPreference.getString(TEXT,"No Value");
//        log.d("",value);
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
//        webView.addJavascriptInterface(new WebAppInterface(this), "jsinterface");

        if(value =="No Value"){
            Toast.makeText(context, value, Toast.LENGTH_LONG).show();
            webView.loadUrl(url);
        }
        else{
            Toast.makeText(context, value, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,buffer.toString(), Toast.LENGTH_LONG).show();
            webView.loadUrl(buffer.toString());
        }
//    System.out.println(buffer.toString());




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


//        return responce;
        return url;
    }
    catch(Exception e){
        System.out.println(e);
        return  "";
    }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getDetail(){
        try {
//            FileInputStream fis = context.openFileInput("userDetail.json");
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader bufferedReaders = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String linee;
//            while ((linee = bufferedReaders.readLine()) != null) {
//                sb.append(linee);
//            }
//            File file;
//            file = new File("/userDetail.json");
//            final File f = new File(MainActivity.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            String dirPath = context.getFilesDir().getAbsolutePath();
            String filePath = dirPath + "/userDetail.json";
            File audioFile = new File(filePath);

            audioFile.getParentFile().mkdirs();

            FileInputStream fis = new FileInputStream(audioFile);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilderss = new StringBuilder();
            BufferedReader reader = new BufferedReader(inputStreamReader);
                String lineee = reader.readLine();
                while (lineee != null) {
                    stringBuilderss.append(lineee).append('\n');
                    lineee = reader.readLine();
                }


            File file = context.getFileStreamPath("userDetail.json");
//            File file = new File( "files/userDetail.json");
            File stream =new File(context.getFilesDir().getAbsoluteFile(),"/userDetail.json");
            System.out.println(context.getFilesDir().getAbsoluteFile());
            System.out.println(stream);
            FileReader fileReader = new FileReader(file.getAbsoluteFile());

               BufferedReader bufferedReader = new BufferedReader(fileReader);
               StringBuilder stringBuilder= new StringBuilder();
                String line = bufferedReader.readLine();
                JSONObject userd = new JSONObject(line);
                int id=userd.getInt("id");
                String name=userd.getString("name");
                String username=userd.getString("username");
                String token=userd.getString("token");
                if (id == 1){
                    webView.loadUrl(url);
                }
                else{
                    int usertype= userd.getInt("userType");
                    int adminid= userd.getInt("adminId");
                    StringBuffer buffer=new StringBuffer(url);
                    buffer.append("?id="+URLEncoder.encode(String.valueOf(id)));
                    buffer.append("&name="+URLEncoder.encode(String.valueOf(name)));
                    buffer.append("&username="+URLEncoder.encode(String.valueOf(username)));
                    buffer.append("&token="+URLEncoder.encode(String.valueOf(token)));
                    buffer.append("&usertype="+URLEncoder.encode(String.valueOf(usertype)));
                    buffer.append("&adminid="+URLEncoder.encode(String.valueOf(adminid)));
                    webView.loadUrl(buffer.toString());
                }


        }
        catch (Exception e){
//            webView.loadUrl(url);
             e.printStackTrace();
        }
        webView.setWebViewClient(new MyWebViewClient());
    }
}
