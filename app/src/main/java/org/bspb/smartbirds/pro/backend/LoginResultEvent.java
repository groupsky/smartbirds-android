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
        BAD_PASSWORD
    }

    public Status status;

    public User user;

    public LoginResultEvent(Status status) {
        this.status = status;
    }

    public LoginResultEvent(User user) {
        this.status = Status.SUCCESS;
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResultEvent{" +
                "status=" + status +
                ", user=" + user +
                '}';
    }

}
