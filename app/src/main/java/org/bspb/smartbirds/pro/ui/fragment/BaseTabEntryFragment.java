package org.bspb.smartbirds.pro.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment(R.layout.fragment_monitoring_form_tabs)
public abstract class BaseTabEntryFragment extends BaseEntryFragment {

    @ViewById(R.id.pager)
    ViewPager viewPager;
    ActionBar ab;

    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    protected boolean isValid() {
        boolean valid = super.isValid();
        if (viewPager == null) return valid;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) return valid;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object fragment = adapter.instantiateItem(viewPager, i);
            if (!(fragment instanceof BaseFormFragment)) continue;
            valid &= ((BaseFormFragment) fragment).isValid();
        }
        return valid;
    }

    @Override
    protected HashMap<String, String> serialize(Date entryTime) {
        HashMap<String, String> data = super.serialize(entryTime);
        if (viewPager == null) return data;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) return data;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object fragment = adapter.instantiateItem(viewPager, i);
            if (!(fragment instanceof BaseFormFragment)) continue;
            data.putAll(((BaseFormFragment) fragment).serialize());
        }
        return data;
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        if (viewPager == null) return;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object fragment = adapter.instantiateItem(viewPager, i);
            if (!(fragment instanceof BaseFormFragment)) continue;
            ((BaseFormFragment) fragment).doDeserialize(monitoringCode, data);
        }
    }

    @AfterViews
    protected void updateViewPager() {
        if (ab != null) {
            viewPager.setCurrentItem(ab.getSelectedTab().getPosition());
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (ab != null) ab.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ab = activity.getActionBar();
        if (ab != null) {
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {

                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    if (viewPager != null)
                        viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            };

            ab.addTab(ab.newTab().setText(R.string.tab_required).setTabListener(tabListener));
            ab.addTab(ab.newTab().setText(R.string.tab_optional).setTabListener(tabListener));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_monitoring_form_tabs, container, false);
        }
        return contentView;
    }

}
