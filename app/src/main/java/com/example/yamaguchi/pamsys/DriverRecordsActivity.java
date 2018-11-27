package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.Common.Callbacks;
import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.SendLocationAsyncTask;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.Common.UpLoadAsyncTask;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.DataTruncation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DriverRecordsActivity extends Activity implements Callbacks.UpLoadCallback,Callbacks.SendLocationCallback {

    ServiceInformation serviceInformation = new ServiceInformation();

    //region 運行設定情報
    TextView dateTextView;
    TextView driverNameTextView;
    TextView courceTextView;
    //endregion
    //region 運賃集計
    TextView totalPassengerCommonAdultTextView;
    TextView totalCashCommonAdultTextVew;
    TextView totalPassengerCommonChildTextView;
    TextView totalCashCommonChildTextVew;
    TextView totalPassengerHandicappedAdultTextView;
    TextView totalCashHandicappedAdultTextVew;
    TextView totalPassengerHandicappedChildTextView;
    TextView totalCashHandicappedChildTextVew;
    TextView totalPassengerTextView;
    TextView totalCashTextView;
    //endregion
    //region 回数券利用集計
    TextView totalCouponTicketAdultTextView;
    TextView totalCashCouponTicketAdultTextVew;
    TextView totalCouponTicketChildTextView;
    TextView totalCashCouponTicketChildTextVew;
    TextView totalCouponTicketHandicappedTextView;
    TextView totalCashCouponTicketHandicappedTextVew;
    TextView totalCouponTicketTextView;
    TextView totalCashCouponTicketTextView;
    //endregion
    //region 回数券販売実績
    TextView soldCouponTicketForAdultTextView;
    TextView totalSalesForAdultTextView;
    TextView soldCouponTicketForChildTextView;
    TextView totalSalesForChildTextView;
    TextView soldCouponTicketForHandicappedTextView;
    TextView totalSalesForHandicappedTextView;
    TextView totalCountSoldTicketTextView;
    TextView totalSalesTextView;
    //endregion
    //region 総計
    TextView totalCountTextView;
    TextView totalTextView;
    //endregion
    Button finishServiceButton;
    private ProgressDialog progress;

    //static ServiceInformation serviceInformation = new ServiceInformation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_records);

        //region 運行設定情報
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        driverNameTextView = (TextView) findViewById(R.id.driverNameTextView);
        courceTextView = (TextView) findViewById(R.id.courceTextView);
        //endregion
        //region 運賃集計
        totalPassengerCommonAdultTextView = (TextView) findViewById(R.id.totalPassengerCommonAdultTextView);
        totalCashCommonAdultTextVew = (TextView) findViewById(R.id.totalCashCommonAdultTextView);
        totalPassengerCommonChildTextView = (TextView) findViewById(R.id.totalPassengerCommonChildTextView);
        totalCashCommonChildTextVew = (TextView) findViewById(R.id.totalCashCommonChildTextView);
        totalPassengerHandicappedAdultTextView = (TextView) findViewById(R.id.totalPassengerHandicappedAdultTextView);
        totalCashHandicappedAdultTextVew = (TextView) findViewById(R.id.totalCashHandicappedAdultTextView);
        totalPassengerHandicappedChildTextView = (TextView) findViewById(R.id.totalPassengerHandicappedChildTextView);
        totalCashHandicappedChildTextVew = (TextView) findViewById(R.id.totalCashHandicappedChildTextView);
        totalPassengerTextView = (TextView) findViewById(R.id.totalPassengerTextView);
        totalCashTextView = (TextView) findViewById(R.id.totalCashTextView);
        //endregion
        //region 回数券利用集計
        totalCouponTicketAdultTextView = (TextView) findViewById(R.id.totalCouponTicketAdultTextView);
        totalCashCouponTicketAdultTextVew = (TextView) findViewById(R.id.totalCashCouponTicketAdultTextView);
        totalCouponTicketChildTextView = (TextView) findViewById(R.id.totalCouponTicketChildTextView);
        totalCashCouponTicketChildTextVew = (TextView) findViewById(R.id.totalCashCouponTicketChildTextView);
        totalCouponTicketHandicappedTextView = (TextView) findViewById(R.id.totalCouponTicketHandicappedTextView);
        totalCashCouponTicketHandicappedTextVew = (TextView) findViewById(R.id.totalCashCouponTicketHandicappedTextView);
        totalCouponTicketTextView = (TextView) findViewById(R.id.totalCouponTicketTextView);
        totalCashCouponTicketTextView = (TextView) findViewById(R.id.totalCashCouponTicketTextView);
        //endregion
        //region 回数券販売実績
        soldCouponTicketForAdultTextView = (TextView) findViewById(R.id.soldCouponTicketForAdultTextView);
        totalSalesForAdultTextView = (TextView) findViewById(R.id.totalSalesForAdultTextView);
        soldCouponTicketForChildTextView = (TextView) findViewById(R.id.soldCouponTicketForChildTextView);
        totalSalesForChildTextView = (TextView) findViewById(R.id.totalSalesForChildTextView);
        soldCouponTicketForHandicappedTextView = (TextView) findViewById(R.id.soldCouponTicketForHandicappedTextView);
        totalSalesForHandicappedTextView = (TextView) findViewById(R.id.totalSalesForHandicappedTextView);
        totalCountSoldTicketTextView = (TextView) findViewById(R.id.totalCountSoldTicketTextView);
        totalSalesTextView = (TextView) findViewById(R.id.totalSalesTextView);
        //endregion
        //region 総計
        totalCountTextView = (TextView) findViewById(R.id.totalCountTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        //endregion
        finishServiceButton = (Button) findViewById(R.id.finishServiceButton);

        final UpLoadAsyncTask[] upLoadAsyncTask = new UpLoadAsyncTask[1];

        //region jsonからオブジェクトへ変換する
        Intent intent = getIntent();
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        if (intent.getBooleanExtra("isFromServiceInformationSettingActivity", false)) {
            jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.PREVIOUS, DirectoryHelper.SERVICEINFORMATION_JSON));
            finishServiceButton.setVisibility(View.INVISIBLE);
        } else {
            jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
            findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        }
        //endregion

        //region passengerinformationOfRoute以下のデータを集計する
        serviceInformation.passengerInfomation.calculate();
        //endregion

        //region 記録された情報を，UIに反映する
        //region 運行設定情報
        dateTextView.setText(serviceInformation.datetime);
        driverNameTextView.setText(serviceInformation.driverName);
        courceTextView.setText(serviceInformation.passengerInfomation.course.name + " コース");
        //endregion
        //region 運賃集計
        totalPassengerCommonAdultTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.fareCommonAdult.count));
        totalCashCommonAdultTextVew.setText(makeCashString(serviceInformation.passengerInfomation.fareCommonAdult.totalCost));
        totalPassengerCommonChildTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.fareCommonChild.count));
        totalCashCommonChildTextVew.setText(makeCashString(serviceInformation.passengerInfomation.fareCommonChild.totalCost));
        totalPassengerHandicappedAdultTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.fareHandicappedAdult.count));
        totalCashHandicappedAdultTextVew.setText(makeCashString(serviceInformation.passengerInfomation.fareHandicappedAdult.totalCost));
        totalPassengerHandicappedChildTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.fareHandicappedChild.count));
        totalCashHandicappedChildTextVew.setText(makeCashString(serviceInformation.passengerInfomation.fareHandicappedChild.totalCost));
        totalPassengerTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.totalCash.count));
        totalCashTextView.setText(makeCashString(serviceInformation.passengerInfomation.totalCash.totalCost));
        //endregion
        //region 回数券利用集計
        totalCouponTicketAdultTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.couponTicketCommonAdult.count));
        totalCashCouponTicketAdultTextVew.setText(makeCashString(serviceInformation.passengerInfomation.couponTicketCommonAdult.totalCost));
        totalCouponTicketChildTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.couponTicketCommonChild.count));
        totalCashCouponTicketChildTextVew.setText(makeCashString(serviceInformation.passengerInfomation.couponTicketCommonChild.totalCost));
        totalCouponTicketHandicappedTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.couponTicketCommonHandicapped.count));
        totalCashCouponTicketHandicappedTextVew.setText(makeCashString(serviceInformation.passengerInfomation.couponTicketCommonHandicapped.totalCost));
        totalCouponTicketTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.totalCouponTicket.count));
        totalCashCouponTicketTextView.setText(makeCashString(serviceInformation.passengerInfomation.totalCouponTicket.totalCost));
        //endregion
        //region 回数券販売実績
        soldCouponTicketForAdultTextView.setText(makeTicketCountString(serviceInformation.passengerInfomation.couponSoldResultAdult.count));
        totalSalesForAdultTextView.setText(makeCashString(serviceInformation.passengerInfomation.couponSoldResultAdult.totalCost));
        soldCouponTicketForChildTextView.setText(makeTicketCountString(serviceInformation.passengerInfomation.couponSoldResultChild.count));
        totalSalesForChildTextView.setText(makeCashString(serviceInformation.passengerInfomation.couponSoldResultChild.totalCost));
        soldCouponTicketForHandicappedTextView.setText(makeTicketCountString(serviceInformation.passengerInfomation.couponSoldResultHandicapped.count));
        totalSalesForHandicappedTextView.setText(makeCashString(serviceInformation.passengerInfomation.couponSoldResultHandicapped.totalCost));
        totalCountSoldTicketTextView.setText(makeTicketCountString(serviceInformation.passengerInfomation.totalCouponTicketSold.count));
        totalSalesTextView.setText(makeCashString(serviceInformation.passengerInfomation.totalCouponTicketSold.totalCost));
        //endregion
        //region 総計
        totalCountTextView.setText(makePassengerCountString(serviceInformation.passengerInfomation.sumSales.count));
        totalTextView.setText(makeCashString(serviceInformation.passengerInfomation.sumSales.totalCost));
        //endregion

        //endregion

        //記録されている情報を送信し，アプリを終了する
        findViewById(R.id.finishServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //確認用のダイヤログを出す
                new AlertDialog.Builder(DriverRecordsActivity.this)
                        .setTitle("確認")
                        .setMessage("タブレットに記録された情報をサーバに送信し，アプリを終了しますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                                directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.UPLOAD, DirectoryHelper.PREVIOUS);
                                serviceInformation.serviceInformationToJson(getApplicationContext(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
                                serviceInformation.serviceInformationToJson(getApplicationContext(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.PREVIOUS, DirectoryHelper.SERVICEINFORMATION_JSON));

                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
                                String json = "";
                                try {
                                    json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInformation);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                // TODO: 2017/11/18 アップロードするコードを実装する
                                upLoadAsyncTask[0] = new UpLoadAsyncTask(DriverRecordsActivity.this, null);
                                upLoadAsyncTask[0].execute(getString(R.string.server_name) + getString(R.string.server_upload), json);
                                //予約リストを削除する
                                File deleteFolder = new File(getApplicationContext().getFilesDir(), directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS));
                                directoryHelper.delete(deleteFolder);
                                //運行便数などの設定を初期化する
                                SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), 0);
                                SharedPreferenceHelper.setCurrentCourseNum(getApplicationContext(), 1);

                                //位置情報まとめたファイルを送信　2018追加
                                PassengerManagementOnRegularServiceActivity pMORSA = new PassengerManagementOnRegularServiceActivity();
                                pMORSA.OutputOfServiceLog("end");
                                Uri.Builder builder = new Uri.Builder();
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
                                String today = sdf.format(date);
                                String filename = "Course_" + serviceInformation.passengerInfomation.course.name + ".txt";
                                String rootpath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log" +"/"+ today;
                                File dataFile = new File(rootpath,filename);
                                SendLocationAsyncTask sendLocation = new SendLocationAsyncTask(DriverRecordsActivity.this,
                                        filename,dataFile,rootpath,today);
                                sendLocation.execute(builder);


                            }
                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //特に何もしない
                            }
                        })
                        .show();
            }
        });

        //戻るボタン
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(DriverRecordsActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 人数のカウントを単位付きで文字列に変換する
     *
     * @param count
     * @return
     */
    private String makePassengerCountString(int count) {
        String message = String.valueOf(count);
        return message + "人";
    }

    /**
     * 人数のカウントを単位付きで文字列に変換する
     *
     * @param count
     * @return
     */
    private String makeTicketCountString(int count) {
        String message = String.valueOf(count);
        return message + "冊";
    }

    /**
     * 人数のカウントを単位付きで文字列に変換する
     *
     * @param cash
     * @return
     */
    private String makeCashString(int cash) {
        String message = String.valueOf(cash);
        return message + "円";
    }


    private void jsonToServiceInformation(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        File file = new File(getApplicationContext().getFilesDir(), path);
        try {
            serviceInformation = objectMapper.readValue(file, ServiceInformation.class);
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * this.serviceInformatiopnに含まれるpassengerInformationOfBusStopを取得する
     *
     * @return
     */
    private PassengerInfomationsOfBusStop getPassengerInfomationsOfBusStop() {
        int currentNum = SharedPreferenceHelper.getCurrentBusStopNum(this);
        return getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(currentNum);
    }

    @Override
    public void onPostUpLoad(String result, File file) {
        progress.dismiss();
        if (result.equals("")) {
            DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
            directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.UPLOAD);
            Date date = new Date();
            String dateTime = String.valueOf(date.getTime());
            serviceInformation.serviceInformationToJson(getApplicationContext(), directoryHelper.path(DirectoryHelper.UNREACHED) + "/" + dateTime + ".json");
            Toast.makeText(DriverRecordsActivity.this, "データの送信に失敗しました", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(DriverRecordsActivity.this, "アップロードが完了しました", Toast.LENGTH_LONG).show();
        //アプリをタスクからも削除する
        Intent intent = new Intent(DriverRecordsActivity.this, ServiceInformationSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
        finish();
        moveTaskToBack(true);
    }

    @Override
    public void onPreUpLoad() {
        progress = new ProgressDialog(DriverRecordsActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("アップロード中...");
        progress.show();
    }

    @Override
    public void onUpLoadCancelled() {

    }

    @Override
    public void onResultSendLocation(String result){

    }

}
