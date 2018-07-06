package com.vrushabh.technews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
     ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        ImageView imageView=(ImageView)findViewById(R.id.img);

    }
}
