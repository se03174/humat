package com.example.humat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {
    private boolean is_inient = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(is_inient == false)
                {
                    Intent intent = new Intent(FirstActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, 1500);// 0.6초 정도 딜레이를 준 후 시작
    }

    public void onClick(View v)
    {
        is_inient = true;
        Intent intent = new Intent(FirstActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}