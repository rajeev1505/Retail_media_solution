package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 5/4/16.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.retail.solutions.pvt.Ltd.R;
import com.godbtech.sync.GBrowserNotifyMessageEvt;
import com.godbtech.sync.GNativeSync;
import com.godbtech.sync.GSyncServerConfig;
import com.godbtech.sync.GSyncStatusEvt;
import com.godbtech.sync.GSyncable;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

public class GoSyncActivity extends Activity implements GSyncable,View.OnClickListener
{

    public 	static final int    INTENT_CODE = 121122;

    private GSyncStatusHandler gSyncStatusHandler = new GSyncStatusHandler();
    private GBrowserNotifyHandler gBrowserNotifyHandler = new GBrowserNotifyHandler();
    private GSyncResultHandler gSyncResultNotifyHandler = new GSyncResultHandler();
    private GSyncItemStatusHandler gSyncItemStatusHandler = new GSyncItemStatusHandler();

    private Intent syncIntent;

    EditText statusEditText = null;
    ScrollView scrollView = null;
    ProgressBar spb1;
    ProgressBar spb2;
    ProgressBar spb3;
    Button bt;

    private boolean 	syncInProgress = false;
    private boolean 	didSyncSucceed = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gsync);

        statusEditText = (EditText)findViewById(R.id.itemstatus);
        statusEditText.setKeyListener(null);

        spb1 = (ProgressBar)findViewById(R.id.status1);
        spb2 = (ProgressBar)findViewById(R.id.status2);
        spb3 = (ProgressBar)findViewById(R.id.status3);
        bt= (Button)findViewById(R.id.next_id);
        bt.setOnClickListener( this);

        scrollView = (ScrollView)findViewById(R.id.sv);

         loadSyncLibrary();

        syncIntent = getIntent();//will be passed from the main activity

        syncInProgress = true;
        didSyncSucceed = false;

        doSync(syncIntent);
    }


    protected GSyncServerConfig getSyncConfigFromSyncIntent(Intent syncIntent)
    {
        GSyncServerConfig gCfg = new GSyncServerConfig();
        gCfg.setSyncServerAddress(syncIntent.getStringExtra("syncServerIP"));// set SyncServer Address, can be IP
        gCfg.setSyncServerPort(syncIntent.getIntExtra("syncServerPort", 80));// set SyncServer Port
        gCfg.setSyncServerBasePath(syncIntent.getStringExtra("syncServerBasePath"));// set Base path
        gCfg.setSyncServerUsername(syncIntent.getStringExtra("syncServerUsername"));
        gCfg.setSyncServerPassword(syncIntent.getStringExtra("syncServerPassword"));
        gCfg.setSyncServerUserID(syncIntent.getStringExtra("syncServerUserID"));
        gCfg.setLogEnabled(true);//enable logging
        gCfg.setDBName(syncIntent.getStringExtra("dbName"));//db name
        gCfg.setSyncMode(syncIntent.getIntExtra("syncMode", 1));// set Synchronization to full sync if missing
        gCfg.setUseProxy(syncIntent.getBooleanExtra("proxyEnabled", false));
        gCfg.setHttpProxy(syncIntent.getStringExtra("httpProxy"));
        gCfg.setHttpProxyPort(syncIntent.getIntExtra("httpProxyPort", 8080));
        gCfg.setSockConnectTimeoutMillis(syncIntent.getIntExtra("sockConnectTimeoutMillis", 0));
        gCfg.setSockSendTimeoutMillis(syncIntent.getIntExtra("sockSendTimeoutMillis", 0));
        gCfg.setSockRecvTimeoutMillis(syncIntent.getIntExtra("sockRecvTimeoutMillis", 0));
        gCfg.setD4S(syncIntent.getStringExtra("d4s"));
        gCfg.setChunkedTableList(syncIntent.getStringExtra("chunkedTableList"));
        gCfg.setMaxRecChunkSize(syncIntent.getIntExtra("maxRecChunkSize", 0));
        //Log.d("GSync", "Syncing with " + gCfg.toString());
        return gCfg;
    }

    public void doSync(Intent syncIntent)
    {
        GNativeSync gNSync = GNativeSync.getNativeSyncSingleton();

        gNSync.addSyncListener(this);// important register sync listener to recieve notifications
        GSyncServerConfig gCfg = getSyncConfigFromSyncIntent(syncIntent);
        String rootPath = syncIntent.getStringExtra("dbName");
        if(rootPath==null || rootPath.lastIndexOf("/")<=0) return;
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
        extractPEMFile(getAssets(), rootPath);//extracts on every launch, you can change it to extract only if file isnt present.
        clearViews();
        gNSync.startSync(gCfg);
    }

    private void clearViews()
    {
        statusEditText.setText("");
    }

    @Override
    public void syncStatusEvent(GSyncStatusEvt statEvt)
    {
        String msg = statEvt.getMsg();
        if (msg == null)
            msg = "";
        Bundle b = new Bundle();
        b.putString("syncstatusmsg", 	msg);
        b.putInt("status1", 			statEvt.getTStatus1());
        b.putInt("status2max", 			statEvt.getStatus2Max());
        b.putInt("status2", 			statEvt.getStatus2());
        b.putInt("status3max", 			statEvt.getStatus3Max());
        b.putInt("status3", 			statEvt.getStatus3());
        Message m = Message.obtain();
        m.setData(b);

        if(statEvt.getTStatus1() == 100)//100 sync failed 200 sync succeeded
        {
            syncInProgress = false;
            didSyncSucceed = false;
            m.setTarget(gSyncResultNotifyHandler);
        }
        else
        if(statEvt.getTStatus1() == 200)//sync succeeded
        {
            didSyncSucceed = true;
            syncInProgress = false;
            m.setTarget(gSyncResultNotifyHandler);
        }
        else
            m.setTarget(gSyncStatusHandler);
        m.sendToTarget();
        //Log.i("GSync", statEvt.toString());
    }

    @Override
    public void browserNotifyEvent(GBrowserNotifyMessageEvt gNMsgEvt)
    {
        String msg = null;
        Bundle b = new Bundle();
        Message m = Message.obtain();
        msg = gNMsgEvt.getNotifySrc() + ":" + gNMsgEvt.getMsgType();
        m.setTarget(gBrowserNotifyHandler);
        b.putString("browsernotifymsg", msg);
        m.setData(b);
        m.sendToTarget();
        //Log.i("GSync", gNMsgEvt.toString());
    }

    @Override
    public void itemStatusEvent(String ItemName, String Msg)
    {
        Bundle b = new Bundle();
        Message m = Message.obtain();
        m.setTarget(gSyncItemStatusHandler);
        b.putString("itemname", ItemName);
        b.putString("msg", 		Msg);
        m.setData(b);
        m.sendToTarget();
        //Log.i("GSync", "onItemStatus " + ItemName + " : " + Msg);
    }


    private void updateStatus(Message msg)
    {
        //if(status==null) return;
        Bundle bundle = msg.getData();
        int s1 		= bundle.getInt("status1");
        int s2 		= bundle.getInt("status2");
        int s2max 	= bundle.getInt("status2max");
        int s3 		= bundle.getInt("status3");
        int s3max 	= bundle.getInt("status3max");

        spb1.setMax(5);//currently fixed at 5
        spb1.setProgress(s1);

        //Log.i("GSync", "status1 " + s1);

        spb2.setMax(s2max);
        spb2.setProgress(s2);

        spb3.setMax(s3max);
        spb3.setProgress(s3);

        //System.out.println("status2max " + bundle.getInt("status2max"));
    }

    private void updateNotify(String notification)
    {
        if(notification==null) return;
        //currently not used
    }

    private void updateResult(String notification)
    {
        if(notification==null) return;
        //currently not used
    }

    private void updateItemStatus(String itemName, String msg)
    {
        if(itemName==null) itemName="";
        if(msg==null) msg="";
        String t = statusEditText.getText().toString();
        if(t==null)t="";
        statusEditText.append(msg + " " + itemName + "\n");
        scrollView.post(new Runnable()
        {
            public void run()
            {
                scrollView.smoothScrollTo(0, statusEditText.getBottom());
            }
        });
        //Log.i("GSync", "->" + itemName + " " + msg + "\n");
    }

    @Override
    public void onClick(View v) {

        Intent intent=new Intent(v.getContext(),com.retail.solutions.pvt.Ltd.MainActivity.class);
        startActivity(intent);

    }


    class GSyncStatusHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            GoSyncActivity.this.updateStatus(msg);
        }
    };

    class GBrowserNotifyHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            GoSyncActivity.this.updateNotify(msg.getData().getString("browsernotifymsg"));
        }
    };

    class GSyncResultHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            GoSyncActivity.this.updateResult(msg.getData().getString("syncstatusmsg"));
        }
    };

    class GSyncItemStatusHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            GoSyncActivity.this.updateItemStatus(msg.getData().getString("itemname"), msg.getData().getString("msg"));
        }
    };


    @Override
    public void onBackPressed()
    {
        if(syncInProgress)
        {
            Toast.makeText(this, "Sync in process.. Please wait for it to complete.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("syncstat", didSyncSucceed);
        setResult(RESULT_OK,	returnIntent);
        finish();
    }

    /**
     * Load sync library.
     */
    private void loadSyncLibrary()
    {
        try
        {
            String trgLib = "gSyncDLL";
            System.loadLibrary(trgLib);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("GSync", e.getMessage());
        }
    }

    /**
     * extract ROOT.PEM to /data/data/yourpackage/ if it exists
     * this is to support https based synchronization
     */
    void extractPEMFile(AssetManager mgr, String rootPath)
    {
        try
        {
            String[] filelist = mgr.list("");
            for(String asset: filelist)
            {
                if(asset.equalsIgnoreCase("ROOT.pem"))
                {
                    File f = new File(rootPath, "ROOT.pem");
                    FileOutputStream os = new FileOutputStream(f);
                    byte[] buf = new byte[200 * 1024];
                    int n;
                    InputStream is = mgr.open(asset);
                    while ((n = is.read(buf)) > 0)
                        os.write(buf, 0, n);
                    os.close();
                    is.close();
                }
            }
        }
        catch(Exception e)
        {
        }
    }


}
