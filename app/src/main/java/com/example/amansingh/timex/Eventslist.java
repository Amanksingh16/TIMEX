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

public class Eventslist extends AppCompatActivity {

    ListView listView;
    SQLiteDatabase db;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    SharedPreferences s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);

        listView = (ListView) findViewById(R.id.eventslist);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE, null);
        arrayList = new ArrayList<String>();
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        listView.getBackground().setAlpha(190);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Eventslist.this,Event.class);
                i.putExtra("event_name",String.valueOf(listView.getItemAtPosition(position)).substring(16,String.valueOf(listView.getItemAtPosition(position)).length()-28));
                startActivity(i);
                finish();
            }
        });

        Cursor cursor = db.rawQuery("Select * from Event where username = '"+s.getString("username",null)+"'", null);
        cursor.moveToFirst();
        do {
            if (cursor.getCount() != 0)
            {
                arrayList.add("\n  Event Name - " + cursor.getString(cursor.getColumnIndex("name")) + "\n  Created On - " + cursor.getString(cursor.getColumnIndex("date")) + "\n");
            } else {
                Toast.makeText(this, "No values are there", Toast.LENGTH_SHORT).show();
            }
        } while (cursor.moveToNext());
      adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
      listView.setAdapter(adapter);
    }
}