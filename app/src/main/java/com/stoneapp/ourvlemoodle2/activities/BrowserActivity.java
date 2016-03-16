/*
 * Copyright 2016 Matthew Stone and Romario Maxwell.
 *
 * This file is part of OurVLE.
 *
 * OurVLE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OurVLE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OurVLE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.stoneapp.ourvlemoodle2.activities;

import com.stoneapp.ourvlemoodle2.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JsResult;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressWarnings("FieldCanBeLocal")
public class BrowserActivity extends AppCompatActivity {
    private String url;
    private WebView wview;
    private ProgressBar progressBar;
    private ActionBar abar;
    private Toolbar toolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbrowser);

        wview = (WebView) findViewById(R.id.webview);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);

        // Warn the user of the white screen problem
        Toast.makeText(this, "A white screen might appear for a while\n \t\t\t\t\t\t\tWait until it loads", Toast.LENGTH_SHORT).show();

        try {
            url = getIntent().getExtras().getString("url");
        } catch(Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        abar = getSupportActionBar();
        if (abar != null) {
            abar.setDisplayHomeAsUpEnabled(true);
            abar.setTitle(R.string.app_name);
        }

        wview.getSettings().setJavaScriptEnabled(true);
        wview.getSettings().setDomStorageEnabled(true);

        wview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message , JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress * 100);
                // Return the app name after finish loading
                if (progress == 100)
                    progressBar.setVisibility(View.GONE);

             }

        });

        wview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {}
        });

        wview.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
