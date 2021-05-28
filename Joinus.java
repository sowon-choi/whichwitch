package com.project.whichwitch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Joinus extends AppCompatActivity {
    static FirebaseAuth auth=MainActivity.auth;
    static FirebaseFirestore db=MainActivity.db;


    Button joinus_joinus_btn, joinus_login_btn;
    EditText joinus_name_edittext, joinus_email_edittext, joinus_pw_edittext, joinus_pwcheck_edittext, joinus_phone_edittext;
    TextView joinus_emailcheck_text, joinus_pwcheck_text, joinus_pwpatterncheck_text;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    private String email="";
    private String password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinus);

        joinus_name_edittext=(EditText)findViewById(R.id.joinus_name_edittext);
        joinus_email_edittext=(EditText)findViewById(R.id.joinus_email_edittext);
        joinus_pw_edittext=(EditText)findViewById(R.id.joinus_pw_edittext);
        joinus_pwcheck_edittext=(EditText)findViewById(R.id.joinus_pwcheck_edittext);
        joinus_phone_edittext=(EditText)findViewById(R.id.joinus_phone_edittext);

        joinus_emailcheck_text=(TextView)findViewById(R.id.joinus_emailcheck_text);
        joinus_pwpatterncheck_text=(TextView)findViewById(R.id.joinus_pwpatterncheck_text);
        joinus_pwcheck_text=(TextView)findViewById(R.id.joinus_pwcheck_text);

        joinus_joinus_btn=(Button)findViewById(R.id.joinus_joinus_btn);
        joinus_joinus_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                email=joinus_email_edittext.getText().toString();
                password=joinus_pw_edittext.getText().toString();

                if(isVallidEmail()&&isValidPassword()&&isEqualPassword()){
                    try{
                        createUser(email,password);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        joinus_login_btn=(Button)findViewById(R.id.joinus_login_btn);
        joinus_login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private boolean isVallidEmail(){
        if (email.isEmpty()) {
            joinus_emailcheck_text.setText("올바른 이메일 형식이 아닙니다.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            joinus_emailcheck_text.setText("올바른 이메일 형식이 아닙니다.");
            return false;
        } else {
            joinus_emailcheck_text.setText("");
            return true;
        }
    }

    private boolean isValidPassword() {
        if (password.isEmpty()) {
            joinus_pwpatterncheck_text.setText("영문자, 숫자, 특수기호 포함 4~16글자");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            joinus_pwpatterncheck_text.setText("영문자, 숫자, 특수기호 포함 4~16글자");
            return false;
        } else {
            joinus_pwpatterncheck_text.setText("");
            return true;
        }
    }

    private boolean isEqualPassword() {
        if(joinus_pw_edittext.getText().toString().equals(joinus_pwcheck_edittext.getText().toString())){
            joinus_pwcheck_text.setText("");
            return true;
        }else{
            joinus_pwcheck_text.setText("비밀번호 불일치");
            return false;
        }
    }

    private void createUser(String email,String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", joinus_name_edittext.getText().toString());
                            user.put("email", joinus_email_edittext.getText().toString());
                            // user.put("pw", joinus_pw_edittext.getText().toString());
                            user.put("phone", joinus_phone_edittext.getText().toString());

                            db.collection("collection").document(joinus_email_edittext.getText().toString())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog.Builder dlg = new AlertDialog.Builder(Joinus.this);
                                            dlg.setTitle("알림");
                                            dlg.setMessage("회원가입이 완료되었습니다.");
                                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            dlg.show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Joinus.this, "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(Joinus.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}