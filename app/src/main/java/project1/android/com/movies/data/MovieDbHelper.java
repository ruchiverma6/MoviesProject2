package project1.android.com.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ruchi on 18/11/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="movie.db";

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
// Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +  MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DATE + " Text NOT NULL, " + MovieContract.MovieEntry.COLUMN_TIME + " TEXT NOT NULL, " + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL "+
                 ", " +

                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";




        final String SQL_CREATE_SORTBY_TABLE = "CREATE TABLE " + MovieContract.SortByEntry.TABLE_NAME + " (" +

                MovieContract.SortByEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +


                MovieContract.SortByEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
                MovieContract.SortByEntry.COLUMN_SORT_TYPE + " TEXT NOT NULL, " +



                " FOREIGN KEY (" + MovieContract.SortByEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), " + " UNIQUE (" + MovieContract.SortByEntry.COLUMN_MOVIE_KEY +", "+ MovieContract.SortByEntry.COLUMN_SORT_TYPE+ ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +

                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +


                MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_ID+ " TEXT UNIQUE NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +

                MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL, " +

                MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE + " INTEGER NOT NULL, " +
                " UNIQUE (" + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +

                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +


                MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID+ " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID+ " TEXT UNIQUE NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR+ " TEXT NOT NULL, " +

                MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +

                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SORTBY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.SortByEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
