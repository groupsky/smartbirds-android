package org.bspb.smartbirds.pro.backend;

/**
 * Created by dani on 08.08.16.
 */
public class LoginResultEvent {

    public enum Status {
        SUCCESS,
        PASSWORD_SHORT,
        CONNECTIVITY,
        BAD_PASSWORD
    }

    public Status status;

    public LoginResultEvent(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginResultEvent{" +
                "status=" + status +
                '}';
    }

}
