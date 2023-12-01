package com.example.warroomapp.Activity.Class;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

public class ActivityUtils {

    public static boolean isActivityRunning(Context context, Class<?> activityClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            // Get the list of running tasks
            for (ActivityManager.RunningTaskInfo taskInfo : activityManager.getRunningTasks(Integer.MAX_VALUE)) {
                ComponentName componentName = taskInfo.topActivity;
                if (componentName != null && componentName.getClassName().equals(activityClass.getName())) {
                    return true;
                }
            }

            // Alternatively, you can check running processes
            for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }

        return false;
    }
}
