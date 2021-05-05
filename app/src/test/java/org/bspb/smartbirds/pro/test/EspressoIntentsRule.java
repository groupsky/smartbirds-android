package org.bspb.smartbirds.pro.test;

import androidx.test.espresso.intent.Intents;

import org.junit.rules.ExternalResource;

public class EspressoIntentsRule extends ExternalResource {
    @Override
    protected void before() throws Throwable {
        // initialize Espresso Intents capturing
        Intents.init();
        super.before();
    }

    @Override
    protected void after() {
        super.after();
        // release Espresso Intents capturing
        Intents.release();
    }
}
