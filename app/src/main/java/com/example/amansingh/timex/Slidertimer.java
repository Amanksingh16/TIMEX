package com.example.amansingh.timex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Slidertimer extends AppCompatActivity {

    SeekBar seek;
    int progressChangedValue = 0;
    TextView text;
    Button start,stop;
    SQLiteDatabase db;
    CountDownTimer timer;
    Calendar calender;
    Intent i;
    SharedPreferences s;
    SimpleDateFormat simpleDateFormat;
    String Date;
    int status , count = 0;
    MediaPlayer player;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidertimer);

            i = getIntent();
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        start = (Button)findViewById(R.id.start_stop_button);
            stop = (Button)findViewById(R.id.stop);
            stop.setEnabled(false);
        db = this.openOrCreateDatabase("accounts", MODE_PRIVATE, null);

        text = (TextView)findViewById(R.id.time_text_view);
            seek = (SeekBar) findViewById(R.id.timer_seekbar);
start.getBackground().setAlpha(180);
stop.getBackground().setAlpha(180);
            text.getBackground().setAlpha(130);

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(progressChangedValue*1000), TimeUnit.MILLISECONDS.toMinutes(progressChangedValue*1000) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(progressChangedValue*1000)), TimeUnit.MILLISECONDS.toSeconds(progressChangedValue*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressChangedValue*1000)));
                    text.setText(hms);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(progressChangedValue*1000), TimeUnit.MILLISECONDS.toMinutes(progressChangedValue*1000) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(progressChangedValue*1000)), TimeUnit.MILLISECONDS.toSeconds(progressChangedValue*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressChangedValue*1000)));
                    text.setText(hms);
                }
            });
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (progressChangedValue != 0)
                    {
                        start.setEnabled(false);
                        stop.setEnabled(true);
                        calender = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date = simpleDateFormat.format(calender.getTime());
                        timer = new CountDownTimer(progressChangedValue * 1000, 1000) {

                           String starttime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(progressChangedValue * 1000), TimeUnit.MILLISECONDS.toMinutes(progressChangedValue * 1000) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(progressChangedValue * 1000)), TimeUnit.MILLISECONDS.toSeconds(progressChangedValue * 1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressChangedValue * 1000)));

                            public void onTick(long millisUntilFinished) {
                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                text.setText(hms);
                                seek.setProgress((int) (millisUntilFinished / 1000));
                            }

                            public void onFinish()
                            {
                                seek.setProgress(0);
                                text.setText("00:00:00");
                                start.setEnabled(true);
                                stop.setEnabled(false);
                                status = 1;
                                player = MediaPlayer.create(getApplicationContext(),R.raw.song);
                                player.start();
                                recordtime(starttime,Date,v);
                            }
                        }.start();
                    }
                    else
                    {
                        if (progressChangedValue == 0)
                        {
                            Toast.makeText(Slidertimer.this,"First set the timer",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    timer.cancel();
                    stop.setEnabled(false);
                    start.setEnabled(true);
                    seek.setProgress(0);
                    text.setText("00:00:00");
                }
            });
        }
        void recordtime(final String time, final String date, final View view)
        {
            AlertDialog.Builder alt = new AlertDialog.Builder(Slidertimer.this);
            final EditText textview = new EditText(Slidertimer.this);
            textview.setHint("Enter Details : ");
            alt.setView(textview);
            alt.setMessage("Do you want to save this timer").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            book1(time,date,textview.getText().toString(),view);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    private void book1(String Time,String Date,String details,View view1)
    {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS slidertimetable(id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,status INTEGER,participant_name VARCHAR,date VARCHAR , time VARCHAR, details VARCHAR)");
            if (i.hasExtra("eventnames"))
            {
                Cursor cursor = db.rawQuery("Select * from Event where name = '"+i.getStringExtra("eventnames")+"' and username = '"+s.getString("username",null)+"'",null);
                cursor.moveToFirst();
                Cursor cursor1 = db.rawQuery("Select * from participant where participant_id = '" + cursor.getInt(cursor.getColumnIndex("id")) + "' and name = '"+i.getStringExtra("participantname")+"' and username = '"+s.getString("username",null)+"'", null);
                cursor1.moveToFirst();
                db.execSQL("INSERT INTO slidertimetable(date,time,participant_name,details,status,username)values('"+Date+"','"+Time+"','"+cursor1.getString(cursor1.getColumnIndex("name"))+"','"+details+"','"+1+"','"+s.getString("username",null)+"')");
                Toast.makeText(this, ""+cursor1.getString(cursor1.getColumnIndex("name")), Toast.LENGTH_SHORT).show();
                Snackbar.make(view1, "Your Details are saved for "+cursor1.getString(cursor1.getColumnIndex("name")), Snackbar.LENGTH_LONG).show();
            }
            else
                if (i.hasExtra("part_name"))
                {
                    db.execSQL("INSERT INTO slidertimetable(date,time,participant_name,details,status,username)values('"+Date+"','"+Time+"','"+i.getStringExtra("part_name")+"','"+details+"','"+1+"','"+s.getString("username",null)+"')");
                    Toast.makeText(this, "Saved for "+i.getStringExtra("part_name"), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            else
                {
                db.execSQL("INSERT INTO slidertimetable(date ,status, time, details,username) VALUES('" + Date + "','"+0+"','" + Time + "','" + details + "','"+s.getString("username",null)+"')");
                Snackbar.make(view1, "Your Timer Details are Saved", Snackbar.LENGTH_LONG).show();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.maxlimit)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Slidertimer.this);
            final EditText textview = new EditText(Slidertimer.this);
            textview.setHint("Max Limit in minutes");
            builder.setView(textview);
            builder.setMessage("Change your slider max limit").setCancelable(false)
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (textview.getText().length() != 0) {
                                seek.setMax(Integer.parseInt(textview.getText().toString()) * 60);
                                Toast.makeText(Slidertimer.this, "Max Limit Changed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Slidertimer.this, "Input a correct value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Confirm");
            alert.show();
        }

        if(item.getItemId() == R.id.timetext)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Slidertimer.this);
            final EditText textview = new EditText(Slidertimer.this);
            textview.setHint("Enter Time in Seconds");
            builder.setView(textview);
            builder.setMessage("Set the Slider Timer").setCancelable(false)
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (textview.getText().length() != 0)
                            {
                                if(Integer.parseInt(textview.getText().toString()) <= seek.getMax()) {
                                    seek.setProgress(Integer.parseInt(textview.getText().toString()));
                                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(seek.getProgress() * 1000), TimeUnit.MILLISECONDS.toMinutes(seek.getProgress() * 1000) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seek.getProgress() * 1000)), TimeUnit.MILLISECONDS.toSeconds(seek.getProgress() * 1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seek.getProgress() * 1000)));
                                    text.setText(hms);
                                    Toast.makeText(Slidertimer.this, "Timer Set", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(Slidertimer.this, "You have exceded the Max Limit", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Slidertimer.this, "Input a correct value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Confirm");
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
