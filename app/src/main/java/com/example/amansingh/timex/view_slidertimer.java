package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class view_slidertimer extends AppCompatActivity
{
    TextView name , timer , date;
    Button delete;
    SQLiteDatabase db;
    Intent intent;
    String timername;
    SharedPreferences s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slidertimer);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        name = (TextView)findViewById(R.id.timername);
        timer = (TextView)findViewById(R.id.timerslider);
        date = (TextView)findViewById(R.id.sliderdate);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        delete = (Button)findViewById(R.id.deleteslider);
        delete.getBackground().setAlpha(180);
        intent = getIntent();
        timername = intent.getStringExtra("timername");
        if(intent.hasExtra("slidername"))
        {
            Cursor cursor = db.rawQuery("Select * from slidertimetable where details = '"+intent.getStringExtra("slidername")+"' and status = '"+1+"' and username='"+s.getString("username",null)+"'",null);
            cursor.moveToFirst();
            name.setText(name.getText().toString()+""+cursor.getString(cursor.getColumnIndex("details")));
            timer.setText(timer.getText().toString()+""+cursor.getString(cursor.getColumnIndex("time")));
            date.setText(date.getText().toString()+""+cursor.getString(cursor.getColumnIndex("date")));
        }
        else {
            Cursor cursor = db.rawQuery("Select * from slidertimetable where details = '" + timername + "' and username = '"+s.getString("username",null)+"'", null);
            cursor.moveToFirst();
            name.setText(name.getText().toString()+""+cursor.getString(cursor.getColumnIndex("details")));
            timer.setText(timer.getText().toString()+""+cursor.getString(cursor.getColumnIndex("time")));
            date.setText(date.getText().toString()+""+cursor.getString(cursor.getColumnIndex("date")));
        }
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(intent.hasExtra("slidername"))
                {
                    db.execSQL("delete from slidertimetable where details = '" + intent.getStringExtra("stopname") + "' and status = '"+1+"' and username = '"+s.getString("username",null)+"'");
                    Toast.makeText(view_slidertimer.this, "Timer Deleted Succesfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(view_slidertimer.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    db.execSQL("delete from slidertimetable where details = '" + timername + "'and username = '"+s.getString("username",null)+"'");
                    Toast.makeText(view_slidertimer.this, "Timer Deleted Succesfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(view_slidertimer.this, listslidertimer.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
