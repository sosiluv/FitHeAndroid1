package com.fithe.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fithe.fragment.NaviMainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.util.HashMap;
import java.util.Map;

import com.fithe.login.common.HttpClient;
import com.fithe.login.common.Users;
import com.fithe.login.common.Web;
import com.fithe.login.loginandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button loginbtn,regbtn,findpw;
    EditText mid, mpw;
    // 현재시간 초기화
    private long backBtnTime = 0;
    //구글 로그인
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    //네이버 로그인
    private static String OAUTH_CLIENT_ID = "aRlA2v6pNfyBOsqQs1uj";
    private static String OAUTH_CLIENT_SECRET = "Owi1dyHutC";
    private static String OAUTH_CLIENT_NAME = "fithe";
    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private OAuthLoginButton mOAuthLoginButton;
    String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.googleButton);
        loginbtn = (Button)findViewById(R.id.login_btn);
        mid = (EditText) findViewById(R.id.register_id);
        mpw = (EditText) findViewById(R.id.register_pw);
        regbtn = (Button)findViewById((R.id.reg_btn));
        findpw = (Button)findViewById(R.id.findidpw);

        //구글
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //네이버
        mContext = this;
        System.out.println("mContext>>>>>>>>>>>>>>>>>>>>>>>>>>" + mContext);
        // 초기화
        initData();


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Map<String, String> map = new HashMap<>();
                    map.put("id", mid.getText().toString());
                    map.put("pwd", mpw.getText().toString());
                    System.out.println(map);
                    MapTask task = new MapTask();
                    task.execute(map);
                    System.out.println(task.execute(map).get());


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,
                        "비밀번호 찾기 버튼",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MailActivity.class);
                startActivity(intent);
            }
        });

       regbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(LoginActivity.this,
                       "회원가입 버튼",
                       Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(getApplicationContext(), regActivity.class);
               startActivity(intent);
                }
            }
       );

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }


    // http통신--------------------------------------------------
    public class MapTask extends AsyncTask<Map, Integer, String> {

        //doInBackground 전에 동작
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //작업을 쓰레드로 처리
        @Override
        protected String doInBackground(Map... maps) {
            //HTTP 요청 준비
            HttpClient.Builder http = new HttpClient.Builder("POST",  Web.servletURL + "androidSignIn.do");

            //Parameter 전송
            http.addAllParameters(maps[0]);

            //HTTP 요청 전송
            HttpClient post = http.create();
            System.out.println("post>>>>>>>>>>>>>>>>>>>>>>>>>"+maps[0]);
            post.request();

            //응답 상태 코드
            int statusCode = post.getHttpStatusCode();
            System.out.println("statusCode>>>>>>>>>>>>>>>>>>>>>>>>>"+statusCode);

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

            if(user != null && user.getEnabled() != 0) {
                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), PhoneAuthActivity.class);
                intent1.putExtra("uid",user.getId());
                intent1.putExtra("uemail",user.getEmail());
                intent1.putExtra("ugender",user.getGender());

                startActivity(intent1);
            } else {
                Toast.makeText(getApplicationContext(), "회원 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // 구글로그인  ------------------------------------------------------
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            System.out.println("onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                System.out.println("onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                System.out.println("account>>>>>>>>>>>>>>>>>>>>>>>>>>"+account);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.out.println("firebaseAuthWithGoogle>>>>>>>>>>>>>>>>>>>>>>>>>>");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("onComplete>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            // Sign in success, update UI with the signed-in user's information
//                            Snackbar.make(findViewById(R.id.login_btn), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Snackbar.make(findViewById(R.id.login_btn), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Toast.makeText(this, "Google 로그인 성공.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, NaviMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //-------------------네이버 로그인 함수
    private void initData(){

        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext,OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME);
        mOAuthLoginButton = findViewById(R.id.naverButton);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {

            if(success){
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                Toast.makeText(mContext,"Naver 로그인 성공",Toast.LENGTH_SHORT).show();


              RequestApiTask task = new RequestApiTask(mContext, mOAuthLoginInstance);
              task.execute();// 로그인이 성공하면 네이버의 계정 정보를 가져온다


            }else{
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext,"errorCode:" + errorCode + ", errorDesc:" + errorDesc,Toast.LENGTH_SHORT).show();
            }
        };
    };

    // 네이버 사용자 정보 가져오기 쓰레드로 처리 해야함
    public class RequestApiTask extends AsyncTask<Void, Void, String> {
        private final Context mContext;
        private final OAuthLogin mOAuthLoginInstance;
        public RequestApiTask(Context mContext, OAuthLogin mOAuthLoginModule) {
            this.mContext = mContext;
            this.mOAuthLoginInstance = mOAuthLoginModule;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            try {
                //json객체를 문자열로 변환시켜서 intent로 보내줌
                System.out.println("onPostExecute>>>>>>>>>>>>>>>>>>>>>>>>");
                JSONObject loginResult = new JSONObject(content);
                if (loginResult.getString("resultcode").equals("00")){
                    System.out.println("onPostExecute> getString>>>>>>>>>>>>>>>>>>>>>>>");
                    JSONObject response = loginResult.getJSONObject("response");
                    String id = response.getString("id");
                    String email = response.getString("email");
                    String mobile = response.getString("mobile");
                    Log.d("response", "response " + response);
                    Intent intent = new Intent(getApplicationContext(), NaviMainActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("email",email);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);
                    Bundle bundle = new Bundle();
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    protected void redirectSignupActivity(){
//        final Intent intent = new Intent(this, logoutActivity.class);
//        startActivity(intent);
//        finish();
//    }

    //뒤로가기 버튼 두번 누르면 종료
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }



}







