package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import others.Constants;

public class Paystack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paystack);

        ProgressDialog progDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progDialog.setCancelable(false);

        Intent intent = getIntent();

        if(intent.getStringExtra("auth") == null) {
            finish();  // close activity
            return;
        }

        String award = "0";

        if(intent.getStringExtra("award").equalsIgnoreCase("1")) {
            award = "1";
        }

        String url = Constants.BASE_URL + "paystack/auth/" + intent.getStringExtra("auth") + "/" +award;

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDialog.show();
                if (url.contains("http://exitme")) {
                    finish();  // close activity
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progDialog.dismiss();
            }
        });

        webView.loadUrl(url);
    }


    public void goBack(View view) {
        finish();
    }

}
