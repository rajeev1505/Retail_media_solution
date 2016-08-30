package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 5/4/16.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.godbtech.notifications.GNotifiable;
import com.godbtech.notifications.GRegisterToSyncServer;
import com.godbtech.sync.GUtils;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService implements GNotifiable
{
    //static GHandler handler1;

    public GCMIntentService()
    {
        super(GMainActivity.GCM_PROJECT_ID);
        //handler=new GHandler(getBaseContext());
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
        new GRegisterToSyncServer(context, registrationId, 1, this, GMainActivity.getSyncBudle(context)).start();
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
        if(registrationId!=null && registrationId.length()>0)
            postNotify(context, 230, 3, "notification_unregistered", registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        String payload = intent.getStringExtra("payload");//key is always "payload" for godb, this will be used in the server side sending program
        if(!GUtils.isValidJSON(payload))//only add to notification bar if its not json
        {
            String appLabel = context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
            gCreateNotification(context, appLabel, payload);
        }
        postNotify(context, 230, 1, "notification_received", payload);
    }

    @Override
    protected void onError(Context context, String errorId)
    {
        postNotify(context, 230, 4, "notification_error", "Failed with error code::"+errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        return super.onRecoverableError(context, errorId);
    }

    public static void gCreateNotification(Context context, String title, String message)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(com.retail.solutions.pvt.Ltd.R.drawable.icon, message, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults|=Notification.DEFAULT_SOUND;
        notification.defaults|=Notification.DEFAULT_LIGHTS;
        notification.defaults|=Notification.DEFAULT_VIBRATE;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(context, GMainActivity.class);
        intent.setComponent(cn);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //	notification.setLatestEventInfo(context, title, message, pendingIntent);
        notificationManager.notify(0, notification);
    }

    @Override
    public void postNotify(Context context, int sourceType, int notifyType, String msgType, String message)
    {
        //you should write your device register handling code here
        String str="";
        if(sourceType==230)
        {
            if(notifyType==1)
            {
                str="Notification recevied. " + message;//"notification_received" will be the msgType
                System.out.println(str);
            }
            else
            if(notifyType==2)
            {
                str="Successfully Registered device Id Is " + message;//"notification_registered" will be the msgType
                System.out.println(str);
            }
            else
            if(notifyType==3)
            {
                str="Successfully UnRegistered device " + message;//"notification_unregistered" will be the msgType
                System.out.println(str);
            }
            else
            if(notifyType==4)//"notification_error", "notification_registration_error", "notification_unregistration_error" will be the possible MsgType$. notification_error will be for common errors
            {
                str="Error. " + message;
            }
            else
            {
                str="Message. " + message;
                System.out.println(str);
            }
            handleToast(str);
        }
    }

    private void handleToast(String str)
    {
        Message msg=handler.obtainMessage();
        Bundle b=new Bundle();
        b.putString("message", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    static class GHandler extends Handler
    {
        Context context;
        GHandler(Context context)
        {
            this.context=context;
        }

        @Override
        public void handleMessage(Message msg)
        {
            String aResponse = msg.getData().getString("message");
            if(aResponse!=null)
                Toast.makeText(context,"Server Response: "+aResponse, Toast.LENGTH_LONG).show();
        }
    }

    Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            String aResponse = msg.getData().getString("message");
            if(aResponse!=null)
                Toast.makeText(getBaseContext(),"Server Response: "+aResponse, Toast.LENGTH_LONG).show();
            return true;
        }
    });

}
