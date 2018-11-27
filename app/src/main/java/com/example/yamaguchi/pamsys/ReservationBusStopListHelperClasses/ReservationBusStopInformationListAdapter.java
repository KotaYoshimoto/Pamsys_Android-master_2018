package com.example.yamaguchi.pamsys.ReservationBusStopListHelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yamaguchi.pamsys.R;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/03.
 */

public class ReservationBusStopInformationListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<ReservationBusStopInformation> infomations = null;

    public ReservationBusStopInformationListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setReservationBusStopInformationList(ArrayList<ReservationBusStopInformation> infomations) {
        this.infomations = infomations;
    }

    @Override
    public int getCount() {
        return this.infomations == null ? 0 : this.infomations.size();
    }

    @Override
    public Object getItem(int i) {
        return this.infomations == null ? null : this.infomations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.infomations == null ? null : i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.reservationtable_rowdesign, viewGroup,false);

        ((TextView)view.findViewById(R.id.busStopName)).setText(this.infomations.get(i).busStopName);
        ((TextView)view.findViewById(R.id.willGetOffCount)).setText(String.valueOf(this.infomations.get(i).willGetOffCount));
        ((TextView)view.findViewById(R.id.willGetOnCount)).setText(String.valueOf(this.infomations.get(i).willGetOnCount));

        return view;
    }
}
