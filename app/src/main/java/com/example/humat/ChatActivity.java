package com.example.humat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.animation.content.Content;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Context;

public class ChatActivity extends Fragment {
    private Context ct;
    private EditText et;
    private ListView listView;
    private Button sendBTN;

    private ArrayList<MessageItem> messageItems=new ArrayList<>();
    private ChatAdapter adapter;

    private DatabaseReference mDatabase;

    //Firebase Database 관리 객체참조변수
    private FirebaseDatabase firebaseDatabase;

    //'chat'노드의 참조객체 참조변수
    private DatabaseReference chatRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ChatActivity", "onCreateView()");

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        View v = inflater.inflate(R.layout.activity_chat, container, false);
        ct = container.getContext();

        // 제목줄 제목글시를 닉네임으로(또는 채팅방)
        //ct.getSupportActionBar().setTitle(G.nickName);

        et=v.findViewById(R.id.et);
        listView=v.findViewById(R.id.listview);
        adapter=new ChatAdapter(messageItems,getLayoutInflater());
        listView.setAdapter(adapter);
        sendBTN = v.findViewById(R.id.sendBTN);

        //Firebase DB관리 객체와 'caht'노드 참조객체 얻어오기
        firebaseDatabase= FirebaseDatabase.getInstance();
        chatRef= firebaseDatabase.getReference("chat");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//
                } else {

                    UserDTO userDTO = task.getResult().getValue(UserDTO.class);
                    Fibase.USER_NAME = userDTO.getName();
                }
            }
        });



        //firebaseDB에서 채팅 메세지들 실시간 읽어오기..
        //'chat'노드에 저장되어 있는 데이터들을 읽어오기
        //chatRef에 데이터가 변경되는 것으 듣는 리스너 추가
        chatRef.addChildEventListener(new ChildEventListener() {
            //새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                MessageItem messageItem= dataSnapshot.getValue(MessageItem.class);

                //새로운 메세지를 리스뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem);

                //리스트뷰를 갱신
                adapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size()-1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // button 리스너
        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //firebase DB에 저장할 값들( 닉네임, 메세지, 프로필 이미지URL, 시간)
                String nickName= Fibase.USER_NAME;
                String message= et.getText().toString();
                //String pofileUrl= G.porfileUrl;

                //메세지 작성 시간 문자열로..
                Calendar calendar= Calendar.getInstance(); //현재 시간을 가지고 있는 객체
                String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE); //14:16

                //firebase DB에 저장할 값(MessageItem객체) 설정
                MessageItem messageItem= new MessageItem(nickName,message,time);
                //'char'노드에 MessageItem객체를 통해
                chatRef.push().setValue(messageItem);

                //EditText에 있는 글씨 지우기
                et.setText("");

                //소프트키패드를 안보이도록..
        InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);

                //처음 시작할때 EditText가 다른 뷰들보다 우선시 되어 포커스를 받아 버림.
                //즉, 시작부터 소프트 키패드가 올라와 있음.

                //그게 싫으면...다른 뷰가 포커스를 가지도록
                //즉, EditText를 감싼 Layout에게 포커스를 가지도록 속성을 추가!![[XML에]
            }
        });

        return v;
    }
}