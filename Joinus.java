package com.project.whichwitch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Joinus extends AppCompatActivity {
    Button joinus_joinus_btn;
    Button joinus_login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinus);
        joinus_joinus_btn=(Button)findViewById(R.id.joinus_joinus_btn);
        joinus_joinus_btn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     AlertDialog.Builder dlg = new AlertDialog.Builder(Joinus.this);
                     dlg.setTitle("알림");
                     dlg.setMessage("회원가입이 완료되었습니다.");
                     dlg.setPositiveButton("확인", null);
                     dlg.show();
                 }
             }
        );

        joinus_login_btn=(Button)findViewById(R.id.joinus_login_btn);
        joinus_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}