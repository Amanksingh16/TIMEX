package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class listslidertimer extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    SQLiteDatabase db;
    SharedPreferences s;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listslidertimer);

        listView = (ListView)findViewById(R.id.listslidertimer);
        listView.getBackground().setAlpha(100);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(listslidertimer.this,view_slidertimer.class);
                i.putExtra("timername",String.valueOf(listView.getItemAtPosition(position)).substring(16,String.valueOf(listView.getItemAtPosition(position)).length()-1));
                startActivity(i);
                finish();
            }
        });

        arrayList = new ArrayList<String>();
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        Cursor cursor = db.rawQuery("Select * from slidertimetable where status = 0 and username = '"+s.getString("username",null)+"'",null);
        cursor.moveToFirst();
        do {
            if (cursor.getCount() != 0)
            {
                name = cursor.getString(cursor.getColumnIndex("details"));
                arrayList.add("\n  Timer Name - " + name + "\n");
            }
            else
            {
                Toast.makeText(this, "No values are there", Toast.LENGTH_SHORT).show();
            }
        }
        while(cursor.moveToNext());
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
    }
