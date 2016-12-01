package project1.android.com.project1.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import project1.android.com.project1.R;
import project1.android.com.project1.SettingsActivity;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.MovieData;
import project1.android.com.project1.data.MovieDetailData;
import project1.android.com.project1.data.ResultData;
import project1.android.com.project1.data.ReviewData;
import project1.android.com.project1.data.ReviewResult;
import project1.android.com.project1.data.TrailerResult;
import project1.android.com.project1.data.TrailersData;
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


    public static void downloadData(Context context,String url, DataUpdateListener dataUpdateListener, int classType,String sortBy) {

        DataDownloaderAsyncTask downloaderAsyncTask = new DataDownloaderAsyncTask(context);
        downloaderAsyncTask.setClassType(classType);
        downloaderAsyncTask.setSortByType(sortBy);
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

    public static Data getData(HttpHelper httpHelper,Context context,String url,int classType,String sortBy){
        Message msg = httpHelper.doHttpGet(url);
        Bundle bundle = msg.getData();
        int responseCode = bundle.getInt(Constant.RESPONSE_CODE);
        String responseStr = null;

        Data movieData;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseStr = bundle.getString(Constant.RESPONSE_DATA);
            movieData = parseResponse(responseStr,classType);

            saveDataInDb(context,movieData,classType,sortBy);
        } else {

            movieData=null;
            // movieData = handleErrorScenario(responseCode);
        }

        return movieData;
    }


    public static void saveDataInDb(Context context,Data movieData,int classType,String sortType) {

        switch (classType) {


            case Constant.MOVIE_TYPE: {
                MovieData movieData1 = (MovieData) movieData;
                ArrayList<ResultData> resultDataArrayList = movieData1.getResults();
                Vector<ContentValues> contentValuesVector = new Vector<>(resultDataArrayList.size());
                Vector<ContentValues> contentValuesVectorSortBy = new Vector<>(resultDataArrayList.size());
                for (ResultData resultData : resultDataArrayList) {
                    ContentValues contentValuesMovies = new ContentValues();
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, resultData.getId());
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, resultData.getPoster_path());
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_TITLE, resultData.getTitle());
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_DATE, resultData.getRelease_date());
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_TIME, "120");
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, resultData.getOverview());
                    contentValuesMovies.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, resultData.getVote_average());
                    contentValuesVector.add(contentValuesMovies);

                    ContentValues contentValuesSortBy = new ContentValues();
                    contentValuesSortBy.put(MovieContract.SortByEntry.COLUMN_SORT_TYPE, sortType);
                    contentValuesSortBy.put(MovieContract.SortByEntry.COLUMN_MOVIE_KEY, resultData.getId());

                    contentValuesVectorSortBy.add(contentValuesSortBy);

                }
                if (contentValuesVectorSortBy.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVectorSortBy.toArray(new ContentValues[contentValuesVectorSortBy.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.SortByEntry.CONTENT_URI, contentValuesArray);
                }
                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVector.toArray(new ContentValues[contentValuesVector.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
                }


               /* Cursor cursor= context.getContentResolver().query(MovieContract.MovieEntry.buildMovieWithSortBy("popular"),null,null,null,null);
                cursor.getCount();

                Cursor cursortop= context.getContentResolver().query(MovieContract.MovieEntry.buildMovieWithSortBy("top_rated"),null,null,null,null);
                cursortop.getCount();

                Cursor cursornew= context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                cursornew.getCount();

                Cursor cursor2= context.getContentResolver().query(MovieContract.SortByEntry.CONTENT_URI,null,null,null,null);
                cursor2.getCount();
                if(cursor.moveToFirst()){
                    while (cursor.moveToNext()){
                        String title= cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_TITLE));
                        System.out.println(title);
                    }
                }*/
                break;
            }
            case Constant.MOVIE_DETAIL_TYPE:
                break;

            case Constant.TRAILERS_TYPE: {

                TrailersData trailersData = (TrailersData) movieData;
                ArrayList<TrailerResult> resultDataArrayList = trailersData.getmTrailerResults();
                Vector<ContentValues> contentValuesVector = new Vector<>(resultDataArrayList.size());
                Vector<ContentValues> contentValuesVectorSortBy = new Vector<>(resultDataArrayList.size());
                String movieID = trailersData.getId();
                for (TrailerResult resultData : resultDataArrayList) {
                    ContentValues contentValuesMovies = new ContentValues();
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID, movieID);
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, resultData.getId());
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, resultData.getKey());
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, resultData.getName());
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, resultData.getSite());
                    contentValuesMovies.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE, resultData.getType());

                    contentValuesVector.add(contentValuesMovies);


                }

                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVector.toArray(new ContentValues[contentValuesVector.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValuesArray);
                }

                Cursor cursor= context.getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,null,null,null,null);
                cursor.getCount();
                break;
            }
            case Constant.REVIEW_TYPE: {
                ReviewData reviewData = (ReviewData) movieData;
                ArrayList<ReviewResult> reviewResults = reviewData.getResults();
                Vector<ContentValues> contentValuesVector = new Vector<>(reviewResults.size());
                Vector<ContentValues> contentValuesVectorReviews = new Vector<>(reviewResults.size());
                String movieID = reviewData.getId();
                for (ReviewResult resultData : reviewResults) {
                    ContentValues contentValuesMovies = new ContentValues();
                    contentValuesMovies.put(MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID, movieID);
                    contentValuesMovies.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, resultData.getId());
                    contentValuesMovies.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, resultData.getAuthor());
                    contentValuesMovies.put(MovieContract.ReviewEntry.COLUMN_CONTENT, resultData.getContent());
                    contentValuesMovies.put(MovieContract.ReviewEntry.COLUMN_URL, resultData.getUrl());


                    contentValuesVector.add(contentValuesMovies);


                }
                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVector.toArray(new ContentValues[contentValuesVector.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValuesArray);
                }

                Cursor cursor= context.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,null,null,null,null);
                cursor.getCount();
                break;
            }
            default:
                break;


        }




    }



    public static Data parseResponse(String responseString,int classType) {
        Gson gson = new GsonBuilder().create();
        Data movieData = null;
        switch (classType) {
            case 0:
                movieData = gson.fromJson(responseString, MovieData.class);
                break;
            case 1:
                movieData = gson.fromJson(responseString, MovieDetailData.class);
                break;

            case 2:
                movieData = gson.fromJson(responseString, TrailersData.class);
                break;

            case 3:
                movieData = gson.fromJson(responseString, ReviewData.class);
                break;
            default:
                break;
        }


        return movieData;
    }

}
