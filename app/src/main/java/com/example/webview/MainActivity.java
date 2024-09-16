package com.example.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_CHOOSER_RESULT_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private FrameLayout frameLayout;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";
    private ValueCallback<Uri[]> filePathCallback;
    private String currentPhotoPath;
    public static WebView webView;
    public static  ProgressDialog progressDialog;
    private  SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout homeBtn, menuBtn, settingsBtn, noticeBtn,bottom_navigation;
    private boolean isBottomNavVisible=true;
    private float screenHeight;



    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle webViewBundle = new Bundle();
        webView = findViewById(R.id.webView);
        webView.saveState(webViewBundle);
        homeBtn = findViewById(R.id.HomeButton);
        menuBtn = findViewById(R.id.menuButton);
        settingsBtn = findViewById(R.id.SettingsButton);
        noticeBtn = findViewById(R.id.NoticeButton);
        bottom_navigation=findViewById ( R.id.bottom_navigation );
        frameLayout=findViewById ( R.id.frameLayout );

        FirebaseApp.initializeApp(getApplicationContext ());



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, please wait...");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        CookieManager.getInstance().flush();

        bottom_navigation.setVisibility ( View.GONE );

        // Accept cookie

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);


        // Manage Cache
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);


        // Save cookie in the shared prefference

//        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");
//        String sessionData=new WebAppInterface (getApplicationContext () ).getSessionData();
//        Log.d ( "Session Data ",sessionData.toString() );


        // Calculate swipe region height





        // Hide bottom navigation bar on Scroll
        webView.setOnScrollChangeListener ( (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY>oldScrollY){
                hideBottomNavigation();
            }else if(scrollY<oldScrollY){
                showBottomNavigationBar();
            }

        } );

        // Get screen height to calculate the top 25% threshold
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Disable SwipeRefreshLayout initially
        swipeRefreshLayout.setEnabled(false);

        // Set a touch listener to enable swipe refresh only in the top 25% of the screen
        webView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float touchY = event.getY();
                    // Check if the touch is within the top 25% of the screen
                    if (touchY < screenHeight * 0.25) {
                        swipeRefreshLayout.setEnabled(true);  // Enable swipe to refresh
                    } else {
                        swipeRefreshLayout.setEnabled(false); // Disable swipe to refresh
                    }
                }
                return false; // Allow other touch events to proceed
            }

            @NonNull
            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone ();
            }

        });

        // Set the refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            webView.reload(); // Refresh WebView content
            swipeRefreshLayout.setRefreshing(false); // Stop the refresh indicator
        });

        // Button listeners
        setupButtonListeners();

        // WebViewClient and WebChromeClient
        setupWebViewClient();
        setupWebChromeClient();

        if (isLoggedIn()) {
            // Load the dashboard or main page if the user is logged in
            webView.loadUrl("https://edschool.in/cbsps/admin-dashboard.php");
            CookieManager.getInstance().getCookie("https://edschool.in/cbsps/admin-dashboard.php");
            saveCookies ( "https://edschool.in/cbsps/admin-dashboard.php"  );


        } else {
            // Load the login page if the user is not logged in
            webView.loadUrl("https://edschool.in/demo/login.php");
            CookieManager.getInstance().getCookie("https://edschool.in/demo/login.php");
            saveCookies ( "https://edschool.in/demo/login.php"  );

        }


    }

    private void showBottomNavigationBar() {
        if(!isBottomNavVisible){
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) swipeRefreshLayout.getLayoutParams ();
            layoutParams.weight=10;
            swipeRefreshLayout.setLayoutParams ( layoutParams );
            View view=getWindow ().getDecorView ();
            view.setSystemUiVisibility ( view.SYSTEM_UI_FLAG_VISIBLE );
            bottom_navigation.animate ().translationY ( 0 ).setDuration ( 200 );
            isBottomNavVisible=true;
        }

    }

    private void hideBottomNavigation() {
        if(isBottomNavVisible){
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) swipeRefreshLayout.getLayoutParams ();
            layoutParams.weight=0;
            swipeRefreshLayout.setLayoutParams ( layoutParams );
            View view=getWindow ().getDecorView ();
            view.setSystemUiVisibility ( view.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
            bottom_navigation.animate ().translationY ( bottom_navigation.getHeight () ).setDuration ( 200 );
            isBottomNavVisible=false;
        }
    }

    public void setupButtonListeners() {
        homeBtn.setOnClickListener(v -> {
            frameLayout.setVisibility ( View.GONE );
            webView.setVisibility ( View.VISIBLE );
            webView.loadUrl("http://edschool.in/cbsps/admin-dashboard.php");
            CookieManager.getInstance().getCookie("http://edschool.in/cbsps/admin-dashboard.php");
            updateButtonColors(homeBtn);
        });
        menuBtn.setOnClickListener(v -> {
            frameLayout.setVisibility ( View.GONE );
            webView.setVisibility ( View.VISIBLE );
            webView.loadUrl("https://edschool.in/demo/menu.php");
            CookieManager.getInstance().getCookie("https://edschool.in/demo/menu.php");


            updateButtonColors(menuBtn);
        });
        settingsBtn.setOnClickListener(v -> {
            frameLayout.setVisibility ( View.GONE );
            webView.setVisibility ( View.VISIBLE );
            webView.loadUrl("https://edschool.in/demo/settings/setup.php");
            CookieManager.getInstance().getCookie("https://edschool.in/demo/settings/setup.php");
            updateButtonColors(settingsBtn);
        });
        noticeBtn.setOnClickListener(v -> {
            webView.setVisibility ( View.GONE );
            progressDialog.show ();
            // Simulate loading for 3 seconds
            new Handler ().postDelayed( () -> {
                // Dismiss the progress dialog when loading is done
                progressDialog.dismiss ();
            }, 3000);
            FragmentManager manager= getSupportFragmentManager ();
            FragmentTransaction transaction=manager.beginTransaction ();
            transaction.replace ( R.id.frameLayout,new Broadcast () );
            transaction.addToBackStack ( null );
            transaction.commit ();
            frameLayout.setVisibility ( View.VISIBLE );
            updateButtonColors(noticeBtn);

        });
    }


    public  void updateButtonColors(LinearLayout activeButton) {
        homeBtn.setBackgroundColor(Color.WHITE);
        menuBtn.setBackgroundColor(Color.WHITE);
        settingsBtn.setBackgroundColor(Color.WHITE);
        noticeBtn.setBackgroundColor(Color.WHITE);
        activeButton.setBackgroundColor(getResources ().getColor ( R.color.gray ));
    }

    private void setupWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if(!url.contains ( "login" )){
                        bottom_navigation.setVisibility ( View.VISIBLE );

                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "Error: " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWebChromeClient() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                MainActivity.this.filePathCallback = filePathCallback;
                checkAndRequestPermissions();
                return true;
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_RESULT_CODE && filePathCallback != null) {
            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    File file = new File(currentPhotoPath);
                    Uri uri = Uri.fromFile(file);
                    results = new Uri[]{uri};
                } else {
                    Uri resultUri = data.getData();
                    results = resultUri != null ? new Uri[]{resultUri} : null;
                }
            }

            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> MainActivity.super.onBackPressed())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        saveCookies(webView.getUrl());
    }

