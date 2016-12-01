package project1.android.com.movies.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Vector;

import project1.android.com.movies.R;
import project1.android.com.movies.data.Data;
import project1.android.com.movies.data.ErrorInfo;
import project1.android.com.movies.data.MovieContract;
import project1.android.com.movies.data.MovieData;
import project1.android.com.movies.data.MovieDetailData;
import project1.android.com.movies.data.ResultData;
import project1.android.com.movies.data.ReviewData;
import project1.android.com.movies.data.ReviewResult;
import project1.android.com.movies.data.TrailerResult;
import project1.android.com.movies.data.TrailersData;
import project1.android.com.movies.listeners.DataUpdateListener;

/**
 * Created by ruchi on 31/10/16.
 */
public class DataDownloaderAsyncTask extends AsyncTask<String, Void, Data> {
    //Reference variable to hold context object
    private Context context;
    //Reference variable to hold HttpHelper object
    private HttpHelper httpHelper;
    //Reference variable to hold url value.
    private String url;
    //DataUpdateListener object to notify sources after complete download of data.
    private DataUpdateListener dataUpdateListener;
    private int classType;
    Data movieData = null;
    private String sortBy;

    public DataDownloaderAsyncTask(Context context) {
        httpHelper = new HttpHelper(context);
      this.context=context;

    }

    @Override
    protected Data doInBackground(String... params) {

        Message msg = httpHelper.doHttpGet(params[0]);
        Bundle bundle = msg.getData();
        int responseCode = bundle.getInt(Constant.RESPONSE_CODE);
        String responseStr = null;

        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseStr = bundle.getString(Constant.RESPONSE_DATA);
            movieData = parseResponse(responseStr);

            saveDataInDb(movieData,sortBy);
        } else {


            movieData = handleErrorScenario(responseCode);
        }

        return movieData;
    }

    private void saveDataInDb(Data movieData,String sortType) {

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
                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVector.toArray(new ContentValues[contentValuesVector.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
                }

                if (contentValuesVectorSortBy.size() > 0) {
                    ContentValues[] contentValuesArray = contentValuesVectorSortBy.toArray(new ContentValues[contentValuesVectorSortBy.size()]);
                    context.getContentResolver().bulkInsert(MovieContract.SortByEntry.CONTENT_URI, contentValuesArray);
                }
                Cursor cursor= context.getContentResolver().query(MovieContract.MovieEntry.buildMovieWithSortBy("popular"),null,null,null,null);
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
                }
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

    private Data handleErrorScenario(int responseCode) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setStatusCode(responseCode);
        getErrorMessage(responseCode);
        errorInfo.setErrorMsg(getErrorMessage(responseCode));
        movieData = errorInfo;
        return movieData;
    }

    private String getErrorMessage(int responseCode) {
        String errorMsg=null;
        switch (responseCode) {
            case Constant.NO_INTERNET_CONNECTION_STATUS:

                errorMsg = context.getResources().getString(R.string.no_internet_connectivity_msg);

                break;
            default:
                errorMsg = context.getResources().getString(R.string.data_no_retrival_message);
                break;
        }
            return errorMsg;
        }


    @Override
    protected void onPostExecute(Data movieData) {
        super.onPostExecute(movieData);

            dataUpdateListener.onDataUpdate(movieData);

    }

    private Data parseResponse(String responseString) {
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

    public void setDataUpdateListener(DataUpdateListener dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
    }

    public void setClassType(int type) {
        this.classType = type;
    }

    public void setSortByType(String sortBy) {
        this.sortBy=sortBy;

    }
}
