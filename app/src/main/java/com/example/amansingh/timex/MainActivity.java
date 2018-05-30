package com.example.amansingh.timex;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView username,phone;
    SQLiteDatabase db;
    SharedPreferences s;
    ListView listView;
    String newphone;
    int phonestatus,permission,otp_value;
    ArrayList<String>arrayList,phonelist;
    ArrayAdapter<String>arrayAdapter;
    ImageView image,userimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        userimage = (ImageView)findViewById(R.id.userimage);
        listView = (ListView)findViewById(R.id.detailslist);
        listView.getBackground().setAlpha(180);
        arrayList = new ArrayList<String>();
        phonelist = new ArrayList<String>();
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        db = this.openOrCreateDatabase("accounts",Context.MODE_PRIVATE,null);
        db.execSQL("Create table if not exists details(name VARCHAR,username VARCHAR,image blob)");
        Cursor cursor = db.rawQuery("Select * from details where username = '"+s.getString("username",null)+"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()==0)
        {
            ContentValues cv = new ContentValues();
            cv.putNull("image");
            cv.put("username",""+s.getString("username",null));
            db.insert("details",null,cv);
            Toast.makeText(this, "Details Inserted", Toast.LENGTH_SHORT).show();
        }
        else {
            if (cursor.getBlob(cursor.getColumnIndex("image")) != null) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                userimage.setImageBitmap(getImage(image));
            }
        }
        phone = (TextView)header.findViewById(R.id.phonenumber);
        username = (TextView)header.findViewById(R.id.user_name);
        image = (ImageView)header.findViewById(R.id.imageuser);
        username.setText("Welcome "+s.getString("username",null));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},123);
            permission = 0;
        }
        else
        {
            permission = 1;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0: final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final EditText textview = new EditText(MainActivity.this);
                        textview.setHint("Enter Full Name");
                        builder.setView(textview);
                        builder.setMessage("Change your Name").setCancelable(false)
                                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (textview.getText().length() != 0)
                                        {
                                            s.edit().putInt("status",1).apply();
                                            db.execSQL("Update details set name = '"+textview.getText().toString()+"' where username = '"+s.getString("username",null)+"'");
                                            Toast.makeText(MainActivity.this, "Name Changed", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(MainActivity.this,MainActivity.class);
                                            startActivity(i);
                                            finish();
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
                        break;

                    case 1: final AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        final EditText textview2 = new EditText(MainActivity.this);
                        textview2.setHint("Enter New Number");
                        builder2.setView(textview2);
                        builder2.setMessage("Change Your Phone Number").setCancelable(false)
                                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(permission == 1) {
                                            Cursor cursor = db.rawQuery("Select * from user", null);
                                            cursor.moveToFirst();
                                            do {
                                                phonelist.add(cursor.getString(cursor.getColumnIndex("phone")));
                                            }
                                            while (cursor.moveToNext());
                                            for (int i = 0; i < phonelist.size(); i++) {
                                                phonestatus = 1;
                                                if (textview2.getText().toString().length() != 0) {
                                                    if (textview2.getText().toString().equals(phonelist.get(i))) {
                                                        Toast.makeText(MainActivity.this, "Phone already Registered", Toast.LENGTH_SHORT).show();
                                                        phonestatus = 0;
                                                        break;
                                                    }
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Input a correct format number", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if (phonestatus == 1)
                                            {
                                                sendsms(textview2.getText().toString());
                                                final AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                                                final EditText textview3 = new EditText(MainActivity.this);
                                                newphone = textview2.getText().toString();
                                                textview3.setHint("Enter Otp");
                                                builder3.setView(textview3);
                                                builder3.setMessage("Enter OTP to regsiter phone").setCancelable(false).setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                         if(textview3.getText().toString().equals(String.valueOf(otp_value)))
                                                         {
                                                             db.execSQL("Update user set phone = '"+newphone+"' where user = '"+s.getString("username",null)+"'");
                                                             Toast.makeText(MainActivity.this, "Phone Number Changed", Toast.LENGTH_SHORT).show();
                                                             Intent i = new Intent(MainActivity.this,MainActivity.class);
                                                             startActivity(i);
                                                         }
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                AlertDialog alert3 = builder3.create();
                                                alert3.setTitle("Confirm");
                                                alert3.show();
                                            }
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "SMS Permission is not granted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert2 = builder2.create();
                        alert2.setTitle("Confirm");
                        alert2.show();
                        break;

                     default:
                         break;
                }
            }
        });
        Cursor c = db.rawQuery("Select * from user where user = '"+s.getString("username",null)+"'",null);
        c.moveToFirst();
        phone.setText(""+c.getString(c.getColumnIndex("phone")));
        Cursor cursor1 = db.rawQuery("select * from slidertimetable where username = '"+s.getString("username",null)+"'",null);
        Cursor cursor2 = db.rawQuery("select * from Event where username = '"+s.getString("username",null)+"'",null);
        Cursor cursor3 = db.rawQuery("select * from stopwatch1 where username = '"+s.getString("username",null)+"'",null);
        if(s.getInt("status",0)!=1) {
            arrayList.add("Full Name - ");
        }
        else
        {
            arrayList.add("Full Name - "+cursor.getString(cursor.getColumnIndex("name")));
        }
        arrayList.add("Phone - "+c.getString(c.getColumnIndex("phone")));
        arrayList.add("Number of events - "+cursor2.getCount());
        arrayList.add("Number of Slider Timers - "+cursor1.getCount());
        arrayList.add("Number of Stopwatch - "+cursor3.getCount());

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                   startActivityForResult(i,1);
            }
        });


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        FragmentManager fragment = getFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        } else
        if (id == R.id.nav_first) {
            fragment.beginTransaction()
            .replace(R.id.content_frame , new First())
                    .commit();
  
        } else if (id == R.id.nav_second) {
            fragment.beginTransaction()
                    .replace(R.id.content_frame , new Second()).commit();

        } else if (id == R.id.nav_third) {
            fragment.beginTransaction()
                    .replace(R.id.content_frame , new Third()).commit();

        } else if (id == R.id.logout)
        {
               Intent i = new Intent(MainActivity.this,Login.class);
               s.edit().putInt("autologin",0).apply();
               startActivity(i);
               finish();
        }
        else if (id == R.id.changepass)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final EditText textview = new EditText(MainActivity.this);
            textview.setHint("Enter New Password");
            builder.setView(textview);
            builder.setMessage("Change your Password ! ").setCancelable(false)
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.execSQL("Update user set pass = '"+textview.getText().toString()+"' where user = '"+s.getString("username",null)+"'");
                            Toast.makeText(MainActivity.this, "Your Password Changed, Login Again", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this,Login.class);
                            s.edit().putInt("autologin",0).apply();
                            startActivity(i);
                            finish();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                userimage.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imagearray = stream.toByteArray();
                ContentValues cv = new ContentValues();
                cv.put("image",imagearray);
                db.update("details",cv,"username='"+s.getString("username",null)+"'",null);
                Toast.makeText(this, "Image Added", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission is granted here", Toast.LENGTH_SHORT).show();
                permission = 1;
            }
            else {
                permission = 0;
            }
        }
    }
    private void sendsms(String phone)
    {
        Random r = new Random();
        otp_value = 100000+r.nextInt(900000);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone,null,"Please Confirm your number to complete the regustration. Enter the OTP : "+otp_value,null,null);
    }
}
