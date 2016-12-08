package project1.android.com.project1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;
import java.util.List;

import project1.android.com.project1.adapters.TrailerAdapter;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.TrailerResult;
import project1.android.com.project1.data.TrailersData;
import project1.android.com.project1.helper.AsyncQueryHandlerListener;
import project1.android.com.project1.helper.Constant;
import project1.android.com.project1.helper.CustomAsyncQueryHandler;
import project1.android.com.project1.helper.Utils;
import project1.android.com.project1.listeners.DataUpdateListener;

/**
 * Created by ruchi on 11/21/2016.
 */

public class TrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DataUpdateListener, AsyncQueryHandlerListener {

    private static final int TRAILER_LOADER = 0;
    private static final int TRAILERS_DATA = 103;
    private ListView mTrailerListView;
    private Activity mActivity;
    private TrailerAdapter mTrailersAdapter;
    private String movieID;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private ArrayList<TrailerResult> results;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trailers_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ((ContainerActivity) mActivity).setActionBarTitle(getString(R.string.trailers));
        Bundle bundle = getArguments();
        movieID = bundle.getString(Constant.MOVIE_ID_KEY);
        initComponents();
        downloadData();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initComponents() {
        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.prog_bar);
        mErrorTextView = (TextView) mActivity.findViewById(R.id.error_view);
        mTrailersAdapter = new TrailerAdapter(mActivity, null, 0);
        mTrailerListView = (ListView) mActivity.findViewById(R.id.trailer_list_view);
        mTrailerListView.setAdapter(mTrailersAdapter);
        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mTrailersAdapter.getItem(position);
                String trailerId = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY));
                watchYoutubeVideo(trailerId);
            }
        });

    }

    private void downloadData() {
        getData();
    }

    private void updateDataOnUI() {
        mTrailerListView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
        if (null != getLoaderManager().getLoader(TRAILER_LOADER)) {
            getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
        } else {
            getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        }

    }

    private void getData() {
        String[] selectionArgs = new String[]{movieID};
        CustomAsyncQueryHandler customAsyncQueryHandler = new CustomAsyncQueryHandler(mActivity.getContentResolver());
        customAsyncQueryHandler.setAsyncQueryHandlerListener(this);
        customAsyncQueryHandler.startQuery(TRAILERS_DATA, null, MovieContract.TrailerEntry.CONTENT_URI, null, MovieContract.TrailerEntry.selection, selectionArgs, null);
    }


    public void watchYoutubeVideo(String id) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                Constant.DEVELOPER_KEY, id);
        PackageManager packageManager = mActivity.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(webIntent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = new String[]{movieID};
        return new CursorLoader(mActivity, MovieContract.TrailerEntry.CONTENT_URI, null, MovieContract.TrailerEntry.selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTrailersAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailersAdapter.swapCursor(null);
    }

    @Override
    public void onDataUpdate(Data trailerData) {
        if (isAdded()) {
            mProgressBar.setVisibility(View.GONE);
            if (null != trailerData && trailerData instanceof ErrorInfo) {
                updateErrorMsgOnUI(((ErrorInfo) trailerData).getErrorMsg());
            } else {
                results = ((TrailersData) trailerData).getmTrailerResults();
                if (null != results && results.size() == 0) {
                    updateErrorMsgOnUI(getString(R.string.no_trailer_msg));
                } else {
                    updateDataOnUI();
                }
            }
        }

    }

    private void updateErrorMsgOnUI(String msg) {
        mTrailerListView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(msg);
    }


    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {

            case TRAILERS_DATA:
                if (isAdded()) {
                    if (null != cursor && cursor.moveToFirst()) {
                        updateDataOnUI();
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        Utils.downloadData(mActivity, String.format(Constant.TRAILERS_URL, movieID, Constant.MOVIE_DB_API_KEY), this, Constant.TRAILERS_TYPE, null);
                    }
                }
                break;
        }
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }
}
