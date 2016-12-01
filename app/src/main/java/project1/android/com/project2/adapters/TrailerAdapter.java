package project1.android.com.project2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import project1.android.com.project2.R;
import project1.android.com.project2.data.MovieContract;

/**
 * Created by v-ruchd on 11/21/2016.
 */

public class TrailerAdapter extends CursorAdapter {

    private Context mContext;

    private LayoutInflater mLayoutInflater;


    private ImageView mPlayIcon;
    private TextView mTrailerNumTextViewTitle;

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }




    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mPlayIcon = (ImageView) view.findViewById(R.id.play_imageview_icon);
        mTrailerNumTextViewTitle = (TextView) view.findViewById(R.id.trailer_num_text_view);
        String trailerNum=cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME));
        mTrailerNumTextViewTitle.setText(trailerNum);
    }
}
