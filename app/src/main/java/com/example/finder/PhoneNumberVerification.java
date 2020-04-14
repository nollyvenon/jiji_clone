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

import Database.DatabaseOpenHelper;
import data.AdPoster;
import others.BottomAppBarEvent;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneNumberVerification extends AppCompatActivity {

    AdPoster adPoster = new AdPoster();
    DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);

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
        userType = intent.getStringExtra("userType");

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
                            saveToDB();
                            checkType();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect verification code, try adding your phone number again", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
    }

    public void checkType() {
        Call<AdPoster> call = ApiClient.connect().getStatus(adPoster.getVerifiedPhoneNumber());
        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(PhoneNumberVerification.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                AdPoster ad = response.body();
                assert ad != null;

                if(userType == null) userType = "Finds";

                if (Boolean.parseBoolean(ad.getStatus())) {
                    adPoster.setAuth(ad.getAuth());
                    adPoster.setUserType(ad.getUserType());
                }

                if(userType.equals(Constants.ADS)) {
                    checkAd(ad);
                } else {
                    checkFind(ad);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
                //Toast.makeText(PhoneNumberVerification.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendVerificationCode() {
        String num = editTextTel.getText().toString();
        if(num.length() == 11) {
            phoneNumber = "+234" + num.substring(1);
        } else {
            phoneNumber = num;
        }

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

        infoText.setText(R.string.phone_verification_info);
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
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    public void saveToDB() {
        adPoster.setVerifiedPhoneNumber(phoneNumber);
        if(dbo.getAdPoster().getVerifiedPhoneNumber() != null) {
            dbo.updateAdPoster(adPoster);
        } else {
            dbo.saveAdPoster(adPoster);
        }
        dbo.close();
    }

    public void checkFind(AdPoster ad) {
        adPoster.setFinds(Constants.FINDS);
        if(ad.getUserType() != null) {
            if (ad.getUserType().equals(Constants.FINDS) || ad.getUserType().equals(Constants.ADS)) {
                saveToDB();
                Intent intent = new Intent(getApplicationContext(), FindPostForm.class);
                startActivity(intent);
            }
        }

        if(!Boolean.parseBoolean(ad.getStatus())) {
            Intent intent = new Intent(getApplicationContext(), FindPosterRegistration.class);
            startActivity(intent);
        }
    }

    public void checkAd(AdPoster ad) {
        adPoster.setAds(Constants.ADS);
        if(ad.getUserType() != null) {
            if (ad.getUserType().equals(Constants.ADS)) {
                saveToDB();
                Intent intent = new Intent(getApplicationContext(), AdPostForm.class);
                startActivity(intent);
            } else if ((ad.getUserType().equals(Constants.FINDS) && ad.getBusinessName().isEmpty()) || !Boolean.parseBoolean(ad.getStatus())) {
                Intent intent = new Intent(getApplicationContext(), AdPosterRegistration.class);
                intent.putExtra("username", "ad.getUsername()");
                intent.putExtra("phoneNumber", ad.getPhoneNumber());
                intent.putExtra("hide", "hide");
                startActivity(intent);
            }
        }   else {
            Intent intent = new Intent(getApplicationContext(), AdPosterRegistration.class);
            startActivity(intent);
        }
    }

    public void goBack(View view) {
        finish();
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
