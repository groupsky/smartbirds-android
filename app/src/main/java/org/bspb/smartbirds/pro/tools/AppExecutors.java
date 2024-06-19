package org.bspb.smartbirds.pro.tools;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static Executor backgroundThread;
    private static Executor mainThread;

    public static Executor background() {
        if (backgroundThread == null) {
            backgroundThread = Executors.newSingleThreadExecutor();
        }
        return backgroundThread;
    }

    public static Executor mainThread() {
        if (mainThread == null) {
            mainThread = new MainThreadExecutor();
        }
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
