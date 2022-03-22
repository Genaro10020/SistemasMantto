package com.example.sitemasmantto;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cambioLogin();
    }

public  void  cambioLogin(){
    Handler handler = new Handler();
    handler.postDelayed(() -> {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
        Log.e("entre","despues de"+1000);
    },2000);
}

}