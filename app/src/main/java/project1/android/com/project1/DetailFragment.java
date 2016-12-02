package project1.android.com.project1;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.Helper.Utils;
import project1.android.com.project1.adapters.MoviesCursorAdapter;
import project1.android.com.project1.data.MovieContract;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{
    private Button mTrailersBtn;
    private Button mReviewButton;
    private TrailersFragment mTrailersFragment;
    private ReviewsFragment mReviewsFragment;
    private FrameLayout mFrameLayout;
    private FragmentManager mFragmentSupportManager;
    //ListView object to hold movie trailers
    private ListView mMovieTrailerListView;
    static final String DETAIL_URI = "URI";

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
    private Activity mActivity;
    private GridView mGridView;
    //MoviesArrayAdapter to bind data to GridView.
    private MoviesCursorAdapter mMovieCursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        Bundle arguments = getArguments();
        if (arguments != null) {
            uri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            movieId=uri.getPathSegments().get(2);
        }
        setUpActionBar();
       // Bundle bundle = getIntent().getExtras();
      //  movieId = bundle.getString(Constant.movie_id_key);
        initComponents();
        getLoaderManager().initLoader(0, null, this);

        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Fragment trailerFragment = getChildFragmentManager().findFragmentByTag(getString(R.string.trailers));
                Fragment reviewFragment = getChildFragmentManager().findFragmentByTag(getString(R.string.reviews));
                if (trailerFragment != null) {
                  //  getSupportActionBar().setTitle(getString(R.string.trailers));
                } else if (reviewFragment != null) {
                   // getSupportActionBar().setTitle(getString(R.string.reviews));
                } else {
                  //  getSupportActionBar().setTitle(getString(R.string.title_activity_movie_detail));
                }
            }
        });
    }

    private void setUpActionBar() {
      /*  Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/
    }

    /***
     * Method to initialize components.
     */
    private void initComponents() {
        mErrorTextView = (TextView) mActivity.findViewById(R.id.error_text_view);
        mDetailLayout = (RelativeLayout) mActivity.findViewById(R.id.movie_detail_layout);
        mMoviePosterImageView = (ImageView) mActivity.findViewById(R.id.movie_poster_detail_image_view);
        mMovieDescriptionTextView = (TextView) mActivity.findViewById(R.id.movie_description_text_view);
        mMarkAsFavBtn = (ToggleButton) mActivity.findViewById(R.id.fav_button);
        mNumTextView = (TextView) mActivity.findViewById(R.id.num_text_view);
        mYearTextView = (TextView) mActivity.findViewById(R.id.year_text_view);

        mMoviePosterTitleTextView = (TextView) mActivity.findViewById(R.id.movie_poster_title);

        mFragmentSupportManager = getChildFragmentManager();

        mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.frame_content);
        mTrailersFragment = new TrailersFragment();
        mReviewsFragment = new ReviewsFragment();
        mTrailersBtn = (Button) mActivity.findViewById(R.id.trailers_btn);
        mReviewButton = (Button) mActivity.findViewById(R.id.review_btn);
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
            mActivity.getContentResolver().insert(MovieContract.SortByEntry.CONTENT_URI, contentValues);
        } else {
            String[] selectionArgs = new String[]{movieId, getString(R.string.favorite)};
            mActivity.getContentResolver().delete(MovieContract.SortByEntry.CONTENT_URI, MovieContract.SortByEntry.selection, selectionArgs);
        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ";
        String[] selectionArgs = new String[]{movieId};
        return new CursorLoader(mActivity, MovieContract.MovieEntry.CONTENT_URI, null, selection, selectionArgs, null);
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
    private void addFragment(Fragment fragment, String tag) {

        Bundle bundle = new Bundle();
        bundle.putString(Constant.MOVIE_ID_KEY, movieId);

        fragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.frame_content, fragment, tag).addToBackStack(null).commit();


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
        Picasso.with(mActivity).load(moviePosterImageUrl).into(mMoviePosterImageView);
        if (isMovieAddedToFavorite()) {
            mMarkAsFavBtn.setChecked(true);
        } else {
            mMarkAsFavBtn.setChecked(false);
        }

    }

    private boolean isMovieAddedToFavorite() {
        String selection = MovieContract.SortByEntry.COLUMN_MOVIE_KEY + " =? AND " + MovieContract.SortByEntry.COLUMN_SORT_TYPE + " =? ";
        String[] selectionArgs = new String[]{movieId, getString(R.string.favorite)};
        Cursor cursor = mActivity.getContentResolver().query(MovieContract.SortByEntry.CONTENT_URI, null, selection, selectionArgs, null);
        if (null != cursor && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }


}
