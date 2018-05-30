package com.example.amansingh.timex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Stopwatch extends AppCompatActivity implements View.OnClickListener{

    final int MSG_START_TIMER = 0;
    final int MSG_PAUSE_TIMER = 3;
    final int MSG_STOP_TIMER = 1;
    final int MSG_RESUME_TIMER = 4;
    final int MSG_UPDATE_TIMER = 2;

    test2 timer = new test2();
    final int REFRESH_RATE = 100;
    String time;

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    tvTextView.setText(""+ timer.getElapsedtimemin()+":"+String.format("%02d",timer.getElapsedTimeSecs())+":"+String.format("%03d",timer.getElapsedTime()));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    time = tvTextView.getText().toString();
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop();//stop timer
                    tvTextView.setText(""+ timer.getElapsedtimemin()+":"+String.format("%02d",timer.getElapsedTimeSecs())+":"+String.format("%03d",timer.getElapsedTime()));
                    break;

                case MSG_PAUSE_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER);
                    tvTextView.setText(tvTextView.getText().toString());
                    timer.pause();
                    break;

                case MSG_RESUME_TIMER :
                    timer.resume();
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                default:
                    break;
            }
        }
    };

    TextView tvTextView;
    Button btnStart,btnStop,btnpause,lap;
    LinearLayout linearLayout;
    SQLiteDatabase db;
    List<String> list;
    Intent i;
    Calendar calender;
    SimpleDateFormat simpleDateFormat;
    String Date;
    SharedPreferences s;
    TextView textView;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        tvTextView = (TextView)findViewById(R.id.timer1);
        i = getIntent();
        btnStart = (Button)findViewById(R.id.start1);
        btnStop= (Button)findViewById(R.id.stop1);
        btnpause = (Button)findViewById(R.id.pause1);
        lap = (Button)findViewById(R.id.lapbutton);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        linearLayout = (LinearLayout)findViewById(R.id.containerfull);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnpause.setOnClickListener(this);
        lap.setOnClickListener(this);
        btnStart.getBackground().setAlpha(180);
        btnStop.getBackground().setAlpha(180);
        btnpause.getBackground().setAlpha(180);
        lap.getBackground().setAlpha(180);
        list = new ArrayList<String>();
        btnpause.setEnabled(false);
        btnStop.setEnabled(false);
        db = this.openOrCreateDatabase("accounts",Context.MODE_PRIVATE,null);
    }
    int count = 0;

    public void onClick(View v) {
        if(btnStart == v) {
            mHandler.sendEmptyMessage(MSG_START_TIMER);
            btnStart.setEnabled(false);
            btnpause.setEnabled(true);
            btnStop.setEnabled(true);
            calender = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date = simpleDateFormat.format(calender.getTime());
        }
        else
        if(btnStop == v){
            mHandler.sendEmptyMessage(MSG_STOP_TIMER);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnpause.setEnabled(false);
            db.execSQL("CREATE TABLE IF NOT EXISTS stopwatch1 (id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,status INTEGER,participant_name VARCHAR,name VARCHAR , time VARCHAR, date VARCHAR)");
            db.execSQL("CREATE TABLE IF NOT EXISTS lap1 (id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,lap_ID INTEGER,lap VARCHAR)");
            AlertDialog.Builder alt = new AlertDialog.Builder(Stopwatch.this);
            final EditText textview = new EditText(Stopwatch.this);
            textview.setHint("Enter Details : ");
            alt.setView(textview);
            alt.setMessage("Do you want to save this timer").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(i.hasExtra("eventnames")) {
                                Cursor cursor = db.rawQuery("Select * from Event where name = '" + i.getStringExtra("eventnames") + "' and username ='"+s.getString("username",null)+"'", null);
                                cursor.moveToFirst();
                                Cursor cursor1 = db.rawQuery("Select * from participant where participant_id = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and name = '"+i.getStringExtra("participantname")+"' and username = '"+s.getString("username",null)+"'", null);
                                cursor1.moveToFirst();
                                db.execSQL("INSERT INTO stopwatch1(name,time,date,participant_name,status,username) values('" + textview.getText().toString() + "','" + time + "','" + Date + "','" + cursor1.getString(cursor1.getColumnIndex("name")) + "','"+1+"','"+s.getString("username",null)+"')");
                                Cursor cursor2 = db.rawQuery("Select * from stopwatch1 where date = '" + Date + "'and username = '"+s.getString("username",null)+"'", null);
                                cursor2.moveToFirst();
                                int ID = cursor2.getInt(cursor.getColumnIndex("id"));
                                if (count > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        db.execSQL("INSERT INTO lap1(lap_ID,lap,username) values('" + ID + "','" + list.get(i) + "','"+s.getString("username",null)+"')");
                                    }
                                    Intent i = new Intent(Stopwatch.this, Stopwatch.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved for "+cursor1.getString(cursor1.getColumnIndex("name")), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent i = new Intent(Stopwatch.this, Stopwatch.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved without laps", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            if (i.hasExtra("part_name"))
                            {
                                db.execSQL("INSERT INTO stopwatch1(name,time,date,participant_name,status,username) values('" + textview.getText().toString() + "','" + time + "','" + Date + "','" + i.getStringExtra("part_name") + "','"+1+"','"+s.getString("username",null)+"')");
                                Cursor cursor2 = db.rawQuery("Select * from stopwatch1 where date = '" + Date + "'and username = '"+s.getString("username",null)+"'", null);
                                cursor2.moveToFirst();
                                int ID = cursor2.getInt(cursor2.getColumnIndex("id"));
                                if (count > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        db.execSQL("INSERT INTO lap1(lap_ID,lap,username) values('" + ID + "','" + list.get(i) + "','"+s.getString("username",null)+"')");
                                    }
                                    Intent i = new Intent(Stopwatch.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved for "+i.getStringExtra("part_name"), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent i = new Intent(Stopwatch.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved without laps", Toast.LENGTH_SHORT).show();
                                }
                            }
                              else {
                                db.execSQL("INSERT INTO stopwatch1(name,time,date,status,username) values('" + textview.getText().toString() + "','" + time + "','" + Date + "','"+0+"','"+s.getString("username",null)+"')");
                                Cursor cursor = db.rawQuery("Select * from stopwatch1 where date = '" + Date + "'and username = '"+s.getString("username",null)+"'", null);
                                cursor.moveToFirst();
                                int ID = cursor.getInt(cursor.getColumnIndex("id"));
                                if (count > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        db.execSQL("INSERT INTO lap1(lap_ID,lap,username) values('" + ID + "','" + list.get(i) + "','"+s.getString("username",null)+"')");
                                    }
                                    Intent i = new Intent(Stopwatch.this, Stopwatch.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent i = new Intent(Stopwatch.this, Stopwatch.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(Stopwatch.this, "Stopwatch Timer saved without laps", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alt.create();
            alertDialog.setTitle("Confirm");
            alertDialog.show();
        }
        else
            if(btnpause == v) {
                if (btnpause.getText().toString().equals("Pause")) {
                    mHandler.sendEmptyMessage(MSG_PAUSE_TIMER);
                    btnpause.setText("Resume");
                }
                else
                if (btnpause.getText().toString().equals("Resume"))
                {
                    mHandler.sendEmptyMessage(MSG_RESUME_TIMER);
                    btnpause.setText("Pause");
                }
            }
        else
            if(lap == v)
            {
                count++;
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               View addview = inflater.inflate(R.layout.row,null);
               textView = (TextView)addview.findViewById(R.id.lap);
               textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
               textView.setText("Lap "+count+" : "+tvTextView.getText());
               list.add(tvTextView.getText().toString());
               linearLayout.addView(addview);
            }
    }
}
