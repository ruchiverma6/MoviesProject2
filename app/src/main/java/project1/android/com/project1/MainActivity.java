package project1.android.com.project1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, DataUpdateListener, LoaderManager.LoaderCallbacks<Cursor>,MovieFragment.Callback {

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
    private String selectedSortBy;
    private boolean isRestarted = false;
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            results = savedInstanceState.getParcelableArrayList(Constant.RESULT_DATA_ARRAY);
        }
      //  selectedSortBy = Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key));
        setUpActionBar();

       // initComponents();
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
      //  getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
     //   downloadData();

    }

    private void setUpActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  if (isRestarted) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, this);
            isRestarted = false;
        }*/

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestarted = true;
    }

    /**
     * Method for initializing components.
     */
    private void initComponents() {


        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mMovieCursorAdapter = new MoviesCursorAdapter(this, null, 0);
        mGridView.setAdapter(mMovieCursorAdapter);
        mGridView.setOnItemClickListener(this);

        existingCursor = getData();

    }

    private Cursor getData() {
        Uri urin = MovieContract.MovieEntry.buildMovieWithSortBy(Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key)));

        Cursor cursorn = getContentResolver().query(urin, null, null, null, null);
        return cursorn;
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
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.no_fav_movies));
            } else {
                Utils.downloadData(this, String.format(Constant.POPULAR_MOVIES_URL, selectedSortBy, Constant.MOVIE_DB_API_KEY), this, Constant.MOVIE_TYPE, selectedSortBy);
            }
        }
    }

    private void updateDataOnUI() {
        mErrorTextView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        if (null != getSupportLoaderManager().getLoader(MOVIE_LOADER)) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        } else {
            getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Utils.launchSettingActivity(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mMovieCursorAdapter.getItem(position);
        String movieID = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        Uri uri = MovieContract.MovieEntry.buildMovieWithSortByAndId(selectedSortBy, movieID);
        openMovieDetail(movieID);
    }

    private void openMovieDetail(String movieID) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.movie_id_key, movieID);
        intent.putExtras(bundle);
        startActivity(intent);
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

    private void updateErrorMsgOnUI(ErrorInfo movieData) {
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(movieData.getErrorMsg());
        mGridView.setVisibility(View.GONE);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != results && results.size() > 0) {
            outState.putParcelableArrayList(Constant.RESULT_DATA_ARRAY, results);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieWithSortBy(Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key)));

        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        mMovieCursorAdapter.swapCursor(cursor);
        updateDataInCaseOfEmptyCursor(cursor);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Toast.makeText(this,"gjh",Toast.LENGTH_LONG).show();
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
          /*  Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();*/
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
