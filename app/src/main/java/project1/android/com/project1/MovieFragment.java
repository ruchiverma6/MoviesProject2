package project1.android.com.project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.Helper.Utils;
import project1.android.com.project1.adapters.MoviesCursorAdapter;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.MovieData;
import project1.android.com.project1.data.ResultData;
import project1.android.com.project1.listeners.DataUpdateListener;

/**
 * Created by v-ruchd on 11/30/2016.
 */

public class MovieFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,DataUpdateListener {
    private Activity mActivity;
    private String selectedSortBy;


    //Reference variable to hold Grid view object
    private GridView mGridView;

    //MoviesArrayAdapter to bind data to GridView.
    private MoviesCursorAdapter mMovieCursorAdapter;

    //Reference variable to hold context object.
    private Context context;
    //Array to hold results;
    private ArrayList<ResultData> results;

    private ArrayList<ResultData> resultDataArrayList = new ArrayList<>();

    //Reference variable to hold textview.
    private TextView mErrorTextView;
    private static final int MOVIE_LOADER = 0;
    private Cursor existingCursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        selectedSortBy = Utils.getSharedPreferenceValue(mActivity, getString(R.string.sort_by_key));
        initComponents();
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        downloadData();
    }


    /***
     * Method to download data based on selected sort type.
     */
    public void downloadData() {
        if (null != existingCursor && existingCursor.getCount() > 0) {
            updateDataOnUI();
        } else {
            selectedSortBy = Utils.getSharedPreferenceValue(mActivity, getString(R.string.sort_by_key));
            if (selectedSortBy.equals(getString(R.string.favorite))) {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.no_fav_movies));
            } else {
                Utils.downloadData(mActivity, String.format(Constant.POPULAR_MOVIES_URL, selectedSortBy, Constant.MOVIE_DB_API_KEY), this, Constant.MOVIE_TYPE, selectedSortBy);
            }
        }
    }


    /**
     * Method for initializing components.
     */
    private void initComponents() {
        mErrorTextView = (TextView) mActivity.findViewById(R.id.error_text_view);
        mGridView = (GridView) mActivity.findViewById(R.id.grid_view);
        mMovieCursorAdapter = new MoviesCursorAdapter(mActivity, null, 0);
        mGridView.setAdapter(mMovieCursorAdapter);
        mGridView.setOnItemClickListener(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieWithSortBy(Utils.getSharedPreferenceValue(mActivity, getString(R.string.sort_by_key)));

        return new CursorLoader(mActivity, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieCursorAdapter.swapCursor(cursor);
        updateDataInCaseOfEmptyCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mMovieCursorAdapter.getItem(position);
        if (cursor != null) {
            String movieID = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            Uri uri = MovieContract.MovieEntry.buildMovieWithSortByAndId(selectedSortBy, movieID);
            ((Callback) getActivity())
                    .onItemSelected(uri);
         //   openMovieDetail(movieID);


        }

    }

    private void openMovieDetail(String movieID) {
        Intent intent = new Intent(mActivity, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.movie_id_key, movieID);
        intent.putExtras(bundle);
        startActivity(intent);
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


    private void updateDataInCaseOfEmptyCursor(Cursor cursor) {
        String emptyDataMsg = null;
        if (null == cursor || cursor.getCount() == 0) {
            if (selectedSortBy.equals(getString(R.string.most_popular))) {
                emptyDataMsg = getString(R.string.data_no_retrival_message);
            } else if (selectedSortBy.equals(getString(R.string.top_rated))) {
                emptyDataMsg = getString(R.string.data_no_retrival_message);
            } else if (selectedSortBy.equals(getString(R.string.favorite))) {
                emptyDataMsg = getString(R.string.no_fav_movies);
            }
            mGridView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(emptyDataMsg);
        } else {
            mGridView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDataUpdate(Data movieData) {
        if (null != movieData && movieData instanceof ErrorInfo) {
            updateErrorMsgOnUI((ErrorInfo) movieData);
        } else {

            results = ((MovieData) movieData).getResults();

            updateDataOnUI();
        }
    }


    private void updateDataOnUI() {
        mErrorTextView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        if (null != getLoaderManager().getLoader(MOVIE_LOADER)) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        } else {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }

    }


    private void updateErrorMsgOnUI(ErrorInfo movieData) {
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(movieData.getErrorMsg());
        mGridView.setVisibility(View.GONE);

    }
}
