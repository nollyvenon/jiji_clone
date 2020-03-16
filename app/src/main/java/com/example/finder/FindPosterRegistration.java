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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Database.DatabaseFinderHelper;
import data.FindPoster;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;

public class FindPosterRegistration extends AppCompatActivity {

    DatabaseFinderHelper dbo;
    private static final int REQUEST_CODE_GALLERY = 999;
    Button uploadImage, addFinder;
    CircleImageView profileImage;

    EditText username;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_poster_registration);

        dbo = new DatabaseFinderHelper(this);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(this.capitalise("Profile Update"));

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
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phone_number);
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

//    private byte[] imageToByte(ImageView logo) {
//        Bitmap bitmap = ((BitmapDrawable) logo.getDrawable()).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        return bytes;
//    }

    public void saveToDB() {
        if(dbo.fetchFindPoster().size() > 0)  return;

        FindPoster findPoster = new FindPoster();
        findPoster.setPhoneNumber(phoneNumber.getText().toString());
        findPoster.setUsername(username.getText().toString());

        if (username.getText().toString().isEmpty()) {
            username.setError("Enter Your FullName");
            username.requestFocus();
            return;
        }

        dbo.saveFindPoster(findPoster);
        dbo.close();
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
