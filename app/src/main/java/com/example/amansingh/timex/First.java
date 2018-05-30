package com.example.amansingh.timex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class First extends Fragment
{
    View myview;
    Button slider,listslider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.first,container,false);
        slider = (Button)myview.findViewById(R.id.usesslider);
        listslider = (Button)myview.findViewById(R.id.listslider);
        slider.getBackground().setAlpha(180);
        listslider.getBackground().setAlpha(180);
        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Slidertimer.class);
                startActivity(i);
            }
        });
        listslider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),listslidertimer.class);
                startActivity(i);
            }
        });
        return myview;

    }
}
