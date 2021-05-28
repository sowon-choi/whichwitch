package com.project.whichwitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class Viewlist extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    ImageButton viewlist_menu_btn,menu_close_btn,viewlist_plus_btn;
    Button   menu_mypage_btn, menu_friends_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlist);

        drawerLayout = (DrawerLayout)findViewById(R.id.Viewlist);
        drawerView = (View)findViewById(R.id.Menu);

        viewlist_menu_btn = (ImageButton)findViewById(R.id.viewlist_menu_btn);
        viewlist_menu_btn.setOnClickListener(new View.OnClickListener() { // 메뉴 오픈
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        drawerLayout.setDrawerListener(listner);
        drawerView.setOnTouchListener(new View.OnTouchListener() { // 오른쪽으로 슬라이드 했을 때
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        menu_close_btn = (ImageButton)findViewById(R.id.menu_close_btn);
        menu_close_btn.setOnClickListener(new View.OnClickListener() { // 메뉴 닫힘
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });


        menu_mypage_btn = (Button)findViewById(R.id.menu_mypage_btn);
        menu_mypage_btn.setOnClickListener(new View.OnClickListener() { // 내 정보 버튼
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Mypage.class);
                startActivity(intent);
            }
        });

        menu_friends_btn = (Button)findViewById(R.id.menu_friends_btn);
        menu_friends_btn.setOnClickListener(new View.OnClickListener() { // 친구목록 버튼
            @Override
            public void onClick(View view) {
                // 친구목록 메뉴 보여
                Toast.makeText(Viewlist.this, "친구목록 버튼입니다.",Toast.LENGTH_LONG).show();
            }
        });

        viewlist_plus_btn =(ImageButton)findViewById(R.id.viewlist_plus_btn);
        viewlist_plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Mapview.class);
                startActivity(intent);
            }
        });
    }


    DrawerLayout.DrawerListener listner = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) { // 슬라이드 메뉴 오픈

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) { // 슬라이드 메뉴 닫힘

        }

        @Override
        public void onDrawerStateChanged(int newState) { // 슬라이드 메뉴 변화

        }
    };


}