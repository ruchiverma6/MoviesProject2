package project1.android.com.project1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by ruchi on 18/11/16.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_WITH_SORT_BY = 101;
    static final int MOVIE_WITH_SORT_BY_ID = 102;
    static final int TRAILERS = 103;
    static final int TRAILER_WITH_MOVIE_ID = 104;
    static final int REVIEW_WITH_MOVIE_ID = 105;
    static final int REVIEWS = 106;
    // static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int SORTBY = 300;


    private MovieDbHelper mOpenHelper;

    private static final String selectionMovieWithSortBy = MovieContract.SortByEntry.TABLE_NAME + "." + MovieContract.SortByEntry.COLUMN_SORT_TYPE + " = ? ";
    private static final String selectionMovieDetailWithSortBy = MovieContract.SortByEntry.TABLE_NAME + "." + MovieContract.SortByEntry.COLUMN_MOVIE_KEY + " =? AND " +  MovieContract.SortByEntry.TABLE_NAME + "." + MovieContract.SortByEntry.COLUMN_SORT_TYPE + " =? ";
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_SORT_BY);
        //  matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);


        matcher.addURI(authority, MovieContract.PATH_SORTBY, SORTBY);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_SORT_BY);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/*", MOVIE_WITH_SORT_BY_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/*", TRAILER_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/*", REVIEW_WITH_MOVIE_ID);
        return matcher;
    }


    private static final SQLiteQueryBuilder sMovieBySortByQueryBuilder;

    static {
        sMovieBySortByQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieBySortByQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.SortByEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.SortByEntry.TABLE_NAME +
                        "." + MovieContract.SortByEntry.COLUMN_MOVIE_KEY);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "sortby"
            case SORTBY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.SortByEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE_WITH_SORT_BY: {
                retCursor = getMoviesWithSortBy(uri, projection, sortOrder);
                break;
            }

            case MOVIE_WITH_SORT_BY_ID: {
                retCursor = getMovieDetailWithSortBy(uri, projection, sortOrder);
                break;
            }

            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }



            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

 /*   private Cursor getMovieWithSortByAndId(Uri uri, String[] projection, String sortOrder) {
        String sortBy= uri.getPathSegments().get(1);
        String movieId=uri.getPathSegments().get(2);
        getContext().getContentResolver().qu
        return null;
    }*/

    private Cursor getMoviesWithSortBy(Uri uri, String[] projection, String sortOrder) {
        String sortBy = uri.getPathSegments().get(1);
        String[] selectionArgs = new String[]{sortBy};
        return sMovieBySortByQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selectionMovieWithSortBy,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieDetailWithSortBy(Uri uri, String[] projection, String sortOrder) {
        String sortBy = uri.getPathSegments().get(1);
        String movieId=uri.getPathSegments().get(2);
        String[] selectionArgs = new String[]{movieId,sortBy};
        return sMovieBySortByQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selectionMovieDetailWithSortBy,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_WITH_SORT_BY_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_SORT_BY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case SORTBY:
                return MovieContract.SortByEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            }
            case SORTBY: {
                long _id = db.insert(MovieContract.SortByEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.SortByEntry.buildSortByUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case SORTBY: {
                db.beginTransaction();
                int Count = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.SortByEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            Count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return Count;
            }
            case TRAILERS: {
                db.beginTransaction();
                int Count = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            Count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return Count;
            }

            case REVIEWS:
            {
                db.beginTransaction();
                int Count = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            Count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return Count;
        }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsAffected=0;

        switch (match) {

            case SORTBY: {
                rowsAffected= db.delete(MovieContract.SortByEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
