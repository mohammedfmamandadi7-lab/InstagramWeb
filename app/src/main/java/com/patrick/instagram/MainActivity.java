package com.patrick.instagram;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

private WebView webView;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setStatusBarColor(0xFF000000);
    getWindow().setNavigationBarColor(0xFF000000);

    webView = new WebView(this);

    WebSettings settings = webView.getSettings();

    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setDatabaseEnabled(true);
    settings.setLoadWithOverviewMode(true);
    settings.setUseWideViewPort(true);
    settings.setSupportZoom(false);
    settings.setMediaPlaybackRequiresUserGesture(true);

    webView.setWebViewClient(new WebViewClient());
    webView.setWebChromeClient(new WebChromeClient());

    webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    setContentView(webView);

    webView.loadUrl("https://www.instagram.com/");
}

@Override
public void onBackPressed() {
    if (webView != null && webView.canGoBack()) {
        webView.goBack();
    } else {
        super.onBackPressed();
    }
}

@Override
protected void onDestroy() {
    if (webView != null) {
        webView.destroy();
    }
    super.onDestroy();
}

}
