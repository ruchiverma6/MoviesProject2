package project1.android.com.project1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ruchi on 18/11/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "project1.android.com.project1";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_SORTBY = "sorttype";

    public static final String PATH_TRAILERS = "trailers";

    public static final String PATH_REVIEWS= "reviews";


    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        // Table name
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movieid";

        public static final String COLUMN_MOVIE_POSTER_URL = "movieposterurl";

        public static final String COLUMN_MOVIE_POSTER_TITLE = "moviepostertitle";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VOTE_AVERAGE = "voteaverage";




        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            Student: This is the buildWeatherLocation function you filled in.
         */
        public static Uri buildMovieWithSortBy(String sortBy) {
            return CONTENT_URI.buildUpon().appendPath(sortBy).build();
        }

        public static Uri buildMovieWithSortByAndId(String sortBy,String movieID) {
            return CONTENT_URI.buildUpon().appendPath(sortBy).appendPath(movieID).build();
        }
    }

    public static final class SortByEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORTBY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORTBY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORTBY;
        // Table name
        public static final String TABLE_NAME = "sortby";
        public static final String COLUMN_SORT_TYPE = "sorttype";
        public static final String COLUMN_MOVIE_KEY="movie_id";

public static String selection=COLUMN_MOVIE_KEY + " =? AND " + COLUMN_SORT_TYPE + " =? ";

        public static Uri buildSortByUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TrailerEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String TABLE_NAME="trailer";
        public static final String COLUMN_TRAILER_MOVIE_ID="trailer_movie_id";
        public static final String COLUMN_TRAILER_ID="id";
        public static final String COLUMN_TRAILER_NAME="name";
        public static final String COLUMN_TRAILER_TYPE="type";
        public static final String COLUMN_TRAILER_KEY="key";
        public static final String COLUMN_TRAILER_SITE="site";

      public static   String selection = MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID + " = ? ";


        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class ReviewEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String TABLE_NAME="review";
        public static final String COLUMN_REVIEW_MOVIE_ID="review_movie_id";
        public static final String COLUMN_REVIEW_ID="id";
        public static final String COLUMN_AUTHOR="author";
        public static final String COLUMN_CONTENT="content";
        public static final String COLUMN_URL="url";
        public static String selection= COLUMN_REVIEW_MOVIE_ID + " =? ";


        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
