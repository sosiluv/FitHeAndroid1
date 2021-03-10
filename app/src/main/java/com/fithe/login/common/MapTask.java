package com.fithe.login.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Map;

public class MapTask extends AsyncTask<Map, Integer, String> {

    Context context;

    public MapTask(Context context) {

        this.context = context;

    }
    //doInBackground 전에 동작
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Map... maps) {

        //HTTP 요청 준비
        HttpClient.Builder http = new HttpClient.Builder("POST",  Web.servletURL + "androidSignIn");

        //Parameter 전송
        http.addAllParameters(maps[0]);

        //HTTP 요청 전송
        HttpClient post = http.create();
        post.request();

        //응답 상태 코드
        int statusCode = post.getHttpStatusCode();

        //응답 본문
        String body = post.getBody(); //Spring의 Controller에서 반환한 값. JSON 형식

        return body;
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        Log.d("JSON_RESULT", s);

        //JSON 형식의 데이터를 Class Object로 바꿔준다.
        Gson gson = new Gson();
        Users user = gson.fromJson(s, Users.class);

        if(user != null && user.getEnabled() != 0) {
            Toast.makeText(context, "로그인", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "회원 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

