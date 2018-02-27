package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

@EFragment
public class NewPlantsEntryFormFragment extends BaseTabEntryFragment {


    @AfterViews
    protected void setupTabs() {
        setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return NewPlantsEntryRequiredFormFragment_.builder().setNewEntry(isNewEntry()).build();
                    case 1:
                        return NewPlantsEntryOptionalFormFragment_.builder().setNewEntry(isNewEntry()).build();
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
        return EntryType.PLANTS;
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewPlantsEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewPlantsEntryFormFragment_.builder().entryId(id).build();
        }
    }

}
