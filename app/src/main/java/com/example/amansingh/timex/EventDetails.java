package com.example.amansingh.timex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetails extends AppCompatActivity {

    TextView name , date , total_participants , participated;
    Button delete , participants;
    Intent intent;
    SQLiteDatabase db;
    SharedPreferences s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        intent = getIntent();
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        name = (TextView)findViewById(R.id.eventdetailname);
        date = (TextView)findViewById(R.id.dateevent);
        total_participants = (TextView)findViewById(R.id.eventparticipants);
        participated = (TextView)findViewById(R.id.participated);
        delete = (Button)findViewById(R.id.deleteevent);
        participants = (Button)findViewById(R.id.event_participants);
        delete.getBackground().setAlpha(180);
        participants.getBackground().setAlpha(180);

        final Cursor cursor = db.rawQuery("Select * from Event where name = '"+intent.getStringExtra("Eventname")+"' and username = '"+s.getString("username",null)+"'",null);
        cursor.moveToFirst();
        Cursor cursor1 = db.rawQuery("Select * from participant where participant_id = '"+cursor.getInt(cursor.getColumnIndex("id"))+"'and username = '"+s.getString("username",null)+"'",null);
        cursor1.moveToFirst();
        name.setText(name.getText().toString()+cursor.getString(cursor.getColumnIndex("name")));
        date.setText(date.getText().toString()+cursor.getString(cursor.getColumnIndex("date")));
        total_participants.setText(total_participants.getText().toString()+cursor.getString(cursor.getColumnIndex("noofparticipants")));
        participated.setText(participated.getText().toString()+cursor1.getCount());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
                builder.setMessage("You want to delete this event and participants ").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              db.execSQL("Delete from Event where name = '"+cursor.getString(cursor.getColumnIndex("name"))+"'and username = '"+s.getString("username",null)+"'");
                              db.execSQL("Delete from participant where participant_id = '"+cursor.getInt(cursor.getColumnIndex("id"))+"' and username = '"+s.getString("username",null)+"'");
                                Toast.makeText(EventDetails.this, "This Event is Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EventDetails.this,MainActivity.class);
                                startActivity(intent);
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
        });


        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this,Participants.class);
                intent.putExtra("nameevent",cursor.getString(cursor.getColumnIndex("name")));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_participants) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
            final EditText textview = new EditText(EventDetails.this);
            textview.setHint("Total Participants");
            builder.setView(textview);
            builder.setMessage("Change your Total Participants").setCancelable(false)
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.execSQL("Update Event set noofparticipants='"+Integer.parseInt(textview.getText().toString())+"' where username = '"+s.getString("username",null)+"'");
                            Intent i = new Intent(EventDetails.this,EventDetails.class);
                            i.putExtra("Eventname",intent.getStringExtra("Eventname"));
                            startActivity(i);
                            finish();
                            Toast.makeText(EventDetails.this, "Total Participants Changed", Toast.LENGTH_SHORT).show();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
