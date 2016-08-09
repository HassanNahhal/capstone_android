package com.conestogac.receipt_keeper.webview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ReceiptKeeperApplication;
import com.conestogac.receipt_keeper.uploader.Customer;
import com.conestogac.receipt_keeper.uploader.CustomerRepository;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private WebView webView;
    public static final String EXTRA_URL = "url";
    private String url = "";

    private CustomerRepository userRepo;
    private String customerId;
    private String groupId;

    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private ProgressBar web_progress;
    private String tokenId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_view);
        app = (ReceiptKeeperApplication)getApplicationContext();
        adapter = app.getLoopBackAdapter();
        userRepo = adapter.createRepository(CustomerRepository.class);

        web_progress = (ProgressBar)findViewById(R.id.progress);
        web_progress.setVisibility(View.GONE);

        url = getIntent().getStringExtra(EXTRA_URL);
        //set statusbar color to be consistent
        //due to some reason style does not work, so use these code snippet
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        webView = (WebView) findViewById(R.id.webView);
        setupWebView();
//        try {
//            silentLogin(app.getCurrentUser().getEmail(), app.getCurrentUser().getPassword());
//        } catch (Exception e) {
//            Toast.makeText(WebViewActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
//        }
    }

    /*
        This will be called after silent login
     */
    private void showWebView() {
        final Map<String, String> extraHeaders = new HashMap<String, String>();

        extraHeaders.put("authorization", tokenId);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished (WebView view, String url) {
                Log.d(TAG, url);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                web_progress.setVisibility(View.VISIBLE);

                if(newProgress > 90){
                    web_progress.setVisibility(View.GONE);
                }
            }

        });
        webView.loadUrl(url,extraHeaders);
        Log.d(TAG, "Token:"+extraHeaders);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setBuiltInZoomControls(true);  //Zoom
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(10 * 1024 * 1024);
        settings.setAppCachePath("");
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // Flash settings
        settings.setPluginState(WebSettings.PluginState.ON);

        // Geo location settings
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath("/data/data/receiptkeeper");
        webView.setFocusable(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        showWebView();
    }

    private void silentLogin(String email, String password) {
        //Login
        userRepo.loginUser(email , password
                , new CustomerRepository.LoginCallback() {
                    @Override
                    public void onSuccess(AccessToken token, Customer currentUser) {
                        customerId = currentUser.getId().toString();
                        groupId = currentUser.getGroupId();
                        if (groupId == null) groupId = "";

                        tokenId = token.getId().toString();
                        showWebView();
                    }
                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "Login E", t);
                        Toast.makeText(WebViewActivity.this, getString(R.string.save_fail_login_message), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


}
