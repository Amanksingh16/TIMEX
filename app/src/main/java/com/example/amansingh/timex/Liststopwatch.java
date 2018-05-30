package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Liststopwatch extends AppCompatActivity {

    ListView listView;
    SharedPreferences s;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase db;
    int laps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liststopwatch);
        listView = (ListView)findViewById(R.id.liststopwatchtimer);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        listView.getBackground().setAlpha(100);
        arrayList = new ArrayList<String>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Liststopwatch.this,viewstopwatch.class);
                i.putExtra("timername",String.valueOf(listView.getItemAtPosition(position)).substring(16,String.valueOf(listView.getItemAtPosition(position)).length()-22));
                startActivity(i);
                finish();
            }
        });

        Cursor cursor = db.rawQuery("Select * from stopwatch1 where status = 0 and username = '"+s.getString("username",null)+"'",null);
        cursor.moveToFirst();
        do {
            if (cursor.getCount() != 0) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Cursor cursor1 = db.rawQuery("Select * from lap1 where lap_ID = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and username = '"+s.getString("username",null)+"'", null);
                cursor1.moveToFirst();
                do {
                    if (cursor1.getCount() != 0) {
                        laps++;
                    }
                }
                while (cursor1.moveToNext());
                arrayList.add("\n  Timer Name - " + name + "\n  Number Of Laps - " + laps + "\n");
                laps = 0;
            }
            else
            {
                Toast.makeText(this, "No Values are here", Toast.LENGTH_SHORT).show();
            }
        }
        while(cursor.moveToNext());

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
