package com.estimote.proximitycontent.log;

import com.estimote.proximitycontent.model.LogModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by PEEPO on 25/3/2561.
 */

public class MyLog {
    private String action;
    private String email;

    //init database and calendar
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("log");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");

    public MyLog(String action, String email) {
        this.action = action;
        this.email = email;
    }

    public void addLog(){
        myRef.child(myRef.push().getKey()).setValue(new LogModel(
                String.valueOf(email),
                String.valueOf(df.format(calendar.getTime())),
                String.valueOf(tf.format(calendar.getTime())),
                this.action));
    }

}
