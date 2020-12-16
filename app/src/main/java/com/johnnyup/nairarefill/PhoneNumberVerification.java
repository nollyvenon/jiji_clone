package com.johnnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
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
    String codeSent, userType, phoneNumber, countryText;
    private int countryPosition = 0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);
        dialog = new ProgressDialog(PhoneNumberVerification.this);

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

        activateSMS.setOnClickListener(v -> sendVerificationCode());

        sendCode.setOnClickListener(v -> verifySignInCode());


        Spinner countrySpinner = findViewById(R.id.country_list);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, country);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryPosition = position;
                countryText = parent.getItemAtPosition(position).toString();
                //editTextTel.setText("+" + code[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveToDB();
                        checkType();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(),
                                    "Incorrect verification code, try adding your phone number again", Toast.LENGTH_LONG).show();
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

                if (userType == null) userType = "Finds";

                if (Boolean.parseBoolean(ad.getStatus())) {
                    adPoster.setAuth(ad.getAuth());
                    adPoster.setUserType(ad.getUserType());
                }

                if (userType.equals(Constants.ADS)) {
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
        String noCodeTel = editTextTel.getText().toString();
        String num = "+" + PhoneNumberVerification.code[countryPosition] + noCodeTel;
        if (noCodeTel.startsWith("+")) {
            editTextTel.setError("Pick a country And Telephone must not contain country code");
            return;
        } else {
            phoneNumber = num;
        }

        if (phoneNumber.isEmpty()) {
            editTextTel.setError("Telephone number is required");
            editTextTel.requestFocus();
            return;
        }

        dialog.setMessage("Loading...");
        dialog.show();

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
            dialog.dismiss();
            activateSMS.setVisibility(View.INVISIBLE);
            editTextTel.setVisibility(View.INVISIBLE);
            subInfoText.setVisibility(View.INVISIBLE);
            Spinner countrySpinner = findViewById(R.id.country_list);
            countrySpinner.setVisibility(View.INVISIBLE);

            infoText.setText(R.string.phone_verification_info);
            sendCode.setVisibility(View.VISIBLE);
            editTextCode.setVisibility(View.VISIBLE);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneNumberVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    public static String[] getCode() {
        return code;
    }

    public static String[] country = new String[]{
            "Nigeria",
            "Afghanistan",
            "Albania",
            "Algeria",
            "American Samoa",
            "Andorra",
            "Angola",
            "Anguilla",
            "Antarctica",
            "Antigua and Barbuda",
            "Argentina",
            "Armenia",
            "Aruba",
            "Australia",
            "Austria",
            "Azerbaijan",
            "Bahamas",
            "Bahrain",
            "Bangladesh",
            "Barbados",
            "Belarus",
            "Belgium",
            "Belize",
            "Benin",
            "Bermuda",
            "Bhutan",
            "Bolivia",
            "Bosnia and Herzegovina",
            "Botswana",
            "Brazil",
            "British Indian Ocean Territory",
            "British Virgin",
            "Brunei",
            "Bulgaria",
            "Burkina Faso",
            "Burundi",
            "Cambodia",
            "Cameroon ",
            "Canada",
            "Cape Verde",
            "Cayman Islands",
            "Central African Republic",
            "Chad",
            "Chile",
            "China",
            "Christmas Island",
            "Cocos Islands",
            "Colombia",
            "Comoros",
            "Cook Islands",
            "Costa Rica",
            "Croatia",
            "Cuba ",
            "Curacao",
            "Cyprus ",
            "Czech Republic",
            "Democratic Republic of the Congo",
            "Denmark ",
            "Djibouti ",
            "Dominica ",
            "Dominican Republic",
            "East Timor",
            "Ecuador",
            "Egypt ",
            "El Salvador",
            "Equatorial Guinea",
            "Eritrea ",
            "Estonia ",
            "Ethiopia ",
            "Falkland Islands ",
            "Faroe Islands ",
            "Fiji ",
            "Finland ",
            "France",
            "French Polynesia",
            "Gabon ",
            "Gambia",
            "Georgia ",
            "Germany ",
            "Ghana ",
            "Gibraltar",
            "Greece ",
            "Greenland",
            "Grenada ",
            "Guam ",
            "Guatemala",
            "Guernsey ",
            "Guinea ",
            "Guinea-Bissau",
            "Guyana",
            "Haiti ",
            "Honduras ",
            "Hong Kong ",
            "Hungary ",
            "Iceland ",
            "India ",
            "Indonesia ",
            "Iran ",
            "Iraq ",
            "Ireland ",
            "Isle of Man ",
            "Israel",
            "Italy ",
            "Ivory Coast",
            "Jamaica ",
            "Japan ",
            "Jersey ",
            "Jordan ",
            "Kazakhstan",
            "Kenya ",
            "Kiribati",
            "Kosovo",
            "Kuwait ",
            "Kyrgyzstan",
            "Laos ",
            "Latvia",
            "Lebanon ",
            "Lesotho ",
            "Liberia ",
            "Libya ",
            "Liechtenstein",
            "Lithuania",
            "Luxembourg",
            "Macau ",
            "Macedonia",
            "Madagascar",
            "Malawi",
            "Malaysia",
            "Maldives ",
            "Mali ",
            "Malta ",
            "Marshall Islands ",
            "Mauritania",
            "Mauritius ",
            "Mayotte ",
            "Mexico ",
            "Micronesia",
            "Moldova ",
            "Monaco ",
            "Mongolia",
            "Montenegro",
            "Montserrat",
            "Morocco ",
            "Mozambique",
            "Myanmar ",
            "Namibia ",
            "Nauru ",
            "Nepal ",
            "Netherlands",
            "Netherlands Antilles",
            "New Caledonia ",
            "New Zealand ",
            "Nicaragua ",
            "Niger ",
            "Niue ",
            "North Korea",
            "Northern Mariana Islands",
            "Norway ",
            "Oman ",
            "Pakistan",
            "Palau ",
            "Palestine",
            "Panama ",
            "Papua New Guinea",
            "Paraguay ",
            "Peru ",
            "Philippines ",
            "Pitcairn ",
            "Poland ",
            "Portugal",
            "Puerto Rico",
            "Qatar ",
            "Republic of the Congo",
            "Reunion ",
            "Romania ",
            "Russia",
            "Rwanda ",
            "Saint Barthelemy",
            "Saint Helena",
            "Saint Kitts and Nevis",
            "Saint Lucia",
            "Saint Martin",
            "Saint Pierre and Miquelon",
            "Saint Vincent and the Grenadines",
            "Samoa ",
            "San Marino",
            "Sao Tome and Principe",
            "Saudi Arabia ",
            "Senegal ",
            "Serbia ",
            "Seychelles",
            "Sierra Leone",
            "Singapore",
            "Sint Maarten",
            "Slovakia",
            "Slovenia",
            "Solomon Islands",
            "Somalia ",
            "South Africa",
            "South Korea ",
            "South Sudan ",
            "Spain ",
            "Sri Lanka",
            "Sudan ",
            "Suriname",
            "Svalbard and Jan Mayen ",
            "Swaziland ",
            "Sweden ",
            "Switzerland",
            "Syria ",
            "Taiwan",
            "Tajikistan",
            "Tanzania ",
            "Thailand ",
            "Togo ",
            "Tokelau ",
            "Tonga ",
            "Trinidad and Tobago ",
            "Tunisia ",
            "Turkey ",
            "Turkmenistan",
            "Turks and Caicos Islands",
            "Tuvalu ",
            "U.S. Virgin Islands",
            "Uganda ",
            "Ukraine ",
            "United Arab Emirates",
            "United Kingdom ",
            "United States ",
            "Uruguay ",
            "Uzbekistan ",
            "Vanuatu ",
            "Vatican ",
            "Venezuela ",
            "Vietnam ",
            "Wallis and Futuna",
            "Western Sahara",
            "Yemen ",
            "Zambia",
            "Zimbabwe"};

    public static String[] code = new String[]{
            "234","93",
            "355", "213", "1-684", "376",
            "244", "1-264", "672", "1-268", "54", "374", "297", "61", "43", "994", "1-242", "973", "880", "1-246", "375", "32",
            "501", "229", "1-441", "975", "591", "387", "267", "55", "246", "1-284","673","359","226","257","855","237","1","238",
            "1-345", "236", "235", "56","86","61","61","57","269","682","506","385","53","599","357","420","243","45","253","1-767",
            "1-809","670","593","20","503","240","291","372","251","500","298","679","358","33","689","241","220","995","49","233","350",
            "30",
            "299",
            "1-473",
            "1-671",
            "502",
            "44-1481",
            "224",
            "245",
            "592",
            "509",
            "504",
            "852",
            "36",
            "354",
            "91",
            "62",
            "98",
            "964",
            "353",
            "44-1624",
            "972",
            "39",
            "225",
            "1-876",
            "81",
            "44-1534",
            "962",
            "7",
            "254",
            "686",
            "383",
            "965",
            "996",
            "856",
            "371",
            "961",
            "266",
            "231",
            "218",
            "423",
            "370",
            "352",
            "853",
            "389",
            "261",
            "265",
            "60",
            "960",
            "223",
            "356",
            "692",
            "222",
            "230",
            "262",
            "52",
            "691",
            "373",
            "377",
            "976",
            "382",
            "1-664",
            "212",
            "258",
            "95",
            "264",
            "674",
            "977",
            "31",
            "599",
            "687",
            "64",
            "505",
            "227",
            "683",
            "850",
            "1-670",
            "47",
            "968",
            "92",
            "680",
            "970",
            "507",
            "675",
            "595",
            "51",
            "63",
            "64",
            "48",
            "351",
            "1-787",
            "974",
            "242",
            "262",
            "40",
            "7",
            "250",
            "590",
            "290",
            "1-869",
            "1-758",
            "590",
            "508",
            "1-784",
            "685",
            "378",
            "239",
            "966",
            "221",
            "381",
            "248",
            "232",
            "65",
            "1-721",
            "421",
            "386",
            "677",
            "252",
            "27",
            "82",
            "211",
            "34",
            "94",
            "249",
            "597",
            "47",
            "268",
            "46",
            "41",
            "963",
            "886",
            "992",
            "255",
            "66",
            "228",
            "690",
            "676",
            "1-868",
            "216",
            "90",
            "993",
            "1-649",
            "688",
            "1-340",
            "256",
            "380",
            "971",
            "44",
            "1",
            "598",
            "998",
            "678",
            "379",
            "58",
            "84",
            "681",
            "212",
            "967",
            "260",
            "263"

    };

    public void saveToDB() {
        adPoster.setVerifiedPhoneNumber(phoneNumber);
        if (dbo.getAdPoster().getVerifiedPhoneNumber() != null) {
            dbo.updateAdPoster(adPoster);
        } else {
            dbo.saveAdPoster(adPoster);
        }
        dbo.close();
    }

    public void checkFind(AdPoster ad) {
        adPoster.setFinds(Constants.FINDS);
        if (ad.getUserType() != null) {
            if (ad.getUserType().equals(Constants.FINDS) || ad.getUserType().equals(Constants.ADS)) {
                saveToDB();
                Intent intent = new Intent(getApplicationContext(), FindPostForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                this.finish();
            }
        }

        if (!Boolean.parseBoolean(ad.getStatus())) {
            Intent intent = new Intent(getApplicationContext(), FindPosterRegistration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            this.finish();
        }
    }

    public void checkAd(AdPoster ad) {
        adPoster.setAds(Constants.ADS);
        if (ad.getUserType() != null) {
            if (ad.getUserType().equals(Constants.ADS)) {
                saveToDB();
                Intent intent = new Intent(getApplicationContext(), AdPostForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                this.finish();
            } else if ((ad.getUserType().equals(Constants.FINDS) && ad.getBusinessName().isEmpty()) || !Boolean.parseBoolean(ad.getStatus())) {
                Intent intent = new Intent(getApplicationContext(), AdPosterRegistration.class);
                intent.putExtra("username", "ad.getUsername()");
                intent.putExtra("phoneNumber", ad.getPhoneNumber());
                intent.putExtra("hide", "hide");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                this.finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), AdPosterRegistration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            this.finish();
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
