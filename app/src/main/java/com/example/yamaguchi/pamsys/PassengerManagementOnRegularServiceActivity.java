package com.example.yamaguchi.pamsys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.yamaguchi.pamsys.Common.Callbacks;
import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.SendLocationAsyncTask;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.Common.UpLoadAsyncTask;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.BusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.ServiceInformationMasterData;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.example.yamaguchi.pamsys.PassengerManagementActivityHelperClasses.BusStopListAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class PassengerManagementOnRegularServiceActivity extends Activity
        implements TextToSpeech.OnInitListener, LocationListener, View.OnFocusChangeListener
        , Callbacks.SendLocationCallback {

    static ServiceInformation serviceInformation = new ServiceInformation();

    TextToSpeech textToSpeech;

    //region 運行情報関係
    TextView routeNameTextView;
    TextView serviceTypeTextView;
    TextView routeNumberTextView;
    TextView carTypeTextView;
    TextView couceTextView;
    TextView courseNumberTextView;
    //endregion
    //region 停留所関係
    Spinner busStopListSpinner;
    Button showNextBusStopButton;
    Button voiceGuidButton;
    ToggleButton autoSujestToggleButton;
    //endregion
    //region 乗車人数表
    EditText entrainningCommonAdultEditText;
    EditText entrainningCommonChildtEditText;
    EditText entrainningHndicappedAdultEditText;
    EditText entrainningHndicappedChildEditText;
    EditText entrainningCouponAdultEditText;
    EditText entrainningCouponChildEditText;
    EditText entrainningCouponHandicappedEditText;
    EditText entrainningCommuterPassEditText;
    TextWatcher[] textWatchers;
    //endregion
    //region 降車人数表
    Button addPassengerCountButton;
    TextView passengerCountTextView;
    Button dclimentPassengerCountButton;
    //endregion
    //region その他
    Button finishAnServiceButton;
    boolean busStopSuggestFlag;
    boolean isFinalBusStop = false;
    LocationManager locationManager;
    //endregion

    //アナウンスしたバス停
    ArrayList<String> speeched;
    //SimpleDateFormat sdf;
    String date,today,filename;
    File dataFile;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_management_on_regular_service);

        textToSpeech = new TextToSpeech(this, this);

        //
        speeched = new ArrayList<>();
        //sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss.SSS", Locale.JAPAN);


        /*
        UIとの紐づけ
         */
        //region 運行情報関係
        routeNameTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNameTextView);
        serviceTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.serviceTypeTextView);
        routeNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNumberTextView);
        carTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.carTypeTextView);
        couceTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courceTextView);
        courseNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courseNumberTextView);
        //endregion
        //region 停留所関係
        busStopListSpinner = (Spinner) findViewById(R.id.busStopListSpinner);
        showNextBusStopButton = (Button) findViewById(R.id.showNextBusStopButton);
        voiceGuidButton = (Button) findViewById(R.id.voiceGuidButton);
        autoSujestToggleButton = (ToggleButton) findViewById(R.id.autoSujestToggleButton);
        busStopSuggestFlag = autoSujestToggleButton.isChecked();
        //endregion
        //前回の停留所が何番目であるかを記録する
        //region 乗車人表
        entrainningCommonAdultEditText = (EditText) findViewById(R.id.entrainningCommonAdultEditText);
        entrainningCommonAdultEditText.setOnFocusChangeListener(this);
        entrainningCommonChildtEditText = (EditText) findViewById(R.id.entrainningCommonChildtEditText);
        entrainningCommonChildtEditText.setOnFocusChangeListener(this);
        entrainningHndicappedAdultEditText = (EditText) findViewById(R.id.entrainningHndicappedAdultEditText);
        entrainningHndicappedAdultEditText.setOnFocusChangeListener(this);
        entrainningHndicappedChildEditText = (EditText) findViewById(R.id.entrainningHndicappedChildEditText);
        entrainningHndicappedChildEditText.setOnFocusChangeListener(this);
        entrainningCouponAdultEditText = (EditText) findViewById(R.id.entrainningCouponAdultEditText);
        entrainningCouponAdultEditText.setOnFocusChangeListener(this);
        entrainningCouponChildEditText = (EditText) findViewById(R.id.entrainningCouponChildEditText);
        entrainningCouponChildEditText.setOnFocusChangeListener(this);
        entrainningCouponHandicappedEditText = (EditText) findViewById(R.id.entrainningCouponHandicappedEditText);
        entrainningCouponHandicappedEditText.setOnFocusChangeListener(this);
        entrainningCommuterPassEditText = (EditText) findViewById(R.id.entrainningCommuterPassEditText);
        entrainningCommuterPassEditText.setOnFocusChangeListener(this);
        //endregion
        //region 降車人数表
        addPassengerCountButton = (Button) findViewById(R.id.addPassengerCountButton);
        passengerCountTextView = (TextView) findViewById(R.id.passengerCountEditText);
        dclimentPassengerCountButton = (Button) findViewById(R.id.dclimentPassengerCountButton);
        //endregion
        //region その他
        finishAnServiceButton = (Button) findViewById(R.id.finishAnServiceButton);
        //endregion

        //region 位置情報関係
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationStart();
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }*/

        //endregion

    /*
    データの読み込み
     */
        final DirectoryHelper directoryHelper = new DirectoryHelper(this);

        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));

        //region　運行情報関係
        setServiceInformation();
        //endregion

        //region 停留所関係
        final ArrayAdapter<PassengerInfomationsOfBusStop> passengerInfomationsOfBusStopArrayAdapter =
                new ArrayAdapter<PassengerInfomationsOfBusStop>(this,
                        R.layout.busstop_spinner,
                        getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(this)).
                                passengerInfomationsOfBusStops);
        passengerInfomationsOfBusStopArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        busStopListSpinner.setAdapter(passengerInfomationsOfBusStopArrayAdapter);
        //
        busStopListSpinner.setSelection(SharedPreferenceHelper.getCurrentBusStopNum(this));

        setBusStop((PassengerInfomationsOfBusStop) busStopListSpinner.getSelectedItem());
