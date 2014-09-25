package org.bspb.smartbirds.pro.events;

import org.androidannotations.annotations.EBean;

import de.greenrobot.event.EventBus;

/**
 * Created by groupsky on 14-9-25.
 */
@EBean(scope = EBean.Scope.Singleton)
public class EEventBus extends EventBus {
}
