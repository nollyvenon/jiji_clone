package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import Database.DatabasePhoneHelper;
import data.VerifiedPhoneNumber;
import others.BottomAppBarEvent;
import others.Constants;

public class PhoneNumberVerification extends AppCompatActivity {

    VerifiedPhoneNumber verifiedPhoneNumber = new VerifiedPhoneNumber();
    DatabasePhoneHelper dbo = new DatabasePhoneHelper(this);

    EditText editTextTel, editTextCode;
    Button activateSMS, sendCode;
    TextView infoText, subInfoText;
    FirebaseAuth mAuth;
    String codeSent, userType, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        activateSMS = findViewById(R.id.submit_phone_number);
        sendCode = findViewById(R.id.set_code);
        infoText = findViewById(R.id.info_text);
        subInfoText = findViewById(R.id.sub_info_text);

        editTextTel = findViewById(R.id.phone_number);
        editTextCode = findViewById(R.id.edit_set_code);

        Intent intent = getIntent();
        userType = intent.getStringExtra("user_type");

        activateSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });
    }

    private void verifySignInCode() {
        String code = editTextCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(userType.equals(Constants.ADS)) {
                                Intent intent = new Intent(getApplicationContext(), AdPosterRegistration.class);
                                startActivity(intent);
                            } else if(userType.equals(Constants.FINDS)) {
                                Intent intent = new Intent(getApplicationContext(), FindPosterRegistration.class);
                                startActivity(intent);
                            }
                            saveToDB();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect verification code", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
    }

    private void sendVerificationCode() {
        phoneNumber = "+" + editTextTel.getText().toString();

        if (phoneNumber.isEmpty()) {
            editTextTel.setError("Telephone number is required");
            editTextTel.requestFocus();
            return;
        }

        if (phoneNumber.length() < 10) {
            editTextTel.setError("Enter a valid phone number");
            editTextTel.requestFocus();
            return;
        }

        activateSMS.setVisibility(View.INVISIBLE);
        editTextTel.setVisibility(View.INVISIBLE);
        subInfoText.setVisibility(View.INVISIBLE);

        infoText.setText("Enter the 6 digit code sent to you");
        sendCode.setVisibility(View.VISIBLE);
        editTextCode.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    public void saveToDB() {
        verifiedPhoneNumber.setUserType(userType);
        verifiedPhoneNumber.setPhoneNumber(phoneNumber);
        if(dbo.getVerifiedPhone().size() > 0) {
            dbo.updateVerifiedPhone(verifiedPhoneNumber);
        } else {
            dbo.saveVerifiedPhone(verifiedPhoneNumber);
        }
        dbo.close();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(PhoneNumberVerification.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(PhoneNumberVerification.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(PhoneNumberVerification.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(PhoneNumberVerification.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(PhoneNumberVerification.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
