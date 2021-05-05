package org.bspb.smartbirds.pro.test;

import androidx.annotation.Nullable;

import org.junit.rules.ExternalResource;

import static androidx.test.internal.util.Checks.checkNotNull;

public class SmartbirdsScenarioRule extends ExternalResource {

    /**
     * Same as {@link java.util.function.Supplier} which requires API level 24.
     *
     * @hide
     */
    interface Supplier {
        SmartbirdsScenario get();
    }

    private final Supplier scenarioSupplier;

    @Nullable
    private SmartbirdsScenario scenario;


    SmartbirdsScenarioRule(SmartbirdsState state) {
        scenarioSupplier = () -> SmartbirdsScenario.launch(checkNotNull(state));
    }

    @Override
    protected void before() throws Throwable {
        scenario = scenarioSupplier.get();
    }

    @Override
    protected void after() {
        scenario.close();
    }
}
