package com.patrick.instagram;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

public class MainActivity extends Activity {

private WebView webView;
private ValueCallback<Uri[]> fileCallback;

private View fullscreenView;
private WebChromeClient.CustomViewCallback fullscreenCallback;

private static final int FILE_REQUEST = 1001;

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
    settings.setMediaPlaybackRequiresUserGesture(false);

    CookieManager cookies = CookieManager.getInstance();
    cookies.setAcceptCookie(true);
    cookies.setAcceptThirdPartyCookies(webView, true);

    webView.setWebViewClient(new WebViewClient());

    webView.setWebChromeClient(new WebChromeClient() {

        @Override
        public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params) {

            if (fileCallback != null) {
                fileCallback.onReceiveValue(null);
            }

            fileCallback = callback;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_MIME_TYPES,
                    new String[]{
                            "image/*",
                            "video/*"
                    });

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            Intent chooser = Intent.createChooser(
                    intent,
                    "انتخاب عکس یا ویدیو"
            );

            startActivityForResult(chooser, FILE_REQUEST);

            return true;
        }

        @Override
        public void onShowCustomView(
                View view,
                CustomViewCallback callback) {

            if (fullscreenView != null) {
                callback.onCustomViewHidden();
                return;
            }

            fullscreenView = view;
            fullscreenCallback = callback;

            ViewGroup decor = (ViewGroup)
                    getWindow().getDecorView();

            decor.addView(fullscreenView,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        @Override
        public void onHideCustomView() {
            hideFullscreen();
        }
    });

    webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    setContentView(webView);

    if (savedInstanceState == null) {
        webView.loadUrl("https://www.instagram.com/");
    } else {
        webView.restoreState(savedInstanceState);
    }
}

private void hideFullscreen() {

    if (fullscreenView == null) return;

    ViewGroup decor = (ViewGroup)
            getWindow().getDecorView();

    decor.removeView(fullscreenView);

    fullscreenView = null;

    getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
    );

    if (fullscreenCallback != null) {
        fullscreenCallback.onCustomViewHidden();
        fullscreenCallback = null;
    }
}

@Override
protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == FILE_REQUEST && fileCallback != null) {

        Uri[] results = null;

        if (resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {

                int count = data.getClipData().getItemCount();

                results = new Uri[count];

                for (int i = 0; i < count; i++) {
                    results[i] = data.getClipData()
                            .getItemAt(i).getUri();
                }

            } else if (data.getData() != null) {

                results = new Uri[]{
                        data.getData()
                };
            }
        }

        fileCallback.onReceiveValue(results);
        fileCallback = null;
    }
}

@Override
public void onBackPressed() {

    if (fullscreenView != null) {
        hideFullscreen();
    } else if (webView != null && webView.canGoBack()) {
        webView.goBack();
    } else {
        super.onBackPressed();
    }
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (webView != null) {
        webView.saveState(outState);
    }
}

@Override
protected void onDestroy() {

    if (fileCallback != null) {
        fileCallback.onReceiveValue(null);
        fileCallback = null;
    }

    if (webView != null) {
        webView.destroy();
    }

    super.onDestroy();
}

}
