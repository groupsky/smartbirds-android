package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.bspb.smartbirds.pro.R;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by groupsky on 26.01.17.
 */

public abstract class BaseTabEntryFragment extends BaseEntryFragment {

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
        deserializeTabs(data);
    }

    protected void deserializeTabs(HashMap<String, String> data) {
        if (viewPager == null) return;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object fragment = adapter.instantiateItem(viewPager, i);
            if (!(fragment instanceof BaseFormFragment)) continue;
            ((BaseFormFragment) fragment).doDeserialize(monitoringCode, data);
        }
    }

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
        if (activity instanceof AppCompatActivity) {
            ab = ((AppCompatActivity) activity).getSupportActionBar();
        }

        if (ab != null) {
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new androidx.appcompat.app.ActionBar.TabListener() {

                @Override
                public void onTabSelected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction ft) {
                    if (viewPager != null)
                        viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction ft) {

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.pager);
        updateViewPager();
    }
}
