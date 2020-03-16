package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import Database.DatabaseOpenHelper;
import Database.DatabasePhoneHelper;
import data.AdPoster;
import data.VerifiedPhoneNumber;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;

public class AdPosterRegistration extends AppCompatActivity {
    AdPoster adPoster = new AdPoster();
    DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);

    private static final int REQUEST_CODE_GALLERY = 999;
    Button uploadImage, addFinder;
    CircleImageView profileImage;

    EditText location;
    EditText marketArea;
    EditText businessYear;
    EditText username;
    EditText businessName;
    EditText businessDescription;
    EditText serviceDescription;
    EditText phoneNumber;
    EditText accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_poster_registration);

        this.initViews();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AdPosterRegistration.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);
            }
        });

        addFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB();
            }
        });

        Intent intent = getIntent();
        if(!intent.getStringExtra("userType").isEmpty()) {
            ArrayList<AdPoster> adPoster = dbo.getAdPoster();
            editAdProfile(adPoster);
        }
    }

    private void editAdProfile(ArrayList<AdPoster> adPoster) {
        if(adPoster.size() > 0) {

            byte[] bitmapByteAry = adPoster.get(0).getProfileImage();
            if(bitmapByteAry != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByteAry, 0, bitmapByteAry.length);
                profileImage.setImageBitmap(bitmap);
            }
            location.setText(adPoster.get(0).getLocation());
            marketArea.setText(adPoster.get(0).getMarketArea());
            businessYear.setText(adPoster.get(0).getBusinessYear());
            businessName.setText(adPoster.get(0).getBusinessName());
            businessDescription.setText(adPoster.get(0).getBusinessDescription());
            serviceDescription.setText(adPoster.get(0).getServiceDescription());
            phoneNumber.setText(adPoster.get(0).getPhoneNumber());
            accountNumber.setText(adPoster.get(0).getAccountNumber());
            username.setText(adPoster.get(0).getUsername());

        }
    }

    private String capitalise(String string) {
        String str = string;
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    private void initViews() {
        uploadImage = findViewById(R.id.upload_btn);
        profileImage = findViewById(R.id.profile_image);
        addFinder = findViewById(R.id.save_ad_poster);

        profileImage = findViewById(R.id.profile_image);
        location = findViewById(R.id.location);
        marketArea = findViewById(R.id.market_area);
        businessYear = findViewById(R.id.business_year);
        username = findViewById(R.id.username);
        businessName = findViewById(R.id.business_name);
        businessDescription = findViewById(R.id.business_description);
        serviceDescription = findViewById(R.id.service_description);
        phoneNumber = findViewById(R.id.phone_number);
        accountNumber = findViewById(R.id.account_number);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Permission to access file storage not granted", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageToByte(ImageView logo) {
        Bitmap bitmap = ((BitmapDrawable) logo.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    public void saveToDB() {
        if(dbo.getAdPoster().size() > 0)  return;

        adPoster.setProfileImage(imageToByte(profileImage));
        adPoster.setLocation(location.getText().toString());
        adPoster.setMarketArea(marketArea.getText().toString());
        adPoster.setBusinessYear(businessYear.getText().toString());
        adPoster.setBusinessName(businessName.getText().toString());
        adPoster.setBusinessDescription(businessDescription.getText().toString());
        adPoster.setServiceDescription(serviceDescription.getText().toString());
        adPoster.setPhoneNumber(phoneNumber.getText().toString());
        adPoster.setAccountNumber(accountNumber.getText().toString());
        adPoster.setUsername(username.getText().toString());

        if (location.getText().toString().isEmpty()) {
            location.setError("Please Enter Office Location");
            location.requestFocus();
            return;
        }

        if (marketArea.getText().toString().isEmpty()) {
            marketArea.setError("Please Enter Market Area");
            marketArea.requestFocus();
            return;
        }

        if (businessYear.getText().toString().isEmpty()) {
            businessYear.setError("Please Enter Years of Business Existence");
            businessYear.requestFocus();
            return;
        }

        if (businessName.getText().toString().isEmpty()) {
            businessName.setError("Please Enter Business Name");
            businessName.requestFocus();
            return;
        }

        if (businessDescription.getText().toString().isEmpty()) {
            businessDescription.setError("Give The Description of Business");
            businessDescription.requestFocus();
            return;
        }

        if (serviceDescription.getText().toString().isEmpty()) {
            serviceDescription.setError("Service Description is Empty");
            serviceDescription.requestFocus();
            return;
        }

        if (accountNumber.getText().toString().isEmpty()) {
            accountNumber.setError("Enter Your Account Number");
            accountNumber.requestFocus();
            return;
        }

        if (username.getText().toString().isEmpty()) {
            username.setError("Enter Your FullName");
            username.requestFocus();
            return;
        }

        dbo.saveAdPoster(adPoster);
        dbo.close();

//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPosterRegistration.this);
        bottomAppBarEvent.postAd();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPosterRegistration.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPosterRegistration.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPosterRegistration.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPosterRegistration.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
