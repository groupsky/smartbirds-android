package org.bspb.smartbirds.pro.events;

import android.os.Looper;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.utils.ExtensionsKt;

import de.greenrobot.event.EventBus;

/**
 * Created by groupsky on 14-9-25.
 */
@EBean(scope = EBean.Scope.Singleton)
public class EEventBus extends EventBus {

    public EEventBus() {
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
