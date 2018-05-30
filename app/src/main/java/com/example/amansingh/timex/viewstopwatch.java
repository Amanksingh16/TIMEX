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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class viewstopwatch extends AppCompatActivity {

    TextView name,date,timer;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    SQLiteDatabase db;
    SharedPreferences s;
    Button delete;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewstopwatch);

        intent = getIntent();
        name = (TextView)findViewById(R.id.stoptimername);
        date = (TextView)findViewById(R.id.stopwatchdate);
        timer = (TextView)findViewById(R.id.timerstopwatch);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        listView = (ListView)findViewById(R.id.liststoplaps);
        listView.getBackground().setAlpha(150);
        delete = (Button)findViewById(R.id.deletestopwatch);
        delete.getBackground().setAlpha(180);
        final Cursor cursor;
        arrayList = new ArrayList<String>();
        if(intent.hasExtra("stopname"))
        {
            cursor = db.rawQuery("Select * from stopwatch1 where name = '"+intent.getStringExtra("stopname")+"'and status='"+1+"' and username = '"+s.getString("username",null)+"'",null);
            cursor.moveToFirst();
            name.setText(name.getText().toString()+""+cursor.getString(cursor.getColumnIndex("name")));
            timer.setText(timer.getText().toString()+""+cursor.getString(cursor.getColumnIndex("time")));
            date.setText(date.getText().toString()+""+cursor.getString(cursor.getColumnIndex("date")));
            Cursor cursor1 = db.rawQuery("Select * from lap1 where lap_ID = '"+cursor.getInt(cursor.getColumnIndex("id"))+"' and username = '"+s.getString("username",null)+"'",null);
            cursor1.moveToFirst();
            int i = 1;
            do
            {
                if(cursor1.getCount()!=0)
                {
                    arrayList.add("\n  Lap "+i+" - "+cursor1.getString(cursor1.getColumnIndex("lap")));
                }
                else
                {
                    Toast.makeText(this, "No laps are present", Toast.LENGTH_SHORT).show();
                }
                i++;
            }
            while (cursor1.moveToNext());
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
        }
        else {
            cursor = db.rawQuery("Select * from stopwatch1 where name = '" + intent.getStringExtra("timername") + "' and status ='"+0+"' and username = '"+s.getString("username",null)+"'", null);
            cursor.moveToFirst();
            name.setText(name.getText().toString() + "" + cursor.getString(cursor.getColumnIndex("name")));
            timer.setText(timer.getText().toString() + "" + cursor.getString(cursor.getColumnIndex("time")));
            date.setText(date.getText().toString() + "" + cursor.getString(cursor.getColumnIndex("date")));
            Cursor cursor1 = db.rawQuery("Select * from lap1 where lap_ID = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and username = '"+s.getString("username",null)+"'", null);
            cursor1.moveToFirst();
            int i = 1;
            do {
                if (cursor1.getCount() != 0) {
                    arrayList.add("\n  Lap " + i + " - " + cursor1.getString(cursor1.getColumnIndex("lap")));
                } else {
                    Toast.makeText(this, "No laps are present", Toast.LENGTH_SHORT).show();
                }
                i++;
            }
            while (cursor1.moveToNext());
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
        }
            delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.hasExtra("stopname"))
                {
                    db.execSQL("Delete from stopwatch1 where name = '" + intent.getStringExtra("stopname") + "' and status = '"+1+"' and username='"+s.getString("username",null)+"'");
                    db.execSQL("Delete from lap1 where lap_ID = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and username='"+s.getString("username",null)+"'");
                    Toast.makeText(viewstopwatch.this, "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(viewstopwatch.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    db.execSQL("Delete from stopwatch1 where name = '" + intent.getStringExtra("timername") + "' and username ='"+s.getString("username",null)+"'");
                    db.execSQL("Delete from lap1 where lap_ID = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and username = '"+s.getString("username",null)+"'");
                    Toast.makeText(viewstopwatch.this, "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(viewstopwatch.this, Liststopwatch.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
