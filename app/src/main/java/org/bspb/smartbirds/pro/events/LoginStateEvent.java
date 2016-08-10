package org.bspb.smartbirds.pro.events;

/**
 * Created by dani on 10.08.16.
 */
public class LoginStateEvent {

    boolean running;

    public LoginStateEvent(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return "LoginStateEvent{" +
                "running=" + running +
                '}';
    }
}
