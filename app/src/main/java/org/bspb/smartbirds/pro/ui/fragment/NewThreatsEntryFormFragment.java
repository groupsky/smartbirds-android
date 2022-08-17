package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

/**
 * Created by dani on 11-07-19.
 */
@EFragment
public class NewThreatsEntryFormFragment extends BaseTabEntryFragment {

    @AfterViews
    protected void setupTabs() {
        setAdapter(new FragmentStatePagerAdapter(getFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            NewThreatsEntryRequiredFormFragment requiredFormFragment;
            NewThreatsEntryOptionalFormFragment optionalFormFragment;

            @Override
            public androidx.fragment.app.Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        requiredFormFragment = NewThreatsEntryRequiredFormFragment_.builder().setNewEntry(isNewEntry()).readOnly(readOnly).build();
                        if (optionalFormFragment != null) {
                            requiredFormFragment.setOnPrimaryTypeChangedListener(optionalFormFragment);
                        }
                        return requiredFormFragment;
                    case 1:
                        optionalFormFragment = NewThreatsEntryOptionalFormFragment_.builder().setNewEntry(isNewEntry()).readOnly(readOnly).build();
                        if (requiredFormFragment != null) {
                            requiredFormFragment.setOnPrimaryTypeChangedListener(optionalFormFragment);
                        }
                        return optionalFormFragment;
                    default:
                        throw new IllegalArgumentException("Unhandled position" + position);
                }
            }

            // Workaround for keeping primaryTypeChanged listener on configuration change
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                Object res = super.instantiateItem(container, position);
                if (res instanceof NewThreatsEntryRequiredFormFragment && requiredFormFragment == null) {
                    requiredFormFragment = (NewThreatsEntryRequiredFormFragment) res;
                }
                if (res instanceof NewThreatsEntryOptionalFormFragment && optionalFormFragment == null) {
                    optionalFormFragment = (NewThreatsEntryOptionalFormFragment) res;
                    if (requiredFormFragment != null) {
                        requiredFormFragment.setOnPrimaryTypeChangedListener(optionalFormFragment);
                    }
                }

                return res;
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
        return EntryType.THREATS;
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon, double geolocationAccuracy) {
            return NewThreatsEntryFormFragment_.builder().lat(lat).lon(lon).geolocationAccuracy(geolocationAccuracy).build();
        }

        @Override
        public Fragment load(long id, boolean readOnly) {
            return NewThreatsEntryFormFragment_.builder().entryId(id).readOnly(readOnly).build();
        }
    }

}
