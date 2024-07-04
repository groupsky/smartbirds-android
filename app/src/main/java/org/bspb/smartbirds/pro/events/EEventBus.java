package org.bspb.smartbirds.pro.events;

import android.util.Log;

import org.bspb.smartbirds.pro.SmartBirdsApplication;

import de.greenrobot.event.EventBus;

/**
 * Created by groupsky on 14-9-25.
 */
public class EEventBus extends EventBus {

    private static volatile EEventBus instance;

    public static EEventBus getInstance() {
        if (instance == null) {
            synchronized (EEventBus.class) {
                if (instance == null) {
                    instance = new EEventBus();
                }
            }
        }
        return instance;
    }

    private EEventBus() {
        super();
        TAG = SmartBirdsApplication.TAG + ".Bus";
    }

    @Override
    public void post(Object event) {
        Log.d(TAG, String.format("posting %s", event));
        super.post(event);
    }

    @Override
    public void postSticky(Object event) {
        Log.d(TAG, String.format("posting sticky %s", event));
        super.postSticky(event);
    }
}
