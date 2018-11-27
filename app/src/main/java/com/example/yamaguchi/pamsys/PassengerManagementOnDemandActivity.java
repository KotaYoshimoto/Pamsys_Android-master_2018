package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Reservations;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.ServiceInformationMasterData;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PassengerManagementOnDemandActivity extends Activity implements TextToSpeech.OnInitListener ,View.OnFocusChangeListener{

    ServiceInformation serviceInformation = new ServiceInformation();
    Reservations reservations = new Reservations();
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
    TextView busStopTextView;
    Button voiceGuidButton;
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
    //endregion
    //region 降車人数表
    EditText detrainningCommonAdultEditText;
    EditText detrainningCommonChildtEditText;
    EditText detrainningHndicappedAdultEditText;
    EditText detrainningHndicappedChildEditText;
    EditText detrainningCouponAdultEditText;
    EditText detrainningCouponChildEditText;
    EditText detrainningCouponHandicappedEditText;
    EditText detrainningCommuterPassEditText;
    //endregion
    Button recordingButton;
    TextView entrainningReservationTextView;
    TextView detrainningReservationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_management_on_demand);

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
        busStopTextView = (TextView) findViewById(R.id.busStopTextView);
        voiceGuidButton = (Button) findViewById(R.id.voiceGuidButton);
        //endregion
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
        //endregion
        entrainningReservationTextView = (TextView) findViewById(R.id.entrainningReservationTextView);
        detrainningReservationTextView = (TextView) findViewById(R.id.detarainningReservationTextView);

        textToSpeech = new TextToSpeech(this, this);

        /*
        データの読み込み
         */
        final DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
        String currentCourseNum = String.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()));
        jsonToReservations(directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + currentCourseNum + ".json");

        //region 運行情報関係，停留所表示関係
        setServiceInformation();
        busStopTextView.setText(getPassengerInfomationsOfBusStop().busStop.name);
        setBusStop(getPassengerInfomationsOfBusStop());
        //endregion

        //region 各予定人数を表示
        Intent intent = getIntent();
        entrainningReservationTextView.setText("乗車人数(予定人数 " + String.valueOf(intent.getIntExtra("willGetOnCount", 0)) + " 人)");
        detrainningReservationTextView.setText(String.valueOf(intent.getIntExtra("willGetOffCount", 0)));
        //endregion

        //アナウンスボタン
        findViewById(R.id.voiceGuidButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = getString(R.string.serif01);
                string += getPassengerInfomationsOfBusStop().busStop.kanaName;
                string += getString(R.string.serif02);
                speechText(string);
            }
        });

        //予約者リスト表示ボタン
        findViewById(R.id.showReservationListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReservationPassengerListDialogFragment reservationPassengerListDialogFragment = new ReservationPassengerListDialogFragment();
                reservationPassengerListDialogFragment.show(getFragmentManager(), "ReservationPassengerListDialogFragment");
            }
        });

        //回数券販売ボタン
        findViewById(R.id.couponSaleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                setPassengerInformationOnBusStopFromUI(getPassengerInfomationsOfBusStop());
                serviceInformation.serviceInformationToJson(
                        getApplicationContext(),
                        directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
                );

                Intent intent = new Intent(PassengerManagementOnDemandActivity.this, CouponTicketSaleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //戻るボタン
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DirectoryHelper directoryHelper = new DirectoryHelper(getApplicationContext());
                setPassengerInformationOnBusStopFromUI(getPassengerInfomationsOfBusStop());
                serviceInformation.serviceInformationToJson(
                        getApplicationContext(),
                        directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
                );
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Activityが再開したときの処理
        String currentCourseNum = String.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()));
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToReservations(directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + currentCourseNum + ".json");
        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));

        setServiceInformation();
        setBusStop(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops.get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //状態を保存する
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        try {
            setPassengerInformationOnBusStopFromUI(getPassengerInfomationsOfBusStop());
            serviceInformation.serviceInformationToJson(
                    getApplicationContext(),
                    directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON)
            );
        } catch (RuntimeException e) {
//            Toast.makeText(PassengerManagementOnDemandActivity.this, "数字以外の文字が入力されています．", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(PassengerManagementOnDemandActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
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
     * 選択されている停留所の乗降者情報を取得，反映する
     */
    private void setBusStop(PassengerInfomationsOfBusStop busStop) {
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
    //endregion
}
