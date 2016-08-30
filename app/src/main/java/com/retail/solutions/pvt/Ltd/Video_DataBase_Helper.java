package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 6/4/16.
 */
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Video_DataBase_Helper  extends SQLiteOpenHelper {
    private static final String TAG = "message" ;
    ArrayList<Video_Data>videos=new ArrayList<Video_Data>();

    private static String DB_PATH = "/data/data/" + GMainActivity.class.getPackage().getName() + "/";
    private static final String DATABASE_NAME = "sync.bdb.db";
    private static final int DATABASE_VERSION = 3;
    private SQLiteDatabase db;
    private final Context mycontext;

    public static final String TABLE_NAME ="retail_videodata";
    public static final String ADPLAY="Ad_Play";
    public static final String STOREID="Store_Id";
    public static final String FILENAME="File_Name";
    public static final String STARTDATE="startdate";
    public static final String ENDDATE="enddate";
    public static final String STARTTME="starttime";
    public static final String ENDTIME="endtime";
    public static final String CLICK="Click";



    public static final String TABLE_NAME_CONT ="retail_videodata_cont";

    public static final String TABLE_NAME_CONT1 ="retail_videodata_cont1";

    public static final String COLUMN_STOREMEDIAID="Store_Media_Id";


    // retail_Ad _Ticker Table......
    public static final String TABLE_NAME1="retail_ad_ticker";
    public static final String COLUMN_AD_TEXT="Ad_Text";
    public static final String COLUMN_AD_MAIN_ID="Ad_Main_Id";
    public static final String COLUMN_STATUS="Status";
    public static final String COLUMN_ACTIVE="Active";

    //retail_media Table......
    public static final String COLUMN_MD_POS_ID="MD_POS_ID";
    public static final String COLUMN_MD_POS_DESC="MD_POS_DESC";
    public static final String COLUMN_LENGTH="Length";
    public static final String COLUMN_WIDTH="Width";

    //retail_cust Table......
    public static final String COLUMN_CUST_ID="Cust_Id";


    //retail_media_click....
    public static final String COLUMN_MOBILE_NO = "Mobile_No";
    public static final String COLUMN_CLICK = "Click";




    @SuppressLint("NewApi")
    public Video_DataBase_Helper(Context context)
    {

        super(context, DB_PATH+DATABASE_NAME , null, 1);
        this.mycontext=context;
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = mycontext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(db != null)
            db.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase arg0)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        db.execSQL("DROP TABLE IF EXISTS retail_videodata");
        onCreate(db);
    }


    /*public  Cursor getStoreID(){

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor datacursor=db.rawQuery("select Store_Id from retail_store",null);
        return datacursor;

    }*/


    //retail_videodata*************************************************************************************************

    public void insertVideoData(String ADPLAY,String STOREID ,String STOREMEDIAID, String FILENAME, String STARTDATE, String ENDDATE, String STARTTME, String ENDTIME){
        try {
            Log.e("#########","We Are Inside DataBase Class");
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv=new ContentValues();
            Log.e("########","Ad_Play in database:"+ADPLAY);
            Log.e("########","Store_id:"+STOREID);
            Log.e("########","Store_Media_id:"+STOREMEDIAID);
            Log.e("#######", "File_Name in database:" +FILENAME);
            Log.e("#######", "Start Date in database:" + STARTDATE);
            Log.e("#######","End Date in database:"+ENDDATE);
            Log.e("#######", "Start Time in database:" + STARTTME);
            Log.e("#######", "End Time in database:" + ENDTIME);
            cv.put("Ad_Play", ADPLAY);
            cv.put("Store_Id",STOREID);
            cv.put("Store_Media_Id",STOREMEDIAID);
            cv.put("File_Name", FILENAME);
            cv.put("startdate", STARTDATE);
            cv.put("enddate", ENDDATE);
            cv.put("starttime", STARTTME);
            cv.put("endtime", ENDTIME);




            long result = db.insert("retail_videodata", null, cv);
            Log.e("Message", "############## data inserted and result is " + result);
            //db.close();
        } catch (Exception e) {
            Log.e("Message","##############:"+e);
        }
        //db.close();
    }


    public ArrayList<Video_Data> getVideosDetails(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor datacursor = db.rawQuery("select * from retail_videodata", null);
        datacursor.moveToFirst();

        if(datacursor.moveToFirst()){
            do{
                String ADPLAY=datacursor.getString(0);
                String STOREID=datacursor.getString(1);
                String STOREMEDIAID=datacursor.getString(2);
                String FILENAME=datacursor.getString(3);
                String STARTDATE=datacursor.getString(4);
                String ENDDATE=datacursor.getString(5);
                String STARTTIME=datacursor.getString(6);
                String ENDTIME=datacursor.getString(7);



                Video_Data tempVideo=new Video_Data();
                tempVideo.Ad_Play=ADPLAY;
                tempVideo.Store_Id=STOREID;
                tempVideo.Store_Media_Id=STOREMEDIAID;
                tempVideo.File_Name=FILENAME;
                tempVideo.startdate=STARTDATE;
                tempVideo.enddate=ENDDATE;
                tempVideo.starttime=STARTTIME;
                tempVideo.endtime=ENDTIME;

                videos.add(tempVideo);
            }
            while(datacursor.moveToNext());
        }
        return videos;
    }




    //retail_videodata_cont***************************************************************************************8

    public void insertVideoDataCont(String ADPLAY,String STOREID ,String STOREMEDIAID, String FILENAME, String STARTDATE, String ENDDATE, String STARTTME, String ENDTIME){
        try {
            Log.e("#########","We Are Inside DataBase Class");
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv=new ContentValues();
            Log.e("########","Ad_Play in database:"+ADPLAY);
            Log.e("########","Store_id:"+STOREID);
            Log.e("########","Store_Media_id:"+STOREMEDIAID);
            Log.e("#######", "File_Name in database:" +FILENAME);
            Log.e("#######", "Start Date in database:" + STARTDATE);
            Log.e("#######","End Date in database:"+ENDDATE);
            Log.e("#######", "Start Time in database:" + STARTTME);
            Log.e("#######", "End Time in database:" + ENDTIME);
            cv.put("Ad_Play", ADPLAY);
            cv.put("Store_Id",STOREID);
            cv.put("Store_Media_Id",STOREMEDIAID);
            cv.put("File_Name", FILENAME);
            cv.put("startdate", STARTDATE);
            cv.put("enddate", ENDDATE);
            cv.put("starttime", STARTTME);
            cv.put("endtime", ENDTIME);
            long result = db.insert("retail_videodata_cont", null, cv);
            Log.e("Message", "############## data inserted and retail_videodata_cont result is " + result);
            //db.close();
        } catch (Exception e) {
            Log.e("Message","##############:"+e);
        }
        //db.close();
    }


    public ArrayList<Video_Data> getVideosContDetails(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor datacursor = db.rawQuery("select * from retail_videodata_cont", null);
        datacursor.moveToFirst();

        if(datacursor.moveToFirst()){
            do{
                String ADPLAY=datacursor.getString(0);
                String STOREID=datacursor.getString(1);
                String STOREMEDIAID=datacursor.getString(2);
                String FILENAME=datacursor.getString(3);
                String STARTDATE=datacursor.getString(4);
                String ENDDATE=datacursor.getString(5);
                String STARTTIME=datacursor.getString(6);
                String ENDTIME=datacursor.getString(7);

                Video_Data tempVideo=new Video_Data();
                tempVideo.Ad_Play=ADPLAY;
                tempVideo.Store_Id=STOREID;
                tempVideo.Store_Media_Id=STOREMEDIAID;
                tempVideo.File_Name=FILENAME;
                tempVideo.startdate=STARTDATE;
                tempVideo.enddate=ENDDATE;
                tempVideo.starttime=STARTTIME;
                tempVideo.endtime=ENDTIME;
                videos.add(tempVideo);
            }
            while(datacursor.moveToNext());
        }
        return videos;
    }



    //retail_VideoData_Cont1*************************************************************************************
    public void insertVideoDataCont1(String ADPLAY,String STOREID ,String STOREMEDIAID, String FILENAME, String STARTDATE, String ENDDATE, String STARTTME, String ENDTIME){
        try {
            Log.e("#########","We Are Inside DataBase Class");
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv=new ContentValues();
            Log.e("########","Ad_Play in database:"+ADPLAY);
            Log.e("########","Store_id:"+STOREID);
            Log.e("########","Store_Media_id:"+STOREMEDIAID);
            Log.e("#######", "File_Name in database:" +FILENAME);
            Log.e("#######", "Start Date in database:" + STARTDATE);
            Log.e("#######","End Date in database:"+ENDDATE);
            Log.e("#######", "Start Time in database:" + STARTTME);
            Log.e("#######", "End Time in database:" + ENDTIME);
            cv.put("Ad_Play", ADPLAY);
            cv.put("Store_Id",STOREID);
            cv.put("Store_Media_Id",STOREMEDIAID);
            cv.put("File_Name", FILENAME);
            cv.put("startdate", STARTDATE);
            cv.put("enddate", ENDDATE);
            cv.put("starttime", STARTTME);
            cv.put("endtime", ENDTIME);
            long result = db.insert("retail_videodata_cont1", null, cv);
            Log.e("Message", "############## data inserted and retail_videodata_cont1 result is " + result);
            //db.close();
        } catch (Exception e) {
            Log.e("Message","##############:"+e);
        }
        //db.close();
    }


    public ArrayList<Video_Data> getVideosCont1Details(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor datacursor = db.rawQuery("select * from retail_videodata_cont1", null);
        datacursor.moveToFirst();

        if(datacursor.moveToFirst()){
            do{
                String ADPLAY=datacursor.getString(0);
                String STOREID=datacursor.getString(1);
                String STOREMEDIAID=datacursor.getString(2);
                String FILENAME=datacursor.getString(3);
                String STARTDATE=datacursor.getString(4);
                String ENDDATE=datacursor.getString(5);
                String STARTTIME=datacursor.getString(6);
                String ENDTIME=datacursor.getString(7);

                Video_Data tempVideo=new Video_Data();
                tempVideo.Ad_Play=ADPLAY;
                tempVideo.Store_Id=STOREID;
                tempVideo.Store_Media_Id=STOREMEDIAID;
                tempVideo.File_Name=FILENAME;
                tempVideo.startdate=STARTDATE;
                tempVideo.enddate=ENDDATE;
                tempVideo.starttime=STARTTIME;
                tempVideo.endtime=ENDTIME;
                videos.add(tempVideo);
            }
            while(datacursor.moveToNext());
        }
        return videos;
    }


    public ArrayList<String> getScreenLayout()
    {


        ArrayList<String> getscreenlayout = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from retail_media ", null);
        Log.e("Message", "############## data fetched  and result is " + res);

        if( res.moveToFirst())
            Log.e(TAG, "%%%%%%%%%%%%%Get Screen Size from retail_media table ");
        {
            do {
                getscreenlayout.add(res.getString(res.getColumnIndex(COLUMN_MD_POS_ID)));
                getscreenlayout.add(res.getString(res.getColumnIndex(COLUMN_MD_POS_DESC)));
                getscreenlayout.add(res.getString(res.getColumnIndex(COLUMN_LENGTH)));
                getscreenlayout.add(res.getString(res.getColumnIndex(COLUMN_WIDTH)));

            }while (res.moveToNext()) ;
        }
        return getscreenlayout;
    }



    public ArrayList<String> getMediaid()
    {
        ArrayList<String> getmediaid = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select Store_Media_Id from retail_store ", null);

        //Cursor res =  db.rawQuery("select * from retail_store", null);
        Log.e("Message", "############## data fetched  and result is " + res);
        if( res.moveToFirst())
            Log.e(TAG, "Get Store_Media_Id from retail_store table ");
        {
            do {
                getmediaid.add(res.getString(res.getColumnIndex(COLUMN_STOREMEDIAID)));

            }while (res.moveToNext()) ;
        }

        return getmediaid;
    }





    public ArrayList<String> getStoreid()
    {
        ArrayList<String> getstoreid = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from retail_store ", null);

        Log.e("Message", "############## data fetched  and result is " + res);
        if( res.moveToFirst())
            Log.e(TAG, "Get Store_id from retail_store table ");
        {
            do {
                getstoreid.add(res.getString(res.getColumnIndex(STOREID)));

            }while (res.moveToNext()) ;
        }

        return getstoreid;
    }

    public ArrayList getAllAdTicker()
    {
        ArrayList getalladticker = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select Ad_Text from retail_ad_ticker where Status Like 'A%'and Active Like 'Y%' ", null );
        if( res.moveToFirst())
            Log.e(TAG, "Get Ad_Text from retail_ad_ticker table");
        {
            do {
                getalladticker.add(res.getString(res.getColumnIndex(COLUMN_AD_TEXT)));

            }while (res.moveToNext()) ;
        }

        return getalladticker;
    }



    public ArrayList getAllCust_Id()
    {
        ArrayList getallcust_id = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select Cust_Id from retail_cust", null );
        if( res.moveToFirst())
            Log.e(TAG, "Get Cust_Id from retail_cust table");
        {
            do {
                getallcust_id.add(res.getString(res.getColumnIndex(COLUMN_CUST_ID)));

            }while (res.moveToNext()) ;
        }

        return getallcust_id;
    }


    public boolean InsertdataintoRetail_click(String Edit_mobile_no, String str_store_media_id, String ad_play, String touch_count) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("Mobile_No", Edit_mobile_no);
            value.put("Store_Media_Id", str_store_media_id);
            value.put("Ad_Play", ad_play);
            value.put("Click", touch_count);
            long result = db.insert("retail_media_click", null, value);

            Log.e("#########", "We Are Inside DataBase Class");
            Log.e("########", "Ad_play id :" + ad_play);
            Log.e("########", "Store_Media_id:" + str_store_media_id);
            Log.e("#######", "Number of Click:" + touch_count);
            Log.e("#######", "Mobile_No:" + Edit_mobile_no);
            Log.e("Message", "############## data inserted and retail_media_click result is " + result);
            return true;

        } catch (Exception e) {
            Log.e("Message", "##############:" + e);

        }
        return true;
    }


    public boolean CheckIsDataAlreadyInDBorNot(String Phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = new String[1];
        params[0] = Phone + "%";
        String Query = ("select Mobile_No from retail_media_click where " + " Mobile_No like ?");
        Cursor cursor = db.rawQuery(Query, params);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;


    }

    public ArrayList<Video_Data> getAllMediaClick() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor datacursor = db.rawQuery("select * from retail_media_click", null);
        datacursor.moveToFirst();

        if (datacursor.moveToFirst()) {
            do {
                String ADPLAY=datacursor.getString(0);
                String COLUMN_STOREMEDIAID=datacursor.getString(1);
                String COLUMN_MOBILE_NO=datacursor.getString(2);
                String COLUMN_CLICK=datacursor.getString(3);

                Video_Data tempVideo = new Video_Data();
                tempVideo.Ad_Play =ADPLAY ;
                tempVideo.Store_Media_Id =COLUMN_STOREMEDIAID;
                tempVideo.Mobile_No=COLUMN_MOBILE_NO;
                tempVideo.Click = COLUMN_CLICK;
                videos.add(tempVideo);
            }
            while (datacursor.moveToNext());
        }
        return videos;
    }

}

