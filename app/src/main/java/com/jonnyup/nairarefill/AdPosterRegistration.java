package com.jonnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class AdPosterRegistration extends AppCompatActivity {
    AdPoster adPoster = new AdPoster();
    DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
    Bitmap bitmap;
    Intent intent;

    Uri outputFileUri;
    int TAKE_PHOTO_CODE = 888;
    public static int count = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 777;
    private static final int REQUEST_CODE_GALLERY = 999;
    Button uploadImage, addPoster;
    CircleImageView profileImage;
    Boolean isEdit = false;
    String currentPhotoPath;

    EditText location, marketArea, businessYear;
    EditText username, businessName, businessDescription, serviceDescription, email;
    EditText phoneNumber, accountNumber, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_poster_registration);

        this.initViews();

        AdPoster a = dbo.getAdPoster();
        adPoster.setAuth(a.getAuth());
        adPoster.setVerifiedPhoneNumber(a.getVerifiedPhoneNumber());

        findViewById(R.id.bottom_appbar).setVisibility(View.GONE);

        uploadImage.setOnClickListener(v -> dispatchTakePictureIntent());

        addPoster.setOnClickListener(v -> saveToDB(v));

        intent = getIntent();
        if (intent.getStringExtra("hide") != null) {
            isEdit = true;
            this.setEditText();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            final String dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/picFolder/";
            File newdir = new File(dir);
            if (!newdir.exists()) {
                newdir.mkdir();
            }

            count++;
            String file = dir + count + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }

            // Continue only if the File was successfully created
            //if (photoFile != null) {
            outputFileUri = FileProvider.getUriForFile(this,
                        "com.example.finder.fileprovider",
                        newfile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            //}
        }

    }

    private void setEditText() {
        Call<AdPoster> call = ApiClient.connect().getUserByAuth(adPoster.getAuth());
        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPosterRegistration.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                AdPoster ad = response.body();
                assert ad != null;

                Glide.with(AdPosterRegistration.this)
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
                location.setText(ad.getLocation());
                marketArea.setText(ad.getMarketArea());
                businessYear.setText(ad.getBusinessYear());
                businessName.setText(ad.getBusinessName());
                businessDescription.setText(ad.getBusinessDescription());
                serviceDescription.setText(ad.getServiceDescription());
                accountNumber.setText(ad.getAccountNumber());
                email.setText(ad.getEmail());

                password.setVisibility(View.INVISIBLE);
                confirmPassword.setVisibility(View.INVISIBLE);
                TextView pwd = findViewById(R.id.pwd);
                pwd.setVisibility(View.INVISIBLE);
                TextView cpwd = findViewById(R.id.cpwd);
                cpwd.setVisibility(View.INVISIBLE);
                addPoster.setText("Update");

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addPoster.getLayoutParams();
                params.topMargin = -250;
                addPoster.setLayoutParams(params);
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
            }
        });
    }

    private void initViews() {
        uploadImage = findViewById(R.id.upload_btn);
        profileImage = findViewById(R.id.profile_image);
        addPoster = findViewById(R.id.save_ad_poster);

        location = findViewById(R.id.location);
        marketArea = findViewById(R.id.market_area);
        businessYear = findViewById(R.id.business_year);
        username = findViewById(R.id.username);
        businessName = findViewById(R.id.business_name);
        businessDescription = findViewById(R.id.business_description);
        serviceDescription = findViewById(R.id.service_description);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        accountNumber = findViewById(R.id.account_number);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.comfirm_password);
        findViewById(R.id.account_number_title).setVisibility(View.GONE);
        accountNumber.setVisibility(View.GONE);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_GALLERY) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_CODE_GALLERY);
