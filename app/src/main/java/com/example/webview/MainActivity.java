package com.example.webview;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final int FILE_CHOOSER_RESULT_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final String asw_fcm_channel="Notice";
    private static final boolean ASWP_OFFLINE = true;
    NotificationManager asw_notification;
    Notification asw_notification_new;

    private ValueCallback<Uri[]> filePathCallback;
    private String currentPhotoPath;
    public WebView webView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout homeBtn, menuBtn, settingsBtn, noticeBtn;
    private String fcm_token;
    public static String accessKey = "";



    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Fetch Access Token asynchronously




        homeBtn = findViewById(R.id.HomeBox);
        menuBtn = findViewById(R.id.MenuBox);
        settingsBtn = findViewById(R.id.SettingBox);
        noticeBtn = findViewById(R.id.NoticeBox);

        webView = findViewById(R.id.web_view_contribute);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, please wait...");







        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        homeBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                webView.loadUrl ( "http://edschool.in/cbsps/admin-dashboard.php" );
                homeBtn.setBackgroundColor ( Color.GRAY );
                menuBtn.setBackgroundColor ( Color.WHITE );
                settingsBtn.setBackgroundColor ( Color.WHITE );
                noticeBtn.setBackgroundColor ( Color.WHITE );
            }
        } );

        menuBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                webView.loadUrl ( "http://edschool.in/cbsps/student/student_registration.php" );
                menuBtn.setBackgroundColor ( Color.GRAY );
                homeBtn.setBackgroundColor ( Color.WHITE );
                settingsBtn.setBackgroundColor ( Color.WHITE );
                noticeBtn.setBackgroundColor ( Color.WHITE );
            }
        } );

        settingsBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                webView.loadUrl ( "http://edschool.in/cbsps/settings/branch_create.php" );
                settingsBtn.setBackgroundColor ( Color.GRAY );
                menuBtn.setBackgroundColor ( Color.WHITE );
                homeBtn.setBackgroundColor ( Color.WHITE );
                noticeBtn.setBackgroundColor ( Color.WHITE );
            }
        } );

        noticeBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (MainActivity.this,NoticeWindow.class) );
                noticeBtn.setBackgroundColor ( Color.GRAY );
                settingsBtn.setBackgroundColor ( Color.WHITE );
                menuBtn.setBackgroundColor ( Color.WHITE );
                homeBtn.setBackgroundColor ( Color.WHITE );
            }
        } );








        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(MainActivity.this, "Error: " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                MainActivity.this.filePathCallback = filePathCallback;

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);

                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                           openCamera ();
                    }
                }else{
                    openCamera ();
                }
                return true;
            }
        });

        webView.loadUrl("http://edschool.in/cbsps");


    }




    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (ContextCompat.checkSelfPermission ( MainActivity.this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission ( MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE );
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_RESULT_CODE && filePathCallback != null) {
            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    // If there's no data, return the file URI
                    File file = new File(currentPhotoPath);
                    Uri uri = Uri.fromFile(file);
                    results = new Uri[]{uri};
                } else {
                    // Handle the URI from the gallery
                    Uri resultUri = data.getData();
                    results = resultUri != null ? new Uri[]{resultUri} : null;
                }
            }

            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    public void openCamera(){
        Intent captureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select or capture a photo");
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.webview.fileprovider", photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
        }

        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);

    }

}

