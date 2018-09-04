package com.work.lazxy.writeaway.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.base.BaseActivity
import com.work.lazxy.writeaway.utils.IntentUtils
import com.work.lazxy.writeaway.utils.SystemUtils

import kotlinx.android.synthetic.main.activity_about_app.*

/**
 * Created by lazxy on 2017/6/9.
 */

class AboutAppActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        collapsingLayout.title = "关于App"
        tvVersion.text = "Version " + SystemUtils.getVersionName(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webContent.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        } else {
            webContent.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }
        webContent.webViewClient = AboutClient()
    }

    override fun initListener() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun initLoad() {
        webContent.loadUrl("file:///android_asset/about_app.html")
    }

    private inner class AboutClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            //从本地其他浏览器打开外链
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                IntentUtils.startActivityToBrowser(view.context, url)
                return true
            } else {
                return false
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
            webContent.visibility = View.VISIBLE
        }
    }
}