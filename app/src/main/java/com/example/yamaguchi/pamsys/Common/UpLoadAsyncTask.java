package com.example.yamaguchi.pamsys.Common;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by yamaguchi on 2017/11/18.
 */

public class UpLoadAsyncTask extends AsyncTask<String, Integer, String> {

    Callbacks.UpLoadCallback callback;
    String data;
    File file = null;
    HttpURLConnection con = null;

//    public UpLoadAsyncTask(Callbacks.UpLoadCallback callback) {
//        super();
//        this.callback = callback;
//    }
    public UpLoadAsyncTask(Callbacks.UpLoadCallback callback, File file) {
        super();
        this.callback = callback;
        this.file = file;
    }


    @Override
    protected String doInBackground(String... params) {

        String message = "";
        this.data = params[0];

        try {
            URL url = new URL(params[0]); //ファイルを受け取るCGIを指定
            Authenticator.setDefault(new BasicAuth() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return super.getPasswordAuthentication();
                }
            });

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");   // POST
            con.setDoOutput(true);          // POSTによるデータ送信を行う
            con.connect();

            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
            osw.write(params[1]);  //2行目以降はファイルの中身
            osw.flush();
            osw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s;
            while ((s = br.readLine()) != null) message += s;
            br.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return message;
        }
    }

    @Override
    protected void onPreExecute() {
        callback.onPreUpLoad();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // Log.d("url", values[0].toString());
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onPostUpLoad(result, this.file);
    }

    @Override
    protected void onCancelled() {
        callback.onUpLoadCancelled();
    }
}
