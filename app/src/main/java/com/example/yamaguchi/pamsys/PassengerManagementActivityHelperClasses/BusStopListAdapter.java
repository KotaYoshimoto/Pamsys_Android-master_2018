package com.example.yamaguchi.pamsys.PassengerManagementActivityHelperClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.R;

import java.util.List;

/**
 * Created by yamaguchi on 2017/11/15.
 */

public class BusStopListAdapter extends ArrayAdapter<PassengerInfomationsOfBusStop> {

    Context context;
    LayoutInflater layoutInflater = null;
    List<PassengerInfomationsOfBusStop> passengerInfomationsOfBusStopList = null;

    public BusStopListAdapter(@NonNull Context context, int resource, @NonNull List<PassengerInfomationsOfBusStop> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.passengerInfomationsOfBusStopList = objects;
    }


    @Override
    public int getCount() {
        return passengerInfomationsOfBusStopList.size();
    }

    @Override
    public PassengerInfomationsOfBusStop getItem(int i) {
        return passengerInfomationsOfBusStopList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
