package com.example.yamaguchi.pamsys.Common;

import java.io.File;

/**
 * Created by yamaguchi on 2017/11/18.
 */

public interface Callbacks {

    public interface DownLoadCallback {
        public void onPostDownLoad(String result);

        public void onPreDownLoad();

        public void onDownLoadCancelled();
    }

    public interface UpLoadCallback{
        public void onPostUpLoad(String result, File file);

        public void onPreUpLoad();

        public void onUpLoadCancelled();
    }

    public interface SendLocationCallback {
        public void onResultSendLocation(String result);
    }

}
