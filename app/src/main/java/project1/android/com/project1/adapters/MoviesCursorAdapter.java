package project1.android.com.project1.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project1.android.com.project1.Helper.Constant;
import project1.android.com.project1.R;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.ResultData;


/**
 * Created by ruchi on 29/10/16.
 */
public class MoviesCursorAdapter extends CursorAdapter{

    private LayoutInflater mLayoutInflater;

    //ImageView to show movie poster.
    private ImageView mMoviePosterImageView;

    private ResultData result;
    private ArrayList<ResultData> results;
    private String moviePosterImageUrl;
    private Context context;

    public MoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getCount() {
        if(getCursor()==null){
            return 0;
        }
        return getCursor().getCount();
    }

/* public MoviesArrayAdapter(Context context, ArrayList<ResultData> results) {
        super(context, 0, results);
        this.context=context;
        this.results=results;
        mLayoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }*/


    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        result=getItem(position);
        moviePosterImageUrl =String.format(Constant.MOVIE_POSTER_BASE_URL,Constant.MOVIE_POSTER_IMAGE_WIDTH).concat(result.getPoster_path());
        if(convertView==null){
            convertView=  mLayoutInflater.inflate(R.layout.grid_view_item,null);
        }
        mMoviePosterImageView=(ImageView)convertView.findViewById(R.id.movie_poster_image_view);
        Picasso.with(context).load(moviePosterImageUrl).into(mMoviePosterImageView);
        return convertView;
    }*/

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mMoviePosterImageView=(ImageView)view.findViewById(R.id.movie_poster_image_view);
        moviePosterImageUrl =String.format(Constant.MOVIE_POSTER_BASE_URL,Constant.MOVIE_POSTER_IMAGE_WIDTH).concat(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL)));


        Picasso.with(context).load(moviePosterImageUrl).into(mMoviePosterImageView);
    }

   /* *//***
     * Method to set data to views.
     * @param results
     *//*
    public void setData(ArrayList<ResultData> results) {
        this.results=results;
        notifyDataSetChanged();
    }*/
}
