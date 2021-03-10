package com.fithe.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fithe.fragment.NaviMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
//import com.google.firebase.quickstart.auth.databinding.ActivityPhoneAuthBinding;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.concurrent.TimeUnit;

import com.fithe.login.loginandroid.R;

public class PhoneAuthActivity extends AppCompatActivity  {
    private static final String TAG = "PhoneAuthActivity";
    Button buttonVerifyPhone,buttonResend,registerButton;
    ImageButton imageButton2;
    EditText fieldPhoneNumber,fieldVerificationCode;

    // 뒤로가기 현재시간 초기화
    private long backBtnTime = 0;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";


    private FirebaseAuth mAuth;
    private String mVerificationId;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //public ActivityPhoneAuthBinding mBinding;

    private String uid,uemail,ugender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBinding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(R.layout.aaaaa);
        mAuth = FirebaseAuth.getInstance();
        fieldPhoneNumber = (EditText)findViewById(R.id.fieldPhoneNumber);
        fieldVerificationCode = (EditText)findViewById(R.id.fieldVerificationCode);
        registerButton = (Button)findViewById(R.id.registerButton);
        buttonVerifyPhone = (Button)findViewById(R.id.validateButton);
        buttonResend = (Button)findViewById(R.id.buttonResend);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);

        Intent intent = getIntent();
        uid=intent.getExtras().getString("uid");
        uemail=intent.getExtras().getString("uemail");
        ugender=intent.getExtras().getString("ugender");

        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = fieldPhoneNumber.getText().toString();
                System.out.println("phonenumber >>>>>>>>>>>>>>>>>" + phonenumber);
                System.out.println("mResendToken >>>>>>>>>>>>>>>>>" + mResendToken);
                resendVerificationCode(phonenumber,mResendToken);
            }
        });

        buttonVerifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePhoneNumber()) {
                    return;
                }
                System.out.println("startPhoneNumberVerification진입전");
                startPhoneNumberVerification("+82"+fieldPhoneNumber.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = fieldVerificationCode.getText().toString();
                if(TextUtils.isEmpty(code)){
                    fieldVerificationCode.setError("cannot be empty");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                mVerificationInProgress = false;
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            }


            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                System.out.println("mVerificationId>>>>>>>>>>>>>>>>>>>>"+mVerificationId);
                System.out.println("mResendToken>>>>>>>>>>>>>>>>>>>>"+mResendToken);
                // [START_EXCLUDE]
                // Update UI
//                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }

        };
        // END CALL BACKS
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        System.out.println("startPhoneNumberVerification  >>>>>>>>>>>>>>>>>>>>>>>>>>> 진입");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]

        mVerificationInProgress = true;
        System.out.println("startPhoneNumberVerification  >>>>>>>>>>>>>>>>>>>>>>>>>>> 진입끝");
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        System.out.println("verifyPhoneNumberWithCode  >>>>>>>>>>>>>>>>>>>>>>>>>>> 진입");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        System.out.println("startPhoneNumberVerification  >>>>>>>>>>>>>>>>>>>>>>>>>>> 진입");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        System.out.println("startPhoneNumberVerification  >>>>>>>>>>>>>>>>>>>>>>>>>>> 진입끝");
    }
    // [END resend_verification]

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            System.out.println("user>>>>>>>>>>>>>>>>>>>>>>"+user);
                            FirebaseAuth.getInstance().signOut();
                            // [START_EXCLUDE]
                            UpdateUI();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneAuthActivity.this,
                                    "인증 실패",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(PhoneAuthActivity.this,
                                        "인증 실패",Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }

                        }
                    }
                });
    }
    // [END sign_in_with_phone

    private void signOut() {
        mAuth.signOut();
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = fieldPhoneNumber.getText().toString();
        System.out.println("phoneNumber>>>>>>>>>>>>>>"+phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void UpdateUI(){
        Intent intent1 = new Intent(getApplicationContext(), NaviMainActivity.class);
        intent1.putExtra("uid",uid);
        intent1.putExtra("uemail",uemail);
        intent1.putExtra("ugender",ugender);
        System.out.println("uemailkjljkljlkjkljlkjkljkljkl>>>>>>>>>>>>>>>"+uemail);
        Toast.makeText(PhoneAuthActivity.this,
                "인증 성공",
                Toast.LENGTH_SHORT).show();

        startActivity(intent1);
        finish();

    }


    //뒤로가기 버튼 두번 누르면 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
