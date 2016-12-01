package project1.android.com.movies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import project1.android.com.movies.R;
import project1.android.com.movies.data.MovieContract;

/**
 * Created by v-ruchd on 11/21/2016.
 */

public class ReviewAdapter extends CursorAdapter {


    private LayoutInflater mLayoutInflater;
    private TextView mReviewContent;

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mReviewContent = (TextView) view.findViewById(R.id.review_text_view);
        String content = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
        mReviewContent.setText(content);
    }


}
