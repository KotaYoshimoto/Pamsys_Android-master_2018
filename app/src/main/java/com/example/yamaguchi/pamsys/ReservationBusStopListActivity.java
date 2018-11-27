package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.dreams.DreamService;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.Common.Callbacks;
import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.DownLoadAsyncTask;
import com.example.yamaguchi.pamsys.Common.NetworkExceptions;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Reservation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Reservations;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.example.yamaguchi.pamsys.ReservationBusStopListHelperClasses.ReservationBusStopInformation;
import com.example.yamaguchi.pamsys.ReservationBusStopListHelperClasses.ReservationBusStopInformationListAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.SimpleFormatter;

public class ReservationBusStopListActivity extends Activity implements Callbacks.DownLoadCallback {

    ServiceInformation serviceInformation = new ServiceInformation();
    Reservations reservations = new Reservations();
    ArrayList<ReservationBusStopInformation> reservationBusStopInformations = new ArrayList<ReservationBusStopInformation>();

    //region 運行情報関係
    TextView routeNameTextView;
    TextView serviceTypeTextView;
    TextView routeNumberTextView;
    TextView carTypeTextView;
    TextView couceTextView;
    TextView courseNumberTextView;
    //endregion
    //region 予約表関係
    TextView downLoadStateTextView;
    ListView reservationBusStopListView;
    Button reservationTableDownloadButton;
    //endregion
    //region その他
    Button outOfServiceButton;
    //endregion