//            } else {
//                Toast.makeText(this, "Permission to access file storage not granted", Toast.LENGTH_LONG).show();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri uri = outputFileUri;
            try {
                assert uri != null;
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap = getResizedBitmap(bitmap, 200, 120);

                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString() {
        if (bitmap == null) return "";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        if (bm.getWidth() > bm.getHeight()) {
            matrix.postRotate(90);
        }

        matrix.postScale(scaleWidth, scaleHeight);
        //bm = Bitmap.createBitmap(bm , 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    public void saveToDB(View v) {
        adPoster.setProfileImage(imageToString());
        adPoster.setLocation(location.getText().toString());
        adPoster.setMarketArea(marketArea.getText().toString());
        adPoster.setBusinessYear(businessYear.getText().toString());
        adPoster.setBusinessName(businessName.getText().toString());
        adPoster.setBusinessDescription(businessDescription.getText().toString());
        adPoster.setServiceDescription(serviceDescription.getText().toString());
        adPoster.setPhoneNumber(phoneNumber.getText().toString());
        adPoster.setAccountNumber(accountNumber.getText().toString());
        adPoster.setUsername(username.getText().toString());
        adPoster.setEmail(email.getText().toString());
        adPoster.setPassword(password.getText().toString());
        adPoster.setUserType(Constants.ADS);

        this.validate(adPoster);
        addPoster.setClickable(false);

        if (isEdit) {
            addPoster.setText("Updating...");
        } else {
            addPoster.setText(R.string.registering);
        }

        Call<AdPoster> call = ApiClient.connect().register(
                adPoster.getProfileImage(), adPoster.getLocation(), adPoster.getMarketArea(), adPoster.getEmail(),
                adPoster.getBusinessYear(), adPoster.getBusinessName(), adPoster.getBusinessDescription(),
                adPoster.getServiceDescription(), adPoster.getPhoneNumber(), adPoster.getVerifiedPhoneNumber(),
                adPoster.getAccountNumber(), adPoster.getUsername(), adPoster.getPassword(), adPoster.getUserType()
        );

        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPosterRegistration.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                AdPoster ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    addPoster.setClickable(true);
                    addPoster.setText(R.string.register);

                    adPoster.setAuth(ad.getAuth());
                    dbo.updateAdPoster(adPoster);
                    dbo.close();
                    Intent i;
                    if (intent.getStringExtra("hide") != null) {
                        finish();
//                        i = new Intent(AdPosterRegistration.this, Profile.class);
//                        i.putExtra("id", String.valueOf(ad.getId()));
//                    } else {
//                        i = new Intent(AdPosterRegistration.this, AdPostForm.class);
                    }
                    //startActivity(i);
                    Toast.makeText(AdPosterRegistration.this, "Profile Data saved", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void validate(AdPoster adPoster) {
        if (adPoster.getLocation().isEmpty()) {
            location.setError("Please Enter Office Location");
            location.requestFocus();
            return;
        }

        if (adPoster.getMarketArea().isEmpty()) {
            marketArea.setError("Please Enter Market Area");
            marketArea.requestFocus();
            return;
        }

        if (adPoster.getBusinessYear().isEmpty()) {
            businessYear.setError("Please Enter Years of Business Existence");
            businessYear.requestFocus();
            return;
        }

        if (adPoster.getBusinessName().isEmpty()) {
            businessName.setError("Please Enter Business Name");
            businessName.requestFocus();
            return;
        }

        if (adPoster.getBusinessDescription().isEmpty()) {
            businessDescription.setError("Give The Description of Business");
            businessDescription.requestFocus();
            return;
        }

        if (adPoster.getServiceDescription().isEmpty()) {
            serviceDescription.setError("Service Description is Empty");
            serviceDescription.requestFocus();
            return;
        }

        if (adPoster.getAccountNumber().isEmpty()) {
            accountNumber.setError("Enter Your Account Number");
            accountNumber.requestFocus();
            return;
        }

        if (adPoster.getEmail().isEmpty()) {
            email.setError("Enter Your Email Address");
            email.requestFocus();
            return;
        }

        if (adPoster.getUsername().isEmpty()) {
            username.setError("Enter Your FullName");
            username.requestFocus();
            return;
        }

        if (adPoster.getPassword().isEmpty() || !adPoster.getPassword().equals(confirmPassword.getText().toString())) {
            password.setError("Password does not match");
            confirmPassword.setError("Password does not match");
            password.requestFocus();
            confirmPassword.requestFocus();
        }
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
