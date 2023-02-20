package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Thread td = new Thread(){
            @Override
            public void run() {
                try{

                    sleep( 1000 );
                    Intent it= new Intent(getApplicationContext()
                            ,MainActivity.class  );
                    startActivity( it );

                    ActivityCompat.finishAffinity(Welcome.this);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }; td.start();
    }
}
