package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 5/4/16.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.godbtech.sql.database.sqlite.SQLiteStatement;
import com.godbtech.sync.GBrowserNotifyMessageEvt;
import com.godbtech.sync.GNativeSync;
import com.godbtech.sync.GSyncServerConfig;
import com.godbtech.sync.GSyncStatusEvt;
import com.godbtech.sync.GSyncable;
import com.godbtech.sync.GUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements GSyncable {
    final Context context = this;


    //the following are application only fot secure SQLITE GoDB Sync
    public static final String DB_ENC = "rc4";//should not be made available in source code
    public static final String DB_KEY = "mykey";//should not be made available in source code
    //RC4,AES128-OFB,AES258-OFB are currently Supported algorithms

    Bundle syncDataBundle = null;
    Button syncbutton1;

    private boolean syncInProgress = false;
    private boolean didSyncSucceed = false;
    public static final String GCM_PROJECT_ID = "407176891585";//only if you need GCM notifications

    private GSyncStatusHandler gSyncStatusHandler = new GSyncStatusHandler();
    private GBrowserNotifyHandler gBrowserNotifyHandler = new GBrowserNotifyHandler();
    private GSyncResultHandler gSyncResultNotifyHandler = new GSyncResultHandler();
    private GSyncItemStatusHandler gSyncItemStatusHandler = new GSyncItemStatusHandler();

    private String myString=null;
    // this is my new created Sting


    VideoView videoView;
    TextView textView_scrolling;
    ImageView imageView;
    TextView customerId;
    String Ad_Play, Store_id, Store_Media_Id, startdate, enddate,
            starttime, endtime, Str_store_id, Str_store_media_id, Cust_id, Ad_Play_Click;
    int videoIncrement, imageIncrement = 0;
    int i = 0;
    int j = 0;
    ArrayList<Uri> main_ad_video = new ArrayList<Uri>();
    ArrayList<Uri> cont_sen_ad_video_1 = new ArrayList<Uri>();
    ArrayList<Uri> cont_sen_ad_video_2 = new ArrayList<Uri>();
    SimpleDateFormat timeFormat;
    SimpleDateFormat Addatetime, Addatetimeforclick;

    private LinearLayout horizontalOuterLayout;
    private HorizontalScrollView horizontalScrollview;
    private int scrollMax;
    private int scrollPos = 0;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;
    private TimerTask faceAnimationSchedule;
    private Timer scrollTimer = null;
    private Timer clickTimer = null;
    private Timer faceTimer = null;
    int clickcount = 0;
    String touch_Count;
    String str ;
    String videoname;

    Time validateTime;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Date date = new Date();
        final CharSequence validtime = DateFormat.format("HH:mm:ss", date.getTime());
        Log.e("##validtime", validtime.toString());


        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(new yourAdapter(this, new String[]{"1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13", "14", "15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30"}));


        //call funtion, create video and image folder in external storage of POS device
        creatFoldervideo1();
        // creatFoldervideo2();
        // creatFoldervideo3();
        creatFolderimage();


        horizontalScrollview = (HorizontalScrollView) findViewById(R.id.horiztonal_scrollview_id);
        horizontalOuterLayout = (LinearLayout) findViewById(R.id.horiztonal_outer_layout_id);
        horizontalScrollview.setHorizontalScrollBarEnabled(false);
        ViewTreeObserver viewTreeObserver = horizontalOuterLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                horizontalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                startAutoScrolling();
                LockScroll();

            }
        });


        Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());

        Admainvideo();
        // Adcontsenvideo1();
        //Adcontsenvideo2();


        try {

            //Blinking Image code Here.....................................
            imageView = (ImageView) findViewById(R.id.blinking_image_id);
            File imagedir = new File(Environment.getExternalStorageDirectory() + "/1464772267" + "/CLogo");
            Log.e("####******########", imagedir.toString());
            imagedir.mkdir();
            File imageList[] = imagedir.listFiles();
            Bitmap b = BitmapFactory.decodeFile(imageList[imageIncrement].getAbsolutePath());
            imageView.setImageBitmap(b);
            imageView = (ImageView) findViewById(R.id.blinking_image_id);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Company Logo not Found",Toast.LENGTH_SHORT).show();

        }


        try {

            //all retail_ad_ticker call here and set here......................
            final ArrayList array_list = databasehandler.getAllAdTicker();
            Log.e("###############", array_list.toString().replace("[", "").replace("]", "").replace(",", "----"));
            textView_scrolling = (TextView) findViewById(R.id.scrolling_text_id);
            textView_scrolling.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView_scrolling.setSelected(true);
            textView_scrolling.setSingleLine(true);
            textView_scrolling.setText(array_list.toString().replace("[", "").replace("]", "").replace(",", "."));
        }

        catch (Exception e){

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"News Stream Not Found",Toast.LENGTH_SHORT).show();
        }

        try{

            //all retail_cust_id call here and set here......................
            final ArrayList array_list_custid = databasehandler.getAllCust_Id();
            Log.e("###############", array_list_custid.toString());
            Cust_id = array_list_custid.get(0).toString();
            customerId = (TextView) findViewById(R.id.SecScr_show_customer_id);
            customerId.setText(Cust_id.toString().replace("[", "").replace("]", "").replace(",", ""));


        }

        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Customer id not found",Toast.LENGTH_SHORT).show();


        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();




    }//onCreate end here.......................




    public void validatevideoname() {

        File videodir = new File(Environment.getExternalStorageDirectory() + "/1464772267"+"/MainAd");

        Log.e("####******########", videodir.toString());
        final File[] filest = videodir.listFiles();
        if (videodir.exists() && videodir.isDirectory()) {
            final File[] files = videodir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                        } else {  // it is a file...
                            main_ad_video.add(Uri.fromFile(file));
                        }
                    }
                }
            }

        }

        try
        {
            String uriString = String.valueOf(main_ad_video.get(videoIncrement));
            URI uri = new URI(uriString);

            URL videoUrl = uri.toURL();
            File tempFile = new File(videoUrl.getFile());
            videoname = tempFile.getName();
            Log.e("^^^",videoname);


        }
        catch (Exception e)
        {

        }


      /*  main_ad_video.size();
        Log.e("rajeev", String.valueOf(main_ad_video.size()));

        str=  main_ad_video.get(videoIncrement).toString();
        int length=str.length();
        System.out.println("str length "+ length);
        Log.e("**Rajeev",str);

        if (str.endsWith(".mp4")) {
            str = str.substring(37,43);

            Log.e("!!!Rajeev",str);
        }*/


        if(videoname.startsWith("A")){
            //  Toast.makeText(getApplicationContext(),"video's start with A",Toast.LENGTH_LONG).show();
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    Addatetimeforclick = new SimpleDateFormat("yyyyMMDDHHmmss");

                    final Calendar cal1233 = Calendar.getInstance();

                    Ad_Play_Click = Addatetimeforclick.format(cal1233.getTime());

                    Log.e("%%%%%", Ad_Play_Click.toString());

                    Store_Media_Id = Str_store_media_id;

                    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
                    cdd.show();

                    clickcount = clickcount + 1;
                    if (clickcount == 1) {

                        //  Toast.makeText(getApplicationContext(), "Click : 1", Toast.LENGTH_LONG).show();

                    } else {
                        // Toast.makeText(getApplicationContext(), "Click :" + clickcount, Toast.LENGTH_LONG).show();
                        touch_Count = String.valueOf(clickcount);
                    }

                    return false;
                }
            });
        }


        else {


            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // do nothing here......
                    return true;
                }
            });


        }


        /*if ( str.startsWith("B")){

            Toast.makeText(getApplicationContext(), "video's start with B", Toast.LENGTH_LONG).show();

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // do nothing here......
                    return true;
                }
            });

        }*/

    }


    public void checkWIFIstate() {


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Toast.makeText(getApplication(),"WI-fi Enable",Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(getApplication(), "WI-fi Disable", Toast.LENGTH_LONG).show();


        }
    }

    // uploading function define here.........and call after every video insert
    public void loaddatamainactivity() {

        syncbutton1 = (Button) findViewById(R.id.MainActivitySync);
        syncbutton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loadSyncLibrary();
                doSync(syncDataBundle);

            }
        });


        //Toast.makeText(this, " uploading....", Toast.LENGTH_LONG).show();
        syncbutton1.performClick();
        Log.e("EEEEEEE", syncbutton1.toString());


    }

    //create video and image folder in external storage of POS device(Main ad video)
    public void creatFoldervideo1() {

        //  main_ad_video folder Name Specify Here
        File videodir = new File(Environment.getExternalStorageDirectory() + "/1464772267"+"/MainAd");
        Log.e("####******########", videodir.toString());
        if (videodir.exists() && videodir.isDirectory()) {
            final File[] files = videodir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                        } else {  // it is a file...
                            main_ad_video.add(Uri.fromFile(file));
                        }
                    }
                }
            }

        } else {
            // videodir.mkdir();
        }


    }

    //create video  folder in external storage of POS device(contact sensitive ad video1)
    public void creatFoldervideo2() {

        File videodir2 = new File(Environment.getExternalStorageDirectory() +  "/1464772267"+"/CSensitive1");
        Log.e("####******########", videodir2.toString());
        final File[] filest2 = videodir2.listFiles();
        if (videodir2.exists() && videodir2.isDirectory()) {
            final File[] files = videodir2.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                        } else {  // it is a file...
                            cont_sen_ad_video_1.add(Uri.fromFile(file));
                        }
                    }
                }
            }

        } else {
            // videodir2.mkdir();
        }


    }

    //create video folder in external storage of POS device(contact sensitive ad video2)
    public void creatFoldervideo3() {
        File videodir3 = new File(Environment.getExternalStorageDirectory() +  "/1464772267"+"/CSensitive2");
        Log.e("####******########", videodir3.toString());
        final File[] filest3 = videodir3.listFiles();
        if (videodir3.exists() && videodir3.isDirectory()) {
            final File[] files = videodir3.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                        } else {  // it is a file...
                            cont_sen_ad_video_2.add(Uri.fromFile(file));
                        }
                    }
                }
            }

        } else {
            //videodir3.mkdir();
        }


    }


    //create image folder in external storage of POS device(company logo)
    public void creatFolderimage() {

        File imagedir = new File(Environment.getExternalStorageDirectory() + "/1464772267"+"/CLogo");
        Log.e("####******########", imagedir.toString());
        imagedir.mkdir();
    }


    //inseet main ad video details into data base here................
    public void Admainvideo() {
        Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());

        ArrayList<Video_Data> videodata = new ArrayList<Video_Data>();
        videodata = databasehandler.getVideosDetails();
        Log.e("####", "#####size:" + videodata.size());

        ArrayList<Video_Data> mediaclick = new ArrayList<Video_Data>();
        mediaclick = databasehandler.getAllMediaClick();
        Log.e("####", "#####size:" + mediaclick.size());

        Date date = new Date();
        final CharSequence s = DateFormat.format("yyyy-MM-dd ", date.getTime());
        final Calendar c1 = Calendar.getInstance();
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        Addatetime = new SimpleDateFormat("yyyyMMDDHHmmssmm");

        final List<String> storeidlist = databasehandler.getStoreid();
        Log.e("###############", storeidlist.toString().replace("[", "").replace("]", ""));
        Str_store_id = storeidlist.toString().replace("[", "").replace("]", "");


        final List<String> storemediaid = databasehandler.getMediaid();
        Log.e("###############", storemediaid.toString().replace("[", "").replace("]", ""));
        Str_store_media_id = storemediaid.toString().replace("[", "").replace("]", "");


        // attach videos to videoview xml and video1.............
        videoView = (VideoView) findViewById(R.id.videoView1);
        videoView.setVideoURI(main_ad_video.get(videoIncrement));






        videoView.start();
        startdate = s.toString();
        starttime = timeFormat.format(c1.getTime());
        Ad_Play = Addatetime.format(c1.getTimeInMillis());

        Store_id = Str_store_id;
        Store_Media_Id = Str_store_media_id;

        validatevideoname();

        /*videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Addatetimeforclick = new SimpleDateFormat("yyyyMMDDHHmmss");

                final Calendar cal1233 = Calendar.getInstance();

                Ad_Play_Click = Addatetimeforclick.format(cal1233.getTime());

                Log.e("%%%%%", Ad_Play_Click.toString());

                Store_Media_Id = Str_store_media_id;

                CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
                cdd.show();

                clickcount = clickcount + 1;
                if (clickcount == 1) {

                    Toast.makeText(getApplicationContext(), "Click : 1", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Click :" + clickcount, Toast.LENGTH_LONG).show();
                    touch_Count = String.valueOf(clickcount);
                }

                return false;
            }
        });*/


        // call setOnPreparedListener for set default mute option........
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0, 0);
            }
        });

        // videoIncrement++;

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Calendar c2 = Calendar.getInstance();
                enddate = s.toString();
                Ad_Play = Addatetime.format(c2.getTime());
                endtime = timeFormat.format(c2.getTimeInMillis());
                Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());
                databasehandler.getWritableDatabase();

                //video details insert from here...
                databasehandler.insertVideoData(Ad_Play, Store_id, Store_Media_Id, videoname, startdate, enddate, starttime, endtime);

                // uploading function call here
                loaddatamainactivity();


               /* clickcount = 0;
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {


                        CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
                        cdd.show();

                        clickcount = clickcount + 1;
                        if (clickcount == 1) {
                            Toast.makeText(getApplicationContext(), "Click : 1", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Click :" + clickcount, Toast.LENGTH_LONG).show();
                            touch_Count = String.valueOf(clickcount);
                        }

                        return false;
                    }
                });*/


                videoIncrement++;
                if (videoIncrement < main_ad_video.size()) {
                    videoView = (VideoView) findViewById(R.id.videoView1);
                    videoView.setVideoURI(main_ad_video.get(videoIncrement));
                    videoView.start();
                    Calendar c3 = Calendar.getInstance();
                    startdate = s.toString();
                    Ad_Play = Addatetime.format(c3.getTimeInMillis());
                    starttime = timeFormat.format(c3.getTime());
                    enddate = s.toString();
                    endtime = timeFormat.format(c3.getTime());
                    validatevideoname();


                } else {
                    videoIncrement = 0;
                    videoView = (VideoView) findViewById(R.id.videoView1);
                    videoView.setVideoURI(main_ad_video.get(videoIncrement));
                    videoView.start();
                    Calendar c4 = Calendar.getInstance();
                    startdate = s.toString();
                    Ad_Play = Addatetime.format(c4.getTimeInMillis());
                    starttime = timeFormat.format(c4.getTime());
                    enddate = s.toString();
                    endtime = timeFormat.format(c4.getTime());
                }
            }
        });





    }


    public class CustomDialogClass extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button customer_ok, customer_cal;
        public EditText Edit_mobile_no;

        public CustomDialogClass(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_popup);

            customer_ok = (Button) findViewById(R.id.button_cutomer_ok);
            customer_cal = (Button) findViewById(R.id.button_customer_cancel);
            Edit_mobile_no = (EditText) findViewById(R.id.edit_customermobile);


            customer_ok.setOnClickListener(this);
            customer_cal.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_cutomer_ok:
                    Video_DataBase_Helper helper = new Video_DataBase_Helper(getApplicationContext());

                    if (Edit_mobile_no.getText().toString().length() > 10 || Edit_mobile_no.getText().toString().length() != 10) {
                        Edit_mobile_no.setError("Invalid Number");
                        Edit_mobile_no.setInputType(InputType.TYPE_CLASS_PHONE);

                        KeyListener keyListener = DigitsKeyListener.getInstance("1234567890");
                        Edit_mobile_no.setKeyListener(keyListener);
                        return;
                    }

                    if (helper.CheckIsDataAlreadyInDBorNot(Edit_mobile_no.getText().toString()))

                    {
                        Toast toast1 = Toast.makeText(MainActivity.this, "MOBILE NUMBER ALREADY REGISTERED", Toast.LENGTH_SHORT);
                        toast1.show();
                        return;
                    } else if (helper.InsertdataintoRetail_click(Edit_mobile_no.getText().toString(), Str_store_media_id, Ad_Play, touch_Count))

                    {
                        Toast toast = Toast.makeText(MainActivity.this, "Thank You", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    dismiss();

                    break;
                case R.id.button_customer_cancel:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }


/*    //inseet contact sensitive details video 1 details into data base here................
    public void Adcontsenvideo1() {

        Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());

try {
        ArrayList<Video_Data> videodatacont = new ArrayList<Video_Data>();
        videodatacont = databasehandler.getVideosContDetails();
        Log.e("####", "#####size:" + videodatacont.size());


        Date date = new Date();
        final CharSequence s = DateFormat.format("yyyy-MM-dd ", date.getTime());
        final Calendar c1 = Calendar.getInstance();
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        Addatetime = new SimpleDateFormat("yyyyMMDDHHmmss");


        final List<String> storeidlist = databasehandler.getStoreid();
        Log.e("###############", storeidlist.toString().replace("[", "").replace("]", ""));
        Str_store_id = storeidlist.toString().replace("[", "").replace("]", "");


        final List<String> storemediaid = databasehandler.getMediaid();
        Log.e("###############", storemediaid.toString().replace("[", "").replace("]", ""));
        Str_store_media_id = storemediaid.toString().replace("[", "").replace("]", "");


        // attach videos to videoview xml and video1.............
        videoView = (VideoView) findViewById(R.id.videoView2);
        videoView.setVideoURI(cont_sen_ad_video_1.get(videoIncrement));
        videoView.start();
        startdate = s.toString();
        starttime = timeFormat.format(c1.getTime());
        Ad_Play = Addatetime.format(c1.getTime());
        Store_id = Str_store_id;
        Store_Media_Id = Str_store_media_id;

        // call setOnPreparedListener for set default mute option........
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0, 0);
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Calendar c2 = Calendar.getInstance();
                enddate = s.toString();
                Ad_Play = Addatetime.format(c2.getTime());
                endtime = timeFormat.format(c2.getTime());
                Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());
                databasehandler.getWritableDatabase();

                //video details insert from here...
                databasehandler.insertVideoDataCont(Ad_Play, Store_id, Store_Media_Id, cont_sen_ad_video_1.get(i).toString(), startdate, enddate, starttime, endtime);

                // uploading function call here
                loaddatamainactivity();

                videoIncrement++;

                if (videoIncrement < cont_sen_ad_video_1.size()) {
                    videoView = (VideoView) findViewById(R.id.videoView2);
                    videoView.setVideoURI(cont_sen_ad_video_1.get(videoIncrement));
                    videoView.start();
                    Calendar c3 = Calendar.getInstance();
                    startdate = s.toString();
                    Ad_Play = Addatetime.format(c3.getTime());
                    starttime = timeFormat.format(c3.getTime());
                    enddate = s.toString();
                    endtime = timeFormat.format(c3.getTime());


                } else {
                    videoIncrement = 0;
                    videoView = (VideoView) findViewById(R.id.videoView2);
                    videoView.setVideoURI(cont_sen_ad_video_1.get(videoIncrement));
                    videoView.start();
                    Calendar c4 = Calendar.getInstance();
                    startdate = s.toString();
                    Ad_Play = Addatetime.format(c4.getTime());
                    starttime = timeFormat.format(c4.getTime());
                    enddate = s.toString();
                    endtime = timeFormat.format(c4.getTime());

                }
            }
        });
    }

        catch (IndexOutOfBoundsException indexoutofboundException){
            indexoutofboundException.printStackTrace();

        }


    }


    //inseet contact sensitive details video 2 details into data base here................
    public void Adcontsenvideo2() {
        Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());

try {


    ArrayList<Video_Data> videodatacont1 = new ArrayList<Video_Data>();
    videodatacont1 = databasehandler.getVideosCont1Details();
    Log.e("####", "#####size:" + videodatacont1.size());


    Date date = new Date();
    final CharSequence s = DateFormat.format("yyyy-MM-dd ", date.getTime());
    final Calendar c1 = Calendar.getInstance();
    timeFormat = new SimpleDateFormat("HH:mm:ss");
    Addatetime = new SimpleDateFormat("yyyyMMDDHHmmss");


    final List<String> storeidlist = databasehandler.getStoreid();
    Log.e("###############", storeidlist.toString().replace("[", "").replace("]", ""));
    Str_store_id = storeidlist.toString().replace("[", "").replace("]", "");


    final List<String> storemediaid = databasehandler.getMediaid();
    Log.e("###############", storemediaid.toString().replace("[", "").replace("]", ""));
    Str_store_media_id = storemediaid.toString().replace("[", "").replace("]", "");

    // attach videos to videoview xml and video1.............
    videoView = (VideoView) findViewById(R.id.videoView3);
    videoView.setVideoURI(cont_sen_ad_video_2.get(j));
    videoView.start();
    startdate = s.toString();
    starttime = timeFormat.format(c1.getTime());
    Ad_Play = Addatetime.format(c1.getTime());
    Store_id = Str_store_id;
    Store_Media_Id = Str_store_media_id;

    // call setOnPreparedListener for set default mute option........
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

            mp.setVolume(0, 0);
        }
    });

    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Calendar c2 = Calendar.getInstance();
            enddate = s.toString();
            Ad_Play = Addatetime.format(c2.getTime());
            endtime = timeFormat.format(c2.getTime());
            Video_DataBase_Helper databasehandler = new Video_DataBase_Helper(getApplicationContext());
            databasehandler.getWritableDatabase();

            //video details insert from here...
            databasehandler.insertVideoDataCont1(Ad_Play, Store_id, Store_Media_Id, cont_sen_ad_video_2.get(j).toString(), startdate, enddate, starttime, endtime);

            // uploading function call here..
            loaddatamainactivity();

            j++;

            if (j < cont_sen_ad_video_2.size()) {
                videoView = (VideoView) findViewById(R.id.videoView3);
                videoView.setVideoURI(cont_sen_ad_video_2.get(j));
                videoView.start();
                Calendar c3 = Calendar.getInstance();
                startdate = s.toString();
                Ad_Play = Addatetime.format(c3.getTime());
                starttime = timeFormat.format(c3.getTime());
                enddate = s.toString();
                endtime = timeFormat.format(c3.getTime());


            } else {
                j = 0;
                videoView = (VideoView) findViewById(R.id.videoView3);
                videoView.setVideoURI(cont_sen_ad_video_2.get(j));
                videoView.start();
                Calendar c4 = Calendar.getInstance();
                startdate = s.toString();
                Ad_Play = Addatetime.format(c4.getTime());
                starttime = timeFormat.format(c4.getTime());
                enddate = s.toString();
                endtime = timeFormat.format(c4.getTime());

            }
        }
    });
}

        catch (IndexOutOfBoundsException indexoutofboundException){
            indexoutofboundException.printStackTrace();

        }

    }*/


   /* public void getScrollMaxAmount() {
        int actualWidth = (horizontalOuterLayout.getMeasuredWidth() - 800);
        scrollMax = actualWidth;
    }*/


    public void getScrollMaxAmount() {
        int actualWidth = (horizontalOuterLayout.getMeasuredWidth() - 2048);
        scrollMax = actualWidth;
    }

    /*for Locking Scroll upper side company images*/
    public void LockScroll() {
        ((LockableScrollView) findViewById(R.id.horiztonal_scrollview_id)).setScrollingEnabled(false);
    }


    public void startAutoScrolling() {
        if (scrollTimer == null) {
            scrollTimer = new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if (scrollerSchedule != null) {
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 10, 10);
        }
    }

    public void moveScrollView() {
        scrollPos = (int) (horizontalScrollview.getScrollX() + 1.0);
        if (scrollPos >= scrollMax) {
            scrollPos = 0;
        }
        horizontalScrollview.scrollTo(scrollPos, 0);

    }


    public void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onPause() {
        super.onPause();
        finish();
    }

    public void onDestroy() {
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimerTaks(faceAnimationSchedule);
        clearTimers(scrollTimer);
        clearTimers(clickTimer);
        clearTimers(faceTimer);

        clickSchedule = null;
        scrollerSchedule = null;
        faceAnimationSchedule = null;
        scrollTimer = null;
        clickTimer = null;
        faceTimer = null;
        super.onDestroy();
    }

    private void clearTimers(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void clearTimerTaks(TimerTask timerTask) {
        if (timerTask != null) {
            timerTask.cancel();

        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.retail.solutions.pvt.Ltd/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }


    @Override
    protected void onStart() {
        syncDataBundle = getSyncBudle(this);//get sync parameters as bundle
        //these can be obtained from a form
        setSyncParamView(syncDataBundle);//display the parameters
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction2 = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.retail.solutions.pvt.Ltd/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction2);
    }


    protected void setSyncParamView(Bundle b) {
        TextView syncParamView = (TextView) findViewById(R.id.SyncParamTextView);


    }

    protected static Bundle getSyncBudle(Context context) {
        Bundle b = new Bundle();

        //db name, this will be available in that package
        //do not change this, this is the only directory where this activity can write to
        //specifying another directory will cause the sync to fail
        //String dbDir = context.getApplicationInfo().dataDir + "/databases/";
        String dbDir = context.getApplicationInfo().dataDir + "/";
        File f = new File(dbDir);
        if (!f.exists())
            new File(dbDir).mkdirs();
        //  String dbName = dbDir + "sync.bdb.db";


        //start SQLITE ENCRYPTION
        //if you are using encrypted SQLITE Sync agent use code below
        //Note using this code on an unencrypted SQlite Sync agent will not work
        //For obtaining SQLITE encrypted Sync Agent contact GoDB support
        //start
        String key = DB_KEY;
        //NOTE: Important, For demonstration purposes key is being hardcoded here,
        //In production this key should not be available stored on the device
        //or hardcoded into the application. How to handle key is outside the scope
        //of this demo app.
        String algorithm = DB_ENC;//RC4,AES128-OFB,AES258-OFB are currently Supported algorithms
        //dbName = dbName + ";key="+algorithm+":"+key;
        //end SQLITE ENCRYPTION

        //For SQLITE without encryption key and algorithm will be ignored
      /*  dbName = dbName + ";key="+DB_ENC+":"+DB_KEY;*/

        String dbName = "/data/data/" + GMainActivity.class.getPackage().getName() + "/sync.bdb.db";

        b.putString("dbName", dbName);

        b.putString("syncServerUsername", "1465805506");//sync user name

        //b.putString("syncServerPassword", 	"9999");//sync user name

        b.putString("syncServerPassword", GUtils.getSHADigest("SHA-256", "Admin@123"));//SHA-256 sync server password
        //b.putString("syncServerPassword", GUtils.getMD5Hash("9999"));//MD5 server password
        //b.putString("syncServerPassword", 	"81DC9BDB52D04DC20036DBD8313ED055");//MD5 server password
        b.putString("syncServerUserID", "3");//if needed

        b.putInt("syncMode", GSyncServerConfig.SYNC_DELTA);//delta sync
        b.putBoolean("logEnabled", true);//logs "http.log", "sync.log", "syncstat.log" will be available in
        //folder /data/data/getClass().getPackage().getName()
        //after synchronization
        //b.putString("syncServerIP", 		"192.168.0.50");//sync server ip/domain
        b.putString("syncServerIP", "52.76.28.14");//sync server ip/domain if https is supported

        b.putInt("syncServerPort", 8080);//sync server http port
        //b.putInt("syncServerPort", 		443);//sync server https port if SSL is enabled

        b.putString("syncServerBasePath", "/godbss/");//sync server basepath ex: http://www.yourdomain.com/godbss/, here godbss is the basepath

        //socket parameters
        b.putInt("sockConnectTimeoutMillis", 5000);//five seconds
        b.putInt("sockSendTimeoutMillis", 30000);//30 seconds
        b.putInt("sockRecvTimeoutMillis", 30000);//30 seconds

        //proxy parameters if needed
        b.putBoolean("proxyEnabled", false);
        b.putString("httpProxy", "192.168.0.123");//only valid if proxyEnabled is set to true
        b.putInt("httpProxyPort", 8080);//only valid if proxyEnabled is set to true

        b.putString("d4s", "Synctype=RS_Video");//D4S

        //b.putString("chunkedTableList", 	"fsm_frm_mst");//name of the table which you want to download in chunks
        //b.putInt("maxRecChunkSize", 		999);//chunk size in number of records
        b.putBoolean("withDsEnabled", false);//withds=0 change to true to enable it

        b.putString("gcmProjectID", GCM_PROJECT_ID);//only if you need GCM notifications

        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GoSyncActivity.INTENT_CODE:
                if (resultCode == RESULT_OK) {
                    boolean b = data.getBooleanExtra("syncstat", false);
                    if (b) {
                        // Toast.makeText(this, "Sync Succeded", Toast.LENGTH_LONG).show();
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
                    } else
                        Toast.makeText(this, "Sync Failed", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected GSyncServerConfig getSyncConfigFromSyncIntent(Bundle syncDataBundle) {
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


    public void doSync(Bundle syncDataBundle) {
        GNativeSync gNSync = GNativeSync.getNativeSyncSingleton();

        gNSync.addSyncListener(this);// important register sync listener to recieve notifications
        GSyncServerConfig gCfg = getSyncConfigFromSyncIntent(syncDataBundle);
        String rootPath = syncDataBundle.getString("dbName");
        if (rootPath == null || rootPath.lastIndexOf("/") <= 0) return;
        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
        extractPEMFile(getAssets(), rootPath);//extracts on every launch, you can change it to extract only if file isnt present.
        //clearViews();
        initDB(gCfg.getDBName(), gNSync._hasCodec());
        gNSync.startSync(gCfg);
    }


    @Override
    public void syncStatusEvent(GSyncStatusEvt statEvt) {
        String msg = statEvt.getMsg();
        if (msg == null)
            msg = "";
        Bundle b = new Bundle();
        b.putString("syncstatusmsg", msg);
        b.putInt("status1", statEvt.getTStatus1());
        b.putInt("status2max", statEvt.getStatus2Max());
        b.putInt("status2", statEvt.getStatus2());
        b.putInt("status3max", statEvt.getStatus3Max());
        b.putInt("status3", statEvt.getStatus3());
        Message m = Message.obtain();
        m.setData(b);

        if (statEvt.getTStatus1() == 100)//100 sync failed 200 sync succeeded
        {
            syncInProgress = false;
            didSyncSucceed = false;
//            Toast.makeText(this, "Sync failed", Toast.LENGTH_LONG).show();
            Log.i("GSync", "Sync failed");
            m.setTarget(gSyncResultNotifyHandler);
        } else if (statEvt.getTStatus1() == 200)//sync succeeded
        {
            didSyncSucceed = true;
            syncInProgress = false;
            //          Toast.makeText(this, "Sync Success", Toast.LENGTH_LONG).show();
            Log.i("GSync", "Sync Success");
            m.setTarget(gSyncResultNotifyHandler);
        } else
            m.setTarget(gSyncStatusHandler);
        m.sendToTarget();
        Log.i("GSync :: syncStatus", statEvt.toString());
    }

    @Override
    public void browserNotifyEvent(GBrowserNotifyMessageEvt gNMsgEvt) {
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
    public void itemStatusEvent(String ItemName, String Msg) {
        Bundle b = new Bundle();
        Message m = Message.obtain();
        m.setTarget(gSyncItemStatusHandler);
        b.putString("itemname", ItemName);
        b.putString("msg", Msg);
        m.setData(b);
        m.sendToTarget();
        Log.i("GSync", "onItemStatus " + ItemName + " : " + Msg);
    }


    private void updateStatus(Message msg) {
        //if(status==null) return;


        Bundle bundle = msg.getData();
        int s1 = bundle.getInt("status1");
        int s2 = bundle.getInt("status2");
        int s2max = bundle.getInt("status2max");
        int s3 = bundle.getInt("status3");
        int s3max = bundle.getInt("status3max");
        System.out.println("status2max " + bundle.getInt("status2max"));
        Log.i("GSync", "status2max " + bundle.getInt("status2max"));
    }

    private void updateNotify(String notification) {
        if (notification == null) return;
        //currently not used
    }

    private void updateResult(String notification) {
        if (notification == null) return;
        //currently not used
        //Toast.makeText(this, "Sync Succeded", Toast.LENGTH_LONG).show();
    }

    private void updateItemStatus(String itemName, String msg) {
        if (itemName == null) itemName = "";
        if (msg == null) msg = "";
        Log.i("GSync", "->" + itemName + " " + msg + "\n");
    }


    class GSyncStatusHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateStatus(msg);
        }
    }

    ;

    class GBrowserNotifyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateNotify(msg.getData().getString("browsernotifymsg"));
        }
    }

    ;

    class GSyncResultHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateResult(msg.getData().getString("syncstatusmsg"));
        }
    }

    ;

    class GSyncItemStatusHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateItemStatus(msg.getData().getString("itemname"), msg.getData().getString("msg"));
        }
    }

    ;


    void extractPEMFile(AssetManager mgr, String rootPath) {
        try {
            String[] filelist = mgr.list("");
            for (String asset : filelist) {
                if (asset.equalsIgnoreCase("ROOT.pem")) {
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
        } catch (Exception e) {
        }
    }

    //this is just to create a blank database if doesnt exist
    static void initDB(String dbName, int hasCodec) {
        String keyStr = "";
        com.godbtech.sql.database.sqlite.SQLiteDatabase db = null;
        if (hasCodec == 1) {
            if (dbName.indexOf(";") > 0 && dbName.indexOf("=") > 0) {
                keyStr = "key='" + dbName.substring(dbName.indexOf("=") + 1) + "'";
                dbName = dbName.substring(0, dbName.indexOf(";"));
            }
            db = com.godbtech.sql.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbName, null);
            if (keyStr.length() > 0) {
                String pragma = "PRAGMA " + keyStr;
                db.execSQL(pragma);//set the key
                Log.i("GSync", "testEncryptedDB:::pragma:::" + pragma);
            }
        } else {
            if (dbName.indexOf(";") > 0)
                dbName = dbName.substring(0, dbName.indexOf(";"));
            db = com.godbtech.sql.database.sqlite.SQLiteDatabase.openOrCreateDatabase(dbName, null);
        }
        SQLiteStatement st = null;
        String res = null;
        try {
            st = db.compileStatement("SELECT sqlite_version()");
            res = st.simpleQueryForString();
            //st.close();
            //db.execSQL("CREATE TABLE IF NOT EXISTS t1(x, y)");
            //db.execSQL("delete from t1");
            //db.execSQL("INSERT INTO t1 VALUES (1, 2), (3, 4)");
            //st = db.compileStatement("SELECT sum(x+y) FROM t1");
            //res = st.simpleQueryForString();
            Log.i("GSync", "sqlite_version():::" + res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (st != null)
                st.close();//dont forget to close the db
            if (db != null)
                db.close();//dont forget to close the db
        }
    }

    /**
     * Load sync library.
     */
    private void loadSyncLibrary() {
        try {
            String trgLib = "gSyncDLL";
            System.loadLibrary(trgLib);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GSync", e.getMessage());
        }
    }


}