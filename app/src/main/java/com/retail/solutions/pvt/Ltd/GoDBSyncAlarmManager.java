package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 6/4/16.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class GoDBSyncAlarmManager extends BroadcastReceiver {
    private static final String TAG = GoDBSyncAlarmManager.class.getSimpleName();

    public GoDBSyncAlarmManager() {
    }

    public AlarmManager am;
    public PendingIntent sender;

    @Override
    public void onReceive(final Context context,final Intent intent)
    {
        Log.d(TAG, "@@@@ALarm fired");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        Intent onReceiveIntent = new Intent( context, SyncService.class );
        context.startService(onReceiveIntent);
        wl.release();
    }

    public void setAlarm(Context context)
    {
        Log.d(TAG, "@@@@Setting Alarm" );
        AlarmManager setAlarmManager =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent setAlarmIntent = new Intent(context, GoDBSyncAlarmManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, setAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        setAlarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() +  60000, pi);// Millisec * Second * Minute

        //Toast.makeText(this, "Service REStarted", Toast.LENGTH_LONG).show();
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, GoDBSyncAlarmManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


}
