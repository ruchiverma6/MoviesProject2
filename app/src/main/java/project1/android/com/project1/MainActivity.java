package project1.android.com.project1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.Helper.Utils;
import project1.android.com.project1.adapters.MoviesArrayAdapter;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieData;
import project1.android.com.project1.data.ResultData;
import project1.android.com.project1.listeners.DataUpdateListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DataUpdateListener {

    //Reference variable to hold Grid view object
    private GridView mGridView;

    //MoviesArrayAdapter to bind data to GridView.
    private MoviesArrayAdapter mMovieArrayAdapter;

    //Reference variable to hold context object.
    private Context context;
    //Array to hold results;
    private ArrayList<ResultData> results;

    private ArrayList<ResultData> resultDataArrayList = new ArrayList<>();

    //Reference variable to hold textview.
    private TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(null!=savedInstanceState) {
            results = savedInstanceState.getParcelableArrayList(Constant.RESULT_DATA_ARRAY);
        }
        setUpActionBar();
        initComponents();
        downloadData();
    }

    private void setUpActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void downloadPopularMovies() {


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Method for initializing components.
     */
    private void initComponents() {
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mMovieArrayAdapter = new MoviesArrayAdapter(this, resultDataArrayList);
        mGridView.setAdapter(mMovieArrayAdapter);
        mGridView.setOnItemClickListener(this);
    }


    /***
     * Method to download data based on selected sort type.
     */
    public void downloadData() {
        if (null != results && results.size() > 0) {
             updateDataOnUI();
        } else {
            String selectedSortBy = Utils.getSharedPreferenceValue(this, getString(R.string.sort_by_key));
            Utils.downloadData(this, String.format(Constant.POPULAR_MOVIES_URL, selectedSortBy, Constant.MOVIE_DB_API_KEY), this, Constant.MOVIE_TYPE);
        }
    }

    private void updateDataOnUI() {
        mErrorTextView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);

        mMovieArrayAdapter.addAll(results);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        openMovieDetail(results.get(position));
    }

    private void openMovieDetail(ResultData resultData) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.movie_id_key, resultData.getId());
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
        if(null!=results && results.size()>0) {
            outState.putParcelableArrayList(Constant.RESULT_DATA_ARRAY, results);
        }
    }


}
