package com.fithe.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhn.android.naverlogin.OAuthLogin;

import com.fithe.login.loginandroid.R;

public class logoutActivity extends AppCompatActivity {
    Button logoutBtn,logoutBtn1;
    TextView textview;
    private FirebaseAuth mAuth;
    private static Context mContext;
    private static OAuthLogin mOAuthLoginInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        mContext = this;
        logoutBtn = (Button)findViewById(R.id.logout_btn);
        logoutBtn1 = (Button)findViewById(R.id.login_btn1);
        mAuth = FirebaseAuth.getInstance();
        textview = (TextView)findViewById(R.id.textView5);
        mOAuthLoginInstance = OAuthLogin.getInstance();
        if(mOAuthLoginInstance.getAccessToken(mContext)!=null){
            Intent intent = getIntent();
            textview.setText(intent.getExtras().getString("data"));
        }else if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+user.getEmail());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+user.getDisplayName());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+user.getPhoneNumber());
        }


//        Intent intent = getIntent();
//        textview.setText(intent.getExtras().getString("data"));

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });

        logoutBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    mOAuthLoginInstance = OAuthLogin.getInstance();
                    mOAuthLoginInstance.logout(mContext);
                    NaversignOut();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });



    }

    //구글 로그아웃
    private void signOut(){
        FirebaseAuth.getInstance().signOut();

    }
    //네이버 로그아웃
    private void NaversignOut(){
        new DeleteTokenTask(mContext,mOAuthLoginInstance).execute();
        Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 네이버 토큰삭제
    public static class DeleteTokenTask extends AsyncTask<Void, Void, Boolean> {
        private final Context mContext;
        private final OAuthLogin mOAuthLoginModule;
        public DeleteTokenTask(Context mContext, OAuthLogin mOAuthLoginModule) {
            this.mContext = mContext;
            this.mOAuthLoginModule = mOAuthLoginModule;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("", "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
                Log.d("", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
            }

            return isSuccessDeleteToken;
        }

        protected void onPostExecute(boolean isSuccessDeleteToken) {
        }
    }
}