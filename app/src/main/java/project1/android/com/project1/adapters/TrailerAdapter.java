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

public class TrailerAdapter extends CursorAdapter {

    private Context mContext;

    private LayoutInflater mLayoutInflater;


    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String trailerNum = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME));
        viewHolder.mTrailerNumTextViewTitle.setText(trailerNum);
    }

    static class ViewHolder {
        public TextView mTrailerNumTextViewTitle;

        public ViewHolder(View view) {
            mTrailerNumTextViewTitle = (TextView) view.findViewById(R.id.trailer_num_text_view);
        }

    }
}
