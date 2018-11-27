package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.AnFinishServiceResultActivityHelperClasses.Calculation;
import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class AnFinishedServiceResultActivity extends Activity {

    ServiceInformation serviceInformation = new ServiceInformation();

    //region 運行情報関係
    TextView routeNameTextView;
    TextView serviceTypeTextView;
    TextView routeNumberTextView;
    TextView carTypeTextView;
    TextView couceTextView;
    TextView courseNumberTextView;
    //endregion
    //region 料金集計
    TextView totalPassengerCommonAdultTextView;
    TextView totalPassengerCommonChildTextView;
    TextView totalPassengerHandicappedAdultTextView;
    TextView totalPassengerHandicappedChildTextView;
    TextView totalCouponTicketAdultTextView;
    TextView totalCouponTicketChildTextView;
    TextView totalCouponTicketHandicappedTextView;
    TextView totalCommuterPassTextView;
    TextView totalPassengerTextView;
    TextView totalCashPassengerCommonAdultTextView;
    TextView totalCashPassengerCommonChildTextView;
    TextView totalCashPassengerHandicappedAdultTextView;
    TextView totalCashPassengerHandicappedChildTextView;
    TextView totalCashTextView;
    //endregion
    //region 回数券販売情報
    TextView soldCouponticketCountForAdultTextView;
    TextView soldCouponticketCountForChildTextView;
    TextView soldCouponticketCountForHandicappedTextView;
    TextView totalCountSoldTicketTextView;
    TextView totalSalseForAdultTextView;
    TextView totalSalseForChildTextView;
    TextView totalSalseForHandicappedTextView;
    TextView totalSalse;
    //endregion

    //region その他UI
    Button finishThisServiceButton;
    EditText mileageEditText;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_an_finished_service_result);

        //region 運行情報関係
        routeNameTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNameTextView);
        serviceTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.serviceTypeTextView);
        routeNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.routeNumberTextView);
        carTypeTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.carTypeTextView);
        couceTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courceTextView);
        courseNumberTextView = (TextView) findViewById(R.id.serviceInformationView).findViewById(R.id.courseNumberTextView);
        //endregion
        //region 料金集計
        totalPassengerCommonAdultTextView = (TextView) findViewById(R.id.totalPassengerCommonAdultTextView);
        totalPassengerCommonChildTextView = (TextView) findViewById(R.id.totalPassengerCommonChildTextView);
        totalPassengerHandicappedAdultTextView = (TextView) findViewById(R.id.totalPassengerHandicappedAdultTextView);
        totalPassengerHandicappedChildTextView = (TextView) findViewById(R.id.totalPassengerHandicappedChildTextView);
        totalCouponTicketAdultTextView = (TextView) findViewById(R.id.totalCouponTicketAdultTextView);
        totalCouponTicketChildTextView = (TextView) findViewById(R.id.totalCouponTicketChildTextView);
        totalCouponTicketHandicappedTextView = (TextView) findViewById(R.id.totalCouponTicketHandicappedTextView);
        totalCommuterPassTextView = (TextView) findViewById(R.id.totalCommuterPassTextView);
        totalPassengerTextView = (TextView) findViewById(R.id.totalPassengerTextView);
        totalCashPassengerCommonAdultTextView = (TextView) findViewById(R.id.totalCashCommonAdultTextView);
        totalCashPassengerCommonChildTextView = (TextView) findViewById(R.id.totalCashCommonChildTextView);
        totalCashPassengerHandicappedAdultTextView = (TextView) findViewById(R.id.totalCashHandicappedAdultTextView);
        totalCashPassengerHandicappedChildTextView = (TextView) findViewById(R.id.totalCashHandicappedChildTextView);
        totalCashTextView = (TextView) findViewById(R.id.totalCashTextView);
        //endregion
        //region 回数券販売情報
        soldCouponticketCountForAdultTextView = (TextView) findViewById(R.id.soldCouponTicketForAdultTextView);
        soldCouponticketCountForChildTextView = (TextView) findViewById(R.id.soldCouponTicketForChildTextView);
        soldCouponticketCountForHandicappedTextView = (TextView) findViewById(R.id.soldCouponTicketForHandicappedTextView);
        totalCountSoldTicketTextView = (TextView) findViewById(R.id.totalCountSoldTicketTextView);
        totalSalseForAdultTextView = (TextView) findViewById(R.id.totalSalesForAdultTextView);
        totalSalseForChildTextView = (TextView) findViewById(R.id.totalSalesForChildTextView);
        totalSalseForHandicappedTextView = (TextView) findViewById(R.id.totalSalesForHandicappedTextView);
        totalSalse = (TextView) findViewById(R.id.totalSalesTextView);
        //endregion
        //region その他UI
        finishThisServiceButton = (Button) findViewById(R.id.finishThisServiceButton);
        mileageEditText = (EditText) findViewById(R.id.mileageEditText);
        //endregion

        //jsonからデータを取り出す
        final DirectoryHelper directoryHelper = new DirectoryHelper(this);
        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
        //運行情報を反映する
        setServiceInformation();

        //region 当該コース最終便であるかどうかを判定し，表示を変更する
        if (SharedPreferenceHelper.getCurrentCourseNum(this) == serviceInformation.passengerInfomation.passengerInfomationsOfRoutes.size()) {
            finishThisServiceButton.setText("運行終了");
        } else {
            finishThisServiceButton.setText("当便終了");
            findViewById(R.id.mileageAreaLinearLayout).setVisibility(View.INVISIBLE);
        }
        //endregion

        //region 集計を行う
        Calculation calculation = new Calculation(getPassengerInformationOnRoutesByCourseNum().passengerInfomationsOfBusStops);
        totalPassengerCommonAdultTextView.setText(makePassengerCountString(calculation.getTotalPassengerCommonAdult()));
        totalPassengerCommonChildTextView.setText(makePassengerCountString(calculation.getTotalPassengerCommonChild()));
        totalPassengerHandicappedAdultTextView.setText(makePassengerCountString(calculation.getTotalPassengerHandicappedAdult()));
        totalPassengerHandicappedChildTextView.setText(makePassengerCountString(calculation.getTotalPassengerHandicappedChild()));
        totalCouponTicketAdultTextView.setText(makePassengerCountString(calculation.getTotalCouponTicketAdult()));
        totalCouponTicketChildTextView.setText(makePassengerCountString(calculation.getTotalCouponTicketChild()));
        totalCouponTicketHandicappedTextView.setText(makePassengerCountString(calculation.getTotalCouponTicketHandicapped()));
        totalCommuterPassTextView.setText(makePassengerCountString(calculation.getTotalCommuterPass()));
        totalPassengerTextView.setText(makePassengerCountString(calculation.getTotalPassenger()));
        totalCashPassengerCommonAdultTextView.setText(makeCashString(calculation.getTotalCashCommonAdult()));
        totalCashPassengerCommonChildTextView.setText(makeCashString(calculation.getTotalCashCommonChild()));
        totalCashPassengerHandicappedAdultTextView.setText(makeCashString(calculation.getTotalCashHandicappedAdult()));
        totalCashPassengerHandicappedChildTextView.setText(makeCashString(calculation.getTotalCashHandicappedChild()));
        totalCashTextView.setText(makeCashString(calculation.getTotalCash()));

        soldCouponticketCountForAdultTextView.setText(makeTicketCountString(calculation.getSoldCouponticketCountForAdult()));
        soldCouponticketCountForChildTextView.setText(makeTicketCountString(calculation.getSoldCouponticketCountForChild()));
        soldCouponticketCountForHandicappedTextView.setText(makeTicketCountString(calculation.getSoldCouponticketCountForHandicapped()));
        totalCountSoldTicketTextView.setText(makeTicketCountString(calculation.getSoldCouponSoldTicket()));
        totalSalseForAdultTextView.setText(makeCashString(calculation.getTotalSalseForAdult()));
        totalSalseForChildTextView.setText(makeCashString(calculation.getTotalSalseForChild()));
        totalSalseForHandicappedTextView.setText(makeCashString(calculation.getTotalSalseForHandicapped()));
        totalSalse.setText(makeCashString(calculation.getTotalSalse()));
        //endregion

        //戻るボタン
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //備考欄表示ボタン
        findViewById(R.id.showNoteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteDialogFragment noteDialogFragment = new NoteDialogFragment();
                Bundle args = new Bundle();
                args.putString("note", getPassengerInformationOnRoutesByCourseNum().note);
                noteDialogFragment.setArguments(args);
                noteDialogFragment.show(getFragmentManager(), noteDialogFragment.getClass().getName());
            }
        });

        //同便終了ボタン
        finishThisServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceInformation.passengerInfomation.passengerInfomationsOfRoutes.size() == SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())) {
                    //表示されているコースの運行を終了し，集計確認画面に移動する
                    //入力チェックを行う
                    if (InputChecker.isNumber(mileageEditText.getText().toString())) {
                        //入力されたそう今日距離を登録する
                        serviceInformation.endDistance = InputChecker.numOnlyString(mileageEditText.getText().toString());
                        serviceInformation.serviceInformationToJson(getApplicationContext(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
                        Intent intent = new Intent(AnFinishedServiceResultActivity.this, DriverRecordsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        AnFinishedServiceResultActivity.this.finish();
                    } else {
                        Toast.makeText(AnFinishedServiceResultActivity.this, "走行距離が未入力，または数値以外が入力されています．", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //表示されている便を終了し，次の便のアクティビティに移動する
                    int nextCouseNum = SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext()) + 1;
                    //設定を次の便数に変更し，停留所の位置をリセットする
                    SharedPreferenceHelper.setCurrentCourseNum(getApplicationContext(), nextCouseNum);
                    SharedPreferenceHelper.setCurrentBusStopNum(getApplicationContext(), 0);
                    SharedPreferenceHelper.setPreBusStopNum(getApplicationContext(), 0);
                    //次便が定期便かデマンド便かを判断する
                    if (getPassengerInformationOnRoutesByCourseNum(nextCouseNum).route.carType.equals("定期")) {
                        SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "定期");
                        Intent intent = new Intent(AnFinishedServiceResultActivity.this, PassengerManagementOnRegularServiceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        AnFinishedServiceResultActivity.this.finish();
                    } else {
                        SharedPreferenceHelper.setCurrentCarType(getApplicationContext(), "デマンド");
                        Intent intent = new Intent(AnFinishedServiceResultActivity.this, ReservationBusStopListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        AnFinishedServiceResultActivity.this.finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(AnFinishedServiceResultActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * NoteDialogFragmentから返された備考の内容を保存する
     *
     * @param note
     */
    public void onReturnNote(String note) {
        PassengerInformationsOfRoute passengerInformationsOfRoute = getPassengerInformationOnRoutesByCourseNum();
        passengerInformationsOfRoute.note = note;
        //保存する
        DirectoryHelper directoryHelper = new DirectoryHelper(this);
        serviceInformation.serviceInformationToJson(this, directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
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
        routeNameTextView.setText(getPassengerInformationOnRoutesByCourseNum().route.name);
        serviceTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum().route.serviceType);
        routeNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum().route.routeNumber));
        carTypeTextView.setText(getPassengerInformationOnRoutesByCourseNum().route.carType);
        couceTextView.setText(this.serviceInformation.passengerInfomation.course.name);
        courseNumberTextView.setText(String.valueOf(getPassengerInformationOnRoutesByCourseNum().route.courseNumber));
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

}
