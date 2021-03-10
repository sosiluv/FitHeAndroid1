package com.fithe.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fithe.login.common.HttpClient;
import com.fithe.login.common.Users;
import com.fithe.login.common.Web;
import com.fithe.login.loginandroid.R;
import com.fithe.login.mail.SendMail;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MailActivity extends AppCompatActivity {

    Button mailButton = null;
    EditText emailText = null;
    ImageButton backbtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
        mailButton = (Button) findViewById(R.id.eamilButton);
        emailText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        backbtn = (ImageButton) findViewById(R.id.imageButton);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> map = new HashMap<>();
                map.put("email", emailText.getText().toString());
                MapTaskfindPwd task = new MapTaskfindPwd();
                task.execute(map);
//                SendMail mailServer = new SendMail();
//                mailServer.sendSecurityCode(getApplicationContext(), emailText.getText().toString());
            }
        });


    }

    public class MapTaskfindPwd extends AsyncTask<Map, Integer, String> {

        //doInBackground 전에 동작
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //작업을 쓰레드로 처리
        @Override
        protected String doInBackground(Map... maps) {
            //HTTP 요청 준비

            HttpClient.Builder http = new HttpClient.Builder("POST", Web.servletURL + "findPw.do");

            //Parameter 전송
            http.addAllParameters(maps[0]);

            //HTTP 요청 전송
            HttpClient post = http.create();
            System.out.println("post>>>>>>>>>>>>>>>>>>>>>>>>>" + maps[0]);
            post.request();

            //응답 상태 코드
            int statusCode = post.getHttpStatusCode();
            System.out.println("statusCode>>>>>>>>>>>>>>>>>>>>>>>>>" + statusCode);

            //응답 본문
            String body = post.getBody(); //Spring의 Controller에서 반환한 값. JSON 형식
            return body;
        }


        /*
            doInBackground 후에 동작.
            String s : doInBackground에서 반환한 body
         */
        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Log.d("JSON_RESULT", s);

            //JSON 형식의 데이터를 Class Object로 바꿔준다.
            Gson gson = new Gson();
            Users user = gson.fromJson(s, Users.class);

            if (user != null && user.getPassword() != null) {
                Toast.makeText(getApplicationContext(), "비밀번호 찾기", Toast.LENGTH_SHORT).show();
                SendMail mailServer = new SendMail();
                mailServer.sendSecurityCode(getApplicationContext(),user.getPassword(), emailText.getText().toString());
                Intent loginintent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginintent);

            } else {
                Toast.makeText(getApplicationContext(), "비밀번호찾기 실패.", Toast.LENGTH_SHORT).show();
            }
        }





    }
    //뒤로가기 버튼 두번 누르면 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}