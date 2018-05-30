package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.BatchUpdateException;
import java.util.ArrayList;

public class Participantsdetails extends AppCompatActivity {

    ListView slider , stop;
    TextView part_name , event;
    Button slidertimer,stopwatch,delete;
    SQLiteDatabase db;
    ArrayList<String> arrayList1,arrayList2;
    SharedPreferences s;
    ArrayAdapter <String> adapter1 , adapter2;
    Intent intent;
    int laps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantsdetails);
        intent = getIntent();
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        slider = (ListView)findViewById(R.id.sliderlistview);
        stop = (ListView)findViewById(R.id.stopwatchlistview);
        part_name = (TextView)findViewById(R.id.partname);
        event = (TextView)findViewById(R.id.evenname);
        slidertimer = (Button)findViewById(R.id.slideruse);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        stopwatch = (Button)findViewById(R.id.usestop);
        delete = (Button)findViewById(R.id.deletepart);
        slider.getBackground().setAlpha(180);
        stop.getBackground().setAlpha(180);
        slidertimer.getBackground().setAlpha(180);
        stopwatch.getBackground().setAlpha(180);
        delete.getBackground().setAlpha(180);
        arrayList1 = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();

        part_name.setText(part_name.getText().toString()+intent.getStringExtra("partname"));
        event.setText(event.getText().toString()+intent.getStringExtra("evenname"));

        slider.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Participantsdetails.this,view_slidertimer.class);
                i.putExtra("slidername",String.valueOf(slider.getItemAtPosition(position)).substring(8,String.valueOf(slider.getItemAtPosition(position)).length()-1));
                startActivity(i);
            }
        });

        stop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Participantsdetails.this,viewstopwatch.class);
                i.putExtra("stopname",String.valueOf(stop.getItemAtPosition(position)).substring(8,String.valueOf(stop.getItemAtPosition(position)).length()-1));
                startActivity(i);
            }
        });

        Cursor cursor1 = db.rawQuery("Select * from slidertimetable where participant_name = '"+intent.getStringExtra("partname")+"' and status = '"+1+"' and username = '"+s.getString("username",null)+"' ",null);
        cursor1.moveToFirst();
        do {
            if(cursor1.getCount()!=0) {
                arrayList1.add("\nName - " + cursor1.getString(cursor1.getColumnIndex("details")) + "\n");
            }
            else
            {
                Toast.makeText(this, "No SliderTimer Records", Toast.LENGTH_SHORT).show();
            }
        }while (cursor1.moveToNext());

        Cursor cursor2 = db.rawQuery("Select * from stopwatch1 where participant_name = '"+intent.getStringExtra("partname")+"' and status = '"+1+"' and username = '"+s.getString("username",null)+"' ",null);
        cursor2.moveToFirst();
        do {
            if (cursor2.getCount() != 0) {
                String name = cursor2.getString(cursor2.getColumnIndex("name"));
                Cursor cursor3 = db.rawQuery("Select * from lap1 where lap_ID = '" + cursor2.getInt(cursor2.getColumnIndex("id")) + "'and username ='"+s.getString("username",null)+"'", null);
                cursor3.moveToFirst();
                do {
                    if (cursor3.getCount() != 0) {
                        laps++;
                    }
                }
                while (cursor3.moveToNext());
                arrayList2.add("\nName - " + name + "\n");
                laps = 0;
            }
            else
            {
                Toast.makeText(this, "No Stopwatch Records", Toast.LENGTH_SHORT).show();
            }
        }while (cursor2.moveToNext());

        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList1);
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList2);
        slider.setAdapter(adapter1);
        stop.setAdapter(adapter2);

        slidertimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent i = new Intent(Participantsdetails.this,Slidertimer.class);
                 i.putExtra("part_name",intent.getStringExtra("partname"));
                 startActivity(i);
            }
        });
        stopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Participantsdetails.this,Stopwatch.class);
                i.putExtra("part_name",intent.getStringExtra("partname"));
                startActivity(i);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.rawQuery("Select * from Event where name = '"+intent.getStringExtra("evenname")+"'and username = '"+s.getString("username",null)+"'",null);
                cursor.moveToFirst();
                db.execSQL("Delete from participant where participant_id = '"+cursor.getInt(cursor.getColumnIndex("id"))+"' and name = '"+intent.getStringExtra("partname")+"'and username = '"+s.getString("username",null)+"'");
                Toast.makeText(Participantsdetails.this, "Participant Deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Participantsdetails.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
