package org.bspb.smartbirds.pro.test;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SmartbirdsStateRule implements TestRule {

    SmartbirdsState desiredState;

    public SmartbirdsStateRule(SmartbirdsState state) {
        desiredState = state;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                SmartbirdsScenario.setupState(desiredState);

                base.evaluate();
            }
        };
    }
}
