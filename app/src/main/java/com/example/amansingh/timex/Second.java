package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Second extends Fragment
{
    View myview;
    Button usestopwatch,liststopwatch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.second,container,false);
        usestopwatch = (Button)myview.findViewById(R.id.usestopwatch);
        liststopwatch = (Button)myview.findViewById(R.id.liststopwatch);
        usestopwatch.getBackground().setAlpha(180);
        liststopwatch.getBackground().setAlpha(180);
        usestopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Stopwatch.class);
                startActivity(i);
            }
        });
        liststopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Liststopwatch.class);
                startActivity(i);
            }
        });

        return myview;
    }
}
