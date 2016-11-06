package project1.android.com.project1.Helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ruchi on 31/10/16.
 */
public class HttpHelper {

    private final Context context;

    public HttpHelper(Context context) {
this.context=context;
    }

    public Message doHttpGet(String urlStr) {
        URL url = null;
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String responseStr = null;
        if (Utils.isNetworkAvailable(context)) {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                responseStr = readStream(inputStream);


                bundle.putString(Constant.RESPONSE_DATA, responseStr);
                bundle.putInt(Constant.RESPONSE_CODE, httpURLConnection.getResponseCode());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }

        } else {
            bundle.putInt(Constant.RESPONSE_CODE, Constant.NO_INTERNET_CONNECTION);
        }
        msg.setData(bundle);
        return msg;

    }

    private String readStream(InputStream inputStream) {
        BufferedReader bufferedReader;
        if (inputStream != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != bufferedReader) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return stringBuilder.toString();
        }
        return null;
    }
}
