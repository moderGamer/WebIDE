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

public class RunActivity extends AppCompatActivity {

	private LinearLayout linConsole;
	private TextView textConsole;
	private ImageView closeConsole;
	private Button clearConsole;
	private EditText editConsole;
	private WebView webView;
	private String urlPatch, host;
	private Handler handlerRun;
	private Toolbar toolbar;
	
	private String sLeft = "‹--";
	private String sRight = "--›";
	private String console = "";
	String spaceConsole = "<dr><br><br>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Data.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);
		linConsole = (LinearLayout) findViewById(R.id.linConsole);
		textConsole = (TextView) findViewById(R.id.textConsole);
		closeConsole = (ImageView) findViewById(R.id.closeConsole);
		clearConsole = (Button) findViewById(R.id.clearConsole);
		editConsole = (EditText) findViewById(R.id.editConsole);
		webView = (WebView) findViewById(R.id.webView);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("");
		urlPatch = getIntent().getStringExtra("url");
		host = getIntent().getStringExtra("host");
		handlerRun = new Handler();
		hideConsole();
		//WebServer.startServer("localhost", 9000, "/WebMark/projects");
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
		closeConsole.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(closeConsole.getVisibility() == View.VISIBLE) {
						hideConsole();
					}
				}
			});
		clearConsole.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					textConsole.setText("");
				}
			});
			
			
		editConsole.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View p1, int p2, KeyEvent p3) {
					if((p3.getAction() == KeyEvent.ACTION_DOWN) && ((p2 == KeyEvent.KEYCODE_ENTER))) {
						if(editConsole.getText().toString().equals("console.clear();")) {
							textConsole.setText("");
							editConsole.setText("");
						} else {
							String replaceAll = editConsole.getText().toString().replaceAll(";$", "");
							String str = "javascript:%s%s%s";
							Object[] objArr = new Object[3];
							objArr[0] = replaceAll.startsWith("console.") ? "" : "console.log(";
							objArr[1] = replaceAll;
							objArr[2] = replaceAll.startsWith("console.") ? "" : ");";
							webView.loadUrl(String.format(str, objArr));
							editConsole.setText("");
						}
					}
					return false;
				}
			});
		
	}
	@Override
	protected void onStart() {
		handlerRun.postDelayed(new Runnable() {
				@Override public void run() {
					//HTTPServer.main();
					//WebServer.startServer("localhost", 9000, host);
					webView.getSettings().setJavaScriptEnabled(true);
					webView.loadUrl("file://" + urlPatch);
					//webView.loadUrl("http://localhost:9000/2048");
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
		if(linConsole.getVisibility() == View.GONE) {
			if(webView.canGoBack()) {
				webView.goBack();
			} else {
				super.onBackPressed();
			}
		} else {
			hideConsole();
		}
	}
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(getSupportActionBar().isShowing()) {
					finish();
				} else {
					getSupportActionBar().show();
				}
				return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run, menu);
		return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_console) { //Консоль
			setConsole();
		} if(item.getItemId() == R.id.action_open_in_browser) { //Открыть в браузере
			setOpenInBrowser(webView.getUrl());
		} if(item.getItemId() == R.id.action_hide_action_bar) { //Скрыть статус бар
			getSupportActionBar().hide();
			Toast.makeText(getApplicationContext(), "Action bar hidden, long press back to from action bar show", Toast.LENGTH_SHORT).show();
		}
        return true;
    }
	
	private void setConsole() {
		if(linConsole.getVisibility() == View.GONE) {
			showConsole();
		} else {
			hideConsole();
		}
	}
	
	private void setOpenInBrowser(String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(),"Failed! - " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void hideConsole() {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					linConsole.setVisibility(View.GONE);
				}
			});
	}
	private void showConsole() {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					linConsole.setVisibility(View.VISIBLE);
				}
			});
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
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			String lin = "<font color="+"#D3D3D3"+">———————————————————————</font><br>";
			String messageUP =  "<font></font><font color="+"#2570FC"+">" + message + "</font><br>";
			String lineNumberUP = "<font color="+"#656565"+">"+sLeft+" </font><font color="+"#5890F8"+">" + lineNumber + "</font><br>";
			String sourceIDUP = "<font color="+"#464646"+">"+sRight+" </font><font color="+"#8FB5FC"+">" + sourceID + "</font>";

			String messageDones = messageUP + lin + lineNumberUP + lin + sourceIDUP;
			console = console + messageDones + spaceConsole;
			textConsole.setText(Html.fromHtml(console));
			super.onConsoleMessage(message, lineNumber, sourceID);
		}
		@Override
		public void onReceivedTitle(WebView view, String title) {
			getSupportActionBar().setTitle(title);
			super.onReceivedTitle(view, title);
		}
	}
}
