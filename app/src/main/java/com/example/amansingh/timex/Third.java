package com.example.amansingh.timex;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Third extends Fragment{

    EditText name, participants;
    Button previous,start;
    String day [] = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
    String month[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String year[] = {"2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026"};
    Spinner Day,Month,Year;
    ArrayAdapter <String>adapter1,adapter2,adapter3;
    View myview;
    LinearLayout eventdate;
    String dayval , monthval , yearval;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.third,container,false);
        name = (EditText)myview.findViewById(R.id.eventname);
        participants = (EditText)myview.findViewById(R.id.participants);
        previous = (Button)myview.findViewById(R.id.previous_events);
        start = (Button)myview.findViewById(R.id.eventstart);
        Day = (Spinner)myview.findViewById(R.id.eventday);
        Month = (Spinner)myview.findViewById(R.id.eventmonth);
        Year = (Spinner)myview.findViewById(R.id.eventyear);
        eventdate = (LinearLayout)myview.findViewById(R.id.eventdate);
        eventdate.getBackground().setAlpha(190);
        name.getBackground().setAlpha(190);
        participants.getBackground().setAlpha(190);
        previous.getBackground().setAlpha(180);
        start.getBackground().setAlpha(180);

        adapter1 = new ArrayAdapter<String>(this.getActivity(),R.layout.spinner_list,day);
        adapter2 = new ArrayAdapter<String>(this.getActivity(),R.layout.spinner_list,month);
        adapter3 = new ArrayAdapter<String>(this.getActivity(),R.layout.spinner_list,year);
        Day.setAdapter(adapter1);
        Month.setAdapter(adapter2);
        Year.setAdapter(adapter3);

        Day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dayval = day[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthval = month[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearval = year[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() != 0 && participants.getText().toString().length() != 0) {
                    Intent intent = new Intent(getActivity(), Event.class);
                    intent.putExtra("eventname", name.getText().toString());
                    intent.putExtra("eventdate", dayval + " " + monthval + " " + yearval);
                    intent.putExtra("no_of_participants", participants.getText().toString());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getActivity(), "Input some values", Toast.LENGTH_SHORT).show();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Eventslist.class);
                startActivity(i);
            }
        });
        return myview;
    }
}
