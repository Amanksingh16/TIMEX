package com.example.amansingh.timex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Event extends AppCompatActivity {

    TextView eventname;
    EditText Participant;
    Button stopwatch,slider,savelater,eventdetails;
    Intent intent;
    SharedPreferences s;
    SQLiteDatabase db;
    int count = 1,count1 = 1,count2=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        intent = getIntent();
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        eventname = (TextView)findViewById(R.id.event_name);
        Participant = (EditText)findViewById(R.id.participant_name);
        stopwatch = (Button)findViewById(R.id.stopwatch_event);
        slider = (Button)findViewById(R.id.slider_event);
        eventdetails = (Button)findViewById(R.id.eventdetails);
        savelater = (Button)findViewById(R.id.save_later);
        Participant.getBackground().setAlpha(190);
        stopwatch.getBackground().setAlpha(190);
        slider.getBackground().setAlpha(180);
        eventdetails.getBackground().setAlpha(180);
        savelater.getBackground().setAlpha(180);
        eventdetails.setEnabled(false);
        slider.setEnabled(false);
        stopwatch.setEnabled(false);
        eventname.setText(intent.getStringExtra("eventname"));

        final AlertDialog.Builder builder = new AlertDialog.Builder(Event.this);
        builder.setMessage("When Changing the participant name , save it first ").setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Warning");
        alert.show();
        if(intent.hasExtra("event_name"))
        {
             eventname.setText(intent.getStringExtra("event_name"));
             eventdetails.setEnabled(true);
        }

        eventdetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Event.this,EventDetails.class);
                i.putExtra("Eventname",eventname.getText().toString());
                i.putExtra("participantname",Participant.getText().toString());
                startActivity(i);
            }
        });

        stopwatch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(Participant.getText().toString().length() != 0)
                {
                    Intent i = new Intent(Event.this, Stopwatch.class);
                    i.putExtra("eventnames", eventname.getText().toString());
                    i.putExtra("participantname",Participant.getText().toString());
                    startActivity(i);
                }
                else {
                    Toast.makeText(Event.this, "Add a Participant Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Participant.getText().toString().length() != 0) {
                    Intent i = new Intent(Event.this, Slidertimer.class);
                    i.putExtra("eventnames",eventname.getText().toString());
                    i.putExtra("participantname",Participant.getText().toString());
                    startActivity(i);
                }
                else {
                    Toast.makeText(Event.this, "Add a Participant Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        savelater.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                db.execSQL("CREATE TABLE IF NOT EXISTS Event(id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,name VARCHAR,noofparticipants INTEGER,date VARCHAR)");
                if(Participant.getText().toString().length()!=0)
                {
                    db.execSQL("Create table if not exists participant(id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,participant_id INTEGER,name VARCHAR)");
                    Cursor cursor2 = db.rawQuery("Select * from Event where username = '"+s.getString("username",null)+"'",null);
                    cursor2.moveToFirst();
                    if(cursor2.getCount()!=0)
                    {
                        do {
                            if(cursor2.getString(cursor2.getColumnIndex("name")).equals(eventname.getText().toString()))
                            {
                                count2= 0;
                                break;
                            }
                        }while (cursor2.moveToNext());
                    }
                    if(count2!=0) {
                        db.execSQL("INSERT INTO Event(name,noofparticipants,username,date)values('" + eventname.getText().toString() + "','" + Integer.parseInt(intent.getStringExtra("no_of_participants")) + "','" + s.getString("username", null) + "','" + intent.getStringExtra("eventdate") + "')");
                    }
                        Cursor cursor = db.rawQuery("Select * from Event where name = '"+eventname.getText().toString()+"'and username = '"+s.getString("username",null)+"'",null);
                        cursor.moveToFirst();
                        int ID = cursor.getInt(cursor.getColumnIndex("id"));
                        Cursor cursor1 = db.rawQuery("Select * from participant where username = '"+s.getString("username",null)+"'",null);
                        cursor1.moveToFirst();
                        if(cursor1.getCount()<=cursor.getInt(cursor.getColumnIndex("noofparticipants")))
                        {
                            if(cursor1.getCount()!=0)
                            {
                                count = 1;
                                do {
                                    if (cursor1.getString(cursor1.getColumnIndex("name")).equals(Participant.getText().toString())) {
                                        Toast.makeText(Event.this, "This Participant is already saved", Toast.LENGTH_SHORT).show();
                                        count = 0;
                                        break;
                                    }
                                }while (cursor1.moveToNext());
                            }
                            if(count != 0)
                            {
                                db.execSQL("INSERT INTO participant(participant_id,username,name) values('"+ID+"','"+s.getString("username",null)+"','"+Participant.getText().toString()+"')");
                                Toast.makeText(Event.this, "Details Saved", Toast.LENGTH_SHORT).show();
                                slider.setEnabled(true);
                                stopwatch.setEnabled(true);
                            }
                        }
                        else
                        {
                            Toast.makeText(Event.this, "Number of Participants exceeded", Toast.LENGTH_SHORT).show();
                        }
                    }
                else {
                    if(!intent.hasExtra("event_name"))
                    {
                        Cursor cursor = db.rawQuery("Select * from Event where username = '"+s.getString("username",null)+"'",null);
                        cursor.moveToFirst();
                        if(cursor.getCount()!=0)
                        {
                            do {
                                if(cursor.getString(cursor.getColumnIndex("name")).equals(eventname.getText().toString()))
                                {
                                    Toast.makeText(Event.this, "Already Saved this event", Toast.LENGTH_SHORT).show();
                                    count1= 0;
                                    break;
                                }
                            }while (cursor.moveToNext());
                        }
                        if(count1!=0)
                        {
                            Log.i("h","running");
                            db.execSQL("INSERT INTO Event(name,noofparticipants,username,date)values('"+eventname.getText().toString()+"','"+Integer.parseInt(intent.getStringExtra("no_of_participants"))+"','"+s.getString("username",null)+"','"+intent.getStringExtra("eventdate")+"')");
                            Toast.makeText(Event.this, "Event Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
}
