package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;


@EFragment
public class NewBirdsEntryFormFragment extends BaseTabEntryFragment {

    @AfterViews
    protected void setupTabs() {
        setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public android.app.Fragment getItem(int position) {
                switch (position) {
                    case 0: return NewBirdsEntryRequiredFormFragment_.builder().build();
                    case 1: return NewBirdsEntryOptionalFormFragment_.builder().build();
                    default: throw new IllegalArgumentException("Unhandled position"+position);
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
        return EntryType.BIRDS;
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewBirdsEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewBirdsEntryFormFragment_.builder().entryId(id).build();
        }
    }
}
