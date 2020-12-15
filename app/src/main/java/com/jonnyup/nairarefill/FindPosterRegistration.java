package com.jonnyup.nairarefill;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Database.DatabaseOpenHelper;
import data.AdPoster;
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
    Boolean isEdit = false;
    Intent intent;

    EditText username, phoneNumber, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_poster_registration);

        dbo = new DatabaseOpenHelper(this);

        AdPoster a = dbo.getAdPoster();
        adPoster.setVerifiedPhoneNumber(a.getVerifiedPhoneNumber());

        this.initViews();

        uploadImage.setOnClickListener(v -> ActivityCompat.requestPermissions(FindPosterRegistration.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY));

        addFinder.setOnClickListener(v -> saveToDB());

        intent = getIntent();
        if (intent.getStringExtra("hide") != null) {
            this.setEditText();
        }
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
        if (bitmap == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
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

        if (!isEdit) {
            if (adPoster.getPassword().isEmpty() || !adPoster.getPassword().equals(confirmPassword.getText().toString()))
            {
                password.setError("Password does not match");
                confirmPassword.setError("Password does not match");
                password.requestFocus();
                confirmPassword.requestFocus();
                return;
            }
        }

        addFinder.setClickable(false);
        if (isEdit) {
            addFinder.setText("Updating...");
        } else {
            addFinder.setText(R.string.registering);
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

                addFinder.setClickable(true);
                addFinder.setText(R.string.register);

                AdPoster ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    adPoster.setAuth(ad.getAuth());
                    dbo.updateAdPoster(adPoster);
                    dbo.close();
                    Intent intent = new Intent(FindPosterRegistration.this, FindPostForm.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    FindPosterRegistration.this.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
                //Toast.makeText(FindPosterRegistration.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setEditText() {
        Call<AdPoster> call = ApiClient.connect().getUserByAuth(dbo.getAdPoster().getAuth());
        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FindPosterRegistration.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                AdPoster ad = response.body();
                assert ad != null;

                Glide.with(FindPosterRegistration.this)
                        .asBitmap()
                        .load(Constants.BASE_URL + ad.getProfileImage())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                profileImage.setImageBitmap(resource);
                            }
                        });

                phoneNumber.setText(ad.getPhoneNumber());
                username.setText(ad.getUsername());
                addFinder.setText("Update");

                password.setVisibility(View.GONE);
                confirmPassword.setVisibility(View.GONE);
                findViewById(R.id.password_title).setVisibility(View.GONE);
                findViewById(R.id.comfirm_password_title).setVisibility(View.GONE);
                isEdit = true;

            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
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
