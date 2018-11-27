package com.example.yamaguchi.pamsys.Common;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yamaguchi.pamsys.R;
import com.example.yamaguchi.pamsys.ServiceInformationSettingActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class SendLocationAsyncTask extends AsyncTask<Uri.Builder,Integer,String> {

    //String data;
    private Callbacks.SendLocationCallback callback;
    private File filepath;
    private String filename;
    private String rootpath;
    private String date;

    public SendLocationAsyncTask (Callbacks.SendLocationCallback callback, String filename, File filepath,
                                  String rootpath, String date) {
        super();
        this.callback = callback;
        this.filename = filename;
        this.filepath = filepath;
        this.rootpath = rootpath;
        this.date = date;
    }

    @Override
    protected String doInBackground(Uri.Builder...builders) {

        StringBuilder sb = new StringBuilder();

        //Log.d("in_check", filename +","+ filepath +","+ date);

        if(!PostServiceLog(filename, filepath, date)) {
            //callback.onErrorSendLocation();
            // 送信不可時はデータを残す
            sb.append("failed");
        } else {
            // 送信完了時はデータを削除する
            // このときフォルダの中身が空になったらフォルダも消す
            // 送信完了なら該当する運行ログを削除する
            Log.d("in_check", filename +","+ filepath +","+ date);
            filepath.delete();
            File[] files = new File(rootpath).listFiles();
            if(files.length == 0) new File(rootpath).delete();
            sb.append("success");
        }
        //Log.d("in_check2", filename +","+ filepath +","+ date);
        return sb.toString();
        /*StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(String.valueOf(R.string.server_name) + String.valueOf(R.string.server_location_info)); //ファイルを受け取るCGIを指定
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

            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(),"SJIS");
            osw.write(filename+"\r\n");//
            osw.write(date + "\r\n");//

            try{
                FileInputStream in = new FileInputStream(filepath);
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                while((line = reader.readLine()) != null){
                    osw.write(line + "\n");
                }
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            osw.flush();
            osw.close();

            int status = con.getResponseCode();
            Log.d("status", String.valueOf(status));

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String s;
            while ((s = br.readLine()) != null) {
                builder.append(s);
            }
            br.close();
            con.disconnect();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            builder.append(NetworkExceptions.NOTCONNECT.toString());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append(NetworkExceptions.NOTFOUND.toString());
        }
        return builder.toString();*/
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("connect_debug", result);
        callback.onResultSendLocation(result);
    }

    @Override
    protected void onCancelled() {

    }

    public static boolean PostServiceLog(String txtFilename, File txt, String date) {
        try {
            URL url = new URL("http://pbl.ict.ehime-u.ac.jp/pamsys_2018/" + "driver/serviceLog/saveLocationInfo.php"); //ファイルを受け取るCGIを指定
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

            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(),"SJIS");
            osw.write(txtFilename +"\r\n"); // 1行目にファイルのパス名
            osw.write(date +"\r\n"); // 2行目に日付

            try{
                FileInputStream in = new FileInputStream(txt);
                String lineBuffer;
                BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
                while( (lineBuffer = reader.readLine()) != null ) {
                    osw.write(lineBuffer + "\n");
                }
                in.close();
            }catch( IOException e ){
                e.printStackTrace();
                return false;
            }

            osw.flush();
            osw.close();

            int status = con.getResponseCode();
            Log.d("status", String.valueOf(status));

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }

            /*if(con.getResponseCode() != 200 ){
                throw new Exception("送信失敗");
            }*/
            con.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
