package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText inputNumber;
    Button sendOtpBtn;
    ProgressBar loginProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

//        declaration
        countryCodePicker = findViewById(R.id.login_countrycode);
        inputNumber = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.login_send_otp);
        loginProgressBar = findViewById(R.id.login_phone_progress_bar);

        loginProgressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(inputNumber);

        sendOtpBtn.setOnClickListener((v) -> {
            if(!countryCodePicker.isValidFullNumber()){
                inputNumber.setError("Enter valid number");
                return;
            }
            Intent intent = new Intent(LoginPhoneNumberActivity.this,LoginOtpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}