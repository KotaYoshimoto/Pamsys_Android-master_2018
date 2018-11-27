package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.Common.CallPHPTaskAsync;
import com.example.yamaguchi.pamsys.Common.Callbacks;
import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.DownLoadAsyncTask;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.NetworkExceptions;
import com.example.yamaguchi.pamsys.Common.SendLocationAsyncTask;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.Common.UpLoadAsyncTask;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Course;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.ServiceInformationMasterData;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.example.yamaguchi.pamsys.ServiceInformationSettingActivityHelperClasses.CurrentDateTime;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServiceInformationSettingActivity extends Activity implements Callbacks.DownLoadCallback, Callbacks.UpLoadCallback,Callbacks.SendLocationCallback {

    //region UIの宣言
    TextView showDateTextView;
    Spinner driverSpinner;
    Spinner carNumberSpinner;
    Spinner courceSpinner;
    EditText mileageEditText;
    Button showPreDriveRecordsButton;
    Button sendUnreachedDataButton;
    //endregion
    //
    ServiceInformationMasterData serviceInformationMasterData = new ServiceInformationMasterData();
    ServiceInformation serviceInformation = new ServiceInformation();
    //ダウンロード・アップロードタスク
    DownLoadAsyncTask downLoadAsyncTask;
    UpLoadAsyncTask[] upLoadAsyncTask;
    //PHPを実行するだけのタスク
    CallPHPTaskAsync callPHPTaskAsync;

    ProgressDialog progress;
    //未送信データ
    File[] files;
    String[] sendFolder;
    //IntentのRequestID

    final String rootPath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_informaton_setting);

        //region UIの紐づけ
        showDateTextView = (TextView) findViewById(R.id.showDateTextView);
        driverSpinner = (Spinner) findViewById(R.id.driverSpinner);
        carNumberSpinner = (Spinner) findViewById(R.id.carNumberSpinner);
        courceSpinner = (Spinner) findViewById(R.id.courceSpinner);
        mileageEditText = (EditText) findViewById(R.id.mileageEditText);
        showPreDriveRecordsButton = (Button) findViewById(R.id.showPreDriveRecordsButton);
        sendUnreachedDataButton = (Button) findViewById(R.id.sendUnreachedDataButton);
        //endregion

        /*
        データの読み込み
         */
        //region ServiceInformaitonMasterDataをダウンロードする
        //AsyncTaskを開始する
        downLoadAsyncTask = new DownLoadAsyncTask(ServiceInformationSettingActivity.this, this);
        downLoadAsyncTask.execute(getString(R.string.server_name) + getString(R.string.server_master_data));
        //endregion

        //region 前回の運行記録ボタンについて
        final DirectoryHelper directoryChecker = new DirectoryHelper(this);
        //Previousディレクトリが存在しないなら，Previousディレクトリを作成する
        directoryChecker.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.PREVIOUS);
        //前回の運行記録を表すファイルが存在するなら，ボタンを有効にする
        if (directoryChecker.check(DirectoryHelper.MODE.NOT_CREATE, DirectoryHelper.UPLOAD, DirectoryHelper.PREVIOUS, DirectoryHelper.SERVICEINFORMATION_JSON)) {
            showPreDriveRecordsButton.setEnabled(true);
        } else {
            showPreDriveRecordsButton.setEnabled(false);
        }
        //endregion

        //region 未送信データについて
        final DirectoryHelper directoryHelper = new DirectoryHelper(this);
        directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.UNREACHED);
        String path = getFilesDir().getPath().toString() + "/" + directoryHelper.path(DirectoryHelper.UNREACHED);
        files = new File(path).listFiles();
        //追加
        //final String rootPath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log";

        sendFolder = new File(rootPath).list();

        if (files.length == 0 && (sendFolder == null || sendFolder.length == 0)) {
            sendUnreachedDataButton.setEnabled(false);
        } else {
            sendUnreachedDataButton.setEnabled(true);
        }
        //

        //region 各種部品に，データをセットする
        //日付表示
        CurrentDateTime currentDateTime = new CurrentDateTime();
        showDateTextView.setText(currentDateTime.showDate());
        //endregion

        //前回の運行記録ボタン
        findViewById(R.id.showPreDriveRecordsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceInformationSettingActivity.this, DriverRecordsActivity.class);
                intent.putExtra("isFromServiceInformationSettingActivity", true);
                startActivity(intent);
            }
        });

        //運行開始ボタン
        findViewById(R.id.startServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //サーバの予約情報を初期化するスクリプトを実行する．
                callPHPTaskAsync = new CallPHPTaskAsync();
                callPHPTaskAsync.execute(getString(R.string.server_name) + getString(R.string.server_reseration_confirm));
                //前回の運行が終了しているとき
                if (SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()) == 1 && SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext()) == 0) {
                    if (checkInput()) {
                        setServiceInformation(serviceInformationMasterData, serviceInformation);
                        //Currentフォルダをチェックする
                        DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                        //Upload/Currentディレクトリが存在しないなら，Currentディレクトリを作成する
                        directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT);
                        //region jsonファイルへ
                        FileOutputStream fileOutputStream = null;
                        try {
                            //Upload/Currentディレクトリへ，ServiceInformation.jsonを保存する
                            fileOutputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)));
                            //jsonファイルへ
                            ObjectMapper serviceInformationObjectMapper = new ObjectMapper();
                            serviceInformationObjectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
                            String json = serviceInformationObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInformation);
                            fileOutputStream.write(json.getBytes());
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //endregion
                        //region コース便数・停留所に関する設定を初期化する
                        SharedPreferenceHelper.setCurrentCourseNum(getApplicationContext(), 1);
                        SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), 0);
                        SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), 0);
                        //endregion
                        //region 既存の予約表を削除する
                        File deleteFile = new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS));
                        directoryHelper.delete(deleteFile);
                        //endregion
                        //region 次便の車両種別を判断する　
                        // 次便が定期便である
                        if (getPassengerInformationOnRoutesByCourseNum().route.carType.equals("定期")) {
                            SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "定期");
                            Intent intent = new Intent(ServiceInformationSettingActivity.this, PassengerManagementOnRegularServiceActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            ServiceInformationSettingActivity.this.finish();
                        }
                        //次便がデマンド便である
                        else {
                            SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "デマンド");
                            Intent intent = new Intent(ServiceInformationSettingActivity.this, ReservationBusStopListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            ServiceInformationSettingActivity.this.finish();
                        }
                        //endregion
                    } else {
                        Toast.makeText(getApplicationContext(), "走行距離が入力されていません．\nまた，無効な文字やスペースが含まれています．", Toast.LENGTH_LONG).show();
                    }
                }
                //前回の運行が完全に終了していないとき
                else {
                    //region 再開を尋ねるAlertDialogを生成する
                    new AlertDialog.Builder(ServiceInformationSettingActivity.this)
                            .setTitle("運行開始について")
                            .setMessage("新規に運行記録を開始しますか？\n以前の運行記録を再開しますか？")
                            .setPositiveButton("新規に開始する", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //region 新規に開始するとき
                                    // 入力チェックを行う
                                    if (checkInput()) {
                                        setServiceInformation(serviceInformationMasterData, serviceInformation);
                                        //Currentフォルダをチェックする
                                        DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                                        //Upload/Currentディレクトリが存在しないなら，Currentディレクトリを作成する
                                        directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT);
                                        //region jsonファイルへ
                                        FileOutputStream fileOutputStream = null;
                                        try {
                                            //Upload/Currentディレクトリへ，ServiceInformation.jsonを保存する
                                            fileOutputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)));
                                            //jsonファイルへ
                                            ObjectMapper serviceInformationObjectMapper = new ObjectMapper();
                                            serviceInformationObjectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
                                            String json = serviceInformationObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInformation);
                                            fileOutputStream.write(json.getBytes());
                                            fileOutputStream.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //endregion
                                        //region コース便数・停留所に関する設定を初期化する
                                        SharedPreferenceHelper.setCurrentCourseNum(getApplicationContext(), 1);
                                        SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), 0);
                                        SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), 0);
                                        //endregion
                                        //region 既存のServiceInformation.jsonと予約表を削除する
                                        File deleteFile = new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS));
                                        directoryHelper.delete(deleteFile);
                                        //endregion
                                        //次便が定期便である
                                        if (getPassengerInformationOnRoutesByCourseNum().route.carType.equals("定期")) {
                                            SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "定期");
                                            Intent intent = new Intent(ServiceInformationSettingActivity.this, PassengerManagementOnRegularServiceActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            intent.putExtra("isFromServiceInformationSettingActivity", true);
                                            ServiceInformationSettingActivity.this.finish();
                                        } else {
                                            SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "デマンド");
                                            Intent intent = new Intent(ServiceInformationSettingActivity.this, ReservationBusStopListActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            intent.putExtra("isFromServiceInformationSettingActivity", true);
                                            ServiceInformationSettingActivity.this.finish();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "走行距離が入力されていません．\nまた，無効な文字やスペースが含まれています．", Toast.LENGTH_LONG).show();
                                    }
                                    //endregion
                                }
                            })
                            .setNeutralButton("再開する", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //region　再開する便が定期便かデマンド便かを判断する
                                    if (SharedPreferenceHelper.getCurrentCarType(getApplicationContext()).equals("定期")) {
                                        Intent intent = new Intent(ServiceInformationSettingActivity.this, PassengerManagementOnRegularServiceActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("isFromServiceInformationSettingActivity", true);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(ServiceInformationSettingActivity.this, ReservationBusStopListActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("isFromServiceInformationSettingActivity", true);
                                        startActivity(intent);
                                    }
                                    //endregion
                                }
                            })
                            .setNegativeButton("キャンセル", null)
                            .show();
                    //endregion
                }
            }
        });

        sendUnreachedDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = getFilesDir().getPath().toString() + "/" + directoryHelper.path(DirectoryHelper.UNREACHED);
                files = new File(path).listFiles();
                if(files.length != 0) {//　if文 追加
                    upLoadAsyncTask = new UpLoadAsyncTask[files.length];
                    for (int i = 0; i < files.length; i++) {
                        String json = "";
                        try {
                            FileInputStream fileInputStream = new FileInputStream(files[i]);
                            byte[] readByte = new byte[fileInputStream.available()];
                            fileInputStream.read(readByte);
                            json = new String(readByte);
                            upLoadAsyncTask[i] = new UpLoadAsyncTask(ServiceInformationSettingActivity.this, files[i]);
                            upLoadAsyncTask[i].execute(getString(R.string.server_name) + getString(R.string.server_upload), json);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }//

                //未送信の位置情報データを送信
                sendFolder = new File(rootPath).list();
                int num = sendFolder.length;
                if(num != 0){
                    for(int i = 0;i < num;i++){
                        String[] sendFiles = new File(rootPath,sendFolder[i]).list();
                        if(sendFiles.length != 0){
                            for(int j=0; j < sendFiles.length; j++){
                                Uri.Builder builder = new Uri.Builder();
                                File sendFile = new File(rootPath+"/"+sendFolder[i]+"/"+sendFiles[j]);
                                SendLocationAsyncTask reSendLocation = new SendLocationAsyncTask(
                                        ServiceInformationSettingActivity.this, sendFiles[j], sendFile,
                                        rootPath+"/"+sendFolder[i],"/"+ sendFolder[i]);
                                reSendLocation.execute(builder);
                            }
                        }else{
                            new File(rootPath,sendFolder[i]).delete();
                        }
                    }
                }
            }
        });
    }

    /**
     * 全ての項目に入力されているかどうかを確認する
     *
     * @return
     */
    private boolean checkInput() {
        String text = mileageEditText.getText().toString();
        return InputChecker.isNumber(text);
    }

    /**
     * 画面の部品に入力された情報を，ServiceInformationに格納する
     */
    private void setServiceInformation(ServiceInformationMasterData serviceInformationMasterData, ServiceInformation serviceInformation) {
        //UIの情報を取得する
        serviceInformation.datetime = showDateTextView.getText().toString();
        serviceInformation.driverName = (String) driverSpinner.getSelectedItem();
        serviceInformation.numberplate = (String) carNumberSpinner.getSelectedItem();
        serviceInformation.startDistance = Integer.valueOf(mileageEditText.getText().toString());
        //ServiceInformationMasterDataから選択された内容を,ServiceInformationに格納する
        serviceInformation.passengerInfomation = new PassengerInformation();
        //選択されているコース名と同じ名前のコース情報を取得する
        for (Course course : serviceInformationMasterData.courses) {
            if (course.name.equals(courceSpinner.getSelectedItem())) {
                //serviceInformation.passengerInfomation.course = course;
                PassengerInformation passengerInformation = new PassengerInformation(course);
                serviceInformation.passengerInfomation = passengerInformation;
            }
        }
    }

    /**
     * this.servieInformationに含まれるpassengerInformationsOfRouteを返す
     *
     * @return
     */
    private PassengerInformationsOfRoute getPassengerInformationOnRoutesByCourseNum() {
        int courseNum = SharedPreferenceHelper.getCurrentCourseNum(this);
        for (PassengerInformationsOfRoute item : this.serviceInformation.passengerInfomation.passengerInfomationsOfRoutes) {
            if (item.route.courseNumber == courseNum) {
                return item;
            }
        }
        return null;
    }

    /**
     * this.servieInformationに含まれるpassengerInformationsOfRouteを返す
     *
     * @param courseNum
     * @return
     */
    private PassengerInformationsOfRoute getPassengerInformationOnRoutesByCourseNum(int courseNum) {
        for (PassengerInformationsOfRoute item : this.serviceInformation.passengerInfomation.passengerInfomationsOfRoutes) {
            if (item.route.courseNumber == courseNum) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onPostDownLoad(String result) {
        progress.dismiss();
        //region jsonの保存と読み出し
        DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
        //ダウンロードの成功と不可を判定する
        if (result.equals(NetworkExceptions.NOTCONNECT.toString()) || result.equals(NetworkExceptions.NOTFOUND.toString()) || result.equals("")) {
            //ServiceInformationMasterData.jsonがすでに存在し無い場合，インターネット接続を促す．
            if (!directoryHelper.check(DirectoryHelper.MODE.NOT_CREATE, DirectoryHelper.DOWNLOAD, DirectoryHelper.SERVICEINFORMATION_MASTERDATA, DirectoryHelper.SERVICEINFORMATION_MASTERDATA_JSON)) {
                new AlertDialog.Builder(ServiceInformationSettingActivity.this)
                        .setTitle("注意")
                        .setMessage("インターネットに接続して，もう一度アプリを起動してください．")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ServiceInformationSettingActivity.this.finish();
                            }
                        })
                        .show();
            }//ServiceInformatinoMasterData.jsonが存在した場合，既存のデータを用いて，運行を開始する
            else {
                directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.DOWNLOAD, DirectoryHelper.SERVICEINFORMATION_MASTERDATA);
                File file = new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.SERVICEINFORMATION_MASTERDATA, directoryHelper.SERVICEINFORMATION_MASTERDATA_JSON));
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
                try {
                    serviceInformationMasterData = objectMapper.readValue(file, ServiceInformationMasterData.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }//ダウンロードが可能な場合は，
        else {
            //region テキストをファイルに書き込む
            directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.DOWNLOAD, DirectoryHelper.SERVICEINFORMATION_MASTERDATA);
            File file = new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.SERVICEINFORMATION_MASTERDATA, directoryHelper.SERVICEINFORMATION_MASTERDATA_JSON));
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(result);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion
            //region Jsonファイルをオブジェクトへ変換する
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
            try {
                serviceInformationMasterData = objectMapper.readValue(file, ServiceInformationMasterData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //endregion
        }

        //region　各種UIにAdapterを設定
        //運転手名リスト
        if (serviceInformationMasterData.driverNames != null) {
            ArrayAdapter<String> driverSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.service_information_spinner_item, serviceInformationMasterData.driverNames);
            driverSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            driverSpinner.setAdapter(driverSpinnerAdapter);
        }
        //車両番号リスト
        if (serviceInformationMasterData.numberplates != null) {
            ArrayAdapter<String> carNumberSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.service_information_spinner_item, serviceInformationMasterData.numberplates);
            carNumberSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            carNumberSpinner.setAdapter(carNumberSpinnerAdapter);
        }
        //コースリスト(コース名だけを取り出して，表示させる)
        if (serviceInformationMasterData.courses != null) {
            final ArrayList<String> coucesNames = new ArrayList<String>();
            for (Course item : serviceInformationMasterData.courses) {
                coucesNames.add(item.name);
            }
            ArrayAdapter<String> courseSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.service_information_spinner_item, coucesNames);
            courseSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            courceSpinner.setAdapter(courseSpinnerAdapter);
        }
        //endregion
    }

    @Override
    public void onDownLoadCancelled() {
        progress.dismiss();
    }

    @Override
    public void onPreDownLoad() {
        progress = new ProgressDialog(ServiceInformationSettingActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("ダウンロード中...");
        progress.show();
    }

    @Override
    public void onPostUpLoad(String result, File file) {
        progress.dismiss();
        if (result.equals("")) {
            Toast.makeText(ServiceInformationSettingActivity.this, "データの送信に失敗しました", Toast.LENGTH_LONG).show();
        } else {
            //アプリをタスクからも削除する
            DirectoryHelper directoryHelper = new DirectoryHelper(ServiceInformationSettingActivity.this);
            directoryHelper.delete(file);
            Toast.makeText(ServiceInformationSettingActivity.this, "data:アップロードが完了しました", Toast.LENGTH_SHORT).show();

            if (files.length == 0 && (sendFolder == null ||sendFolder.length == 0)) {//追加　sendFolder関連
                sendUnreachedDataButton.setEnabled(false);
            } else {
                sendUnreachedDataButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onPreUpLoad() {
        progress = new ProgressDialog(ServiceInformationSettingActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("アップロード中...");
        progress.show();
    }

    @Override
    public void onUpLoadCancelled() {

    }

    //追加
    @Override
    public void onResultSendLocation(String result){
        if (result.equals("false")) {
            Toast.makeText(ServiceInformationSettingActivity.this, "データの送信に失敗しました", Toast.LENGTH_LONG).show();
            sendUnreachedDataButton.setEnabled(true);
        } else {
            //再送成功したらフォルダを削除する
            new File(rootPath).delete();
            Toast.makeText(ServiceInformationSettingActivity.this, "loc:アップロードが完了しました", Toast.LENGTH_SHORT).show();
            sendUnreachedDataButton.setEnabled(false);
            /*if (files.length == 0 && (sendFolder == null ||sendFolder.length == 0)) {//追加　sendFolder関連
                sendUnreachedDataButton.setEnabled(false);
            } else {
                sendUnreachedDataButton.setEnabled(true);
            }*/
        }
    }

}
