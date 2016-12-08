package project1.android.com.project1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.MovieData;
import project1.android.com.project1.data.ResultData;
import project1.android.com.project1.helper.AsyncQueryHandlerListener;
import project1.android.com.project1.helper.Constant;
import project1.android.com.project1.helper.CustomAsyncQueryHandler;
import project1.android.com.project1.helper.Utils;
import project1.android.com.project1.listeners.DataUpdateListener;

public class MainActivity extends AppCompatActivity implements DataUpdateListener, MovieFragment.Callback, AsyncQueryHandlerListener {

    private static final String MOVIE_FRAGMENT_TAG = "MVTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final int MOVIES_DATA = 100;
    //Array to hold results;
    private ArrayList<ResultData> results;
    //Reference variable to hold textview.
    private TextView mErrorTextView;
    private Cursor existingCursor;
    private String selectedSortBy;
    private boolean mTwoPane;
    private String movieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null != savedInstanceState) {
            results = savedInstanceState.getParcelableArrayList(Constant.RESULT_DATA_ARRAY);
        }
        selectedSortBy = Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key));
        setUpActionBar();
        initComponents();
        if (findViewById(R.id.movies_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    private void setUpActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    /**
     * Method for initializing components.
     */
    private void initComponents() {
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
    }

    private void getData() {
        CustomAsyncQueryHandler customAsyncQueryHandler = new CustomAsyncQueryHandler(getContentResolver());
        customAsyncQueryHandler.setAsyncQueryHandlerListener(this);
        Uri urin = MovieContract.MovieEntry.buildMovieWithSortBy(Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key)));
        customAsyncQueryHandler.startQuery(MOVIES_DATA, null, urin, null, null, null, null);
    }


    /***
     * Method to download data based on selected sort type.
     */
    public void downloadData() {
        if (null != existingCursor && existingCursor.getCount() > 0) {
            updateDataOnUI();
        } else {
            selectedSortBy = Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key));
            if (selectedSortBy.equals(getString(R.string.favorite))) {

                Fragment movieFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.movie_fragment_tag));
                Fragment detailFragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                if (null != movieFragment) {
                    getSupportFragmentManager().beginTransaction().remove(movieFragment).commit();
                }
                if (null != detailFragment) {
                    getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
                }
                findViewById(R.id.fragment_movies).setVisibility(View.GONE);
                if (mTwoPane) {
                    findViewById(R.id.movies_detail_container).setVisibility(View.GONE);
                }
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.no_fav_movies));
            } else {
                Utils.downloadData(this, String.format(Constant.POPULAR_MOVIES_URL, selectedSortBy, Constant.MOVIE_DB_API_KEY), this, Constant.MOVIE_TYPE, selectedSortBy);
            }
        }
    }

    private void updateDataOnUI() {
        Fragment moviFragment = getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
        if (null != moviFragment && null != ((MovieFragment) moviFragment).getSelectedMovieId()) {
            movieId = ((MovieFragment) moviFragment).getSelectedMovieId();
        } else {
            if (null != existingCursor && existingCursor.moveToFirst()) {

                movieId = existingCursor.getString(existingCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

            }
        }
        Uri movieUri = MovieContract.MovieEntry.buildMovieWithSortBy(Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key)));
        mErrorTextView.setVisibility(View.GONE);
        Bundle movieargs = new Bundle();
        movieargs.putParcelable(MovieFragment.MOVIE_URI, movieUri);
        MovieFragment movieFragment = new MovieFragment();
        movieFragment.setArguments(movieargs);
        movieFragment.setSelectedMovieid(movieId);

        addFragment(R.id.fragment_movies, movieFragment, MOVIE_FRAGMENT_TAG);
        if (mTwoPane) {
            Uri detailUri = MovieContract.MovieEntry.buildMovieWithMovieId(movieId);
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, detailUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            addFragment(R.id.movies_detail_container, detailFragment, DETAILFRAGMENT_TAG);
        }
    }

    private void addFragment(int containerId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(containerId, fragment, tag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Utils.launchSettingActivity(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDataUpdate(Data movieData) {
        if (null != movieData && movieData instanceof ErrorInfo) {
            updateErrorMsgOnUI((ErrorInfo) movieData);
        } else {
            results = ((MovieData) movieData).getResults();
            movieId = results.get(0).getId();
            updateDataOnUI();
        }
    }

    private void updateErrorMsgOnUI(ErrorInfo movieData) {
        Fragment movieFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.movie_fragment_tag));
        Fragment detailFragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != movieFragment) {
            getSupportFragmentManager().beginTransaction().remove(movieFragment).commit();
        }
        if (null != detailFragment) {
            getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
        }
        findViewById(R.id.fragment_movies).setVisibility(View.GONE);
        if (mTwoPane) {
            findViewById(R.id.movies_detail_container).setVisibility(View.GONE);
        }
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(movieData.getErrorMsg());


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != results && results.size() > 0) {
            outState.putParcelableArrayList(Constant.RESULT_DATA_ARRAY, results);
        }
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
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
            case MOVIES_DATA:
                existingCursor = cursor;
                downloadData();
                break;
        }

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }
}
