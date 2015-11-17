package com.dc.lockphone.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dc.lockphone.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by dcoellar on 10/13/15.
 */
public class ContratoActivity extends AppCompatActivity {

    private String id;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contrato);

        Bundle b = getIntent().getExtras();
        id = b.getString("id");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DeviceInsurance");
        query.whereEqualTo("objectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {

                    String LinkTo = object.getParseFile("contrato").getUrl();

                    WebView webView = (WebView) findViewById(R.id.contrato_webview);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + LinkTo);
                    webView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView view, String url) {
                            findViewById(R.id.contrato_progressbar_container).setVisibility(View.GONE);
                        }

                    });
                } else {
                    Log.e("ERROR", "could not get aseguradora");
                    if (e == null) {
                        Log.e("ERROR", e.getMessage());
                    }
                    //TODO - inform user of issues with parse
                }
            }
        });

    }

}
