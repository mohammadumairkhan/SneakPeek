package com.rhuf.brandscan.webview;

import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rhuf.brandscan.R;

public class WebViewActivity extends Activity {

	private WebView webView;
	String URL;
	public static final String DEFAULT_PAGE = "index.html";
	private static final String BASE_URL = "file:///android_asset/html/";


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		savedInstanceState = getIntent().getExtras();
		URL = savedInstanceState.getString("URL");
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setPluginsEnabled(true); 
		webView.getSettings().setJavaScriptEnabled(true); 
		if(URL.contains("help")){
			webView.loadUrl(BASE_URL+DEFAULT_PAGE);
		}
		else{
			webView.loadUrl(URL);
		}
		webView.setWebViewClient(new HelloWebViewClient());
		//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		//webView.loadData(customHtml, "text/html", "UTF-8");

	}

	@Override
	protected void onPause() {
	pauseBrowser();
	super.onPause();
	}

	@Override
	protected void onResume() {
	resumeBrowser();
	super.onResume();
	}


//Pauses WebView Browser
	private void pauseBrowser() {

	// pause flash and javascript etc
	callHiddenWebViewMethod(webView, "onPause");
	webView.pauseTimers();
	}
//Resumes WebView Browser
	private void resumeBrowser() {

	// resume flash and javascript etc
	callHiddenWebViewMethod(webView, "onResume");
	webView.resumeTimers();
	}
//Destroys WebView Browser
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    final WebView webview = (WebView)findViewById(R.id.webView1);
	    // Calling .clearView does not stop the flash player must load new data
	    webview.loadData("", "text/html", "utf-8");
	}
	
	private void callHiddenWebViewMethod(final WebView wv, final String name){
	    if( webView != null ){
	        try {
	            Method method = WebView.class.getMethod(name);
	            method.invoke(webView);
	        } catch (final Exception e) {
	        }
	    }
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	      if (webView.canGoBack()) {
	        webView.goBack();
	        return true;
	      }
	    }
	    return super.onKeyDown(keyCode, event);
	  }

	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
  
	       /* @Override
	        public void onPageFinished(WebView view, String url) {
	            Log.d("url", "onPageFinished url: " +url);
	            // Facebook redirects to this url once a user has logged in, this is a blank page so we override this
	            // http://www.facebook.com/connect/connect_to_external_page_widget_loggedin.php?............
	            if(url.startsWith("http://www.facebook.com/connect/connect_to_external_page_widget_loggedin.php")){
	                String redirectUrl = URL;
	                view.loadUrl(redirectUrl);
	                return;
	            }
	            super.onPageFinished(view, url);
	        }

	    */
	    
	    
	}

	
}