    DownLoadAsyncTask downLoadAsyncTask;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_bus_stop_list);

        //region 運行情報関係
        routeNameTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNameTextView);
        serviceTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.serviceTypeTextView);
        routeNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNumberTextView);
        carTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.carTypeTextView);
        couceTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courceTextView);
        courseNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courseNumberTextView);

        //endregion
        //region 予約リスト関係
        downLoadStateTextView = (TextView) findViewById(R.id.downloadStateTextView);
        reservationTableDownloadButton = (Button) findViewById(R.id.reservationTableDownloadButton);
        reservationBusStopListView = (ListView) findViewById(R.id.reservationBusStopListView);
        //endregion
        //region その他
        outOfServiceButton = (Button) findViewById(R.id.outOfServiceButton);
        //endregion

        //region データの読み込み
        final DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
        //endregion
        //region 運行情報関係
        setServiceInformation();
        //endregion

        progress = new ProgressDialog(ReservationBusStopListActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("ダウンロード中...");

        //region 既存の予約表が保存されていた場合の処理
        File currentFile = new File(getApplicationContext().getFilesDir(), directoryHelper.path(directoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + String.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())) + ".json");
        if(currentFile.exists()) {
            ObjectMapper currentObjectMapper = new ObjectMapper();
            try {
                reservations = currentObjectMapper.readValue(currentFile, Reservations.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //reservations中の，reservationsがnullであった場合，予約が無いものとみなす
            if (reservations.reservations == null || reservations.reservations.size() == 0) {
                downLoadStateTextView.setText(" (予約がありません " + SharedPreferenceHelper.getLaterReservationDownLoaded(getApplicationContext()) + ")");
                outOfServiceButton.setEnabled(true);
                findViewById(R.id.finishAnServiceButton).setEnabled(false);
            } else {
                setReservationBusStopInformation();
                setReservationBusStopListAdapter();
                findViewById(R.id.finishAnServiceButton).setEnabled(true);
            }
        }
        //endregion

        //ダウンロードボタンのイベント
        reservationTableDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Reservationディレクトリの確認を行う
                DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS);
                progress.show();

                //region　ダウンロード可能な時間帯であるような予約表をすべてダウンロードする
                for (PassengerInformationsOfRoute route : serviceInformation.passengerInfomation.passengerInfomationsOfRoutes) {
                    //デマンド便のみを対象とする
                    if (!route.route.carType.equals("デマンド")) {
                        continue;
                    }
                    //現在のコース番号より以前の予約表については，再ダウンロードしない
                    if(route.route.courseNumber < SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())){
                        continue;
                    }
                    //ダウンロードが可能な時間帯であった場合
                    if (isEnableDownLoadReservation(route.route.startTime)) {
                        String targetCourseNum = String.valueOf(route.route.courseNumber);
                        //region 日付とコース便数からパスを生成する
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
                        String datePath = simpleDateFormat.format(new Date());
                        String course = serviceInformation.passengerInfomation.course.name + "/";
                        //endregion
                        downLoadAsyncTask = new DownLoadAsyncTask(ReservationBusStopListActivity.this, ReservationBusStopListActivity.this, targetCourseNum);
                        downLoadAsyncTask.execute(getString(R.string.server_name) + getString(R.string.server_reservation_data) + datePath + course + targetCourseNum + ".json");
                    }
                    //ダウンロードが不可能な時間帯であった場合
                    else {
                        //初回でダウンロードができなかった場合
                        if (route.route.courseNumber == Integer.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()))) {
                            String message = "運行開始時刻の1時間前から，ダウンロードできます．\n" +
                                    "当便の運行開始時刻は，" +
                                    getPassengerInformationOnRoutesByCourseNum().route.startTime +
                                    "です.";
                            Toast.makeText(ReservationBusStopListActivity.this, message, Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    }
                }
                //endregion
            }
        });
        //endregion

        //当便運休ボタン
        outOfServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassengerInformationOnRoutesByCourseNum().route.isServiceWork = false;
                //region　次便の種類によって，画面の遷移先が異なる
                int currentCouseNum = SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext());
                //その便が，当日の最終便であった場合
                if (currentCouseNum == serviceInformation.passengerInfomation.passengerInfomationsOfRoutes.size()) {
                    Intent intent = new Intent(ReservationBusStopListActivity.this, AnFinishedServiceResultActivity.class);
                    startActivity(intent);
                } else {
                    SharedPreferenceHelper.setCurrentCourseNum(getApplicationContext(), currentCouseNum + 1);
                    SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), 0);
                    //次便が定期便である
                    if (getPassengerInformationOnRoutesByCourseNum().route.carType.equals("定期")) {
                        SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "定期");
                        Intent intent = new Intent(ReservationBusStopListActivity.this, PassengerManagementOnRegularServiceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "デマンド");
                        Intent intent = new Intent(ReservationBusStopListActivity.this, ReservationBusStopListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                //endregion
            }
        });

        //戻るボタン
        Intent intent = new Intent();
        if (!getIntent().getBooleanExtra("isFromServiceInformationSettingActivity", false)) {
            findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //回数券販売ボタン
        findViewById(R.id.couponSaleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReservationBusStopListActivity.this, CouponTicketSaleActivity.class);
                startActivity(intent);
            }
        });

        //登録ボタン
        findViewById(R.id.finishAnServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReservationBusStopListActivity.this, AnFinishedServiceResultActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * reservationBusStopInformationをAdapterにセットする
     */
    private void setReservationBusStopListAdapter() {
        ReservationBusStopInformationListAdapter adapter = new ReservationBusStopInformationListAdapter(ReservationBusStopListActivity.this);
        adapter.setReservationBusStopInformationList(reservationBusStopInformations);
        reservationBusStopListView.setAdapter(adapter);
        reservationBusStopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //ReservationBusStopのidを用いて，現在編集中の停留所の番号を設定する
                ReservationBusStopInformation information = (ReservationBusStopInformation) adapterView.getItemAtPosition(i);
                SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), information.id);
                Intent intent = new Intent(ReservationBusStopListActivity.this, PassengerManagementOnDemandActivity.class);
                intent.putExtra("willGetOnCount", ((ReservationBusStopInformation) adapterView.getItemAtPosition(i)).willGetOnCount);
                intent.putExtra("willGetOffCount", ((ReservationBusStopInformation) adapterView.getItemAtPosition(i)).willGetOffCount);
                startActivity(intent);
            }
        });
    }

    /**
     * HH:mmの形の時刻と，現在時刻の差が1時間以内であるかどうかを判定する
     *
     * @param startTime
     * @return
     */
    private boolean isEnableDownLoadReservation(String startTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String now = simpleDateFormat.format(new Date());
        String start = startTime;
        Date nowDate = null;
        Date startDate = null;
        try {
            nowDate = simpleDateFormat.parse(now);
            startDate = simpleDateFormat.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nowDateTime = nowDate.getTime();
        long startDateTime = startDate.getTime();
        long diff = startDateTime - nowDateTime;
        return diff <= 1000 * 60 * 60;
    }

    /**
     * 予約表の内容を作成する
     */
    private void setReservationBusStopInformation() {
        Log.d("execute", "setReservationBusStopInformation()");
        //PassengerInformationOfRouteの情報を基に，reservationBusStopInformationsの要素を作成する
        reservationBusStopInformations.clear();
        for (PassengerInfomationsOfBusStop busStop : getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops) {
            int index = getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.indexOf(busStop);
            reservationBusStopInformations.add(new ReservationBusStopInformation(index, busStop.busStop.name));
        }
        //reservationBusStopInformationsの，予約人数に関する部分を作成する
        for (ReservationBusStopInformation information : reservationBusStopInformations) {
            for (Reservation reservation : reservations.reservations) {
                if (reservation.getOnBusStop.equals(information.busStopName)) {
                    information.willGetOnCount++;
                }
                if (reservation.getOffBusStop.equals(information.busStopName)) {
                    information.willGetOffCount++;
                }
            }
        }
        //降車人数を記録しておく
        for (ReservationBusStopInformation information : reservationBusStopInformations){
            getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(information.id).totalNumberOfGetOffPassenger = information.willGetOffCount;
            Log.d(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(information.id).busStop.name,String.valueOf(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(information.id).totalNumberOfGetOffPassenger));
        }
        //reservationBusStopInformationsの要素の中で，乗降者数がともに0である要素を削除する
        for (int i = 0; i < reservationBusStopInformations.size(); i++) {
            if (reservationBusStopInformations.get(i).willGetOffCount == 0 &&
                    reservationBusStopInformations.get(i).willGetOnCount == 0) {
                reservationBusStopInformations.remove(i);
                i--;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Activityが再開したときの処理
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));

        setServiceInformation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //状態を保存する
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        serviceInformation.serviceInformationToJson(
                getApplicationContext(),
                directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
        );
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(ReservationBusStopListActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Reservations.jsonをオブジェクトへ変換する
     *
     * @param path
     */
    private void jsonToReservations(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        File file = new File(getApplicationContext().getFilesDir(), path);
        try {
            reservations = objectMapper.readValue(file, Reservations.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * jsonファイルをオブジェクトへ変換する
     *
     * @param path
     */
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
     * 運行情報に関する表示を設定する
     */
    private void setServiceInformation() {
        int currentNumber = SharedPreferenceHelper.getCurrentCourseNum(this);

        routeNameTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.name);
        serviceTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.serviceType);
        routeNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.routeNumber));
        carTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.carType);
        couceTextView.setText(this.serviceInformation.passengerInfomation.course.name);
        courseNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.courseNumber));
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        if (result.equals(NetworkExceptions.NOTFOUND.toString())) {
            Toast.makeText(ReservationBusStopListActivity.this, "予約者リストのダウンロードに失敗しました．", Toast.LENGTH_LONG).show();
        } else if (result.equals(NetworkExceptions.NOTCONNECT.toString())) {
            Toast.makeText(ReservationBusStopListActivity.this, "ネットワークの設定を確認してください．", Toast.LENGTH_LONG).show();
        } else {
            //ダウンロードした時刻を保存する
            SharedPreferenceHelper.setLaterReservationDownLoaded(getApplicationContext(), simpleDateFormat.format(new Date()));
            downLoadStateTextView.setText(" (ダウンロード済み " + SharedPreferenceHelper.getLaterReservationDownLoaded(getApplicationContext()) + ")");
            //region 保存先にテキストを保存する
            DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
            directoryHelper.check(DirectoryHelper.MODE.CREATE_DIR, DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS);

            //jsonをオブジェクトに変換する
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
            try {
                Log.d("######", result);
                this.reservations = objectMapper.readValue(result, Reservations.class);
                String currentCourseNum = String.valueOf(reservations.route.courseNumber);
                File file = new File(getApplicationContext().getFilesDir(),
                        directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" +
                                currentCourseNum +
                                ".json");
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, reservations);
                jsonToReservations(directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + currentCourseNum + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }

            File currentFile = new File(getApplicationContext().getFilesDir(), directoryHelper.path(directoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + String.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())) + ".json");
            ObjectMapper currentObjectMapper = new ObjectMapper();
            try {
                reservations = currentObjectMapper.readValue(currentFile, Reservations.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //現在のコース便数と同じ予約表を扱う場合は，reservationsへの読み出しと，
            if (SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()) == reservations.route.courseNumber) {
                //reservations中の，reservationsがnullであった場合，予約が無いものとみなす
                if (reservations.reservations == null || reservations.reservations.size() == 0) {
                    downLoadStateTextView.setText(" (予約がありません " + SharedPreferenceHelper.getLaterReservationDownLoaded(getApplicationContext()) + ")");
                    outOfServiceButton.setEnabled(true);
                    findViewById(R.id.finishAnServiceButton).setEnabled(false);
                } else {
                    downLoadStateTextView.setText(" (ダウンロード済み " + SharedPreferenceHelper.getLaterReservationDownLoaded(getApplicationContext()) + ")");
                    setReservationBusStopInformation();
                    setReservationBusStopListAdapter();
                    findViewById(R.id.finishAnServiceButton).setEnabled(true);
                }
            }
        }
    }

    /**
     * 入力されたコース便数に対応するファイル名の予約者リストのファイルパスを取得する
     *
     * @param currentCourseNum
     * @return
     */
    private String getReservationFilePath(int currentCourseNum) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd/");
        return getString(R.string.server_name) +
                getString(R.string.server_reservation_data) +
                simpleDateFormat.format(new Date()) +
                String.valueOf(currentCourseNum) + ".json";
    }

    @Override
    public void onPreDownLoad() {
    }

    @Override
    public void onDownLoadCancelled() {
        progress.dismiss();
    }


}
