package project1.android.com.project1;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import project1.android.com.project1.adapters.MoviesCursorAdapter;
import project1.android.com.project1.data.MovieContract;

/**
 * Created by v-ruchd on 11/30/2016.
 */

public class MovieFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SELECTED_MOVIEID = "selectedmovieid";
    private Activity mActivity;
    private String selectedSortBy;
    static final String MOVIE_URI = "URI";
    //Reference variable to hold Grid view object
    private GridView mGridView;
    //MoviesArrayAdapter to bind data to GridView.
    private MoviesCursorAdapter mMovieCursorAdapter;
    private static final int MOVIE_LOADER = 0;
    private Uri uri;
    private String selectedMovieID;
    private static final String SELECTED_SCROLL_KEY = "selectedscrollposition";
    private int mScrollPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_MOVIEID)) {
            selectedMovieID = savedInstanceState.getString(SELECTED_MOVIEID);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_SCROLL_KEY)) {
            mScrollPosition = savedInstanceState.getInt(SELECTED_SCROLL_KEY);
        }
        return inflater.inflate(R.layout.movie_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        Bundle arguments = getArguments();
        if (arguments != null) {
            uri = arguments.getParcelable(MovieFragment.MOVIE_URI);
        }
        initComponents();
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != selectedMovieID) {
            outState.putString(SELECTED_MOVIEID, selectedMovieID);
        }
        if (mScrollPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_SCROLL_KEY, mScrollPosition);
        }
    }

    public String getSelectedMovieId() {
        return selectedMovieID;
    }

    public void setSelectedMovieid(String selectedMovieID) {
        this.selectedMovieID = selectedMovieID;
    }

    /**
     * Method for initializing components.
     */
    private void initComponents() {
        mGridView = (GridView) mActivity.findViewById(R.id.grid_view);
        mMovieCursorAdapter = new MoviesCursorAdapter(mActivity, null, 0);
        mGridView.setAdapter(mMovieCursorAdapter);
        mGridView.setOnItemClickListener(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieCursorAdapter.swapCursor(cursor);
        if (mScrollPosition != GridView.INVALID_POSITION) {
            mGridView.post(new Runnable() {
                @Override
                public void run() {
                    mGridView.smoothScrollToPosition(mScrollPosition);

                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mMovieCursorAdapter.getItem(position);
        if (cursor != null) {
            String movieID = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            Uri uri = MovieContract.MovieEntry.buildMovieWithMovieId(movieID);
            ((Callback) getActivity())
                    .onItemSelected(uri);
            selectedMovieID = movieID;

        }
        mScrollPosition = position;


    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


}




