package com.example.amansingh.timex;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Register extends AppCompatActivity {

    EditText username , password , phone , otp;
    Button register,confirm,send;
    SQLiteDatabase db;
    List<String> users,phones;
    int otp_value,status,phonestatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.Register);
        confirm = (Button)findViewById(R.id.confirm);
        send = (Button)findViewById(R.id.send);
        username = (EditText) findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        phone = (EditText)findViewById(R.id.phone);
        otp = (EditText)findViewById(R.id.otp);

        register.getBackground().setAlpha(140);
        confirm.getBackground().setAlpha(140);
        send.getBackground().setAlpha(140);
        username.getBackground().setAlpha(180);
        password.getBackground().setAlpha(180);
        phone.getBackground().setAlpha(180);
        otp.getBackground().setAlpha(180);
        users = new ArrayList<String>();
        phones = new ArrayList<String>();
        db = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT,user VARCHAR , pass VARCHAR, phone VARCHAR)");
        register.setEnabled(false);
        confirm.setEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},123);
            send.setEnabled(false);
        }
        else
        {
            Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
            send.setEnabled(true);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.rawQuery("Select * from user", null);
                cursor.moveToFirst();
                do {
                    if (cursor.getCount() != 0) {
                        phones.add(cursor.getString(cursor.getColumnIndex("phone")));
                    }
                }
                while(cursor.moveToNext());
                for(int i = 0;i < phones.size();i++)
                {
                    phonestatus = 1;
                    if(phone.getText().toString().length()!=0) {
                        if (phone.getText().toString().equals(phones.get(i))) {
                            Toast.makeText(Register.this, "Phone already Registered", Toast.LENGTH_SHORT).show();
                            phonestatus = 0;
                            break;
                        }
                    }
                    else
                    {
                        phonestatus = 0;
                        Toast.makeText(Register.this, "Input a correct format number", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(phonestatus == 1)
                {
                    sendsms();
                    send.setText("SMS Sent");
                    send.setEnabled(false);
                    confirm.setEnabled(true);
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().equals(String.valueOf(otp_value)))
                {
                    confirm.setText("Confirmed");
                    confirm.setEnabled(false);
                    register.setEnabled(true);
                }
                else
                {
                    Toast.makeText(Register.this, "OTP is not Correct", Toast.LENGTH_SHORT).show();
                    send.setEnabled(true);
                    send.setText("Resend");
                    confirm.setEnabled(false);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() >= 4) {
                    if (password.getText().toString().length() >= 8)
                    {
                        register();
                    }
                    else
                    {
                        Toast.makeText(Register.this, "Password must have atleast 8 charactrers", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Register.this, "Username must have atleast 4 charactrers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission is granted here", Toast.LENGTH_SHORT).show();
                send.setEnabled(true);
            }
            else {
                send.setEnabled(false);
            }
        }
    }
    private void sendsms()
    {
        Random r = new Random();
        otp_value = 100000+r.nextInt(900000);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone.getText().toString(),null,"Please Confirm your number to complete the regustration. Enter the OTP : "+otp_value,null,null);
    }

    private void register()
    {
        Cursor cursor = db.rawQuery("Select * from user", null);
        cursor.moveToFirst();
        do
        {
            users.add(cursor.getString(cursor.getColumnIndex("user")));
            Log.i("user",""+users.size());
        }
        while(cursor.moveToNext());
        for(int i = 0;i < users.size();i++)
        {
            status = 1;
            if(username.getText().toString().equals(users.get(i)))
            {
                Toast.makeText(this, "User already Exist", Toast.LENGTH_SHORT).show();
                status = 0;
                break;
            }
        }
        if(status == 1)
        {
            db.execSQL("INSERT into user(user,pass,phone) VALUES('"+username.getText().toString()+"','"+password.getText().toString()+"','"+phone.getText().toString()+"')");
            Toast.makeText(this, "Succesfully Registered", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Register.this,Login.class);
            startActivity(intent);
            finish();
        }
    }
}
