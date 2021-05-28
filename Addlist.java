package com.project.whichwitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Addlist extends AppCompatActivity {
    static FirebaseFirestore db=MainActivity.db;
    static FirebaseAuth auth=MainActivity.auth;

    TextView addlist_location_textview,addlist_radius_textview;
    Spinner addlist_spinner;
    Button addlist_addlist_btn, addlist_dellist_btn,addlist_check_btn,addlist_cancel_btn;

    // spinner_state_str : 알림상태를 str 형태로 받아옴
    String location, myEmail, spinner_state_str;

    // spinner_state_str 값을 통해 1, 2, 3 구별해서 저장
    int radius_value, spinner_state_int;
    double longitude,latitude;
    String location_value;
    RecyclerView addlist_recyclerview;
    LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlist);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myEmail=user.getEmail();
        }

        addlist_recyclerview=findViewById(R.id.addlist_recyclerview);
        linearLayoutManager=new LinearLayoutManager(this);
        addlist_recyclerview.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerAdapter();
        addlist_recyclerview.setAdapter(adapter);

        Intent sintent =getIntent();
        location=sintent.getStringExtra("location");
        radius_value=sintent.getIntExtra("radius_value",30);
        longitude=sintent.getDoubleExtra("longitude",0);
        latitude=sintent.getDoubleExtra("latitude",0);

        addlist_spinner = (Spinner) findViewById(R.id.addlist_spinner);
        addlist_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent.getSelectedItem().toString());

                // spinner_state_str 값이 뭔지 저장
                spinner_state_str = parent.getSelectedItem().toString();

                // spinner_state_str 값에 따라 spinner_state_int 저장
                if(spinner_state_str.equals("반경으로 들어올 때 알림")){
                    spinner_state_int = 1;
                }else if(spinner_state_str.equals("반경에서 나갈 때 알림")){
                    spinner_state_int = 2;
                }else{
                    spinner_state_int = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addlist_location_textview=(TextView)findViewById(R.id.addlist_location_textview);
        addlist_location_textview.setText(location);
        addlist_radius_textview=(TextView)findViewById(R.id.addlist_radius_textview);
        addlist_radius_textview.setText(Integer.toString(radius_value) +"m");

        addlist_addlist_btn=(Button)findViewById(R.id.addlist_addlist_btn); // 할 일 추가
        addlist_addlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText+toggleButton
                Data data = new Data();

                adapter.addItem(data);
                adapter.notifyDataSetChanged();;
            }
        });

        addlist_dellist_btn=(Button)findViewById(R.id.addlist_dellist_btn); // 할 일 삭제
        addlist_dellist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xml에 editText + toggleButton 삭제되면서 삭제하시겠습니까?

            }
        });
        addlist_check_btn=(Button)findViewById(R.id.addlist_check_btn);     // 확인
        addlist_check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adapter에서 listdata 불러오기
                ArrayList<Data> listData = adapter.listData;

                // firestore에 저장할 해쉬맵 만들기
                Map<String,Object> datum = new HashMap<>();
                datum.put("위치", location);  // 위치
                datum.put("반경", radius_value);  // 반경
                if(spinner_state_int==1){ // 알림상태(!!!spinner의 onItemSelected 함수 참고!!!)
                    datum.put("알림상태", 1);
                }else if(spinner_state_int==2){
                    datum.put("알림상태", 2);
                }else{
                    datum.put("알림상태", 3);
                }
                for(Data data: listData){   // 할 일
                    datum.put("할일_"+data.getTodo().getText().toString(),data.getShare().isChecked());
                }

                // document 이름 '위도_경도' 로 저장
                location_value=longitude+"_"+latitude;

                // firestore에 저장
                db.collection("collection").document(myEmail)
                        .collection("todo_list").document(location_value)
                        .set(datum)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Addlist.this, "DB 저장 성공", Toast.LENGTH_LONG);

                                // viewlist 화면으로 이동
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });

        addlist_cancel_btn=(Button)findViewById(R.id.addlist_cancel_btn);   // 취소
        addlist_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 정보 초기화
                finish();
                // mapview 화면으로 이동
            }
        });


    }

    public void onToggleClicked(View v){
        boolean on = ((ToggleButton)v).isChecked();
        if(on){ //비공개

        }else { //공개

        }
    }
}