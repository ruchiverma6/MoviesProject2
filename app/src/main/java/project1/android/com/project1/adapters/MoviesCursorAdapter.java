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

import project1.android.com.project1.helper.Constant;
import project1.android.com.project1.R;
import project1.android.com.project1.data.MovieContract;
import project1.android.com.project1.data.ResultData;


/**
 * Created by ruchi on 29/10/16.
 */
public class MoviesCursorAdapter extends CursorAdapter{

    private LayoutInflater mLayoutInflater;


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


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder=(ViewHolder) view.getTag();
        moviePosterImageUrl =String.format(Constant.MOVIE_POSTER_BASE_URL,Constant.MOVIE_POSTER_IMAGE_WIDTH).concat(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL)));
        Picasso.with(context).load(moviePosterImageUrl).into(viewHolder.mMoviePosterImageView);
    }

    static class ViewHolder{
        public final ImageView mMoviePosterImageView;
        public ViewHolder(View view){
            mMoviePosterImageView=(ImageView)view.findViewById(R.id.movie_poster_image_view);
        }
    }

}
