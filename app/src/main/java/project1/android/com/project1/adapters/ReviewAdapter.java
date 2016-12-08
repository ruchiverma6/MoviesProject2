package project1.android.com.project1.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import project1.android.com.project1.R;
import project1.android.com.project1.data.MovieContract;

/**
 * Created by ruchi on 11/21/2016.
 */

public class ReviewAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String content = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
        viewHolder.mReviewContent.setText(content);
    }

    static class ViewHolder {
        public TextView mReviewContent;

        public ViewHolder(View view) {
            mReviewContent = (TextView) view.findViewById(R.id.review_text_view);

        }

    }
}
