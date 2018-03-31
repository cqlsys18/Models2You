package com.models2you.model.ui.base;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.models2you.model.R;
import com.models2you.model.app.Constants;
import com.models2you.model.util.LogFactory;

/**
 * Created by chandrakant on 10/4/2016.
 */
public class WebViewActivity extends BaseAppCompatActivity {
    private static final LogFactory.Log log = LogFactory.getLog(WebViewActivity.class);

    private WebView webView;
    private TextView retryTextView;
    private View webViewProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        boolean actionBackButtonNeeded = getIntent().getBooleanExtra(Constants.WEB_VIEW_NEED_ACTION_BACK, true);
        if(actionBackButtonNeeded) {
            setActionBarBackEnable();
        }
        String title = getIntent().getStringExtra(Constants.WEB_VIEW_TITLE_EXTRA);
        setTitle(title);
        webView = (WebView) findViewById(R.id.webView);
        webViewProgressBar = findViewById(R.id.webViewProgressBar);
        final String intentUrlString = getIntent().getStringExtra(Constants.WEB_VIEW_URL_EXTRA);
        startWebViewWithLoader(intentUrlString);

        retryTextView = (TextView) findViewById(R.id.retryTextView);
        retryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebView(true);
                startWebViewWithLoader(intentUrlString);
            }
        });
        // by default long press set to false , to enable it pass true in "WEB_VIEW_LONG_PRESS_ENABLED" intent extra
        boolean isLongPressEnabled = getIntent().getBooleanExtra(Constants.WEB_VIEW_LONG_PRESS_ENABLED, false);
        if (!isLongPressEnabled) {
            disableLongPressOnWebView();
        }
    }

    /**
     * disableLongPressOnWebView : method used to disable long press on WebView
     */
    private void disableLongPressOnWebView() {
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setHapticFeedbackEnabled(false); // vibrate false when long click
    }

    /**
     * show or hide WebView
     * @param show to show webView and hide retryTextView and vice versa.
     */
    private void showWebView(boolean show) {
        webView.setVisibility(show ? View.VISIBLE : View.GONE);
        retryTextView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    protected boolean needEventBus(){
        return true;
    }

    @SuppressWarnings("all")
    protected void startWebViewWithLoader(String url) {
        log.debug("startWebViewWithLoader", url);
        webView.setWebViewClient(new WebViewClient() {
            //To open in webView (not in external browser)
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
            }

            public void onPageFinished(WebView view, String url) {
                //setCustomProgressBarIndeterminateVisibility(false);//ProgressBar in ActionBar
                webViewProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showWebView(false);
                if (webViewProgressBar.getVisibility() == View.VISIBLE) {
                    webViewProgressBar.setVisibility(View.GONE);
                }
                webView.loadUrl("about:blank");
            }
        });
        //setCustomProgressBarIndeterminateVisibility(true);//ProgressBar in ActionBar
        webViewProgressBar.setVisibility(View.VISIBLE);
        // Javascript enabled on webView
        webView.loadUrl(url);
    }
}
