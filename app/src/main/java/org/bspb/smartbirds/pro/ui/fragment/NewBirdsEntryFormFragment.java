package org.bspb.smartbirds.pro.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;


@EFragment(R.layout.fragment_monitoring_form_birds)
public class NewBirdsEntryFormFragment extends BaseEntryFragment {

    @ViewById(R.id.pager)
    ViewPager pager;

//    @ViewById(R.id.form_birds_count_units)
//    SingleChoiceFormInput countUnits;
//
//    @ViewById(R.id.form_birds_count_type)
//    SingleChoiceFormInput countType;
//
//    @ViewById(R.id.form_birds_count)
//    DecimalNumberFormInput count;
//    @ViewById(R.id.form_birds_count_min)
//    DecimalNumberFormInput countMin;
//    @ViewById(R.id.form_birds_count_max)
//    DecimalNumberFormInput countMax;

    @Pref
    BirdPrefs_ prefs;

    @AfterViews
    protected void setupPager() {
        pager.setAdapter(new MyAdapter(getChildFragmentManager()));
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        countUnits.setSelection(prefs.birdCountUnits().get());
//        countType.setSelection(prefs.birdCountType().get());
//        handleCountsLogic();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        prefs.birdCountUnits().put(countUnits.getSelection());
//        prefs.birdCountType().put(countType.getSelection());
//    }

    @Override
    EntryType getEntryType() {
        return EntryType.BIRDS;
    }

//    @TextChange(R.id.form_birds_count_type)
//    void handleCountsLogic() {
//        Nomenclature item = countType.getSelectedItem();
//        String countsType = item != null ? item.label.en : null;
//        switch (countsType != null ? countsType.toLowerCase(Locale.ENGLISH) : "") {
//            case "exact number": // Exact count
//                count.setEnabled(true);
//                countMin.setEnabled(false);
//                countMax.setEnabled(false);
//                break;
//            case "min.": // Min count
//                count.setEnabled(false);
//                countMin.setEnabled(true);
//                countMax.setEnabled(false);
//                break;
//            case "max.": // Max count
//                count.setEnabled(false);
//                countMin.setEnabled(false);
//                countMax.setEnabled(true);
//                break;
//            case "range": // Range count
//                count.setEnabled(false);
//                countMin.setEnabled(true);
//                countMax.setEnabled(true);
//                break;
//            case "unspecified number": // Unspecified
//                count.setEnabled(false);
//                countMin.setEnabled(false);
//                countMax.setEnabled(false);
//                break;
//            default:
//                count.setEnabled(true);
//                countMin.setEnabled(true);
//                countMax.setEnabled(true);
//                break;
//        }
//    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return NewBirdsListEntryFormFragment_.builder().build();
                case 1: return NewBirdsExtraEntryFormFragment_.builder().build();
                default:
                    throw new IllegalArgumentException("Only 2 pages are implemented!");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO: load the page titles from resources
            switch (position) {
                case 0: return "Species";
                case 1: return "Data";
                default:
                    throw new IllegalArgumentException("Only 2 pages are implemented!");
            }
        }
    }

}
