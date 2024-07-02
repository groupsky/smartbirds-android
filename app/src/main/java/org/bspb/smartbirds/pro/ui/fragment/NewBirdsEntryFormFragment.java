package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.utils.ExtensionsKt;


public class NewBirdsEntryFormFragment extends BaseTabEntryFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTabs();
    }

    protected void setupTabs() {
        setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public androidx.fragment.app.Fragment getItem(int position) {
                ExtensionsKt.debugLog("Open birds entry form " + isNewEntry() + " " + readOnly);
                return switch (position) {
                    case 0 ->
                            NewBirdsEntryRequiredFormFragment.newInstance(isNewEntry(), readOnly, lat, lon);
                    case 1 ->
                            NewBirdsEntryOptionalFormFragment.Companion.newInstance(isNewEntry(), readOnly);
                    default -> throw new IllegalArgumentException("Unhandled position" + position);
                };
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getActivity().getString(position == 0 ? R.string.tab_required : R.string.tab_optional);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    @Override
    protected EntryType getEntryType() {
        return EntryType.BIRDS;
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon, double geolocationAccuracy) {
            Fragment fragment = new NewBirdsEntryFormFragment();
            Bundle args = new Bundle();
            args.putDouble(ARG_LAT, lat);
            args.putDouble(ARG_LON, lon);
            args.putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Fragment load(long id, boolean readOnly) {
            Fragment fragment = new NewBirdsEntryFormFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_ENTRY_ID, id);
            args.putBoolean(ARG_READ_ONLY, readOnly);
            fragment.setArguments(args);
            return fragment;
        }
    }
}
