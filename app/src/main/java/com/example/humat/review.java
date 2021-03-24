package com.example.humat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class review extends AppCompatActivity {
    // 리뷰 객객
    private DatabaseReference mDatabase;

    ListView dataLST;
    ArrayList<String> arrData;
    ArrayAdapter<String> adapter;

    private String Store_name;
    EditText comment;
    private String comt = "";
    private String review = "reviews";

    String user_name = "";
    String date_time = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        Log.i("review", "onCreate");
        setContentView(R.layout.review_layout);

        Intent rxIntent = getIntent();
        Store_name = rxIntent.getStringExtra("store_name");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        dataLST = findViewById(R.id.list_data);
        arrData = new ArrayList<String>();
        comment = findViewById(R.id.comment);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrData);

        get_user_name();
        get_date_time();

    }

    public void get_date_time() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss");
        date_time = simpleDateFormat.format(mDate);
    }

    public void get_user_name() {           //이름받아온다
        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//
                } else {

                    UserDTO userDTO = task.getResult().getValue(UserDTO.class);
                    user_name = userDTO.getName();
                }
            }
        });
    }


    public void onClick(View v) {
        if (v.getId() == R.id.add_review) {
            if (comment.getText().length() == 0) {
                Toast.makeText(review.this, "리뷰를 작성해주세요.", Toast.LENGTH_LONG).show();
            } else {
                comt = comment.getText().toString();
                RecommenderDTO recommender = new RecommenderDTO(user_name, date_time, comt);
                mDatabase.child(review).child(Store_name).push().setValue(recommender);
                Toast.makeText(review.this, "리뷰가 작성되었습니다.", Toast.LENGTH_LONG).show();

                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        }

        if (v.getId() == R.id.show_view) {

            mDatabase.child(review).child(Store_name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {

                    } else {
                        if (String.valueOf(task.getResult().getValue()).equals("null"))
                        {
                            Toast.makeText(review.this, "리뷰가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {

                            String data = String.valueOf(task.getResult().getValue());
                            data = data.replace("{", "");
                            data = data.replace("}", "");
                            String tmp = "";
                            String[] dat = data.split(",");

                            for (int i = 0; i < dat.length; i += 3) {
                                tmp += dat[i + 1];
                                tmp += "\n";
                                tmp += dat[i + 2];
                                tmp += "\n\n\n";
                            }

                            String dat2 = tmp.replace("name=", "");
                            dat2 = dat2.replace("comment=", "");

                            arrData.clear();
                            arrData.add(dat2);

                            dataLST.setAdapter(adapter);
                        }
                    }
                }
            });

        }

    }
}