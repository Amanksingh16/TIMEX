package com.example.amansingh.timex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Participants extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String>arrayList;
    SQLiteDatabase db;
    SharedPreferences s;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        intent = getIntent();
        listView = (ListView)findViewById(R.id.participants_list);
        arrayList = new ArrayList<String>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                 Intent i = new Intent(Participants.this,Participantsdetails.class);
                 i.putExtra("partname",String.valueOf(listView.getItemAtPosition(position)).substring(3,String.valueOf(listView.getItemAtPosition(position)).length()-1));
                i.putExtra("evenname",""+intent.getStringExtra("nameevent"));
                 startActivity(i);
            }
        });
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);

        Cursor cursor = db.rawQuery("Select * from Event where name = '"+intent.getStringExtra("nameevent")+"' and username = '"+s.getString("username",null)+"'",null);
        cursor.moveToFirst();
        Cursor cursor1 = db.rawQuery("Select * from participant where participant_id = '"+cursor.getInt(cursor.getColumnIndex("id"))+"'and username = '"+s.getString("username",null)+"'",null);
        cursor1.moveToFirst();
        do {
            if (cursor1.getCount() != 0) {
                arrayList.add("\n  " + cursor1.getString(cursor1.getColumnIndex("name")) + "\n");
            }
            else
            {
                Toast.makeText(this, "No Values are there", Toast.LENGTH_SHORT).show();
            }
        }
        while(cursor1.moveToNext());
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
