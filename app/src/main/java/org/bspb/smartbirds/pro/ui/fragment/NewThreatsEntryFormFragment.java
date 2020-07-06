package org.bspb.smartbirds.pro.ui.fragment;

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

    NewThreatsEntryRequiredFormFragment requiredFormFragment;
    NewThreatsEntryOptionalFormFragment optionalFormFragment;

    @AfterViews
    protected void setupTabs() {
        setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {

            @Override
            public androidx.fragment.app.Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        requiredFormFragment = NewThreatsEntryRequiredFormFragment_.builder().setNewEntry(isNewEntry()).build();
                        if (optionalFormFragment != null) {
                            requiredFormFragment.setOnPrimaryTypeChangedListener(optionalFormFragment);
                        }
                        return requiredFormFragment;
                    case 1:
                        optionalFormFragment = NewThreatsEntryOptionalFormFragment_.builder().setNewEntry(isNewEntry()).build();
                        if (requiredFormFragment != null) {
                            requiredFormFragment.setOnPrimaryTypeChangedListener(optionalFormFragment);
                        }
                        return optionalFormFragment;
                    default:
                        throw new IllegalArgumentException("Unhandled position" + position);
                }
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
        public Fragment build(double lat, double lon) {
            return NewThreatsEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewThreatsEntryFormFragment_.builder().entryId(id).build();
        }
    }

}
