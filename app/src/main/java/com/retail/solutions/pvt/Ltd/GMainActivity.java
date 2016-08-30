package com.retail.solutions.pvt.Ltd;

/*******************************************************************************
 *
 *            Main Activity
 *
 *            Copyright 2008 GoDB Tech Private Limited
 *
 * Proprietary and Confidential
 * Unauthorized distribution or copying is prohibited. All rights reserved.
 *
 * This source code is property of GoDB Tech and MUST not be copied, stored,
 * reproduced, printed, emailed, photocopied, camera captured in any form or
 * by any electronic, mechanical or other means now known or invented in future,
 * without written permission from GoDB Tech.
 *
 * Permission to use this software for any purpose is expressly denied.
 *
 ********************************************************************************/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.godbtech.notifications.GNotifiable;
import com.godbtech.notifications.GRegisterToSyncServer;
import com.godbtech.sync.GBrowserNotifyMessageEvt;
import com.godbtech.sync.GNativeSync;
import com.godbtech.sync.GSyncServerConfig;
import com.godbtech.sync.GSyncStatusEvt;
import com.godbtech.sync.GSyncable;
import com.godbtech.sync.GUtils;
import com.google.android.gcm.GCMRegistrar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.godbtech.notifications.GNotifiable;
import com.godbtech.notifications.GRegisterToSyncServer;
import com.godbtech.sync.GBrowserNotifyMessageEvt;
import com.godbtech.sync.GNativeSync;
import com.godbtech.sync.GSyncServerConfig;
import com.godbtech.sync.GSyncStatusEvt;
import com.godbtech.sync.GSyncable;
import com.godbtech.sync.GUtils;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase;
import android.content.res.AssetManager;
import android.os.Bundle;
//import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class GMainActivity  extends Activity implements GSyncable
{

    private static int SPLASH_TIME_OUT = 50000;
   // private static int SPLASH_TIME_OUT = 1000;
    //the following are application only fot secure SQLITE GoDB Sync
    public static final String DB_ENC = "rc4";//should not be made available in source code
    public static final String DB_KEY = "mykey";//should not be made available in source code
    //RC4,AES128-OFB,AES258-OFB are currently Supported algorithms

    Bundle syncDataBundle = null;
    Button syncbutton;

    private static final String TAG = "Connection not available";



    TextView tv;

    private boolean 	syncInProgress = false;
    private boolean 	didSyncSucceed = false;
    public static final String GCM_PROJECT_ID = "407176891585";//only if you need GCM notifications

    private GSyncStatusHandler gSyncStatusHandler = new GSyncStatusHandler();
    private GBrowserNotifyHandler gBrowserNotifyHandler = new GBrowserNotifyHandler();
    private GSyncResultHandler gSyncResultNotifyHandler = new GSyncResultHandler();
    private GSyncItemStatusHandler gSyncItemStatusHandler = new GSyncItemStatusHandler();



    @Override
    protected void onResume()
    {
       // checkWIFIstate();

        loaddata();

        super.onResume();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(GMainActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

        Toast toast = Toast.makeText(getApplicationContext(),
                "sync in process", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();


    }

    public void checkWIFIstate(){


        //wi-fi in enable or not
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //mobile data enable from sim card
       // NetworkInfo mobiledata=connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ( mWifi.isConnected()) {
           // Toast.makeText(getApplication(),"WI-fi Enable",Toast.LENGTH_LONG).show();
            loaddata();
            new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent i = new Intent(GMainActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);

                Toast toast = Toast.makeText(getApplicationContext(),
                        "sync in process", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

        else {



            CustomDialogClass cdd=new CustomDialogClass(GMainActivity.this);
            cdd.show();

          /*  AlertDialog alertDialog = new AlertDialog.Builder(GMainActivity.this).create();

            alertDialog.setTitle("Alert !!!");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            //alertDialog.setCustomTitle(View.inflate(R.layout.alert_dialog));

            alertDialog.setButton("Go To Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                }
            });
            alertDialog.show();*/



            Toast toast= Toast.makeText(getApplicationContext(),
                    "WI-fi is Disconnect,Please connect!!!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();


        }
    }



    public class CustomDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;

        public CustomDialogClass(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:

                    c. startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));


                   // c.finish();
                    break;
                case R.id.btn_no:
                    finish();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    public void loaddata(){

        syncbutton = (Button) findViewById(R.id.TestSyncButton);
        syncbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                loadSyncLibrary();
                doSync(syncDataBundle);
            }
        });

        syncbutton.performClick();
        Log.e("sync button pressed", syncbutton.toString());


    }



    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    protected void onStart()
    {

      //  loaddata();
        syncDataBundle = getSyncBudle(this);//get sync parameters as bundle
        //these can be obtained from a form
        setSyncParamView(syncDataBundle);//display the parameters
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    protected void setSyncParamView(Bundle b)
    {
        TextView syncParamView = (TextView)findViewById(R.id.SyncParamTextView);

      /*  String txt = (String)syncParamView.getText();

        txt = GUtils.replace(txt, "{IP}", b.getString("syncServerIP"));
        txt = GUtils.replace(txt, "{PORT}", ""+b.getInt("syncServerPort"));
        txt = GUtils.replace(txt, "{BP}", b.getString("syncServerBasePath"));
        txt = GUtils.replace(txt, "{UN}", b.getString("syncServerUsername"));
        txt = GUtils.replace(txt, "{PWD}", b.getString("syncServerPassword"));
        txt = GUtils.replace(txt, "{UID}", b.getString("syncServerUserID"));
        txt = GUtils.replace(txt, "{SM}", ""+b.getInt("syncMode"));
        txt = GUtils.replace(txt, "{L}", ""+b.getBoolean("logEnabled"));
        txt = GUtils.replace(txt, "{DB}", b.getString("dbName"));
        txt = GUtils.replace(txt, "{PN}", getClass().getPackage().getName());

        syncParamView.setText(txt);*/
    }

    protected static Bundle getSyncBudle(Context context)
    {
        Bundle b = new Bundle();

        //db name, this will be available in that package
        //do not change this, this is the only directory where this activity can write to
        //specifying another directory will cause the sync to fail
        //String dbDir = context.getApplicationInfo().dataDir + "/databases/";
        String dbDir = context.getApplicationInfo().dataDir + "/";
        java.io.File f = new java.io.File(dbDir);
        if(!f.exists())
            new java.io.File(dbDir).mkdirs();
      //  String dbName = dbDir + "sync.bdb.db";


		//start SQLITE ENCRYPTION
		//if you are using encrypted SQLITE Sync agent use code below
		//Note using this code on an unencrypted SQlite Sync agent will not work
		//For obtaining SQLITE encrypted Sync Agent contact GoDB support
		//start
		String key=DB_KEY;
		//NOTE: Important, For demonstration purposes key is being hardcoded here,
		//In production this key should not be available stored on the device
		//or hardcoded into the application. How to handle key is outside the scope
		//of this demo app.
		String algorithm=DB_ENC;//RC4,AES128-OFB,AES258-OFB are currently Supported algorithms
		//dbName = dbName + ";key="+algorithm+":"+key;
		//end SQLITE ENCRYPTION

        //For SQLITE without encryption key and algorithm will be ignored
      /*  dbName = dbName + ";key="+DB_ENC+":"+DB_KEY;*/

        String dbName = "/data/data/" + GMainActivity.class.getPackage().getName() + "/sync.bdb.db";

        b.putString("dbName", dbName);


        b.putString("syncServerUsername", 	"1465805506");//sync user name changed on 31/05/2016

       // b.putString("syncServerUsername", 	"1464010285");//sync user name changed on 25/05/2016


      //  b.putString("syncServerUsername", 	"1463567540");//till 25/05/2016

        //b.putString("syncServerPassword", 	"9999");//sync user name

        b.putString("syncServerPassword", GUtils.getSHADigest("SHA-256", "Admin@123"));//SHA-256 sync server password
        //b.putString("syncServerPassword", GUtils.getMD5Hash("9999"));//MD5 server password
        //b.putString("syncServerPassword", 	"81DC9BDB52D04DC20036DBD8313ED055");//MD5 server password
        b.putString("syncServerUserID", 	"3");//if needed

        b.putInt("syncMode", 				GSyncServerConfig.SYNC_DELTA);//delta sync
        b.putBoolean("logEnabled", 			true);//logs "http.log", "sync.log", "syncstat.log" will be available in
        //folder /data/data/getClass().getPackage().getName()
        //after synchronization
        //b.putString("syncServerIP", 		"192.168.0.50");//sync server ip/domain
        b.putString("syncServerIP", 		"52.76.28.14");//sync server ip/domain if https is supported

        b.putInt("syncServerPort", 			8080);//sync server http port
        //b.putInt("syncServerPort", 		443);//sync server https port if SSL is enabled

        b.putString("syncServerBasePath", 	"/godbss/");//sync server basepath ex: http://www.yourdomain.com/godbss/, here godbss is the basepath

        //socket parameters
        b.putInt("sockConnectTimeoutMillis", 5000);//five seconds
        b.putInt("sockSendTimeoutMillis", 	30000);//30 seconds
        b.putInt("sockRecvTimeoutMillis", 	30000);//30 seconds

        //proxy parameters if needed
        b.putBoolean("proxyEnabled", 		false);
        b.putString("httpProxy", 			"192.168.0.123");//only valid if proxyEnabled is set to true
        b.putInt("httpProxyPort", 			8080);//only valid if proxyEnabled is set to true

        b.putString("d4s", 					" ");//D4S

        //b.putString("chunkedTableList", 	"fsm_frm_mst");//name of the table which you want to download in chunks
        //b.putInt("maxRecChunkSize", 		999);//chunk size in number of records
        b.putBoolean("withDsEnabled", 		false);//withds=0 change to true to enable it

        b.putString("gcmProjectID", 		GCM_PROJECT_ID);//only if you need GCM notifications

        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case GoSyncActivity.INTENT_CODE:
                if(resultCode == RESULT_OK)
                {
                    boolean b = data.getBooleanExtra("syncstat", false);
                    if(b)
                    {
                        Toast.makeText(this, "Sync Succeded", Toast.LENGTH_LONG).show();
						/*String dbDir = this.getApplicationInfo().dataDir + "/";
						java.io.File f = new java.io.File(dbDir);
						if(!f.exists())
							new java.io.File(dbDir).mkdirs();
						String dbName = dbDir + "sync.bdb.db";
						GDBHelper helper = new GDBHelper(this, dbName);
						SQLiteDatabase db = helper.getWritableDatabase();
						db.execSQL("delete from InvoiceMaster");
						db.execSQL("delete from InvoiceDetail");
						db.execSQL("INSERT INTO InvoiceMaster (InvoiceNo, InvoiceDate, CustCode, GrossAmt, Discount, NetAmt, RepID, IsCommitted, Status) VALUES ('INV000000002', '20150429', 'C000010', '    232.00', '', '    232.00', '0', 1, 'Pending')");
						db.execSQL("INSERT INTO InvoiceDetail (InvoiceNo, ProdCode, QtySold, QtyBonus, Rate, Discount, TotalAmt, RepId) VALUES ('INV000000002', 'P000010', '1', '','32', '', '32', '0')");
						db.execSQL("INSERT INTO InvoiceDetail (InvoiceNo, ProdCode, QtySold, QtyBonus, Rate, Discount, TotalAmt, RepId) VALUES ('INV000000002', 'P000007', '5', '','40', '', '200', '0')");
						db.close();*/
                    }
                    else
                        Toast.makeText(this, "Sync Failed", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected GSyncServerConfig getSyncConfigFromSyncIntent(Bundle syncDataBundle)
    {
        GSyncServerConfig gCfg = new GSyncServerConfig();
        gCfg.setSyncServerAddress(syncDataBundle.getString("syncServerIP"));// set SyncServer Address, can be IP
        gCfg.setSyncServerPort(syncDataBundle.getInt("syncServerPort", 80));// set SyncServer Port
        gCfg.setSyncServerBasePath(syncDataBundle.getString("syncServerBasePath"));// set Base path
        gCfg.setSyncServerUsername(syncDataBundle.getString("syncServerUsername"));
        gCfg.setSyncServerPassword(syncDataBundle.getString("syncServerPassword"));
        gCfg.setSyncServerUserID(syncDataBundle.getString("syncServerUserID"));
        gCfg.setLogEnabled(true);//enable logging
        gCfg.setDBName(syncDataBundle.getString("dbName"));//db name
        gCfg.setSyncMode(syncDataBundle.getInt("syncMode", 1));// set Synchronization to full sync if missing
        gCfg.setUseProxy(syncDataBundle.getBoolean("proxyEnabled", false));
        gCfg.setHttpProxy(syncDataBundle.getString("httpProxy"));
        gCfg.setHttpProxyPort(syncDataBundle.getInt("httpProxyPort", 8080));
        gCfg.setSockConnectTimeoutMillis(syncDataBundle.getInt("sockConnectTimeoutMillis", 0));
        gCfg.setSockSendTimeoutMillis(syncDataBundle.getInt("sockSendTimeoutMillis", 0));
        gCfg.setSockRecvTimeoutMillis(syncDataBundle.getInt("sockRecvTimeoutMillis", 0));
        gCfg.setD4S(syncDataBundle.getString("d4s"));
        gCfg.setChunkedTableList(syncDataBundle.getString("chunkedTableList"));
        gCfg.setMaxRecChunkSize(syncDataBundle.getInt("maxRecChunkSize", 0));
        gCfg.setWithDSEnabled(syncDataBundle.getBoolean("withDsEnabled", false));//withds=1
        //Log.d("GSync", "Syncing with " + gCfg.toString());
        return gCfg;
    }


    public void doSync(Bundle syncDataBundle)
    {
        GNativeSync gNSync = GNativeSync.getNativeSyncSingleton();

        gNSync.addSyncListener(this);// important register sync listener to recieve notifications
        GSyncServerConfig gCfg = getSyncConfigFromSyncIntent(syncDataBundle);
        String rootPath = syncDataBundle.getString("dbName");
        if(rootPath==null || rootPath.lastIndexOf("/")<=0) return;
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
        extractPEMFile(getAssets(), rootPath);//extracts on every launch, you can change it to extract only if file isnt present.
        //clearViews();
        initDB(gCfg.getDBName(), gNSync._hasCodec());
        gNSync.startSync(gCfg);
    }

//	private void clearViews()
//	{
//		statusEditText.setText("");
//	}

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
//            Toast.makeText(this, "Sync failed", Toast.LENGTH_LONG).show();
            Log.i("GSync", "Sync failed");



               //    Intent navegatetoGmain=new Intent(GMainActivity.this,GMainActivity.class);
                 //   startActivity(navegatetoGmain);



            m.setTarget(gSyncResultNotifyHandler);
        }
        else
        if(statEvt.getTStatus1() == 200)//sync succeeded
        {
            didSyncSucceed = true;
            syncInProgress = false;
           // Toast.makeText(this, "Sync Success", Toast.LENGTH_LONG).show();
            Log.i("GSync", "Sync Success");
            m.setTarget(gSyncResultNotifyHandler);
        }
        else
            m.setTarget(gSyncStatusHandler);
        m.sendToTarget();
        Log.i("GSync :: syncStatus", statEvt.toString());
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
        Log.i("GSync", gNMsgEvt.toString());
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
        Log.i("GSync", "^^^onItemStatus " + ItemName + " : " + Msg);
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
//
//		spb1.setMax(5);//currently fixed at 5
//		spb1.setProgress(s1);
//
//		//Log.i("GSync", "status1 " + s1);
//
//		spb2.setMax(s2max);
//		spb2.setProgress(s2);
//
//		spb3.setMax(s3max);
//		spb3.setProgress(s3);

        System.out.println("status2max " + bundle.getInt("status2max"));
        Log.i("GSync", "status2max " + bundle.getInt("status2max"));
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
       // Toast.makeText(this, "Sync Succeded", Toast.LENGTH_LONG).show();
    }

    private void updateItemStatus(String itemName, String msg)
    {
        if(itemName==null) itemName="";
        if(msg==null) msg="";
//		String t = statusEditText.getText().toString();
//		if(t==null)t="";
//		statusEditText.append(msg + " " + itemName + "\n");
//		scrollView.post(new Runnable()
//		{
//			public void run()
//			{
//				scrollView.smoothScrollTo(0, statusEditText.getBottom());
//			}
//		});
        Log.i("GSync", "->" + itemName + " " + msg + "\n");
    }


    class GSyncStatusHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            GMainActivity.this.updateStatus(msg);
        }
    };

    class GBrowserNotifyHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            GMainActivity.this.updateNotify(msg.getData().getString("browsernotifymsg"));
        }
    };

    class GSyncResultHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            GMainActivity.this.updateResult(msg.getData().getString("syncstatusmsg"));
        }
    };

    class GSyncItemStatusHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            GMainActivity.this.updateItemStatus(msg.getData().getString("itemname"), msg.getData().getString("msg"));
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
    //this is just to create a blank database if doesnt exist
    static void initDB(String dbName, int hasCodec)
    {
        String keyStr="";
        com.godbtech.sql.database.sqlite.SQLiteDatabase db = null;
        if(hasCodec==1)
        {
            if(dbName.indexOf(";")>0&&dbName.indexOf("=")>0)
            {
                keyStr="key='"+dbName.substring(dbName.indexOf("=")+1)+"'";
                dbName = dbName.substring(0, dbName.indexOf(";"));
            }
            db = com.godbtech.sql.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbName, null);
            if(keyStr.length()>0)
            {
                String pragma="PRAGMA "+keyStr;
                db.execSQL(pragma);//set the key
                Log.i("GSync", "testEncryptedDB:::pragma:::" + pragma);
            }
        }
        else
        {
            if(dbName.indexOf(";")>0)
                dbName=dbName.substring(0, dbName.indexOf(";"));
            db = com.godbtech.sql.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbName, null);
        }
        com.godbtech.sql.database.sqlite.SQLiteStatement st = null;;
        String res = null;
        try
        {
            st = db.compileStatement("SELECT sqlite_version()");
            res = st.simpleQueryForString();
            //st.close();
            //db.execSQL("CREATE TABLE IF NOT EXISTS t1(x, y)");
            //db.execSQL("delete from t1");
            //db.execSQL("INSERT INTO t1 VALUES (1, 2), (3, 4)");
            //st = db.compileStatement("SELECT sum(x+y) FROM t1");
            //res = st.simpleQueryForString();
            Log.i("GSync", "sqlite_version():::" + res);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(st!=null)
                st.close();//dont forget to close the db
            if(db!=null)
                db.close();//dont forget to close the db
        }
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
}



