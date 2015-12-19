package com.richardshea.locationweather;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

/**
 * Created by richardshea on 11/8/15.
 */
public class DisplayWeatherActivity extends Activity{

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String weather = getIntent().getExtras().getString(NameTag.WEATHER);

        setContentView(R.layout.view_weather);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        String customHtml = "<html><body>" + weather + "</body></html>";
        webView.loadData(customHtml, "text/html", "UTF-8");
    }
}