//        passengerCountEditText.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger));

        //endregion
        // region 停留所変更イベント
        busStopListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //選択される前の状態のItemの位置に，画面に表示された内容を登録する
                try {
                    //現在選択されている停留所の乗車中人数をを記録する
                    SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), Integer.parseInt(passengerCountTextView.getText().toString()));
                    setPassengerInformationOnBusStopFromUI(
                            getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())).
                                    passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext()))
                    );
                    serviceInformation.serviceInformationToJson(
                            getApplicationContext(),
                            directoryHelper.path(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON))
                    );
                    //現在選択されている停留所のポジションと同じ位置にあるpassengerInformationOnBusStopの内容をUIに表示する
                    setBusStop(
                            getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())).
                                    passengerInfomationsOfBusStops.get(i));
                    //直前まで選択されていた停留所の乗車人数の値を，現在の停留所の乗車人数に表示させる
//                    int pre_passenger = getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger;
//                    passengerCountEditText.setText(String.valueOf(pre_passenger));
                    //現在選択されている停留所のポジションを記録する
                    SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), i);

                    //停留所が終点であるかどうかを判定する
                    if (i == adapterView.getCount() - 1) {
                        isFinalBusStop = true;
                        finishAnServiceButton.setEnabled(true);
                        showNextBusStopButton.setEnabled(false);
                    } else {
                        isFinalBusStop = false;
                        finishAnServiceButton.setEnabled(false);
                        //showNextBusStopButton.setEnabled(true);
                    }
                } catch (RuntimeException e) {
                    Toast.makeText(PassengerManagementOnRegularServiceActivity.this, "数字以外の文字が入力されています．", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //特に何もしない？
            }
        });
        //endregion

        //region 停留所自動設定ボタン
        autoSujestToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //トグルスイッチがONの時
                if (autoSujestToggleButton.isChecked() == true) {
                    busStopSuggestFlag = true;
                    showNextBusStopButton.setEnabled(false);
                }
                //トグルスイッチがOFFの時
                else if (autoSujestToggleButton.isChecked() == false) {
                    busStopSuggestFlag = false;
                    //終点であった場合
                    if (isFinalBusStop) {
                        showNextBusStopButton.setEnabled(false);
                    } else {
                        showNextBusStopButton.setEnabled(true);
                    }
                }
            }
        });
        busStopListSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (autoSujestToggleButton.isChecked()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //endregion

        //region EditTextの変更を監視する
        textWatchers = new TextWatcher[8];
        for (int i = 0; i < 8; i++) {
            textWatchers[i] = new TextWatcher() {
                int pre = 0;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    pre = InputChecker.numOnlyString(charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int post = InputChecker.numOnlyString(editable.toString());
                    int passengerUI = InputChecker.numOnlyString(passengerCountTextView.getText().toString());
                    passengerUI += post - pre;
                    passengerCountTextView.setText(Integer.toString(passengerUI));
                }
            };
        }

        entrainningCommonAdultEditText.addTextChangedListener(textWatchers[0]);
        entrainningCommonChildtEditText.addTextChangedListener(textWatchers[1]);
        entrainningHndicappedAdultEditText.addTextChangedListener(textWatchers[2]);
        entrainningHndicappedChildEditText.addTextChangedListener(textWatchers[3]);
        entrainningCouponAdultEditText.addTextChangedListener(textWatchers[4]);
        entrainningCouponChildEditText.addTextChangedListener(textWatchers[5]);
        entrainningCouponHandicappedEditText.addTextChangedListener(textWatchers[6]);
        entrainningCommuterPassEditText.addTextChangedListener(textWatchers[7]);
        //endregion

        //+ボタン
        findViewById(R.id.addPassengerCountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = InputChecker.numOnlyString(passengerCountTextView.getText().toString());
                num++;
                passengerCountTextView.setText(String.valueOf(num));

                //降車人数を下方修正する
                getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger--;
                Log.d("totalNumberOfGetOff", String.valueOf(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger));
            }
        });

        //-ボタン
        findViewById(R.id.dclimentPassengerCountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = InputChecker.numOnlyString(passengerCountTextView.getText().toString());
                if (num > 0) {
                    num--;
                }
                passengerCountTextView.setText(String.valueOf(num));

                //降車人数を加算する
                getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger++;
                Log.d("totalNumberOfGetOff", String.valueOf(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).totalNumberOfGetOffPassenger));
            }
        });

        //当便終了ボタン
        finishAnServiceButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                setPassengerInformationOnBusStopFromUI(
                        getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())).
                                passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())));
                serviceInformation.serviceInformationToJson(
                        getApplicationContext(),
                        directoryHelper.path(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON))
                );

                //アナウンスしたバス停の初期化
                speeched.clear();

                //GPS停止 追加
                locationStop();

                //
                /*Uri.Builder builder = new Uri.Builder();
                OutputOfServiceLog("end");

                String rootpath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log" +"/"+ today;
                SendLocationAsyncTask sendLocation = new SendLocationAsyncTask(PassengerManagementOnRegularServiceActivity.this,
                        filename,dataFile,rootpath,today);
                sendLocation.execute(builder);*/

                Intent intent = new Intent(PassengerManagementOnRegularServiceActivity.this, AnFinishedServiceResultActivity.class);
                startActivity(intent);
            }
        });

        //次へボタン
        showNextBusStopButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                int position = busStopListSpinner.getSelectedItemPosition();
                busStopListSpinner.setSelection(position + 1);
            }
        });

        //回数券販売ボタン
        findViewById(R.id.couponSaleButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serviceInformation.serviceInformationToJson(
                                getApplicationContext(),
                                directoryHelper.path(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON))
                        );
                        Intent intent = new Intent(PassengerManagementOnRegularServiceActivity.this, CouponTicketSaleActivity.class);
                        startActivity(intent);
                    }
                });

        //アナウンスボタン
        findViewById(R.id.voiceGuidButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String string = getString(R.string.serif01);
                        string += ((PassengerInfomationsOfBusStop) busStopListSpinner.getSelectedItem()).busStop.kanaName;
                        string += getString(R.string.serif02);
                        speechText(string);
                    }
                });

        //戻るボタン
        Intent intent = new Intent();
        if (!getIntent().getBooleanExtra("isFromServiceInformationSettingActivity", false)) {
            findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.backButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //表示されている内容も，オブジェクトに格納する
                        finish();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Activityが再開したときの処理
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToServiceInformation(directoryHelper.path(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)));

        setServiceInformation();
        busStopListSpinner.setSelection(SharedPreferenceHelper.getCurrentBusStopNum(this));
        setBusStop(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //表示されている内容も，オブジェクトに格納する
        try {
            //現在選択されている停留所の乗車中人数をを記録する
            SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), Integer.parseInt(passengerCountTextView.getText().toString()));

            setPassengerInformationOnBusStopFromUI(
                    getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())).
                            passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())));
            //オブジェクトを保存する
            DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
            serviceInformation.serviceInformationToJson(
                    getApplicationContext(),
                    directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
            );
        } catch (RuntimeException e) {
            Toast.makeText(PassengerManagementOnRegularServiceActivity.this, "数字以外の文字が入力されています．", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //表示されている内容も，オブジェクトに格納する
        try {
            //現在選択されている停留所の乗車中人数をを記録する
            SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), Integer.parseInt(passengerCountTextView.getText().toString()));

            setPassengerInformationOnBusStopFromUI(
                    getPassengerInformationOnRoutesByCourseNum(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())).
                            passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())));
            //オブジェクトを保存する
            DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
            serviceInformation.serviceInformationToJson(
                    getApplicationContext(),
                    directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
            );
        } catch (RuntimeException e) {
            Toast.makeText(PassengerManagementOnRegularServiceActivity.this, "数字以外の文字が入力されています．", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Uri.Builder builder = new Uri.Builder();
        // 運行ログにend出力
        //OutputOfServiceLog("end");
        // 運行ログを送信
        String rootpath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log" +"/"+ today;
        SendLocationAsyncTask sendLocation = new SendLocationAsyncTask(PassengerManagementOnRegularServiceActivity.this
                , filename,dataFile,rootpath,today);
        sendLocation.execute(builder);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(PassengerManagementOnRegularServiceActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * UIの内容をPassengerInformationOfBusStopに保存する
     *
     * @param buffer
     */
    private void setPassengerInformationOnBusStopFromUI(PassengerInfomationsOfBusStop buffer) {
        //debug
        Log.d("execute", "setPassengerInformationOnBusStopFromUI");

        //region 乗車人表
        buffer.numberOfGetOnPassenger.commonAdult = InputChecker.numOnlyString(entrainningCommonAdultEditText.getText().toString());
        buffer.numberOfGetOnPassenger.commonChild = InputChecker.numOnlyString(entrainningCommonChildtEditText.getText().toString());
        buffer.numberOfGetOnPassenger.handicappedAdult = InputChecker.numOnlyString(entrainningHndicappedAdultEditText.getText().toString());
        buffer.numberOfGetOnPassenger.handicappedChild = InputChecker.numOnlyString(entrainningHndicappedChildEditText.getText().toString());
        buffer.numberOfGetOnPassenger.couponAdult = InputChecker.numOnlyString(entrainningCouponAdultEditText.getText().toString());
        buffer.numberOfGetOnPassenger.couponChild = InputChecker.numOnlyString(entrainningCouponChildEditText.getText().toString());
        buffer.numberOfGetOnPassenger.couponHandicapped = InputChecker.numOnlyString(entrainningCouponHandicappedEditText.getText().toString());
        buffer.numberOfGetOnPassenger.commutarPass = InputChecker.numOnlyString(entrainningCommuterPassEditText.getText().toString());
        //endregion
        //region 降車人数表
//        buffer.totalNumberOfGetOffPassenger = InputChecker.numOnlyString(passengerCountEditText.getText().toString());
        //endregion
    }

    /**
     * 運行情報に関する表示を設定する
     */
    private void setServiceInformation() {
        //debug
//        Log.d("execute", "setServiceInformation()");

        int currentNumber = SharedPreferenceHelper.getCurrentCourseNum(this);

        routeNameTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.name);
        serviceTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.serviceType);
        routeNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.routeNumber));
        carTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.carType);
        couceTextView.setText(this.serviceInformation.passengerInfomation.course.name);
        courseNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum(currentNumber).route.courseNumber));
    }

    /**
     * jsonファイルをオブジェクトへ変換する
     *
     * @param path
     */
    private void jsonToServiceInformation(String path) {
        //debug
        Log.d("execute", " jsonToServiceInformation()");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        File file = new File(getApplicationContext().getFilesDir(), path);
        try {
            this.serviceInformation = objectMapper.readValue(file, ServiceInformation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 選択されている停留所の乗降者情報を取得，反映する
     */
    private void setBusStop(PassengerInfomationsOfBusStop busStop) {
        //debug
        Log.d("execute", "setBusStop()");
        Log.d("check", "numberOfGetOnPassenger.commonAdult\t" + String.valueOf(busStop.numberOfGetOnPassenger.commonAdult));

        //region 乗車人表
        entrainningCommonAdultEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.commonAdult));
        entrainningCommonChildtEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.commonChild));
        entrainningHndicappedAdultEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.handicappedAdult));
        entrainningHndicappedChildEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.handicappedChild));
        entrainningCouponAdultEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.couponAdult));
        entrainningCouponChildEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.couponChild));
        entrainningCouponHandicappedEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.couponHandicapped));
        entrainningCommuterPassEditText.setText(String.valueOf(busStop.numberOfGetOnPassenger.commutarPass));
        //endregion
        //region 降車人数表
