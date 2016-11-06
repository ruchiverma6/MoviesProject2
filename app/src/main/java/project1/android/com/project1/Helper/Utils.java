package project1.android.com.project1.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import project1.android.com.project1.R;
import project1.android.com.project1.SettingsActivity;
import project1.android.com.project1.listeners.DataUpdateListener;

/**
 * Created by ruchi on 4/11/16.
 */
public class Utils {


    /***
     * Method to convert string to date.
     *
     * @param strDate
     * @param format
     * @return
     */
    public static String parseDate(String strDate, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ;
        return dateFormat.format(date);
    }

    public static String getSharedPreferenceValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, context.getString(R.string.prefs_sort_default_value));
    }


    public static void downloadData(Context context,String url, DataUpdateListener dataUpdateListener, int classType) {

        DataDownloaderAsyncTask downloaderAsyncTask = new DataDownloaderAsyncTask(context);
        downloaderAsyncTask.setClassType(classType);
        downloaderAsyncTask.setDataUpdateListener(dataUpdateListener);
        downloaderAsyncTask.execute(url);
    }

    /**
     * Method to launch setting activity.
     *
     * @param context
     */
    public static void launchSettingActivity(Context context) {

        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);

    }

    /***
     * Method to check whether network is connected or not.
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
