package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView img=findViewById(R.id.spimg);
        Animation scaleAnimation= AnimationUtils.loadAnimation(this,R.anim.scale_ip);
        img.setAnimation(scaleAnimation);
        new Handler().postDelayed(() ->
        {
            Intent intent =new Intent(SplashAct.this, MainActivity.class);
            startActivity(intent);
            finish();
        },3000);
    }
}