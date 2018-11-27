package com.example.yamaguchi.pamsys;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
//import android.support.v4.app.DialogFragment;        //ダイアログフラグメントをつかうときは，上のandroid.app.DialogFragmentを使うようにするといいです！！！！！！！！！！！！！！！！！！！！！！！！！！！
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yamaguchi.pamsys.Common.DirectoryHelper;
import com.example.yamaguchi.pamsys.Common.SharedPreferenceHelper;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Reservations;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.ServiceInformation;
import com.example.yamaguchi.pamsys.ReservationBusStopListHelperClasses.ReservationBusStopInformation;
import com.example.yamaguchi.pamsys.ReservationPassengerListFragmentHelperClasses.Reservation;
import com.example.yamaguchi.pamsys.ReservationPassengerListFragmentHelperClasses.WillGetOffPassengerListAdapter;
import com.example.yamaguchi.pamsys.ReservationPassengerListFragmentHelperClasses.WillGetOnPassengerListAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationPassengerListDialogFragment extends DialogFragment {

    ListView willGetOnPassengerDetailListView;
    ListView willGetOffPassengerDetailListView;
    Reservations reservations = new Reservations();

    public ReservationPassengerListDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Activityで生成されたDialogのサイズを，リサイズして最適化する．
        Dialog dialog = getDialog();
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (displayMetrics.widthPixels * 0.8);
        int dialogHeight = (int) (displayMetrics.heightPixels * 0.8);

        layoutParams.width = dialogWidth;
        layoutParams.height = dialogHeight;
        dialog.getWindow().setAttributes(layoutParams);
    }


    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_reservation_passenger_list);

        //予約表内のリストに表示する
        willGetOnPassengerDetailListView = (ListView) dialog.findViewById(R.id.willGetOnPassemgerDetailListView);
        willGetOffPassengerDetailListView = (ListView) dialog.findViewById(R.id.willGetOffPassengerDetailListView);
        WillGetOnPassengerListAdapter willGetOnPassengerListAdapter = new WillGetOnPassengerListAdapter(getActivity());
        WillGetOffPassengerListAdapter willGetOffPassengerListAdapter = new WillGetOffPassengerListAdapter(getActivity());

        //region Adapterに設置するリストを作成する
        ArrayList<Reservation> getOnReservations = new ArrayList<Reservation>();
        ArrayList<Reservation> getOffReservations = new ArrayList<Reservation>();

        DirectoryHelper directoryHelper = new DirectoryHelper(getActivity());
        String currentCourseNum = String.valueOf(SharedPreferenceHelper.getCurrentCourseNum(getActivity()));
        jsonToReservations(directoryHelper.path(DirectoryHelper.DOWNLOAD, DirectoryHelper.RESERVATIONS) + "/" + currentCourseNum + ".json");
        //名前による一致を行うので，動作の保証が難しい
        String busStopName = reservations.route.busStopList.get(SharedPreferenceHelper.getCurrentBusStopNum(getActivity())).name;
        TextView busStopTextView = (TextView)dialog.findViewById(R.id.busStopName);
        busStopTextView.setText(busStopName);

        for (com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Reservation item : reservations.reservations) {
            if (busStopName.equals(item.getOnBusStop)) {
                getOnReservations.add(new Reservation(String.valueOf(item.passengerID), item.passengerName, item.getOnBusStop, item.getOffBusStop, item.note));
            }
            if(busStopName.equals(item.getOffBusStop)){
                getOffReservations.add(new Reservation(String.valueOf(item.passengerID), item.passengerName, item.getOnBusStop, item.getOffBusStop, item.note));
            }
        }

        willGetOnPassengerListAdapter.setReservationPassengerList(getOnReservations);
        willGetOffPassengerListAdapter.setReservationPassengerList(getOffReservations);
        willGetOnPassengerDetailListView.setAdapter(willGetOnPassengerListAdapter);
        willGetOffPassengerDetailListView.setAdapter(willGetOffPassengerListAdapter);
        willGetOnPassengerDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reservation item = (Reservation) adapterView.getItemAtPosition(i);
                if (!item.note.equals("")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("備考欄")
                            .setMessage(item.note)
                            .setPositiveButton("戻る", null)
                            .show();
                }
            }
        });
        willGetOffPassengerDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reservation item = (Reservation) adapterView.getItemAtPosition(i);
                if (!item.note.equals("")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("備考欄")
                            .setMessage(item.note)
                            .setPositiveButton("戻る", null)
                            .show();
                }
            }
        });


        dialog.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return dialog;
    }

    /**
     * Reservations.jsonをオブジェクトへ変換する
     *
     * @param path
     */
    private void jsonToReservations(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        File file = new File(getActivity().getFilesDir(), path);
        try {
            reservations = objectMapper.readValue(file, Reservations.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
