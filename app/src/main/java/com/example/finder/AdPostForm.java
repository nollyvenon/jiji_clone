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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Ads;
import data.Benefit;
import data.CategoryListData;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdPostForm extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 999;
    CircleImageView image;
    Bitmap bitmap;

    EditText description, price, title;
    String categoryText;
    HashMap<Integer, String> checkedList;
    List<String> categoryList;
    ArrayList<String> promotionData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_post_form);

        ImageView bottomBarImg = findViewById(R.id.img_ad_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_ad_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Post Service You Render");

        categoryList = new ArrayList<>();
        categoryList.add("Select Category");

        showCategories();

        Spinner categorySpinner = findViewById(R.id.category_list);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select Category")) {
                } else {
                    categoryText = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getBenefits();

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(AdPostForm.this);
            }
        });
    }

    private void getBenefits() {
        Call<List<Benefit>> call = ApiClient.connect().getBenefits();
        call.enqueue(new Callback<List<Benefit>>() {
            @Override
            public void onResponse(@NonNull Call<List<Benefit>> call, @NonNull Response<List<Benefit>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Benefit> benefits = response.body();

                assert benefits != null;
                for(Benefit benefit : benefits) {
                    promotionData.add(benefit.getBenefit());
                }

                final ListView promotionalList = findViewById(R.id.promotional_list);
                promotionalList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                ArrayAdapter<String> promotionalAdapter = new ArrayAdapter<>(AdPostForm.this, R.layout.item_promotional, R.id.promotion, promotionData);

                checkedList = new HashMap<>();
                promotionalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (checkedList.containsKey(position)) {
                            checkedList.remove(position);
                        } else {
                            checkedList.put(position, promotionalList.getItemAtPosition(position).toString());
                        }
                    }
                });

                promotionalList.setAdapter(promotionalAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Benefit>> call, @NonNull Throwable t) {
            }
        });
    }

    private void showCategories() {
        Call<List<CategoryListData>> call = ApiClient.connect().getCategories();
        call.enqueue(new Callback<List<CategoryListData>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryListData>> call, @NonNull Response<List<CategoryListData>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<CategoryListData> categories = response.body();

                assert categories != null;
                for(CategoryListData category : categories) {
                    categoryList.add(category.getName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryListData>> call, @NonNull Throwable t) {
            }
        });
    }

    public void initForm() {
        description = findViewById(R.id.ad_description);
        title = findViewById(R.id.ad_title);
        price = findViewById(R.id.ad_price);
    }

    public void addAd(View view) {
        initForm();

        String descriptionText = description.getText().toString();
        String titleText = title.getText().toString();
        String priceText = price.getText().toString();
        String benefit = "";

        if (descriptionText.isEmpty()) {
            Toast.makeText(this, "Description field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        if (titleText.isEmpty()) {
            Toast.makeText(this, "Title field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        if (categoryText.isEmpty()) {
            Toast.makeText(this, "Category field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        for (String checked : checkedList.values()) {
            benefit = benefit + checked + ",";
        }

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        Call<Ads> call = ApiClient.connect().addAd(
                descriptionText, titleText, priceText, benefit.substring(0, benefit.length() - 1), imageToString(), categoryText,
                a.getAuth()
        );
        call.enqueue(new Callback<Ads>() {
            @Override
            public void onResponse(@NonNull Call<Ads> call, @NonNull Response<Ads> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdPostForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Ads ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    Intent intent = new Intent(AdPostForm.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
            }
        });
    }

    public void uploadImage(View view) {
        image = findViewById(R.id.image);
        ActivityCompat.requestPermissions(AdPostForm.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY);
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
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString() {
        if(bitmap == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdPostForm.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
