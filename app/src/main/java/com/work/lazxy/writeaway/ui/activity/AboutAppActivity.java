package com.work.lazxy.writeaway.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.common.base.BaseActivity;
import com.work.lazxy.writeaway.ui.widget.ProgressDialog;
import com.work.lazxy.writeaway.utils.IntentUtils;
import com.work.lazxy.writeaway.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lazxy on 2017/6/9.
 */

public class AboutAppActivity extends BaseActivity {

    @BindView(R.id.collapsing_layout_about)
    CollapsingToolbarLayout collapsingLayout;
    @BindView(R.id.toolbar_about)
    Toolbar toolbar;
    @BindView(R.id.tv_about_version)
    TextView versionText;
    @BindView(R.id.progress_about)
    ProgressBar progressBar;
    @BindView(R.id.webView_about)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        collapsingLayout.setTitle("关于App");
        versionText.setText(String.valueOf("Version " + SystemUtils.getVersionName(this)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webView.setWebViewClient(new AboutClient());
    }

    @Override
    public void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initLoad() {
        webView.loadUrl("file:///android_asset/about_app.html");
    }

    private class AboutClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //从本地其他浏览器打开外链
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                IntentUtils.startActivityToBrowser(view.getContext(), url);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }
}