//    public class WebAppInterface {
//        @JavascriptInterface
//        public void broadcastNotification(String topic,String title,String body){
//            Log.d ("Toast Message on Click ","Toast Message is shown on button click ");
//            Log.d ("Message Data Received  ","Topic: "+topic+" Title: "+title + " body : "+body);
//            NotificationChannelHelper.createNotificationChannel ( getApplicationContext (),"Push Notification","NewChannel","This is new channel" );
//            FirebaseMessaging.getInstance ().subscribeToTopic ( topic );
//            sendNotification ( topic,title,body );
//        }
//    }

    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getBoolean(IS_LOGGED_IN_KEY, false);  // Default is false (not logged in)
    }

//    private void setLoggedIn(boolean isLoggedIn) {
//        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
//        editor.putBoolean(IS_LOGGED_IN_KEY, isLoggedIn);
//        editor.apply();
//    }

    public void saveCookies(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(url);

        // Save cookies in SharedPreferences (or any persistent storage)
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cookies", cookies);
        editor.apply();
    }

    public class WebAppInterface {
        Context mContext;
        SharedPreferences sharedPreferences;

        // Constructor to initialize the context and SharedPreferences
        WebAppInterface(Context context) {
            mContext = context;
            sharedPreferences = context.getSharedPreferences ( "MySession", Context.MODE_PRIVATE );
        }

        // Method to store session data sent from JavaScript
        @JavascriptInterface
        public void sendSessionData(String sessionData) {
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            editor.putString ( "session_variable", sessionData );
            editor.apply (); // Save session data asynchronously
        }

        // Optional: Method to retrieve session data
        @JavascriptInterface
        public String getSessionData() {
            return sharedPreferences.getString ( "session_variable", null ); // Retrieve session data
        }
    }

    @Override
    protected void onNewIntent(@SuppressLint("UnsafeIntentLaunch") @NonNull Intent intent) {
        super.onNewIntent ( intent );
        if (intent.getAction().equals("BUTTON_CLICK_ACTION")) {
            // Handle the button click event here
            Log.d("Notification", "Button clicked!");
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData( Uri.parse("https://edschool.in/demo/notification.php"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        }
    }
}


