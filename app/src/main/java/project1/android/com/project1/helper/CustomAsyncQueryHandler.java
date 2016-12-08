package project1.android.com.project1.helper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by v-ruchd on 12/8/2016.
 */

public class CustomAsyncQueryHandler extends AsyncQueryHandler {
    private AsyncQueryHandlerListener asyncQueryHandlerListener;

    public CustomAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public void setAsyncQueryHandlerListener(AsyncQueryHandlerListener asyncQueryHandlerListener){
        this.asyncQueryHandlerListener=asyncQueryHandlerListener;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if(null!=asyncQueryHandlerListener){
            asyncQueryHandlerListener.onInsertComplete(token,cookie,uri);
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if(null!=asyncQueryHandlerListener){
            asyncQueryHandlerListener.onDeleteComplete(token,cookie,result);
        }
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(null!=asyncQueryHandlerListener){
            asyncQueryHandlerListener.onQueryComplete(token,cookie,cursor);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        if(null!=asyncQueryHandlerListener){
            asyncQueryHandlerListener.onUpdateComplete(token,cookie,result);
        }
    }
}
