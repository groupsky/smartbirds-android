package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;


@EFragment(R.layout.fragment_stats)
public class StatsFragment extends Fragment {

    @ViewById(R.id.webview)
    WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @AfterViews
    void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getString(R.string.stats_url));
    }

}
