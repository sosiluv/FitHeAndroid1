package com.fithe.login.mail;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fithe.login.LoginActivity;
import com.fithe.login.common.HttpClient;
import com.fithe.login.common.Users;
import com.fithe.login.common.Web;
import com.fithe.login.mail.GMailSender;
import com.google.gson.Gson;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendMail extends AppCompatActivity {
    String user="sosiluv@gmail.com"; // 보내는 계정의id
    String password="@Qrltkdwkd12"; // 보내는 계정의 pw

    public void sendSecurityCode(Context context,String pwd, String sendTo) {
        try { GMailSender gMailSender = new GMailSender(user, password);
            gMailSender.sendMail("비밀번호 찾기", "당신의 비밀번호는 "+pwd+"입니다.", sendTo);
            Toast.makeText(context, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(context, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

