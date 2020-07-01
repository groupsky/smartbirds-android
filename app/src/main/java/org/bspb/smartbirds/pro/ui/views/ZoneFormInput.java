package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;
import org.bspb.smartbirds.pro.ui.utils.SmartArrayAdapter;

import java.util.ArrayList;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.text.InputType.TYPE_TEXT_VARIATION_FILTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI;
import static android.widget.AdapterView.INVALID_POSITION;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Zones.CONTENT_URI;

/**
 * Created by groupsky on 06.10.16.
 */

@EView
public class ZoneFormInput extends TextViewFormInput implements SupportStorage {

    @Bean
    NomenclaturesBean nomenclatures;

    private SmartArrayAdapter<ZoneHolder> mAdapter;
    /**
     * The currently selected item.
     */
    ZoneHolder mSelectedItem = null;
    private CursorLoader cursorLoader;
    private final Loader.OnLoadCompleteListener<Cursor> listener = new Loader.OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            if (mAdapter != null) {
                mAdapter.clear();
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    mAdapter.add(new ZoneHolder(Zone.fromCursor(data)));
                }
                mAdapter.notifyDataSetChanged();
                setSelection(new ZoneHolder(getText().toString()));
            }
        }
    };

    public ZoneFormInput(Context context) {
        this(context, null);
    }

    public ZoneFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.zoneFormInputStyle);
    }

    public ZoneFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        SmartArrayAdapter<ZoneHolder> adapter = new SmartArrayAdapter<>(context,
                android.R.layout.select_dialog_singlechoice, new ArrayList<ZoneHolder>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            cursorLoader = new CursorLoader(getContext(), CONTENT_URI, Zone.DEFAULT_PROJECTION, null, null, null);
            cursorLoader.registerListener(0, listener);
            cursorLoader.startLoading();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (cursorLoader != null) {
            cursorLoader.stopLoading();
            cursorLoader.unregisterListener(listener);
            cursorLoader = null;
        }
    }

    /**
     * The Adapter is used to provide the data which backs this Spinner.
     * It also provides methods to transform spinner items based on their position
     * relative to the selected item.
     *
     * @param adapter The SpinnerAdapter to use for this Spinner
     */
    public void setAdapter(SmartArrayAdapter<ZoneHolder> adapter) {
        mAdapter = adapter;
        setText(getText());
    }

    public String getSelection() {
        return mSelectedItem != null ? mSelectedItem.label : null;
    }

    public Zone getSelectedItem() {
        return mSelectedItem != null
                ? mSelectedItem.zone
                : null;
    }

    public void setSelection(String label) {
        setSelection(new ZoneHolder(label));
    }

    protected void setSelection(ZoneHolder item) {
        // find our item that has nomenclature inside
        if (item != null) {
            int idx = mAdapter.getPosition(item);
            if (idx != INVALID_POSITION) {
                item = mAdapter.getItem(idx);
            } else {
                item = null;
            }
        }

        if (item != null) {
            if (item.equals(mSelectedItem)) return;
        } else if (mSelectedItem == null) return;

        mSelectedItem = item;
        if (item != null) {
            setText(item.label);
            setError(null);
        } else {
            setText("");
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        new PopupDialog().show();
        return true;
    }

    @Override
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        storage.put(fieldName, getText().toString());
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        String value = storage.get(fieldName);
        setText(value);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mAdapter != null) {
            setSelection(new ZoneHolder(text.toString()));
        }
        super.setText(text, type);
    }

    private class PopupDialog implements DialogInterface.OnClickListener, TextWatcher, DialogInterface.OnCancelListener, Filter.FilterListener {

        private AlertDialog mPopup;
        private ZoneHolder mLastSelected = null;

        public void show() {
            boolean needFilter = mAdapter.getCount() > 20;

            EditText view = null;
            if (needFilter) {
                view = new EditText(getContext());
                view.setImeOptions(view.getImeOptions() | IME_FLAG_NO_EXTRACT_UI | IME_ACTION_DONE);
                view.setInputType(TYPE_TEXT_VARIATION_FILTER);
                view.setHint(getHint());
                view.setSingleLine();
                view.addTextChangedListener(this);
            }

            mAdapter.getFilter().filter(null);

            setSelection(new ZoneHolder(getText().toString()));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setSingleChoiceItems(mAdapter, mSelectedItem != null ? mAdapter.getPosition(mSelectedItem) : INVALID_POSITION, this)
                    .setCancelable(true)
                    .setOnCancelListener(this)
                    .setNegativeButton(android.R.string.cancel, this)
                    .setNeutralButton(R.string.clear, this);
            if (needFilter) {
                builder.setCustomTitle(view);
            }
            mPopup = builder.create();

            final ListView listView = mPopup.getListView();
            listView.setTextDirection(getTextDirection());
            listView.setTextAlignment(getTextAlignment());

            mPopup.show();

            mPopup.getButton(BUTTON_POSITIVE).setEnabled(mSelectedItem != null);
            if (needFilter) {
                mPopup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                view.requestFocus();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                case BUTTON_POSITIVE:
                    if (mLastSelected == null) return;
                    setSelection(mLastSelected);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                case BUTTON_NEUTRAL:
                    setSelection((ZoneHolder) null);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                default:
                    mLastSelected = mAdapter.getItem(which);
                    onClick(dialog, BUTTON_POSITIVE);
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            ((Filterable) mAdapter).getFilter().filter(s);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            ((Filterable) mAdapter).getFilter().filter(null);
        }

        @Override
        public void onFilterComplete(int count) {
            setSelection(new ZoneHolder(getText().toString()));
        }
    }

    static class ZoneHolder {
        final Zone zone;
        final String label;

        ZoneHolder(String label) {
            this.label = label;
            this.zone = null;
        }

        ZoneHolder(Zone zone) {
            this.zone = zone;
            label = zone.id;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ZoneHolder that = (ZoneHolder) o;

            return label.equals(that.label);
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
