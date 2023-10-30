package com.eltonfaust.wakeupplugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

import android.app.ActivityManager;
import android.os.PowerManager;
import android.os.AsyncTask;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class WakeupReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "WakeupReceiver";

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())) {
                if (task.numRunning > 0) {
                    return true;
                }
            }
        }

        return false;
    }
    private void launchWakeupService(Context context, String extras) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String alarmsStreamingUrl = preferences.getString("alarms_streaming_url", null);
        String alarmsRingtone = preferences.getString("alarms_ringtone", null);

        if (alarmsStreamingUrl != null || alarmsRingtone != null) {
            Intent serviceIntent = new Intent(context, WakeupStartService.class);

            if (extras != null) {
                serviceIntent.putExtra("extra", extras);
            }

            serviceIntent.putExtra("wakeup", true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }

    private void launchApp(Context context, Intent intent, String extras, long now) {
        Bundle extrasBundle = intent.getExtras();
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.putExtra("cdvStartInBackground", true);

        String className = launchIntent.getComponent().getClassName();

        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName(className);
            Intent activityIntent = new Intent(context, c);

            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.putExtra("wakeup", intent.getBooleanExtra("wakeup", true));
            activityIntent.putExtra("triggerAt", now);

            if (extrasBundle != null && extrasBundle.get("startInBackground") != null && (boolean) extrasBundle.get("startInBackground")) {
                activityIntent.putExtra("cdvStartInBackground", true);
            }

            if (extras != null) {
                activityIntent.putExtra("extra", extras);
            }

            context.startActivity(activityIntent);

            if (WakeupPlugin.connectionCallbackContext != null) {
                JSONObject o = new JSONObject();
                o.put("type", "wakeup");
                if (extras != null) {
                    o.put("extra", extras);
                }
                o.put("cdvStartInBackground", true);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, o);
                pluginResult.setKeepCallback(true);
                WakeupPlugin.connectionCallbackContext.sendPluginResult(pluginResult);
            }
        } catch (JSONException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void afterLaunchActions(Context context, Intent intent, String extras) {
        Bundle extrasBundle = intent.getExtras();

        if (extrasBundle != null && extrasBundle.getString("type") != null && extrasBundle.getString("type").equals("daylist")) {
            Date next = new Date(new Date().getTime() + (7 * 24 * 60 * 60 * 1000));

            Intent reschedule = new Intent(context, WakeupReceiver.class);

            if (extras != null) {
                reschedule.putExtra("extra", intent.getExtras().get("extra").toString());
            }

            reschedule.putExtra("day", WakeupPlugin.daysOfWeek.get(intent.getExtras().get("day")));
            reschedule.putExtra("cdvStartInBackground", true);

            PendingIntent sender = PendingIntent.getBroadcast(
                context, 19999 + WakeupPlugin.daysOfWeek.get(intent.getExtras().get("day")), intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
            );
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTime(), sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(next.getTime(), sender);
                alarmManager.setAlarmClock(alarmClockInfo, sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, next.getTime(), sender);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, next.getTime(), sender);
            }
        }
    }

    private String extras;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        long now = new Date().getTime();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                Bundle extrasBundle = intent.getExtras();
                if (extrasBundle != null && extrasBundle.get("extra") != null) {
                    extras = extrasBundle.get("extra").toString();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                launchWakeupService(context, extras);
                launchApp(context, intent, extras, now);
                afterLaunchActions(context, intent, extras);
            }
        }.execute();
    }
}
