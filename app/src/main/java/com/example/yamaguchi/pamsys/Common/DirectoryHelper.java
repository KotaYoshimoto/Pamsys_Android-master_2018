package com.example.yamaguchi.pamsys.Common;

import android.content.Context;
import android.media.AudioRecord;
import android.util.Log;

import com.example.yamaguchi.pamsys.R;

import java.io.File;

/**
 * Created by yamaguchi on 2017/11/14.
 */

public class DirectoryHelper {


    public static String PREVIOUS = null;
    public static String SERVICEINFORMATION_JSON = null;
    public static String CURRENT = null;
    public static String UPLOAD = null;
    public static String DOWNLOAD = null;
    public static String SERVICEINFORMATION_MASTERDATA_JSON = null;
    public static String SERVICEINFORMATION_MASTERDATA=null;
    public static String RESERVATIONS = null;
    public static String RESERVATIONS_JSON = null;
    public static String UNREACHED = null;

    public enum MODE {NOT_CREATE, CREATE_DIR}

    private Context context;

    /**
     * ファイルやフォルダの存在をチェックし，存在しないなら場合によって作成する
     *
     * @param context
     */
    public DirectoryHelper(Context context) {
        this.context = context;
        PREVIOUS = context.getString(R.string.previous);
        SERVICEINFORMATION_JSON = context.getString(R.string.service_information);
        CURRENT = context.getString(R.string.current);
        UPLOAD = context.getString(R.string.upload);
        DOWNLOAD = "DownLoad";
        SERVICEINFORMATION_MASTERDATA_JSON = "ServiceInformationMasterData.json";
        SERVICEINFORMATION_MASTERDATA = "ServiceInformationMasterData";
        RESERVATIONS = "Reservations";
        RESERVATIONS_JSON = "Reservations.json";
        UNREACHED = "Unreached";
    }

    /**
     * パスを生成する
     *
     * @param names
     * @return
     */
    public String path(String... names) {
        String path = "";
        for (int i = 0; i < names.length; i++) {
            path += names[i];
            if (i == names.length - 1) {
                break;
            }
            path += "/";
        }
        return path;
    }

    /**
     * ディレクトリが存在するかどうかを判定し，存在しないなら場合によって作成する
     *
     * @param mode
     * @param names
     * @return
     */
    public boolean check(MODE mode, String... names) {

        String path = path(names);
        //debug
        Log.d(getClass().getName(), path);

        File file = new File(context.getFilesDir(), path);
        if (!file.exists()) {
            if (mode == MODE.CREATE_DIR) {
                file.mkdirs();
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ファイル・フォルダを削除する
     * @param f
     */
    public static void delete(File f) {
        if (f.exists() == false) {
            return;
        }
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
            f.delete();
        }
    }

}
