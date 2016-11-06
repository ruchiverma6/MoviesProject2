package project1.android.com.project1;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.Helper.Utils;
import project1.android.com.project1.data.Data;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieDetailData;
import project1.android.com.project1.listeners.DataUpdateListener;

public class MovieDetailActivity extends AppCompatActivity implements DataUpdateListener {

    //ListView object to hold movie trailers
    private ListView mMovieTrailerListView;

    //TrailersAdapter object to bind data to TrailersListView
    private TrailersAdapter mTrailersAdapter;

    //TextView object to hold movie poster title.
    private TextView mMoviePosterTitleTextView;

    //ImageView object to hold movie poster imageview.
    private ImageView mMoviePosterImageView;

    //TextView object to hold year.
    private TextView mYearTextView;

    //TextView object to hold hour.
    private TextView mHourTextView;

    //TextView object to hold num.
    private TextView mNumTextView;

    //Button object to hold mark as favorite button
    private Button mMarkAsFavBtn;

    //TextView object to hold movie description.
    private TextView mMovieDescriptionTextView;


    //Movie Id .
    private String movieId;

    //Movie poster image url string.
    private String moviePosterImageUrl;

    private MovieDetailData movieDetailData;

    private RelativeLayout mDetailLayout;

    private TextView mErrorTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if(null!=savedInstanceState) {
            movieDetailData=savedInstanceState.getParcelable(Constant.MOVIE_DETAIL_DATA);
        }
        setUpActionBar();
        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getString(Constant.movie_id_key);
        initComponents();

       downloadData();

    }

    private void downloadData() {
        if(null!=movieDetailData){
          setDataOnUI();
           }else {
            Utils.downloadData(this,String.format(Constant.MOVIE_DETAIL_URL, movieId, Constant.MOVIE_DB_API_KEY), this, Constant.MOVIE_DETAIL_TYPE);

        }
    }


    /***
     * Method to set up action bar.
     */
    private void setUpActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }


    /***
     * Method to update data on UI.
     */
    private void setDataOnUI() {
        mMoviePosterTitleTextView.setVisibility(View.VISIBLE);
        mDetailLayout.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
        moviePosterImageUrl = String.format(Constant.MOVIE_POSTER_BASE_URL, Constant.MOVIE_POSTER_IMAGE_WIDTH).concat(movieDetailData.getPoster_path());

        mMovieDescriptionTextView.setText(movieDetailData.getOverview());

        mYearTextView.setText(Utils.parseDate(movieDetailData.getRelease_date(), Constant.DATE_FORMAT_PATTERN));
        mHourTextView.setText(movieDetailData.getRuntime().concat(getString(R.string.min)));
        mMoviePosterTitleTextView.setText(movieDetailData.getOriginal_title());
        mNumTextView.setText(String.valueOf(movieDetailData.getVote_average()));
        Picasso.with(this).load(moviePosterImageUrl).into(mMoviePosterImageView);
    }

    /***
     * Method to initialize components.
     */
    private void initComponents() {
        mErrorTextView=(TextView)findViewById(R.id.error_text_view);
        mDetailLayout =(RelativeLayout)findViewById(R.id.movie_detail_layout);
        mMoviePosterImageView = (ImageView) findViewById(R.id.movie_poster_detail_image_view);
        mMovieDescriptionTextView = (TextView) findViewById(R.id.movie_description_text_view);
        mMarkAsFavBtn = (Button) findViewById(R.id.fav_button);
        mNumTextView = (TextView) findViewById(R.id.num_text_view);
        mYearTextView = (TextView) findViewById(R.id.year_text_view);
        mHourTextView = (TextView) findViewById(R.id.hour_text_view);
        mMoviePosterTitleTextView = (TextView) findViewById(R.id.movie_poster_title);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDataUpdate(Data movieData) {
       
        if(movieData instanceof ErrorInfo){
            updateErrorMessageOnUI((ErrorInfo) movieData);
        }else {
            movieDetailData = (MovieDetailData) movieData;
            setDataOnUI();
        }
    }

    private void updateErrorMessageOnUI(ErrorInfo errorInfo) {
        mMoviePosterTitleTextView.setVisibility(View.GONE);
        mDetailLayout.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(errorInfo.getErrorMsg());

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null!=movieDetailData) {
            outState.putParcelable(Constant.MOVIE_DETAIL_DATA, movieDetailData);
        }
    }
}
