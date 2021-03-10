package com.fithe.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.fithe.login.common.HttpClient;
import com.fithe.login.common.Users;
import com.fithe.login.common.Web;
import com.fithe.login.loginandroid.R;

public class regActivity extends AppCompatActivity {

    Button ckbtn, regbtn;
    EditText mid, mpw, memail;
    ImageButton imageButton;
    RadioGroup gender;
    RadioButton select;

    // 현재시간 초기화
    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        mid = (EditText) findViewById(R.id.idText);
        mpw = (EditText) findViewById(R.id.passwordText);
        memail = (EditText) findViewById(R.id.emailText);

        gender = (RadioGroup) findViewById(R.id.genderGroup);

        ckbtn = (Button) findViewById((R.id.validateButton));
        regbtn = (Button) findViewById(R.id.registerButton);
        imageButton = (ImageButton) findViewById(R.id.imageButton);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    select = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
                    System.out.println("select>>>>>>>>>" + select.getText());
                    Map<String, String> map = new HashMap<>();
                    map.put("id", mid.getText().toString());
                    map.put("pwd", mpw.getText().toString());
                    map.put("email", memail.getText().toString());
                    map.put("gender", select.getText().toString());

                    MapTaskInsert task = new MapTaskInsert();
                    task.execute(map);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ckbtn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         try {
                                             String result;
                                             String id = mid.getText().toString();
                                             System.out.println("id >>> : " + id);
                                             Map<String, String> map = new HashMap<>();
                                             map.put("id", id);
                                             TaskidCheck task = new TaskidCheck();
                                             task.execute(map);


                                         } catch (Exception e) {

                                         }
                                     }
                                 }
        );
    }

    // http통신--------------------------------------------------
    public class MapTaskInsert extends AsyncTask<Map, Integer, String> {

        //doInBackground 전에 동작
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //작업을 쓰레드로 처리
        @Override
        protected String doInBackground(Map... maps) {
            //HTTP 요청 준비

            HttpClient.Builder http = new HttpClient.Builder("POST", Web.servletURL + "androidInsert.do");

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

        if (user != null && user.getEnabled() != 0) {
            Toast.makeText(getApplicationContext(), "회원가입성공", Toast.LENGTH_SHORT).show();
            Intent loginintent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginintent);

        } else {
            Toast.makeText(getApplicationContext(), "회원가입실패.", Toast.LENGTH_SHORT).show();
        }
    }

}

    public class TaskidCheck extends AsyncTask<Map, Integer, String> {

        //doInBackground 전에 동작
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //작업을 쓰레드로 처리
        @Override
        protected String doInBackground(Map... maps) {
            //HTTP 요청 준비

            HttpClient.Builder http = new HttpClient.Builder("POST", Web.servletURL + "idCheck.do");

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
            System.out.println("user.getIdCheck()>>>>>>>>>>>>>>>>>>>>>"+user.getIdCheck());
            if (user.getIdCheck() == 0) {
                Toast.makeText(getApplicationContext(), "아이디 사용가능", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "아이디 중복.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //뒤로가기 버튼 두번 누르면 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}