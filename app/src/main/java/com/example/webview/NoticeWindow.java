package com.example.webview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class NoticeWindow extends AppCompatActivity {
    EditText notificationHead,notificationBody;
    Button publish;
    OkHttpClient okHttpClient=new OkHttpClient ();
    private static final String BASE_URL="https://192.168.31.126/DatabaseConnection/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_notice_window );
        notificationHead=findViewById ( R.id.NoticeHeading );
        notificationBody=findViewById ( R.id.NoticeBody );
        publish=findViewById ( R.id.publish );
        

        publish.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String nHead=notificationHead.getText ().toString ();
                String nBody=notificationBody.getText ().toString ();

                RequestBody noticeData = new FormBody.Builder ().add ( "head",nHead ).add ( "body",nBody ).build ();

                okhttp3.Request request=new okhttp3.Request.Builder ().url ( BASE_URL ).post ( noticeData ).build ();

                okhttp3.Call call = okHttpClient.newCall ( request );

                call.enqueue ( new okhttp3.Callback () {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                        NoticeWindow.this.runOnUiThread ( new Runnable () {
                            @Override
                            public void run() {
                                notificationHead.setText ( "" );
                                notificationBody.setText ( "" );
                                System.out.println ("Error : "+e.getMessage ());
                            }
                        } );

                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                        NoticeWindow.this.runOnUiThread ( new Runnable () {
                            @Override
                            public void run() {
                                try {
                                    String resp=response.body ().toString ();
                                    if(response.isSuccessful ()){
                                        notificationHead.setText ( "" );
                                        notificationBody.setText ( "" );
                                        System.out.println ("Connected Successfully ");

                                    }
                                } catch (Exception e) {
                                    System.out.println ("Error : "+e.getMessage ());
                                }

                            }
                        } );

                    }
                } );
            }
        } );
    }


}