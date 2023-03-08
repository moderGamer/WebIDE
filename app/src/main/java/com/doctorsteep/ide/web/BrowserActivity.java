package com.doctorsteep.ide.web;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.doctorsteep.ide.web.data.Data;

public class BrowserActivity extends AppCompatActivity {

	private WebView webView;
	private Toolbar toolbar;
	
	private String title = "";
	private String url = "";
	private Handler handlerRun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Data.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		webView = findViewById(R.id.webView);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		title = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("url");
		getSupportActionBar().setTitle(title);
		
		handlerRun = new Handler();
		
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
	}
	@Override
	protected void onStart() {
		handlerRun.postDelayed(new Runnable() {
				@Override public void run() {
					webView.getSettings().setJavaScriptEnabled(true);
					webView.getSettings().setBuiltInZoomControls(true);
					webView.getSettings().setDisplayZoomControls(false);
					webView.loadUrl(url);
					webView.setWebViewClient(new WebViewClientAndroid());
					webView.setWebChromeClient(new WebChromeClientAndroid());
				}
			}, 500);
		super.onStart();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		try {
			//WebServer.stopServer();
		} catch(Exception e) {}
	}
	@Override
	public void onBackPressed() {
		if(webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
	
	class WebViewClientAndroid extends WebViewClient {
		@TargetApi(Build.VERSION_CODES.N)
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			view.loadUrl(request.getUrl().toString());
			return false;
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return false;
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
	}

	class WebChromeClientAndroid extends WebChromeClient {
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			getSupportActionBar().setSubtitle(title/* + " | " + view.getUrl()*/);
		}
	}
}
