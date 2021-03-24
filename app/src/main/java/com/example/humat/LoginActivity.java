package com.example.humat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button mLoginBtn;
    TextView mResigettxt;
    EditText mEmailText, mPasswordText;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        imm= (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        Fibase.FIREBASEAUTH =  FirebaseAuth.getInstance();

        //버튼 등록하기
        mResigettxt = findViewById(R.id.Join_BTN);
        mLoginBtn = findViewById(R.id.Login_BTN);
        mEmailText = findViewById(R.id.Passck_ETXT);
        mPasswordText = findViewById(R.id.Pass_ETXT);
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT)
                {
                    imm.hideSoftInputFromWindow(mPasswordText.getWindowToken(),0);
                }
                else if(actionId== EditorInfo.IME_ACTION_DONE)
                {
                    imm.hideSoftInputFromWindow(mPasswordText.getWindowToken(),0);
                }
                else if(actionId== EditorInfo.IME_NULL)
                {
                    imm.hideSoftInputFromWindow(mPasswordText.getWindowToken(),0);
                }
                return true;
            }
        });

        //가입 버튼이 눌리면
        mResigettxt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //intent함수를 통해 register액티비티 함수를 호출한다.
                startActivity(new Intent(LoginActivity.this, Join.class));

            }
        });

        //로그인 버튼이 눌리면
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mEmailText.getText().length() == 0 || mPasswordText.getText().length() == 0){
                    Toast.makeText(LoginActivity.this, "이메일, 비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show();
                }else {
                    String email = mEmailText.getText().toString().trim();
                    String pwd = mPasswordText.getText().toString().trim();
                    Fibase.FIREBASEAUTH.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 닉네임 가져오기
                                        G.nickName = mEmailText.getText().toString();

                                        // 내 phone에 nickname 저장
                                        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putString("nickName", G.nickName);
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, Gps.class);
                                        startActivity(intent);
                                        mEmailText.setText("");
                                        mPasswordText.setText("");
                                        finish();

                                    } else {
                                        Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}