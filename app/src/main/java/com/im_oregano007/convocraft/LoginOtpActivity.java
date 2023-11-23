package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.im_oregano007.convocraft.utils.AndroidUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    EditText inputOtp;
    Button verifyBtn;
    TextView resendOtp;
    ProgressBar otpProgressBar;

    String phoneNumber, verificationCode;

    PhoneAuthProvider.ForceResendingToken resendingToken;

    Long timeoutSeconds = 60L;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        inputOtp = findViewById(R.id.login_otp);
        verifyBtn = findViewById(R.id.login_verify);
        resendOtp = findViewById(R.id.resend_otp_text_view);
        otpProgressBar = findViewById(R.id.otp_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");

        sendOtp(phoneNumber,false);



    }

//    send otp for authentication
    void sendOtp(String phoneNumber, boolean isResend){
        changeInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInMethod(phoneAuthCredential);
                                changeInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtils.showToast(getApplicationContext(),"OTP Verification Failed");
                                changeInProgress(false);

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtils.showToast(getApplicationContext(),"OTP sent Successfully");
                                changeInProgress(false);


                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }

    }

    private void signInMethod(PhoneAuthCredential phoneAuthCredential) {
    }

    void changeInProgress(boolean isProgress){
        if(isProgress){
            otpProgressBar.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.INVISIBLE);
        } else{
            otpProgressBar.setVisibility(View.INVISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
        }
    }
}