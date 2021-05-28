package com.project.whichwitch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Mypage extends AppCompatActivity {
    TextView mypage_name_text, mypage_email_text, mypage_newpw_text, mypage_newpwcheck_text, mypage_phone_text;
    EditText mypage_newpw_edittext, joinus_newpwcheck_edittext;
    Button mypage_cngpw_btn, mypage_back_btn;
    static FirebaseFirestore db=MainActivity.db;
    static FirebaseAuth auth=MainActivity.auth;
    String myemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        mypage_name_text=(TextView)findViewById(R.id.mypage_name_text);
        mypage_email_text=(TextView)findViewById(R.id.mypage_email_text);
        mypage_newpw_text=(TextView)findViewById(R.id.mypage_newpw_text);
        mypage_newpwcheck_text=(TextView)findViewById(R.id.mypage_newpwcheck_text);
        mypage_phone_text=(TextView)findViewById(R.id.mypage_phone_text);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myemail=user.getEmail();
            mypage_email_text.setText(myemail);
        }
        DocumentReference docRef = db.collection("collection").document(myemail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                 //   Toast.makeText(Mypage.this, "Cached document data: " + , Toast.LENGTH_SHORT).show();
                    Map<String, Object> data = document.getData();
                    mypage_name_text.setText(data.get("name").toString());
                    mypage_phone_text.setText(data.get("phone").toString());
                } else { //검색 실패

                }
            }
        });
    }
}