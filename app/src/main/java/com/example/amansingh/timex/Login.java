package com.example.amansingh.timex;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    Intent i;
    SharedPreferences s;
    EditText username, password;
    Button login, register;
    String user, pass;
    TextView forgotpass;
    SQLiteDatabase database;
    int count,permission;
    String passvalue;
    List<String> USER, PASS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        s = this.getSharedPreferences("Autologin", Context.MODE_PRIVATE);
        if(s.getInt("autologin",0)==1)
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        database = this.openOrCreateDatabase("accounts", Context.MODE_PRIVATE, null);
        forgotpass = (TextView)findViewById(R.id.forgotpass);
        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.signup);

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
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                final EditText textview = new EditText(Login.this);
                textview.setHint("Enter the username");
                builder.setView(textview);
                builder.setMessage("Forgot your Password?").setCancelable(false)
                        .setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(permission == 1)
                                {
                                    if(textview.getText().toString().length()!=0) {
                                        Cursor cursor = database.rawQuery("Select * from user where user = '" + textview.getText().toString() + "'", null);
                                        cursor.moveToFirst();
                                        if (cursor.getCount() != 0) {
                                            passvalue = cursor.getString(cursor.getColumnIndex("pass"));
                                            sendsms(passvalue,cursor.getString(cursor.getColumnIndex("phone")));
                                            Toast.makeText(Login.this, "SMS Sent to your registered number", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(Login.this, "Username is not correct", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(Login.this, "Input a correct value", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Login.this, "SMS permission is not enabled", Toast.LENGTH_SHORT).show();
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
        });

        username.getBackground().setAlpha(190);
        password.getBackground().setAlpha(190);
        login.getBackground().setAlpha(160);
        register.getBackground().setAlpha(160);
        USER = new ArrayList<String>();
        PASS = new ArrayList<String>();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login()
    {
            try {
                Cursor cursor = database.rawQuery("Select * from user", null);
                cursor.moveToFirst();
                do
                {
                    USER.add(cursor.getString(cursor.getColumnIndex("user")));
                    PASS.add(cursor.getString(cursor.getColumnIndex("pass")));
                }
                while(cursor.moveToNext());
                for (int i = 0; i < USER.size(); i++)
                {
                    if (user.equals(USER.get(i))) {
                        count = 0;
                        if (pass.equals(PASS.get(i)))
                        {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                            count = 1;
                            s.edit().putInt("autologin",1).apply();
                            s.edit().putString("username",user).apply();
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        } else {
                            Toast.makeText(this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                            count = 1;
                             break;
                        }
                    }
                }
                if (count != 1) {
                    Toast.makeText(this, "User Does not Exist", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
    private void sendsms(String password,String phone)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone,null,"Your Password is "+password+" Kindly delete this message after use for account security",null,null);
    }
}