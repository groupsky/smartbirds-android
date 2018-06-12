package org.bspb.smartbirds.pro.backend;

import org.bspb.smartbirds.pro.backend.dto.User;

/**
 * Created by dani on 08.08.16.
 */
public class LoginResultEvent {

    public enum Status {
        SUCCESS,
        PASSWORD_SHORT,
        CONNECTIVITY,
        BAD_PASSWORD,
        MISSING_GDPR,
        ERROR
    }

    public Status status;

    public User user;

    public String message;

    public LoginResultEvent(Status status) {
        this.status = status;
    }

    public LoginResultEvent(User user) {
        this.status = Status.SUCCESS;
        this.user = user;
    }

    public LoginResultEvent(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResultEvent{" +
                "status=" + status +
                ", user=" + user +
                ", message=" + message +
                '}';
    }

}
