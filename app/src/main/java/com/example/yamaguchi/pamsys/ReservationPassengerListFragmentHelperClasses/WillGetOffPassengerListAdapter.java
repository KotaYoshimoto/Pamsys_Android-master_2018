package com.example.yamaguchi.pamsys.ReservationPassengerListFragmentHelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yamaguchi.pamsys.R;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/04.
 */

public class WillGetOffPassengerListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Reservation> reservations = null;

    public WillGetOffPassengerListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setReservationPassengerList(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public int getCount() {
        return this.reservations == null ? 0 : this.reservations.size();
    }

    @Override
    public Object getItem(int i) {
        return this.reservations == null ? null : this.reservations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.reservations == null ? null : Integer.valueOf(reservations.get(i).passengerID);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = layoutInflater.inflate(R.layout.reservatedpassengers_rowdesign, viewGroup, false);

        if (!this.reservations.get(i).note.equals("")) {
            ((TextView) view.findViewById(R.id.passengerIDTextView)).setBackgroundColor(Color.rgb(243, 213, 26));
            ((TextView) view.findViewById(R.id.passengerNameTextView)).setBackgroundColor(Color.rgb(243, 213, 26));
            ((TextView) view.findViewById(R.id.passengerIDTextView)).setText(this.reservations.get(i).passengerID);
            ((TextView) view.findViewById(R.id.passengerNameTextView)).setText(this.reservations.get(i).passengerName);
        } else {
            ((TextView) view.findViewById(R.id.passengerIDTextView)).setText(this.reservations.get(i).passengerID);
            ((TextView) view.findViewById(R.id.passengerNameTextView)).setText(this.reservations.get(i).passengerName);
        }
        return view;
    }
}