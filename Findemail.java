package com.project.whichwitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Findemail extends AppCompatActivity {
    static FirebaseAuth auth=MainActivity.auth;
    static FirebaseFirestore db=MainActivity.db;

    Button findid_checkemail_btn, findid_login_btn;
    EditText findid_name_edittext, findid_phone_edittext;

    private String name="";
    private String phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findemail);

        findid_name_edittext=(EditText)findViewById(R.id.findid_name_edittext);
        findid_phone_edittext=(EditText)findViewById(R.id.findid_phone_edittext);

        name=findid_name_edittext.getText().toString();
        phone=findid_phone_edittext.getText().toString();

        findid_checkemail_btn=(Button)findViewById(R.id.findid_checkemail_btn);
        findid_checkemail_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 이메일 Dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(Findemail.this);
                dlg.setTitle("E-Mail을 확인해주세요");
                dlg.setMessage("gydus@naver.com"); // E-Mail 받아와서 변수로 넣기
                dlg.setPositiveButton("확인", null);   // null -> Intent
                dlg.show();
            }
        });

        findid_login_btn=(Button)findViewById(R.id.findid_login_btn);
        findid_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}