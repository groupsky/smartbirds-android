package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.bspb.smartbirds.pro.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewBirdsEntryFormFragment.Listener} interface
 * to handle interaction events.
 * Use the {@link NewBirdsEntryFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NewBirdsEntryFormFragment extends Fragment {

    private static final String ARG_LAT = "lat";
    private static final String ARG_LON = "lon";
    private Listener listener;
    private double lat;
    private double lon;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FormBirdsFragment.
     */
    public static NewBirdsEntryFormFragment newInstance(double lat, double lon) {
        NewBirdsEntryFormFragment fragment = new NewBirdsEntryFormFragment();
        Bundle arguments = new Bundle();
        arguments.putDouble(ARG_LAT, lat);
        arguments.putDouble(ARG_LON, lon);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * @deprecated Use the {@link NewBirdsEntryFormFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    @Deprecated
    public NewBirdsEntryFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            lat = args.getDouble(ARG_LAT, 0);
            lon = args.getDouble(ARG_LON, 0);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monitoring_form_new_birds_entry, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.form_entry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:
                if (listener != null)
                    listener.onSubmitMonitoringForm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Listener {
        public void onSubmitMonitoringForm();
    }

}
