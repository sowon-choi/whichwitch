package com.project.whichwitch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
        final static FirebaseAuth auth=FirebaseAuth.getInstance();;
        final static FirebaseFirestore db=FirebaseFirestore.getInstance();
        //한번 로그인시 자동 로그인 시켜주기. -> mysql 사용해서 아이디만 저장.?
        private  boolean saveLoginData;
        private SharedPreferences appData;

        EditText login_email_edittext, login_pw_edittext;
        Button login_login_btn, login_joinus_btn, login_findemail_btn, login_findpw_btn;
        TextView login_check_text;

        private String email="";
        private String password="";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            appData = getSharedPreferences("appData",MODE_PRIVATE);
            load();

            login_email_edittext=(EditText)findViewById(R.id.login_email_edittext);
            login_pw_edittext=(EditText)findViewById(R.id.login_pw_edittext);

            login_check_text=(TextView)findViewById(R.id.login_check_text);

            login_login_btn=(Button)findViewById(R.id.login_login_btn);
            login_login_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    email=login_email_edittext.getText().toString();
                    password=login_pw_edittext.getText().toString();
                    if(isVallidEmail()&&isValidPassword()) {
                        loginUser(email, password);


                    }
                }
            });

            login_joinus_btn=(Button)findViewById(R.id.login_joinus_btn);
            login_joinus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(), Joinus.class);
                    startActivity(intent);
                }
            });

            login_findemail_btn=(Button)findViewById(R.id.login_findemail_btn);
            login_findpw_btn=(Button)findViewById(R.id.login_findpw_btn);

            if(saveLoginData){
                login_email_edittext.setText(email);
                login_pw_edittext.setText(password);

                if(isVallidEmail()&&isValidPassword()) {
                    loginUser(email, password);
                }
            }

        }
        private void save(){
            SharedPreferences.Editor editor = appData.edit();
            //에디터 객체.put 타입 (저장시킬 이름, 저장시킬 값.
            //저장 시킬 이름이 이미 존재하면 덮어 씌운다.

            editor.putString("Email",login_email_edittext.getText().toString().trim());
            editor.putString("PWD",login_pw_edittext.getText().toString().trim());
            editor.apply();
            //apply, commit을 안하면 변경된 내용이 저장되지 않음.
        }
        private  void load(){
            saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA",true);
            email=appData.getString("Email","");
            password=appData.getString("PWD","");

        }
        private void loginUser(String email, String password)
        {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                login_check_text.setText("");
                                save();
                             //   Toast.makeText(MainActivity.this, "로그인 성공! 왜 팅기니!", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(),Viewlist.class); // 할일리스트 화면으로 변경
                                startActivity(intent);

                            } else {
                                login_check_text.setText("없는 이메일이거나 비밀번호가 틀렸습니다.");
                                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    private boolean isVallidEmail(){
        if (email.isEmpty()) {
            login_check_text.setText("아이디를 작성해주세요.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_check_text.setText("올바른 이메일 형식이 아닙니다.");
            return false;
        } else {
            login_check_text.setText("");
            return true;
        }
    }

        private boolean isValidPassword() {
            if (password.isEmpty()) {
                login_check_text.setText("비밀번호를 적어주세요");
                return false;
            } else {
                login_check_text.setText("");
                return true;
            }
        }
    }