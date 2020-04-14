package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.FindPoster;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPosterRegistration extends AppCompatActivity {

    AdPoster adPoster = new AdPoster();

    DatabaseOpenHelper dbo;
    Bitmap bitmap;
    private static final int REQUEST_CODE_GALLERY = 999;
    Button uploadImage, addFinder;
    CircleImageView profileImage;

    EditText username, phoneNumber, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_poster_registration);

        dbo = new DatabaseOpenHelper(this);

        AdPoster a = dbo.getAdPoster();
        adPoster.setVerifiedPhoneNumber(a.getVerifiedPhoneNumber());

        this.initViews();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(FindPosterRegistration.this,
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
    }

    private void initViews() {
        uploadImage = findViewById(R.id.upload_btn);
        profileImage = findViewById(R.id.profile_image);
        addFinder = findViewById(R.id.save_ad_poster);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.comfirm_password);
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
                assert uri != null;
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString() {
        if(bitmap == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void saveToDB() {

        adPoster.setProfileImage(imageToString());
        adPoster.setPhoneNumber(phoneNumber.getText().toString());
        adPoster.setUsername(username.getText().toString());
        adPoster.setPassword(password.getText().toString());
        adPoster.setUserType(Constants.FINDS);

        if (username.getText().toString().isEmpty()) {
            username.setError("Enter Your FullName");
            username.requestFocus();
            return;
        }

        if (adPoster.getPassword().isEmpty() || !adPoster.getPassword().equals(confirmPassword.getText().toString())) {
            password.setError("Password does not match");
            confirmPassword.setError("Password does not match");
            password.requestFocus();
            confirmPassword.requestFocus();
            return;
        }

        Call<AdPoster> call = ApiClient.connect().registerFind(
                adPoster.getProfileImage(), adPoster.getPhoneNumber(), adPoster.getVerifiedPhoneNumber(),
                adPoster.getUsername(), adPoster.getPassword(), adPoster.getUserType()
        );

        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(FindPosterRegistration.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                AdPoster ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    adPoster.setAuth(ad.getAuth());
                    dbo.updateAdPoster(adPoster);
                    dbo.close();
                    Intent intent = new Intent(FindPosterRegistration.this, AdPostForm.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
                //Toast.makeText(FindPosterRegistration.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goBack(View view) {
        finish();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPosterRegistration.this);
        bottomAppBarEvent.postAd();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPosterRegistration.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPosterRegistration.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPosterRegistration.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindPosterRegistration.this);
        bottomAppBarEvent.goToMessageListActivity();
    }

}
