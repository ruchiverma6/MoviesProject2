package project1.android.com.project1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import project1.android.com.project1.helper.Constant;
import project1.android.com.project1.helper.Utils;
import project1.android.com.project1.data.ErrorInfo;
import project1.android.com.project1.data.MovieContract;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private Button mTrailersBtn;
    private Button mReviewButton;
    private TrailersFragment mTrailersFragment;
    private ReviewsFragment mReviewsFragment;
    private FrameLayout mFrameLayout;
    private FragmentManager mFragmentSupportManager;
    //ListView object to hold movie trailers
    private ListView mMovieTrailerListView;


    //TextView object to hold movie poster title.
    private TextView mMoviePosterTitleTextView;

    //ImageView object to hold movie poster imageview.
    private ImageView mMoviePosterImageView;

    //TextView object to hold year.
    private TextView mYearTextView;


    //TextView object to hold num.
    private TextView mNumTextView;

    //Button object to hold mark as favorite button
    private ToggleButton mMarkAsFavBtn;

    //TextView object to hold movie description.
    private TextView mMovieDescriptionTextView;


    //Movie Id .
    private String movieId;

    //Movie poster image url string.
    private String moviePosterImageUrl;

    // private MovieDetailData movieDetailData;

    private RelativeLayout mDetailLayout;

    private TextView mErrorTextView;
    private Uri uri;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;
        uri = getIntent().getData();
        setUpActionBar();
        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getString(Constant.movie_id_key);
        initComponents();
        getSupportLoaderManager().initLoader(0, null, this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Fragment trailerFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.trailers));
                Fragment reviewFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.reviews));
                if (trailerFragment != null) {
                    getSupportActionBar().setTitle(getString(R.string.trailers));
                } else if (reviewFragment != null) {
                    getSupportActionBar().setTitle(getString(R.string.reviews));
                } else {
                    getSupportActionBar().setTitle(getString(R.string.title_activity_movie_detail));
                }
            }
        });
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
    private void setDataOnUI(Cursor cursor) {
        mMoviePosterTitleTextView.setVisibility(View.VISIBLE);
        // mDetailLayout.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.GONE);
        cursor.moveToFirst();
        moviePosterImageUrl = String.format(Constant.MOVIE_POSTER_BASE_URL, Constant.MOVIE_POSTER_IMAGE_WIDTH).concat(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL)));

        mMovieDescriptionTextView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION)));

        mYearTextView.setText(Utils.parseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE)), Constant.DATE_FORMAT_PATTERN));

        mMoviePosterTitleTextView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_TITLE)));
        mNumTextView.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE))));
        Picasso.with(this).load(moviePosterImageUrl).into(mMoviePosterImageView);
        if (isMovieAddedToFavorite()) {
            mMarkAsFavBtn.setChecked(true);
        } else {
            mMarkAsFavBtn.setChecked(false);
        }

    }


    private boolean isMovieAddedToFavorite() {
        String selection = MovieContract.SortByEntry.COLUMN_MOVIE_KEY + " =? AND " + MovieContract.SortByEntry.COLUMN_SORT_TYPE + " =? ";
        String[] selectionArgs = new String[]{movieId, getString(R.string.favorite)};
        Cursor cursor = getContentResolver().query(MovieContract.SortByEntry.CONTENT_URI, null, selection, selectionArgs, null);
        if (null != cursor && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    /***
     * Method to initialize components.
     */
    private void initComponents() {
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mDetailLayout = (RelativeLayout) findViewById(R.id.movie_detail_layout);
        mMoviePosterImageView = (ImageView) findViewById(R.id.movie_poster_detail_image_view);
        mMovieDescriptionTextView = (TextView) findViewById(R.id.movie_description_text_view);
        mMarkAsFavBtn = (ToggleButton) findViewById(R.id.fav_button);
        mNumTextView = (TextView) findViewById(R.id.num_text_view);
        mYearTextView = (TextView) findViewById(R.id.year_text_view);

        mMoviePosterTitleTextView = (TextView) findViewById(R.id.movie_poster_title);

        mFragmentSupportManager = getSupportFragmentManager();

        mFrameLayout = (FrameLayout) findViewById(R.id.frame_content);
        mTrailersFragment = new TrailersFragment();
        mReviewsFragment = new ReviewsFragment();
        mTrailersBtn = (Button) findViewById(R.id.trailers_btn);
        mReviewButton = (Button) findViewById(R.id.review_btn);
        mTrailersBtn.setOnClickListener(this);
        mReviewButton.setOnClickListener(this);
        mMarkAsFavBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isAddFavorite = false;
                if (isChecked) {
                    isAddFavorite = true;

                }
                addFavoriteToDb(isAddFavorite);
            }
        });

    }


    private void addFavoriteToDb(boolean IsAddToFavorite) {
        if (IsAddToFavorite) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.SortByEntry.COLUMN_MOVIE_KEY, movieId);
            contentValues.put(MovieContract.SortByEntry.COLUMN_SORT_TYPE, getString(R.string.favorite));
            getContentResolver().insert(MovieContract.SortByEntry.CONTENT_URI, contentValues);
        } else {
            String[] selectionArgs = new String[]{movieId, getString(R.string.favorite)};
            getContentResolver().delete(MovieContract.SortByEntry.CONTENT_URI, MovieContract.SortByEntry.selection, selectionArgs);
        }
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ";
        String[] selectionArgs = new String[]{movieId};
        return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setDataOnUI(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trailers_btn: {
                addFragment(mTrailersFragment, getString(R.string.trailers));
                break;
            }
            case R.id.review_btn: {
                addFragment(mReviewsFragment, getString(R.string.reviews));
                break;
            }
            default:
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.title_activity_movie_detail));

    }

    private void addFragment(Fragment fragment, String tag) {

        Bundle bundle = new Bundle();
        bundle.putString(Constant.MOVIE_ID_KEY, movieId);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_content, fragment, tag).addToBackStack(null).commit();


    }


}