//        passengerCountEditText.setText(String.valueOf(busStop.totalNumberOfGetOffPassenger));
        //endregion
        int passengerCount = SharedPreferenceHelper.getPreBusStopNum(getApplicationContext());
        passengerCountTextView.setText(String.valueOf(passengerCount));
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

    //region 音声合成関係
    @Override
    public void onInit(int i) {
        // TTS初期化l
        if (TextToSpeech.SUCCESS == i) {
            Log.d(PassengerManagementOnRegularServiceActivity.class.getName(), "initialized");
        } else {
            Log.e(PassengerManagementOnRegularServiceActivity.class.getName(), "faile to initialize");
        }
    }

    private void speechText(String string) {
        if (0 < string.length()) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                return;
            }
            // tts.speak(text, TextToSpeech.QUEUE_FLUSH, null) に
            // KEY_PARAM_UTTERANCE_ID を HasMap で設定
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

            textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, map);
            setTtsListener();

        }
    }

    // 読み上げの始まりと終わりを取得
    private void setTtsListener() {
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult = textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    Log.d(this.getClass().getName(), "progress on Done " + utteranceId);
                    voiceGuidButton.setEnabled(true);
                }

                @Override
                public void onError(String utteranceId) {
                    Log.d(this.getClass().getName(), "progress on Error " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId) {
                    Log.d(this.getClass().getName(), "progress on Start " + utteranceId);
                    voiceGuidButton.setEnabled(false);
                }

            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(this.getClass().getName(), "failed to add utterance progress listener");
            }
        } else {
            Log.e(this.getClass().getName(), "Build VERSION is less than API 15");
        }
    }
    //endregion

    //region GPS関係

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d("debug", "checkSelfPermission false");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000/*0*/, 0, this);
    }

    //位置情報取得を止める　追加
    private void locationStop(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
        locationManager = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", "==============================================");

        String Latitude,Longitude;
        Latitude = String.valueOf(location.getLatitude());
        Longitude = String.valueOf(location.getLongitude());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss.SSS",Locale.JAPAN);
        String locationDate = sdf.format(date);
        Log.d("debug", "lat="+Latitude+","+"lng="+Longitude);

        /*SendLocationAsyncTask sendLocation = new SendLocationAsyncTask(PassengerManagementOnRegularServiceActivity.this,
                );//PassengerManagementOnRegularServiceActivity.this*/
        if(Latitude != null && Longitude != null) {
            /*sendLocation.execute(getString(R.string.server_name) + getString(R.string.server_location_info),
                    "course="+this.serviceInformation.passengerInfomation.course.name +"&lat="+ Latitude + "&lng=" + Longitude);*/
            OutputOfServiceLog(locationDate +","+ Latitude +","+ Longitude);

        }

        //busStopSuggestの状態が"ON"であれば、停留所サジェストの機能を実行する。
        if (busStopSuggestFlag == true) {
            //停留所情報半自動サジェスト用
            SuggestedBusStop(location.getLatitude(), location.getLongitude(), getPassengerInformationOnRoutesByCourseNum().route.busStopList);
        } else if (busStopSuggestFlag == false) {
            busStopListSpinner.setClickable(true);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //追加
    @Override
    public void onResultSendLocation(String result){

    }

    //位置情報をテキストファイルに保存
    public void OutputOfServiceLog(String msg) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
        today = sdf.format(date);
        // ファイル生成フォルダ名を指定
        String rootPath = Environment.getExternalStorageDirectory().getPath() + "/pamsys" + "/log" +"/"+ today;
        //String rootPath = Environment.getDataDirectory().getPath() + "/pamsys" + "/log" +"/"+ today;
        File file = new File(rootPath);
        // フォルダ生成(フォルダが存在する場合は何もしない)
        file.mkdirs();

        // 路線名を指定する
        //String filename;
        filename = "Course_" + this.serviceInformation.passengerInfomation.course.name + ".txt";
        dataFile = new File(rootPath,filename);

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this,"Cannot use storage",Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, true); // true:追記 false:上書き
            mOutput.write(msg.getBytes());
            mOutput.write(13);
            mOutput.write(10);
            mOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion
    //region 停留所自動設定関係

    /**
     * busStopSpinner.setOnItemSelectedListenerイベントを半強制的に発生させることで、自動選択させる。
     *
     * @param lat      現在のバスの位置
     * @param lng      現在のバスの位置
     * @param busStops 現在の路線のバス停留所のリスト
     * @see
     */
    public void SuggestedBusStop(double lat, double lng, ArrayList<BusStop> busStops) {
        //距離の比較用
        BusStop bs = null;
        double min_dist = 0.0;
        float[] results = new float[3];


        //停留所すべての要素に対して、バスの現在地との距離を計算する。
        for (BusStop item : busStops) {
            Location.distanceBetween(lat, lng, item.latitude, item.longitude, results);
            if (results[0] < min_dist || min_dist == 0) {
                bs = item;
                min_dist = results[0];
            }
        }
        //debug
        Log.d("debug", "bs = " + bs.name);

        //busStopSpinnerのリストの中で、bsと同一のものがあれば、それを強制的に選択させる。
        int position = busStops.indexOf(bs);


        //最も近いバス停が変わったらアナウンスするコードを記述
        if(speeched.indexOf(bs.name) == -1) {
            String string = getString(R.string.serif01);
            string += bs.name;
            string += getString(R.string.serif02);
            speechText(string);
            speeched.add(bs.name);
        }


        busStopListSpinner.setSelection(position);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Log.d("onFocusChange", "boolean = " + String.valueOf(b));
        // フォーカスが入ったら全選択状態にする
        if (b) {
            Log.d("onFocusChange", "view = " + view);
            //EditText e = (EditText) view;
            //e.selectAll();
            ((EditText)view).selectAll();
        }
    }
}
