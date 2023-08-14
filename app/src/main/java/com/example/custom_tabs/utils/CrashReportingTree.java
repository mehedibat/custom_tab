package com.example.custom_tabs.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import timber.log.Timber;
/**
 * Created by FRabbi on 05-07-2023.
 */
public final class  CrashReportingTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        ErrorReportingUtils.log(priority, tag, message);

        if (t != null) {
            if (priority == Log.ERROR) {
                ErrorReportingUtils.logError(t);
            } else if (priority == Log.WARN) {
                ErrorReportingUtils.logWarning(t);
            }
        }
    }
}
