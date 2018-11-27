package com.example.yamaguchi.pamsys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.InputChecker;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.CouponTicketSaleActivityHelperClasses.CouponTicketType;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.CouponSoldInformation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CouponTicketSaleActivity extends Activity {

    //5行分用意する
    ArrayList<Row> rows = new ArrayList<Row>();

    ServiceInformation serviceInformation = new ServiceInformation();
    ArrayList<CouponSoldInformation> couponSoldInformations = new ArrayList<CouponSoldInformation>();
    DirectoryHelper directoryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_ticket_sale);

        directoryHelper = new DirectoryHelper(this);

        jsonToServiceInformation(directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));

        for (int i = 0; i < 5; i++) {
            rows.add(new Row());
        }
        //region スピナー一つ一つに対して，アダプターを設定していく
        rows.get(0).ticketType = (Spinner) findViewById(R.id.couponTicketSoldInformation_row1).findViewById(R.id.couponTicketsSpinner);
        rows.get(1).ticketType = (Spinner) findViewById(R.id.couponTicketSoldInformation_row2).findViewById(R.id.couponTicketsSpinner);
        rows.get(2).ticketType = (Spinner) findViewById(R.id.couponTicketSoldInformation_row3).findViewById(R.id.couponTicketsSpinner);
        rows.get(3).ticketType = (Spinner) findViewById(R.id.couponTicketSoldInformation_row4).findViewById(R.id.couponTicketsSpinner);
        rows.get(4).ticketType = (Spinner) findViewById(R.id.couponTicketSoldInformation_row5).findViewById(R.id.couponTicketsSpinner);
        for (Row row : rows) {
            Spinner item = row.ticketType;
            ArrayAdapter<CouponTicketType> adapter = new ArrayAdapter<CouponTicketType>(this, R.layout.spinner_dropdown_item, CouponTicketType.values());
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            item.setAdapter(adapter);
        }
        //endregion

        //region EditTextを紐づけする
        rows.get(0).ticketEditText = (EditText) findViewById(R.id.couponTicketSoldInformation_row1).findViewById(R.id.couponTicketNumberEditText);
        rows.get(1).ticketEditText = (EditText) findViewById(R.id.couponTicketSoldInformation_row2).findViewById(R.id.couponTicketNumberEditText);
        rows.get(2).ticketEditText = (EditText) findViewById(R.id.couponTicketSoldInformation_row3).findViewById(R.id.couponTicketNumberEditText);
        rows.get(3).ticketEditText = (EditText) findViewById(R.id.couponTicketSoldInformation_row4).findViewById(R.id.couponTicketNumberEditText);
        rows.get(4).ticketEditText = (EditText) findViewById(R.id.couponTicketSoldInformation_row5).findViewById(R.id.couponTicketNumberEditText);
        //endregion

        //戻るボタン
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //region 前の画面に戻るか確認する
                new AlertDialog.Builder(CouponTicketSaleActivity.this)
                        .setTitle("注意")
                        .setMessage("記入された内容は保存されませんが，よろしいですか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CouponTicketSaleActivity.this.finish();
                            }
                        })
                        .setNegativeButton("キャンセル", null)
                        .show();
                //endregion
            }
        });

        //確認ボタン
        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //入力チェックをする
                if (!checkInput(rows)) {
                    //region 入力に不備があった場合
                    new AlertDialog.Builder(CouponTicketSaleActivity.this)
                            .setTitle("注意")
                            .setMessage("入力された通し番号に誤りがあります．")
                            .setPositiveButton("OK", null)
                            .show();
                    //endregion
                } else {
                    //UIからデータを抽出する
                    String message = getCouponSoldInformationsFromUI(couponSoldInformations, rows);
                    //region 販売した内容を保存するか尋ねる
                    new AlertDialog.Builder(CouponTicketSaleActivity.this)
                            .setTitle("以下の内容で販売しましたか？")
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    addCouponSoldInformation(couponSoldInformations, serviceInformation);
                                    serviceInformation.serviceInformationToJson(getApplicationContext(), directoryHelper.path(DirectoryHelper.UPLOAD, DirectoryHelper.CURRENT, DirectoryHelper.SERVICEINFORMATION_JSON));
                                    CouponTicketSaleActivity.this.finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    couponSoldInformations.clear();
                                }
                            })
                            .show();
                    //endregion
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(CouponTicketSaleActivity.this, "アプリ使用中は，このボタンを使用できません．", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * UIにおける各行をまとめるインナークラス
     */
    class Row {
        public Spinner ticketType;
        public EditText ticketEditText;
    }

    /**
     * @param couponSoldInformations
     * @param serviceInformation
     */
    private void addCouponSoldInformation(ArrayList<CouponSoldInformation> couponSoldInformations, ServiceInformation serviceInformation) {
        //設定からコース便数を取り出し，格納先を決定する
        PassengerInformationsOfRoute passengerInformationsOfRoute = null;
        for (PassengerInformationsOfRoute item : serviceInformation.passengerInfomation.passengerInfomationsOfRoutes) {
            if (item.route.courseNumber == SharedPreferenceHelper.getCurrentCourseNum(getApplicationContext())) {
                passengerInformationsOfRoute = item;
                break;
            }
        }
        //設定から，編集中の停留所を取り出し，格納先を決定する
        if (passengerInformationsOfRoute == null) {
            new RuntimeException(this.getClass().getName() + "addCouponSoldInformation refered null object ...");
        } else {
            for (CouponSoldInformation item : couponSoldInformations) {
                passengerInformationsOfRoute.
                        passengerInfomationsOfBusStops.
                        get(SharedPreferenceHelper.getCurrentBusStopNum(getApplicationContext())).
                        couponSoldInformations.add(item);
            }
        }
    }


    /**
     * 入力された内容を，couponSoldInformationsに格納する
     *
     * @param couponSoldInformations
     * @param rows
     * @return message
     */
    private String getCouponSoldInformationsFromUI(ArrayList<CouponSoldInformation> couponSoldInformations, ArrayList<Row> rows) {
        for (Row item : rows) {
            if (InputChecker.isNumber(item.ticketEditText.getText().toString())) {
                couponSoldInformations.add(new CouponSoldInformation((String) item.ticketType.getSelectedItem().toString(), Integer.valueOf(item.ticketEditText.getText().toString())));
            }
        }
        String message = "";
        for (CouponSoldInformation item : couponSoldInformations) {
            if (item.ticketType.equals(CouponTicketType.Handicapped)) {
                message += item.ticketType + "\t\t\t\t" + String.format("%06d", item.ticketNumber) + "\n";
            } else {
                message += item.ticketType + "\t\t" + String.format("%06d", item.ticketNumber) + "\n";
            }
        }
        return message;
    }

    /**
     * 画面に正しく入力されているかどうかを判定する
     *
     * @param rows
     * @return
     */
    private boolean checkInput(ArrayList<Row> rows) {
        boolean flag = false;
        for (Row item : rows) {
            flag |= InputChecker.isNumber(item.ticketEditText.getText().toString());
        }
        return true;
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

}
