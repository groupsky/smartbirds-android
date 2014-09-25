package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.net.Uri;
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
 * {@link org.bspb.smartbirds.pro.ui.fragment.NewBirdsNestEntryFormFragment.Listener} interface
 * to handle interaction events.
 * Use the {@link NewBirdsNestEntryFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewBirdsNestEntryFormFragment extends Fragment {

    private Listener listener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FormBirdsNestFragment.
     */
    public static NewBirdsNestEntryFormFragment newInstance(String param1, String param2) {
        NewBirdsNestEntryFormFragment fragment = new NewBirdsNestEntryFormFragment();
        return fragment;
    }

    /**
     * @deprecated Use the {@link NewBirdsNestEntryFormFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    @Deprecated
    public NewBirdsNestEntryFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monitoring_form_new_birds_nest, container, false);
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
                    listener.onSubmitMonitoringFormBirdsNest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Listener {
        public void onSubmitMonitoringFormBirdsNest();
    }

}
