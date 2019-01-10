package org.bspb.smartbirds.pro.ui.fragment;

import android.support.v4.app.Fragment;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;


@EFragment(R.layout.fragment_stats)
public class StatsFragment extends Fragment {

    @ViewById(R.id.webview)
    WebView webView;

    @AfterViews
    void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getString(R.string.stats_url));
    }

}
