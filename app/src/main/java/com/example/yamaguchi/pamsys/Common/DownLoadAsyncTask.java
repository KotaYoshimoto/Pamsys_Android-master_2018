package com.example.yamaguchi.pamsys.Common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.ServiceInformationMasterData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;

import javax.security.auth.callback.Callback;

/**
 * Created by yamaguchi on 2017/11/18.
 */

public class DownLoadAsyncTask extends AsyncTask<String, Integer, String> {

    Callbacks.DownLoadCallback callback;
    String currentCourseNum;
    Context context;

    public DownLoadAsyncTask(Context context, Callbacks.DownLoadCallback callback) {
        super();
        this.context = context;
        this.callback = callback;
    }

    public DownLoadAsyncTask(Context context, Callbacks.DownLoadCallback callback, String currentCourseNum) {
        super();
        this.context = context;
        this.callback = callback;
        this.currentCourseNum = currentCourseNum;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();

        Log.d("DownLoadTask",params[0]);

        try {
            URL url = new URL(params[0]);
            Authenticator.setDefault(new BasicAuth() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return super.getPasswordAuthentication();
                }
            });

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            //追記
            reader.close();
            con.disconnect();
            //
        } catch (UnknownHostException e) {
            e.printStackTrace();
                builder.append(NetworkExceptions.NOTCONNECT.toString());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append(NetworkExceptions.NOTFOUND.toString());
        }
        return builder.toString();
    }

    @Override
    protected void onPreExecute() {
        callback.onPreDownLoad();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onPostDownLoad(result);
    }

    @Override
    protected void onCancelled() {
        callback.onDownLoadCancelled();
    }
}
