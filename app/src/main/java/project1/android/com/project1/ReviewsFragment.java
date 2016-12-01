package project1.android.com.project1;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.Helper.Utils;
import project1.android.com.project1.adapters.ReviewAdapter;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.ReviewData;
import project1.android.com.project1.data.ReviewResult;
import project1.android.com.project1.listeners.DataUpdateListener;

/**
 * Created by v-ruchd on 11/21/2016.
 */

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DataUpdateListener {
    private static final int REVIEW_LOADER = 0;
    private ListView mReviewListView;
    private Activity mActivity;
    private ReviewAdapter mReviewAdapter;
    private String movieId;
    private TextView mErrorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reviews_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        Bundle bundle = getArguments();
        if (null != bundle) {
            movieId = bundle.getString(Constant.MOVIE_ID_KEY);
        }
        initComponents();

        downloadData();
    }

    private void initComponents() {
        mErrorTextView = (TextView) mActivity.findViewById(R.id.error_view);
        mReviewListView = (ListView) mActivity.findViewById(R.id.review_list_view);
        mReviewAdapter = new ReviewAdapter(mActivity, null, 0);
        mReviewListView.setAdapter(mReviewAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID + " =? ";
        String selectionArgs[] = new String[]{movieId};
        return new CursorLoader(mActivity, MovieContract.ReviewEntry.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mReviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewAdapter.swapCursor(null);
    }

    private void downloadData() {
        Cursor cursor = getData();
        if (null != cursor && cursor.moveToFirst()) {
            updateDataOnUI();
        } else {
            Utils.downloadData(mActivity, String.format(Constant.REVIEW_URL, movieId, Constant.MOVIE_DB_API_KEY), this, Constant.REVIEW_TYPE, null);
        }

    }

    private void updateDataOnUI() {
        if (null != getLoaderManager().getLoader(REVIEW_LOADER)) {
            getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
        } else {
            getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        }
    }

    private Cursor getData() {
        String[] selectionArgs = new String[]{movieId};
        Cursor cursor = mActivity.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, MovieContract.ReviewEntry.selection, selectionArgs, null);
        return cursor;
    }

    @Override
    public void onDataUpdate(Data reviewData) {

        if (null != reviewData && reviewData instanceof ErrorInfo) {
            updateErrorMsgOnUI(((ErrorInfo) reviewData).getErrorMsg());
        } else {
            ArrayList<ReviewResult> results = ((ReviewData) reviewData).getResults();
            if (null != results && results.size() == 0) {
                updateErrorMsgOnUI(getString(R.string.no_review));
            } else {
                updateDataOnUI();
            }

        }
    }

    private void updateErrorMsgOnUI(String msg) {
        mReviewListView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(msg);
    }
}
