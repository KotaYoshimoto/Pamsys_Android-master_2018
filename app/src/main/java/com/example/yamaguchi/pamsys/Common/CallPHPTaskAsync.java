package com.example.yamaguchi.pamsys.Common;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 * Created by yamaguchi on 2017/11/25.
 */

public class CallPHPTaskAsync extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            Authenticator.setDefault(new BasicAuth() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return super.getPasswordAuthentication();
                }
            });

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //con.setRequestProperty ("Authorization", basicAuth);
            con.setRequestMethod("POST");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
